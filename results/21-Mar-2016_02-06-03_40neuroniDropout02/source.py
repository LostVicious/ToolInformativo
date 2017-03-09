'''Train a LSTM on the IMDB sentiment classification task.
The dataset is actually too small for LSTM to be of any advantage
compared to simpler, much faster methods such as TF-IDF+LogReg.
Notes:
- RNNs are tricky. Choice of batch size is important,
choice of loss and optimizer is critical, etc.
Some configurations won't converge.
- LSTM loss decrease patterns during training can be quite different
from what you see with CNNs/MLPs/etc.
GPU command:
    THEANO_FLAGS=mode=FAST_RUN,device=gpu,floatX=float32 python imdb_lstm.py
'''
#from __future__ import print_function

import numpy as np
np.random.seed(1337)  # for reproducibility

import mysql.connector
from keras.models import Sequential, model_from_json
from keras.layers.core import Dense, Dropout, Activation
from keras.optimizers import SGD,Adagrad,Adam
from keras.regularizers import l1
import matplotlib.pyplot as plt
from matplotlib.colors import ListedColormap
from datetime import datetime
import keras
import eval_model
import os
from time import gmtime, strftime
from shutil import copyfile

from sklearn.linear_model import LinearRegression

#http://stackoverflow.com/questions/11874767/real-time-plotting-in-while-loop-with-matplotlib
#plt.ion()


def check_values(r):
    if (r["price"] is None): return False
    if (r["vwapRatio"] is None): return False
    if (r["vwapRatioFTSE"] is None): return False
    if (r["minuto"] is None): return False
    #if (r["volumeRatio"] is None): return False
    #if (r["pLX.FSPX"] is None): return False
    #if (r["bookImbalance30"] is None): return False
    if (r["minuto"]>1040): return False # entro le 17.20 (senno' ci sono outlier su
    if (r["vwapRatio"]>1.15 or r["vwapRatio"]<0.8 ): return False
    if (r["vwapRatioFTSE"]>1.04) or (r["vwapRatioFTSE"]<0.96): return False
    #if (r["pDaChiusura"]>1.5) or (r["pDaChiusura"]<0.5): return False
    #if (r["pLX.FSPX"]>1.2) or (r["pLX.FSPX"]<0.8): return False
    if (r["volatility"]>1.2): return False
    if (r["mreg120"]>5) or (r["mreg120"]<-5): return False
    if (r["mreg60"]>5) or (r["mreg60"]<-5): return False
    return True
    
def normalized_indicator(r,name):
    #r is a dict, name is the indicator name
    if name=='vwapRatio':
        return (r["vwapRatio"]-1)*50
    elif name == 'vwapRatioFTSE':
        return (r["vwapRatioFTSE"]-1)*50
    elif name == 'minuto':
        return (r["minuto"]-795)*1.0/265
    elif (name == 'mreg60' or name == 'mreg120'):
        return r[name]/5
    else:
        raise ValueError('Indicatore sconosciuto: '+name)
    return 0

def calcola_guadagni(Y_pred,Y_train,all_data,soglia,stampa=False):
    n_success=0
    n_trades=0
    guadagno_totale=0
    giorni_gia_tradati = []
    trades = []
    for i,v in enumerate(Y_pred):
        if (v>soglia):
            guadagno_totale=guadagno_totale+Y_train[i]*1000
            n_trades = n_trades+1
            if Y_train[i]>0: n_success = n_success+1
    if stampa: print("Guadagno totale: {0}".format(guadagno_totale))
    if stampa: print("N trades: {0}".format(n_trades))
    
    percentuale=0
    if (n_trades>0):
        percentuale = n_success*1.0/n_trades
    if stampa: print("Percentuale di successo: {0}".format(percentuale))
    return {"guadagno_totale":guadagno_totale,"percentuale":percentuale,"trades":trades}



def calcola_guadagni_trades(Y_pred,Y_train,all_data,sogliaLong=9999,sogliaShort=-9999,stampa=False):
    n_success=0
    n_trades=0
    guadagno_totale=0
    giorni_gia_tradati = []
    trades = []
    for i,v in enumerate(Y_pred):
        if (v>sogliaLong) or (v<sogliaShort):
            if (all_data[i]["tempo"].date() not in giorni_gia_tradati):
                #guadagno_totale=guadagno_totale+Y_train[i]*1000
                if v>sogliaLong:
                    guadagno_totale=guadagno_totale+all_data[i]["gainLong"+gain_field]
                else:
                    guadagno_totale=guadagno_totale+all_data[i]["gainShort"+gain_field]
                #print(Y_train[i]*1000)
                #n_trades = n_trades+1
                #if Y_train[i]>0: n_success = n_success+1
                trade = {} #all_data[i]
                if v<sogliaShort: trade["gain"] = all_data[i]["gainShort"+gain_field]
                else: trade["gain"] = all_data[i]["gainLong"+gain_field]
                trades.append(trade)
                giorni_gia_tradati.append(all_data[i]["tempo"].date())
    if stampa: print("Guadagno totale: {0}".format(guadagno_totale))
    if stampa: print("N trades: {0}".format(n_trades))
    
    #percentuale=0
    #if (n_trades>0):
    #    percentuale = n_success*1.0/n_trades
    #if stampa: print("Percentuale di successo: {0}".format(percentuale))
    return {"guadagno_totale":guadagno_totale,"trades":trades}



def plot_model(model,X_train,Y_train,dirname=""):
    #h = 0.01
    h = 0.001
    cm = plt.cm.RdBu
    cm = plt.cm.get_cmap('RdYlBu')
    cm_bright = ListedColormap(['#FF0000', '#0000FF'])
    #xx, yy = np.meshgrid(np.arange(-1, 1, h), np.arange(-1, 1, h))
    xx, yy = np.meshgrid(np.arange(min(X_train[:, 0]), max(X_train[:, 0]), h), np.arange(min(X_train[:, 1]), max(X_train[:, 1]), h))
    
    valori = [xx.ravel(), yy.ravel()] #, [0 for i in xx.ravel()]]
    
    for i in range(0,len(indicatori_usati)-2):
        #valori.append([0 for i in xx.ravel()])
        valori.append(np.zeros(len(xx.ravel())))
    
    #Z = model.predict(np.c_[xx.ravel(), yy.ravel(), [0 for i in xx.ravel()]]) #[0 for i in xx.ravel()]
    Z = model.predict(np.c_[valori].T) #[0 for i in xx.ravel()]
    #levels = np.arange(min(Z), max(Z), 0.005)
    #levels = np.linspace(min(Z), max(Z), 200)
    levels = np.linspace(0.00, 1.00, 200)
    #levels = [-1] + list(levels)
    #print(Z)
    
    Z = Z.reshape(xx.shape)
    print Z
    plt.contourf(xx, yy, Z, levels, cmap=cm, alpha=.8)
    plt.scatter(X_train[:, 0], X_train[:, 1], c=Y_train, cmap=cm_bright)
    print "Z MASSIMA = "+str(max(model.predict(X_train)))
    print "Z MINIMA = "+str(min(model.predict(X_train)))
    plt.savefig(dirname+'model.png');
    plt.show()
    
    
def gains_to_binary(Y):
    #binary classification:
    for i in range(0,len(Y)):
        if Y[i]>0:
            Y[i] = 1
        else:
            Y[i] = 0
    return Y

def save_model(model,dirname="",name="model"):
    json_string = model.to_json()
    open(dirname+'{0}.json'.format(name), 'w').write(json_string)
    model.save_weights(dirname+'{0}.h5'.format(name))
    
def load_model_from_file(dirname="",name="model"):
    model = model_from_json(open(dirname+'{0}.json'.format(name)).read())
    model.load_weights(dirname+'{0}.h5'.format(name))
    return model


def eval_model(model,X_orig,Y_orig,data_orig,titoli,sogliaLong=0.85,sogliaShort=0.05,sogliaUscitaLong=0.2,sogliaUscitaShort=0.7):
    trades_totali = []
    for titolo in titoli:
        X_titolo = []
        Y_titolo = []
        data_titolo = []
        for i,x in enumerate(X_orig):
            if (data_orig[i]["codalfa"]==titolo):
                X_titolo.append(x)
                Y_titolo.append(Y_orig[i])
                data_titolo.append(data_orig[i])
        
        X_titolo = np.array(X_titolo)
        
        Y_pred = model.predict(X_titolo)
        
        print titolo
        '''
        plt.plot([d["price"] for d in data_titolo])
        plt.plot(Y_pred)
        plt.show()
        #'''
        giorni_gia_tradati = []
        trades = []
        for i in range(0,len(Y_pred)):
            giorno = data_titolo[i]["tempo"].date()
            if (giorno not in giorni_gia_tradati):
                #print Y_pred[i]
                #LONG:
                if Y_pred[i]>sogliaLong:
                    '''
                    #entriamo long, controlla se in giornata c'era un segnale di uscita
                    for j in range(i+1,len(Y_pred)):
                        if data_titolo[j]["tempo"].date() == giorno:
                            if Y_pred[j]<sogliaUscitaLong:
                                #simula uscita
                                #print "uscita in giornata con soglia: ingresso {0}  , uscita: {1}".format(data_titolo[i]["tempo"],data_titolo[j]["tempo"])
                                #print data_titolo[j]
                                q=15000
                                print("prezzo ingresso: {0}   prezzo uscita: {1}".format(data_titolo[i]["price"],data_titolo[j]["price"]))
                                guadagno = float((data_titolo[j]["price"]-data_titolo[i]["price"])*q/(data_titolo[i]["price"]) - 10 )
                                #guadagno = float( (data_titolo[j]["pCaricoSell"] - data_titolo[i]["pCaricoBuy"])*q/(data_titolo[i]["pCaricoBuy"]) - 10 )
                                print("guadagno: {0}".format(guadagno))
                                break
                        else:
                            guadagno = data_titolo[i]["gainLong"+gain_field]
                            print("guadagno: {0}".format(guadagno))
                            break'''
                    guadagno = data_titolo[i]["gainLong"+gain_field]
                    trades.append({"gain":guadagno,"x":X_titolo[i]})
                    giorni_gia_tradati.append(giorno)
                    #print "entrato: {0}   \t gain: {1}".format(data_titolo[i]["tempo"],guadagno)
                    
                '''#SHORT'''
                if Y_pred[i]<sogliaShort:
                    '''
                    #entriamo long, controlla se in giornata c'era un segnale di uscita
                    for j in range(i+1,len(Y_pred)):
                        if data_titolo[j]["tempo"].date() == giorno:
                            if Y_pred[j]>sogliaUscitaShort:
                                #simula uscita
                                #print "uscita in giornata con soglia: ingresso {0}  , uscita: {1}".format(data_titolo[i]["tempo"],data_titolo[j]["tempo"])
                                #print data_titolo[j]
                                q=15000
                                #print("prezzo ingresso: {0}   prezzo uscita: {1}".format(data_titolo[i]["price"],data_titolo[j]["price"]))
                                
                                guadagno = -float((data_titolo[j]["price"]-data_titolo[i]["price"])*q/(data_titolo[i]["price"])) - 10
                                #guadagno = float( (data_titolo[i]["pCaricoSell"] - data_titolo[j]["pCaricoBuy"])*q/(data_titolo[i]["pCaricoSell"]) - 10 )
                                
                                break
                        else:
                            guadagno = data_titolo[i]["gainShort"+gain_field]
                            #print("guadagno: {0}".format(guadagno))
                            break'''
                    guadagno = data_titolo[i]["gainShort"+gain_field]
                    trades.append({"gain":guadagno,"x":X_titolo[i]})
                    giorni_gia_tradati.append(giorno)
                    #print "entrato: {0}   \t gain: {1}".format(data_titolo[i]["tempo"],guadagno)
        trades_totali.extend(trades)
    
    return {"guadagno_totale":sum([x["gain"] for x in trades_totali]),"trades":trades_totali}
        
def print_equity_line(g,name="equity",dirname=""):
    print g["trades"]
    guadagni = [x["gain"] for x in g["trades"]]
    #plt.subplot(1, 2, 2)
    plt.bar(np.arange(len(g["trades"])),guadagni)

    print "Guadagno medio: "+str(np.mean(guadagni))
    print "n trade: "+str(len(guadagni))
    print "Sharpe: "+str(np.mean(guadagni)/np.std(guadagni))
    
    trades = [0]+guadagni
    plt.plot([sum(trades[0:i+1]) for i in range(0,len(trades))]) #[x["tempo"] for x in g["trades"]]
    plt.savefig(dirname+name+'.png');
    plt.show()

import theano.tensor as T
def custom_objective(y_true, y_pred):
    #y = T.gt(y_pred,0.5) # ritorna un vettore  con 1 dove la condizione a>b e' verificata [0,0,1,0,0]
    #long:
    y1 = T.nnet.sigmoid((y_pred-0.8)*10)
    a1 = - T.sum(y_true*y1)
    #short:
    y2 = T.nnet.sigmoid((0.05-y_pred)*10) #dovrebbe darci 1 quando andiamo short
    a2 = a1 + T.sum((y_true+0.010)*y2)
    #a = T.abs_(y_true-y_pred)
    return a2
    #return T.sum(a)
    
def generator():
    global X_train,Y_train
    #8.5*30 = 255 dati al giorno
    #samples per batch: 150
    i = 0
    k = len(X_train)/256 #"batch size")
    while 1:
        i = (i+1)%len(X_train)
        x = []
        y = []
        for j in range(0,k):
            x.append( X_train[(i+j*260) % len(X_train)] )
            y.append( Y_train[(i+j*260) % len(X_train)] )
        #yield (X_train[i:j],Y_train[i:j])
        yield ( np.array(x) , np.array(y) )

    
gain_field = "99_99"
X_train = None
Y_train = None
indicatori_usati = []

def test_network(titoli = ['A2A','SRG','TRN','ENEL','EGPW'],indicatori=["mreg120","mreg60","vwapRatio","vwapRatioFTSE","minuto"],NNN = 40,dropout=0.2,name="20neuroniDropout05"):
    global indicatori_usati
    indicatori_usati = indicatori
    
    dirname = strftime("results/%d-%b-%Y_%H-%M-%S_", gmtime())+name+"/"
    os.makedirs(dirname)
    copyfile(__file__,dirname+"source.py")

    #'A2A','SRG','TRN','ENEL','EGPW','YNAP','MS','AGL','HER','CPR','FCA','LUX','MONC','SFER','TOD','BRE','AMP','REC']
    #titoli = ['CPR','FCA','LUX','MONC','SFER','TOD']
    #titoli = ['STS','IT','TRN','SRG','HER','ENEL','EGPW','ATL','G','A2A']
    #titoli = ['SRS','RACE','BP']#'PMI','BPE']#'SPM','CRG','BMPS','AST','CVAL']

    cnx = mysql.connector.connect(user='root', password='zxcvbnm',
                                  host='127.0.0.1',
                                  database='hedgefund')
    cursor = cnx.cursor(dictionary=True)
    cursor.execute("SELECT * FROM consolidataintraday where codalfa IN ('"+"','".join(titoli)+"') ORDER BY tempo")

    #print(cursor.column_names)

    X_tot = []
    Y_tot = []
    labels = []
    all_data = []

    for r in cursor:
        if (check_values(r)):
            X_tot.append([ normalized_indicator(r,n) for n in indicatori ])
            #X_tot.append([(r["vwapRatio"]-1)*50,(r["vwapRatioFTSE"]-1)*50,(r["minuto"]-795)*1.0/265]) # (r["volatility"]-0.6)#(r["volumeRatio"]-1)/20])#(r["pDaChiusura"]-1)*10]) #(r["bookImbalance30"]-1)*1.0/2 #(r["minuto"]-795)*1.0/265
            Y_tot.append(r["gainLong"+gain_field]/1000)
            #Y_tot.append(r["gainLong"+gain_field])
            #Y_train.append((r["vwapRatio"]-1)*50)
            labels.append(r["tempo"])
            all_data.append(r)
            #print(r)

    print "Dati mysql caricati."
    global X_train,Y_train                     
    X_train = np.array(X_tot)
    Y_train = np.array(Y_tot)

    print(X_train.shape[0])
    split = int(X_train.shape[0]*0.8)

    X_validate = X_train[split:]
    Y_validate = Y_train[split:]
    all_data_val = all_data[split:]

    X_train = X_train[0:split]
    Y_train = Y_train[0:split]
    all_data_train = all_data[0:split]

    #binary classification:
    Y_train = gains_to_binary(Y_train)
    Y_validate = gains_to_binary(Y_validate)

    #print(Y_train)
    print(X_train.shape)
    print(Y_train.shape)
    print(X_validate.shape)
    print(Y_validate.shape)

    print("min {0}  max {1}".format(min(Y_train),max(Y_train)))

    '''
    #regr = SGDRegressor(n_iter=100,eta0=0.0000001)
    regr = LinearRegression()
    regr.fit(X_train,Y_train)
    print(regr.coef_)
    print(regr.intercept_)
    exit()
    #'''

    train_accuracy_history = []
    train_loss_history = []
    val_accuracy_history = []
    val_loss_history = []


    class LossHistory(keras.callbacks.Callback):
        last_epoch_charted=0
        def on_epoch_end(self, epoch, logs={}):
            '''
            for layer in self.model.layers:
                print(layer.get_weights())'''
            #print logs
            #train_accuracy_history.append(logs["acc"]) 
            #train_loss_history.append(logs["loss"]) 
            val_accuracy_history.append(logs["val_acc"]) 
            val_loss_history.append(logs["val_loss"]) 
            if (epoch-self.last_epoch_charted>5000):
                plot_model(model,X_train,Y_train)
                self.last_epoch_charted = epoch


    model = Sequential()

    model.add(Dense(output_dim=NNN, input_dim=len(indicatori), init="glorot_uniform")) #, weights=[np.array([[1],[1]]),np.array([0])])) #,  W_regularizer=l1(0.05))) # ,  W_regularizer=l1(0.05)))
    model.add(Activation("sigmoid"))
    model.add(Dropout(dropout))
    model.add(Dense(output_dim=NNN, input_dim=NNN, init="glorot_uniform")) #,  W_regularizer=l1(0.05))) # , 
    model.add(Activation("sigmoid"))
    model.add(Dropout(dropout))
    model.add(Dense(output_dim=1, input_dim=NNN, init="glorot_uniform")) #,  W_regularizer=l1(0.05)))
    model.add(Activation("sigmoid"))

    # try using different optimizers and different optimizer configs
    s = {
    "learning_rate" : 0.0001,
    "batch_size" : 256, #256, #256 #512
    "n_epoch" : 100    
        }
    print(s)
    #optimizer = SGD(lr=0.005, momentum=0.5, decay=0.0005, nesterov=False) #lr=0.000001
    #optimizer = SGD(lr=0.005, momentum=0.5, decay=0.0004, nesterov=False) #lr=0.000001

    #optimizer = Adagrad(lr=s["learning_rate"], epsilon=1e-06) #lr=1
    #optimizer = Adam(lr=0.005)  #fa schifo
    optimizer = Adam(lr=0.005)  #fa schifo
    #optimizer = keras.optimizers.Adadelta(lr=0.1, rho=0.5 ) #, rho=0.95, epsilon=1e-06)

    #model.compile(loss='mean_absolute_error',optimizer=optimizer)
    model.compile(loss='binary_crossentropy',optimizer=optimizer,class_mode='binary')
    #model.compile(loss=custom_objective,optimizer=optimizer)#,class_mode='binary')

    print Y_train

    print("Train...")
    load_model = False
    if (not load_model):
        model.fit(X_train, Y_train,validation_data=(X_validate, Y_validate),show_accuracy=True , batch_size=s["batch_size"], nb_epoch=s["n_epoch"],callbacks=[LossHistory()]) #,show_accuracy=True
            #,keras.callbacks.EarlyStopping(monitor='val_loss', patience=10, verbose=1, mode='auto')])
        
        #fit_generator(generator, samples_per_epoch, nb_epoch, verbose=1, show_accuracy=False, callbacks=[], validation_data=None, nb_val_samples=None, class_weight=None, nb_worker=1, nb_val_worker=None)
        #model.fit_generator(generator(), 150, s["n_epoch"],validation_data=(X_validate, Y_validate),show_accuracy=True,callbacks=[LossHistory()])
        save_model(model,dirname=dirname)
    else:
        print("Loading model...")
        model = load_model_from_file(dirname=dirname)

    for layer in model.layers:
        print(layer.get_weights())

    plt.plot(val_accuracy_history)
    plt.plot(val_loss_history)
    plt.plot(train_accuracy_history)
    plt.plot(train_loss_history)
    plt.savefig(dirname+'loss.png');
    plt.show()

    Y_pred = model.predict(X_train)
    Y_pred_val = model.predict(X_validate)

    print(max(Y_pred))

    plot_model(model,X_train,Y_train,dirname=dirname)
    #exit()

    #'''
    soglie = []
    soglie_val = []
    ss = np.linspace(0.40,0.9,20)
    for soglia in ss:
        print "soglia: {0}".format(soglia)
        g = eval_model(model,X_train,Y_train,all_data_train,titoli,sogliaLong=soglia)
        soglie.append(g["guadagno_totale"])
        g = eval_model(model,X_validate,Y_validate,all_data_val,titoli,sogliaLong=soglia)
        soglie_val.append(g["guadagno_totale"])
        print "Guadagno totale: {0}".format(g["guadagno_totale"])

    plt.plot(ss,soglie)
    plt.plot(ss,soglie_val)

    sogliaLongMax = ss[soglie.index(max(soglie))]
    
    #SHORT
    soglie = []
    soglie_val = []
    ss = np.linspace(0.00,0.4,20)
    for soglia in ss:
        print "soglia: {0}".format(soglia)
        g = eval_model(model,X_train,Y_train,all_data_train,titoli,sogliaShort=soglia)
        soglie.append(g["guadagno_totale"])
        g = eval_model(model,X_validate,Y_validate,all_data_val,titoli,sogliaShort=soglia)
        soglie_val.append(g["guadagno_totale"])
        print "Guadagno totale: {0}".format(g["guadagno_totale"])

    plt.plot(ss,soglie)
    plt.plot(ss,soglie_val)
    plt.savefig(dirname+'soglie.png');
    plt.show()
    #'''
    
    sogliaShortMax = ss[soglie.index(max(soglie))]

    sogliaLongMax = max(sogliaLongMax,0.6)
    sogliaShortMax = min(sogliaLongMax,0.2)
    
    print "sogliaLong: "+ sogliaLongMax
    print "sogliaShort: "+ sogliaShortMax
    
    g = eval_model(model,X_train,Y_train,all_data_train,titoli, sogliaLong = sogliaLongMax, sogliaShort = sogliaShortMax)

    #for e in g["trades"]:
    #    print e

    #x = [e["x"] for e in g["trades"]]
    #x = np.array(x)
    #print x
    #plt.scatter(x[:, 0], x[:, 1])
    #plt.show()

    #plot_model(model,x,model.predict(x))

    print_equity_line(g,dirname=dirname,name="training")

    g = eval_model(model,X_validate,Y_validate,all_data_val,titoli, sogliaLong = sogliaLongMax, sogliaShort = sogliaShortMax)
    print_equity_line(g,dirname=dirname,name="testing")

    exit()

    
    
test_network() #titoli=['A2A','SRG','TRN','ENEL','EGPW','YNAP','MS','AGL','HER','CPR','FCA','LUX','MONC','SFER','TOD','BRE','AMP','REC'])

































soglie = []
soglie_short = []
soglie_val = []
soglie_short_val = []

for soglia in np.linspace(min(Y_pred),max(Y_pred),100):
    #print(soglia)
    #g = calcola_guadagni(Y_pred_val,Y_validate,all_data_val,soglia)
    g = calcola_guadagni_trades(Y_pred,Y_train,all_data_train,sogliaLong=soglia)
    soglie.append(g["guadagno_totale"])
    g = calcola_guadagni_trades(Y_pred_val,Y_validate,all_data_val,sogliaLong=soglia)
    soglie_val.append(g["guadagno_totale"])
    
    g_short = calcola_guadagni_trades(Y_pred,Y_train,all_data_train,sogliaShort=soglia)    
    soglie_short.append(g_short["guadagno_totale"])
    
    g_short = calcola_guadagni_trades(Y_pred_val,Y_validate,all_data_val,sogliaShort=soglia)
    soglie_short_val.append(g_short["guadagno_totale"])
    #print(g["guadagno_totale"])

soglia_ottimale = np.linspace(min(Y_pred),max(Y_pred),100)[soglie.index(max(soglie))]
print "soglia ottimale Long: "+str(soglia_ottimale)
soglia_ottimale_short = np.linspace(min(Y_pred),max(Y_pred),100)[soglie_short.index(max(soglie_short))]
print "soglia ottimale Short: "+str(soglia_ottimale_short)

plt.subplot(1, 2, 1)
plt.plot(np.linspace(min(Y_pred),max(Y_pred),100),soglie,label='soglie')
plt.plot(np.linspace(min(Y_pred),max(Y_pred),100),soglie_short,label='soglie_short')
plt.plot(np.linspace(min(Y_pred),max(Y_pred),100),soglie_val,label='soglie_val')
plt.plot(np.linspace(min(Y_pred),max(Y_pred),100),soglie_short_val,label='soglie_short_val')
legend = plt.legend(loc='lower center', framealpha=0)
#plt.show()


X_totale = np.array(X_tot)
Y_totale = np.array(Y_tot)
Y_pred_tot = model.predict(X_totale)

g = calcola_guadagni_trades(Y_pred_tot,Y_tot,all_data,sogliaLong=soglia_ottimale,sogliaShort=soglia_ottimale_short)
#g = calcola_guadagni_trades(Y_pred,Y_train,all_data_train,soglia_ottimale)
guadagni = [x["gain"] for x in g["trades"]]
plt.subplot(1, 2, 2)
plt.bar(np.arange(len(g["trades"])),guadagni)

trades = [0]+guadagni
#for t in trades:
#    print(t)
#print("sommati:")

#for i in range(0,len(trades)):
#    print(sum(trades[0:i+1]))
plt.plot([sum(trades[0:i+1]) for i in range(0,len(trades))]) #[x["tempo"] for x in g["trades"]]
plt.show()

print "Guadagno medio: "+str(np.mean(guadagni))
print "n trade: "+str(len(guadagni))
print "Sharpe: "+str(np.mean(guadagni)/np.std(guadagni))


#plt.plot([x[0] for x in X_train])
'''
plt.plot(Y_train)
plt.plot(Y_pred)
plt.show()
''' 
cnx.close()
exit()





























import numpy as np
np.random.seed(1337)  # for reproducibility

from keras.preprocessing import sequence
from keras.utils import np_utils
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation
from keras.layers.embeddings import Embedding
from keras.layers.recurrent import LSTM
from keras.datasets import imdb

max_features = 20000
maxlen = 100  # cut texts after this number of words (among top max_features most common words)
batch_size = 32

print('Loading data...')
(X_train, y_train), (X_test, y_test) = imdb.load_data(nb_words=max_features,
                                                      test_split=0.2)
print(len(X_train), 'train sequences')
print(len(X_test), 'test sequences')

print("Pad sequences (samples x time)")
X_train = sequence.pad_sequences(X_train, maxlen=maxlen)
X_test = sequence.pad_sequences(X_test, maxlen=maxlen)
print('X_train shape:', X_train.shape)
print('X_test shape:', X_test.shape)

print('Build model...')
model = Sequential()
model.add(Embedding(max_features, 128, input_length=maxlen))
model.add(LSTM(128))  # try using a GRU instead, for fun
model.add(Dropout(0.5))
model.add(Dense(1))
model.add(Activation('sigmoid'))

# try using different optimizers and different optimizer configs
model.compile(loss='binary_crossentropy',
              optimizer='adam',
              class_mode="binary")

print("Train...")
model.fit(X_train, y_train, batch_size=batch_size, nb_epoch=3,
          validation_data=(X_test, y_test), show_accuracy=True)
score, acc = model.evaluate(X_test, y_test,
                            batch_size=batch_size,
                            show_accuracy=True)
print('Test score:', score)
print('Test accuracy:', acc)