package testSocket;

import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class StockListener implements Observer {
	String codAlfa;
	int finestraTemporale;
	Observable player;
	List<Metodo> metodi;
	
	//variabili calcolate in tempo reale
	float vwap=0f,deltaVwap=0f;
	Deque<Float> codaVwap = new LinkedList<Float>();
	int volumeTotale=0;
	long timestampUltimoCheck = 0, timestampUltimaQ = 0;
	float ipercomprato = 0f;
	float vwapRatio = 1.0f;
	int DeltaT = 6*60*1000; //6 minuti;
	int lastSpread=1;
	int ImpactBuy=1;
	int ImpactSell=1;
	Date timestampIndicatori;
	int totalTurnover = 0;
	int turnover = 0;
	int nTradeGrossi = 0;
	int averageturnover = 0;
	int numberoftrades = 0;
	int marketorderdelta = 0;
	float marketbuypercentage = 0.5f;
	double standardDeviation = 0f;
	float bidAskSpread = 1f;
	float bookImpact = 1f;
	int ora = 0, minuto = 0;
	Tick tick;
	
	StockDatabaseMYSQL db = new StockDatabaseMYSQL();
	
	
	
	
	public class trade implements Serializable{
		long timestampLong;
		  Date timestamp;
		  float price;
		  int volume;
		  int val;
		  float vwap;
		  int spread;
		  int impact;
		  public trade(long tL, Date t, float p, int vol, int val, float vw, int spr, int imp) {this.timestampLong=tL;this.timestamp=t;this.price=p;this.volume=vol;this.val=val;this.vwap=vw;this.spread=spr;this.impact=imp;}
		};
	ArrayList<trade> trades = new ArrayList<trade>();

	public StockListener(String codalfa,
			int finestraTemporale, Observable player, List<Metodo> metodi) {
		super();
		this.codAlfa = codalfa;
		this.finestraTemporale = finestraTemporale;
		this.player = player;
		this.metodi = metodi;
		player.addObserver(this);
	}


	//variabili per l'accorpamento dei tick
	
	Float lastBid = 0f;
	Float lastAsk = 0f;
	Boolean lastWasBuy = true, lastWasNeutral=false;
	Long lastTickMillis = Long.MAX_VALUE;
	String lastPriceString = "0";
	Integer cumulatedVolume = 0;
	Boolean marketOrder = false;
	Float lastPrice = 0.0f; //prezzo del tick precedente
	
	public void processaTick(Tick tick) {
		if (tick.volume<=0) return;
		
		this.tick = tick;
		float price = Float.parseFloat(tick.price);	
		
		db.insertTick(tick);
		
		Calendar now = Calendar.getInstance();
		now.setTime(tick.timestamp);
		
		ora = now.get(Calendar.HOUR_OF_DAY);
		minuto = now.get(Calendar.MINUTE);

		
		//aggiorniamo il vwap
		vwap = (vwap*volumeTotale + ((float)price*tick.volume))/(volumeTotale+tick.volume);
		volumeTotale+=tick.volume;
		codaVwap.addFirst(vwap);
		while (codaVwap.size()>60) codaVwap.removeLast(); //delta vwap a un minuto circa
		deltaVwap = vwap-codaVwap.getLast();
		vwapRatio = price/vwap;
		

		totalTurnover+=(float)price*tick.volume;
		

		System.out.println("lastAsk: "+lastAsk);
		System.out.println("lastBid: "+lastBid);
		
		if (lastAsk!=0.0 && lastBid!=0.0) {
			System.out.println(GestioneOrdini.tickSizeFromPriceFloat(lastAsk));
			System.out.println((lastAsk-lastBid));
			lastSpread=Math.round(((lastAsk-lastBid)/GestioneOrdini.tickSizeFromPriceFloat(lastAsk)));
			System.out.println(lastSpread);
			ImpactBuy=Math.abs(Math.round((price-lastAsk)/GestioneOrdini.tickSizeFromPriceFloat(lastAsk)))+1;
			ImpactSell=Math.abs(Math.round((lastBid-price)/GestioneOrdini.tickSizeFromPriceFloat(lastAsk)))+1;
			System.out.println("price : "+price);
			System.out.println("Math.round: "+Math.round((price-lastAsk)/GestioneOrdini.tickSizeFromPriceFloat(lastAsk)));
			System.out.println("Math.Abs: "+Math.abs(Math.round((price-lastAsk)/GestioneOrdini.tickSizeFromPriceFloat(lastAsk))));
			System.out.println("ImpactBuy: "+ImpactBuy);
			System.out.println("ImpactSell: "+ImpactSell);
		}
		else {
			lastSpread=1;
			ImpactBuy=1;
			ImpactSell=1;}
		

		trades.add(new trade(tick.timestamp.getTime(),tick.timestamp,price,tick.volume,(int) (tick.buy ? (float)price*tick.volume : -(float)price*tick.volume),vwap,lastSpread,(int) (tick.buy ? ImpactBuy : ImpactSell)));

		
		for (trade t : trades)
		    System.out.println(t.timestamp+" prezzo: "+t.price+" volume: "+t.volume+" controvalore: "+t.val+"€ vwap: "+t.vwap+"spread: "+t.spread+" Impatto: "+t.impact);
		
		try {

			File file = new File(codAlfa+".tmp");
			
		      if (file.createNewFile()){
			        System.out.println("File is created!");
			      }else{
			        System.out.println("File already exists.");
			      }

				FileOutputStream fop = new FileOutputStream(file);
				ObjectOutputStream oos = new ObjectOutputStream(fop);
				oos.writeObject(trades);
				oos.close();
		      
		    	} catch (IOException e) {
			      e.printStackTrace();
			}

			
		
		
		calcolaIndicatori(tick.timestamp);
}

		
		



	public void calcolaIndicatori (Date timestamp) {
//		System.out.println("timestamp calcolaIndicatori: "+timestamp);
		timestampIndicatori=timestamp;
//		System.out.println("timestampIndicatori è: "+timestampIndicatori);
		long tempoIniziale = tick.timestamp.getTime() - 1000 * 60 * finestraTemporale; //30 minuti
		int qTotaleTrades = 0, qCompratoTrades = 0;
		numberoftrades = 0;
		double sommaScarti=0;
		int sommaSpread=0;
		int sommaBookImpact=0;
		for (int k=0;k<trades.size();k++) {
			sommaScarti+=Math.pow(trades.get(k).price-trades.get(k).vwap, 2);
			sommaSpread+=trades.get(k).spread;
			sommaBookImpact+=trades.get(k).impact*Math.abs(trades.get(k).val);
			if ((trades.get(k).timestampLong >= tempoIniziale ) && (eNegoziazioneContinua(trades.get(k).timestamp))){
				numberoftrades++;
				int val = trades.get(k).val;
				if (val>0) {
					qTotaleTrades+=val;
					qCompratoTrades+=val;
				} else {
					qTotaleTrades+=-val;
				}
			}
		}
		
		
		turnover = qTotaleTrades;
		marketorderdelta = qCompratoTrades-(qTotaleTrades-qCompratoTrades);	
		try {
		averageturnover = (int) qTotaleTrades/numberoftrades;
		marketbuypercentage = (float)qCompratoTrades/qTotaleTrades;}
		catch (Exception e) {
			System.out.println("eccezione!");
			System.out.println("numberoftrades: "+numberoftrades);
			System.out.println("qTotaleTrades: "+qTotaleTrades);
			e.printStackTrace();}
		try {
			standardDeviation = (trades.size()==1 ? Math.sqrt(sommaScarti/(trades.size())) : Math.sqrt(sommaScarti/(trades.size()-1)));
			bidAskSpread = (float) sommaSpread/trades.size();
			bookImpact = (float) sommaBookImpact/totalTurnover;}
		catch (Exception exc){
			System.out.println("eccezione!");
			System.out.println("trades.size(): "+trades.size());
			exc.printStackTrace();}
		
		Indicatori I = new Indicatori(codAlfa,timestampIndicatori,totalTurnover,turnover,numberoftrades,averageturnover,marketorderdelta,marketbuypercentage,standardDeviation,bidAskSpread,bookImpact);
//	StockListener s = new StockListener(codAlfa,30,player, new ArrayList<Metodo>() );
//	StockListener st = gui.titoliInAscolto.get(indexOf(s));
//		
		db.inserisciIndicatori(I);
//	
	
	}
	@Override
	public void update(Observable arg0, Object arg1) {	
		String s = (String)arg1;
		if(s.compareTo("CLEAR")==0) {
			//this.clear();
			return;
		}
		if(s.compareTo("CARICAMENTO_TERMINATO")==0) return;
		Boolean spezza = false;
		String parts[] = s.split(";");
		if (parts[2].compareTo(codAlfa)==0 ) {
			s="";
			if (parts[1].compareTo("PRICE")==0) {
					//stampa se e' passato piu' di 3 millisecondi, o se 'e passato da cquisto a vendita, altrimenti accorpora
					Long millis = Long.parseLong(parts[0]);
					if (Float.parseFloat(parts[4])>lastBid) {
						if (Float.parseFloat(parts[4])>=lastAsk) {
							//questo e' un acquisto ne siamo sicuri
							if (!lastWasBuy) spezza=true;
						}
					} else {
						//e' una vendita
						if(lastWasBuy) spezza=true;
					}
					if (millis-3>lastTickMillis || spezza) {
						if (lastWasNeutral) {
							//il tick e' avvenuto all'interno dello spread quindi non so se considerarlo vendita o acquisto
							//lo considero vendita se il prezzo e' inferiore al prezzo precedente
							if (Float.parseFloat(parts[4])<lastPrice) lastWasBuy=false;  else  lastWasBuy=true;
						}
						//System.out.println((lastWasBuy? "ACQUISTO" : "VENDITA") + "\t" + parts[4]);
//						Tick t = new Tick(parts[2], new Date(lastTickMillis), lastPriceString, cumulatedVolume, lastWasBuy,marketOrder);
						//Globals.db.insertTick(t);
//						if (eNegoziazioneContinua(t.timestamp)) processaTick(t);
						
						cumulatedVolume = 0;
						spezza = false;
						marketOrder = false;
						//System.out.println("AZZERO");
					}
					lastWasNeutral = false;
					if (Float.parseFloat(parts[4])>lastBid) {
						if (Float.parseFloat(parts[4])<lastAsk) {
							lastWasNeutral = true;
						}
						lastWasBuy=true;
					} else {
						lastWasBuy=false;
					}
					
					if (cumulatedVolume>0&&lastPrice!=Double.parseDouble(parts[4])) marketOrder=true;
					cumulatedVolume += Integer.parseInt(parts[5]);
					//System.out.println("Accumulo: "+Integer.parseInt(parts[5]));
					
					lastTickMillis = millis;
					lastPrice = Float.parseFloat(parts[4]);
					lastPriceString = parts[4];
					
					Tick t = new Tick(parts[2], new Date(lastTickMillis), lastPriceString, cumulatedVolume, lastWasBuy,marketOrder);
					if (eNegoziazioneContinua(t.timestamp)) processaTick(t);
					
			} else if (parts[1].compareTo("BOOK_5")==0) {
				lastBid = Float.parseFloat(parts[6]); //prezzo acquisto livello 1
				lastAsk = Float.parseFloat(parts[21]);
			}
		}
	}
	
	public static boolean eNegoziazioneContinua(Date timestamp) {
		Calendar now = Calendar.getInstance();
		now.setTime(timestamp);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minutes = now.get(Calendar.MINUTE);
//		int seconds = now.get(Calendar.SECOND);
		
		if (hour<9) {
			return false;
		} else if (hour==9) {
			if (minutes>=1) return true;
			else return false;
		} else if (hour>9 && hour<17) {
			return true;
		} else if (hour==17) {
			if (minutes<30) return true;
			else return false;
		} 
		return false;
	}
	
}
