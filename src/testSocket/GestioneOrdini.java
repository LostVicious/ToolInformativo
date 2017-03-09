package testSocket;

import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

//singleton class
public class GestioneOrdini implements Runnable {
	ArrayList<Ordine> ordini;
	//ArrayList<Posizione> posizioni;
	Map<String, Posizione> posizioni;
	ArrayList<String> comandiDaInviare;
	boolean stop = false;
	int lastID = 0;
	float perdita_massima = 300;
	long lastGetPosition = 0;
	
	private static GestioneOrdini inst = null;
	
	protected GestioneOrdini() {
		this.ordini = new ArrayList<Ordine>();
		this.posizioni = new HashMap<String, Posizione>();
		this.comandiDaInviare = new ArrayList<String>();
		(new Thread(this)).start();
	}
	
	public static GestioneOrdini getInstance() {
	      if(inst == null) {
	         inst = new GestioneOrdini();
	      }
	      return inst;
    }
	
	
	public void revocaOrdine(String codiceOrdine) {
		inst.comandiDaInviare.add("REVORD "+codiceOrdine);
		/*Iterator it = posizioni.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Posizione> pair = (Map.Entry<String, Posizione>)it.next();
	        Posizione p = pair.getValue();
	        inst.comandiDaInviare.add("REVORD "+codiceOrdine);
	        //p.segnaAnnullato(codiceOrdine);
	    }
		
		for (Iterator<Ordine> iterator = ordini.iterator(); iterator.hasNext();) {
			Ordine o = iterator.next();
			if (o.codiceOrdine.compareTo(codiceOrdine)==0) {
				inst.comandiDaInviare.add("REVORD "+codiceOrdine);
				iterator.remove();
			}
		}*/
	}
	
	public void revocaAllTitolo(String codalfa) {
		//System.out.println("ORDINI: "+ordini);
		inst.comandiDaInviare.add("REVALL "+codalfa);
		
		Posizione p = posizioni.get(codalfa);
		if (p!= null) {
			if (p.ordini[Posizione.STOPLOSS_ATTIVO] != null)
				revocaOrdine(p.ordini[Posizione.STOPLOSS_ATTIVO].codiceOrdine);
			if (p.ordini[Posizione.STOPPROFIT_ATTIVO] != null)
				revocaOrdine(p.ordini[Posizione.STOPPROFIT_ATTIVO].codiceOrdine);
		}
	}
	
	
	public void chiudiPosizioniTitolo(String codalfa) {
		revocaAllTitolo(codalfa);
		
		Posizione p = posizioni.get(codalfa);
		if (p!= null && p.Q!=0) {
			Ordine o = new Ordine(Ordine.AL_MEGLIO,generaCodiceOrdine(codalfa)+"CHIUSURA", codalfa, -p.Q, new BigDecimal("0.0"));
			comandiDaInviare.add(o.comando);
			//operazioneAlMeglio(new Ordine(generaCodiceOrdine(codalfa)+"CHIUSURA", codalfa, -posizione, new BigDecimal("0.0")));
			posizioni.remove(codalfa);
		}
		/*
		int posizione = posizioni.get(codalfa);
		System.out.println("CHIUDO POSIZIONE "+codalfa+" "+posizione);
		revocaAllTitolo(codalfa);
		if (posizione!=0) {
			operazioneAlMeglio(new Ordine(generaCodiceOrdine(codalfa)+"CHIUSURA", codalfa, -posizione, new BigDecimal("0.0")));
			posizioni.put(codalfa, 0);
		}*/
	}
	
	public void eliminaOrdine(String codiceOrdine) {
		for (int i=0;i<ordini.size();i++) {
			if (ordini.get(i).codiceOrdine.compareTo(codiceOrdine)==0) {
				ordini.remove(i);
			}
		}
	}
	
	public void confermaOrdine(String codiceOrdine) {
		Iterator it = posizioni.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Posizione> pair = (Map.Entry<String, Posizione>)it.next();
	        Posizione p = pair.getValue();
	        p.confermaOrdine(codiceOrdine);
	    }
	}
	
	public void segnaEseguito(String codiceOrdine) {
		Iterator it = posizioni.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Posizione> pair = (Map.Entry<String, Posizione>)it.next();
	        pair.getValue().segnaEseguito(codiceOrdine);
	    }
	}
	
	public void segnaAnnullato(String codiceOrdine) {
		Iterator it = posizioni.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry<String, Posizione> pair = (Map.Entry<String, Posizione>)it.next();
	        pair.getValue().segnaAnnullato(codiceOrdine);
	    }
	}
	
	public void chiudiTutteLePosizioni() {
		
		for (String codalfa : posizioni.keySet())
		{
			chiudiPosizioniTitolo(codalfa);
		}
		
		//inst.stop=true;
	}
	
	public void inviaConfermaOrdine(String codiceOrdine) {
		inst.comandiDaInviare.add("CONFORD "+codiceOrdine);
		//CONFORD ORD001
	}
	
	public void modificaOrdine(Ordine o) {
		
	}
	
	public void apriPosizione(String codalfa, int Q, BigDecimal prezzo, int stopProfit, int stopLoss) {
		if (Q==0) return;
		
		prezzo = prezzo.setScale(tickSizeFromPrice(prezzo).scale(), BigDecimal.ROUND_DOWN);		
		//decido la quantita
		//Q = perdita_max/(stopLoss*tickSize)
		int QQ = Math.round( perdita_massima/(tickSizeFromPrice(prezzo).multiply(new BigDecimal(stopLoss)).floatValue()) );
		//QQ=1000;
		if (Q>=0) Q = QQ;
		else Q = -QQ;
		
		BigDecimal prezzoStopProfit, prezzoStopLoss;
		if (Q>0) {
			prezzoStopProfit = prezzo.add( tickSizeFromPrice(prezzo).multiply(new BigDecimal(stopProfit)));
			prezzoStopLoss = prezzo.subtract( tickSizeFromPrice(prezzo).multiply(new BigDecimal(stopLoss)));
		} else {
			prezzoStopProfit = prezzo.subtract( tickSizeFromPrice(prezzo).multiply(new BigDecimal(stopProfit)));
			prezzoStopLoss = prezzo.add( tickSizeFromPrice(prezzo).multiply(new BigDecimal(stopLoss)));
		}
		
		prezzoStopLoss = prezzoStopLoss.setScale(tickSizeFromPrice(prezzo).scale(), BigDecimal.ROUND_DOWN);
		prezzoStopProfit = prezzoStopProfit.setScale(tickSizeFromPrice(prezzo).scale(), BigDecimal.ROUND_DOWN);

		popup("<html><pre>     Volevo TRADARE: "+codalfa+ " "+ "  Q="+Q + "    prezzoSegnaleIngresso:"+prezzo + "  stopProfit: "+prezzoStopProfit + "   stopLoss:"+prezzoStopLoss+ "       </pre></html>");

		//mettiamo un limite di un trade in ogni momento
		if (posizioni.size()>=2) {
			popup("non ho tradato, 2 posizioni gia' aperte");
			return;
		}
		
		
		String codiceOrdine = generaCodiceOrdine(codalfa);
		
		Ordine ordineApertura = null, stopLossDesiderato = null, stopProfitDesiderato = null;
		
		
		if (Q>=0) {
			ordineApertura = new Ordine(Ordine.AL_MEGLIO,codiceOrdine, codalfa, Q, prezzo);
		} else {
			ordineApertura = new Ordine(Ordine.LIMITE,codiceOrdine, codalfa, Q, prezzo.subtract( tickSizeFromPrice(prezzo).multiply(new BigDecimal(10))));
		}
		
		stopProfitDesiderato = new Ordine(Ordine.LIMITE, codiceOrdine+"PROFIT", codalfa, -Q, prezzoStopProfit);
		stopLossDesiderato = new Ordine(Ordine.STOP, codiceOrdine+"LOSS", codalfa, -Q, prezzoStopLoss );
		
		Posizione p = new Posizione(codalfa, Q);
		p.ordini[Posizione.ORDINE_APERTURA] = ordineApertura;
		p.ordini[Posizione.STOPLOSS_DESIDERATO] = stopLossDesiderato;
		p.ordini[Posizione.STOPPROFIT_DESIDERATO] = stopProfitDesiderato;
		
		System.out.println("ORDINE APERTURA: " + p.ordini[Posizione.ORDINE_APERTURA].comando);
		
		posizioni.put(codalfa, p);
	}
	
	public void gestisciConferma(String messaggio) {
		String parts[] = messaggio.split(";");
		if (parts.length<4) return;
		
		String comando = parts[0];
		String codalfa = parts[1];
		
		//e' una risposta a getPosition
		if (comando.compareTo("STOCK")==0) {
			//se non e' zero, quindi c'e' un eseguito parziale, dobbiamo aggiornare lo stop loss
			int posizione = Integer.parseInt(parts[3]); //parte in portafoglio piu' quella in negoziazione (perche' ci mette un attimo a esegiure)
			if (posizione!=0) {
				//se necessario aggiorno lo stopLoss
				Posizione p = posizioni.get(codalfa);
				if (p==null) {
					popup("ERRORE, POSIZIONE SU "+codalfa+" NON APERTA DA NOI");
					
				} else if (p.Q != posizione) {
					//aggiorno lo stopLoss
					Ordine oldStop = p.ordini[Posizione.STOPLOSS_ATTIVO];
					revocaOrdine(p.ordini[Posizione.STOPLOSS_ATTIVO].codiceOrdine);
					p.ordini[Posizione.STOPLOSS_DESIDERATO] = new Ordine(oldStop.tipoOrdine, oldStop.codiceOrdine+(++lastID), codalfa, -posizione, oldStop.prezzo);
					p.Q = posizione;
				}
				
				//aggiorniamo la posizione
				///////posizioni.put(parts[1], posizione);
				
				//aggiorniamo lo stop loss
				//System.out.println("ORDINI: "+inst.ordini);
				int nOrdini = ordini.size(); boolean nuovoStopMesso = false;
				for (int i=0;i<nOrdini;i++) {
					System.out.println("i="+i);
					Ordine o = ordini.get(i);
					if (o.codalfa.compareTo(codalfa)==0) {
						if (o.codiceOrdine.contains("LOSS")) {
							if (o.Q != posizione) {
								//revoco l'ordine e ne immetto un altro a quantita minore
								System.out.println("REVOCO ORDINE "+o.codiceOrdine);
								revocaOrdine(o.codiceOrdine);
								if (!nuovoStopMesso) {
									Ordine nuovoStop = o;
									nuovoStop.Q = -posizione;
									nuovoStop.codiceOrdine = o.codiceOrdine + (++lastID);
									System.out.println("INVIO ORDINE " +nuovoStop.codiceOrdine);
									//---------operazioneStop(nuovoStop);
									nuovoStopMesso = true;
								}
							}
						}
					}
				}
			} else {
				//se effettivamente c'era una posizione aperta su quel titolo la chiudo
				if (posizioni.get(codalfa) != null && posizioni.get(codalfa).ordini[Posizione.ORDINE_APERTURA].eseguito) {
					System.out.println("TRADE CHIUSOOOOOOOOOOOOOOOOOOOOO " + codalfa);
					System.out.println("REVOCO TUTTO SUL TITOLO "+codalfa);
					revocaAllTitolo(codalfa);
					posizioni.remove(codalfa);
				}
				//posizione =0 quindi chiusa, revoco tutti gli stop
				/*int nOrdini = ordini.size();
				for (int i=0;i<nOrdini;i++) {
					System.out.println("i="+i);
					Ordine o = ordini.get(i);
					if (o.codalfa.compareTo(parts[1])==0) {
						revocaOrdine(o.codiceOrdine);
					}
				}*/
				//////////posizioni.put(parts[1], 0);
			}
		}
		
		String codiceOrdine = parts[2];
		if (comando.compareTo("TRADOK")==0) {
			if (parts[3].compareTo("3000")==0) {
				//richiesta ricevuta correttamente
				confermaOrdine(codiceOrdine);
			}
			if (parts[3].compareTo("3001")==0) {
				System.out.println("ESEGUITO ORDINE "+codiceOrdine);
				segnaEseguito(codiceOrdine);
				/*
				//se e' stato eseguito lo stopProfit potrebbe essere parziale e dovremmo aggiornare lo stop loss
				if (codiceOrdine.contains("PROFIT")) {
					
					comandiDaInviare.add("GETPOSITION "+parts[1]);
					
				} else if (codiceOrdine.contains("LOSS")) {
					String codStopProfit = codiceOrdine.replaceAll("LOSS", "PROFIT");
					revocaOrdine(codStopProfit);
					/////////posizioni.put(parts[1], 0);
				} else {
					//ne' stop profit ne' stop loss aggiorno la posizione
					System.out.println("AGGIORNOOOOOO LA POSIZIONE");
					///////int vecchiaQ = (posizioni.get(parts[1]) != null) ? posizioni.get(parts[1]) : 0; 
					//posizioni.put(parts[1], vecchiaQ + Integer.parseInt(parts[5]));
					if (parts[4].compareTo("VENAZ")==0) {
						////posizioni.put(parts[1], -Integer.parseInt(parts[5]));
					} else {
						////////posizioni.put(parts[1], Integer.parseInt(parts[5]));
					}
				}*/
			} else if (parts[3].compareTo("3002")==0) { 
				segnaAnnullato(codiceOrdine);
				//popup("ORDINE ANNULLATO: "+messaggio);
			}
		} else if (comando.compareTo("TRADCONFIRM")==0) {
			//TODO ci fidiamo cecamente senza controllare l'ordine che stiamo confermando
			inviaConfermaOrdine(codiceOrdine);
		} else if (comando.compareTo("TRADERR")==0) {
			//TODO gestisci l'errore manualmente
			popup("ERRORE!!!!  "+messaggio);
			System.out.println("ERRORREEEEEEEEEEE IMMISSIONE ORDINEEEEEEEEEEEEE");
			System.out.println(messaggio);
		}
		
	}
	
	public static void popup(String messaggio) {
		JFrame popup = new JFrame("ERRORE");
		popup.add(new JLabel(messaggio));
		popup.pack();
		popup.setLocationRelativeTo(null);
		popup.setVisible(true);
		Toolkit.getDefaultToolkit().beep();
	}
	
	
	
	public String generaCodiceOrdine(String codalfa) {
		lastID++;
		return (codalfa + System.currentTimeMillis());
	}

	public static BigDecimal tickSizeFromPrice(BigDecimal price) {
		//if (true) return new BigDecimal("0.00001");
		if (price.compareTo(new BigDecimal("0.5"))<=0) return new BigDecimal("0.0001");
		if (price.compareTo(new BigDecimal("1.0"))<=0) return new BigDecimal("0.0005");
		if (price.compareTo(new BigDecimal("2.0"))<=0) return new BigDecimal("0.001");
		if (price.compareTo(new BigDecimal("5.0"))<=0) return new BigDecimal("0.002");
		if (price.compareTo(new BigDecimal("10.0"))<=0) return new BigDecimal("0.005");
		if (price.compareTo(new BigDecimal("50.0"))<=0) return new BigDecimal("0.01");
		if (price.compareTo(new BigDecimal("100.0"))<=0) return new BigDecimal("0.05");
		return new BigDecimal(0);
	}
	
	public static float tickSizeFromPriceFloat(float price) {
		if (price <= 0.5f) return 0.0001f;
		if (price <= 1.0f) return 0.0005f;
		if (price <= 2.0f) return 0.0010f;
		if (price <= 5.0f) return 0.0020f;
		if (price <= 10.0f) return 0.0050f;
		if (price <= 50.0f) return 0.0100f;
		if (price <= 100.0f) return 0.0500f;
		
		return 0.0f;
	}
	
	@Override
	public void run() {
		if (true) return;
		Boolean bloccato = false;
		try {
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			String logfile = dateFormat.format(new Date(System.currentTimeMillis()))+"_logOrdini.txt";
			DataSaver LOG = new DataSaver(logfile);
			Socket socketOrdini = new Socket("localhost", 10002);
			socketOrdini.setSoTimeout(500); //la lettura readline non blocca mai per piu' di 500ms
			DataOutputStream outToServer = new DataOutputStream(socketOrdini.getOutputStream());   
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socketOrdini.getInputStream()));
			while (!this.stop) {
				//System.out.println("asdasd");
				try {
					//Thread.sleep(1000);
					String sentence = "";
					try {
						sentence = inFromServer.readLine();
					} catch (SocketTimeoutException e) {
						sentence = null;
					}
					if (sentence!=null) {
						System.out.println("<  "+sentence);
						LOG.writeLine("<  "+sentence);
						gestisciConferma(sentence);
					}
					
					
					Iterator it = posizioni.entrySet().iterator();
				    while (it.hasNext()) {
				        Map.Entry<String, Posizione> pair = (Map.Entry<String, Posizione>)it.next();
				        Posizione p = pair.getValue();
				        
				        //System.out.println("comandiDainviare: "+comandiDaInviare);
				        
				        if (!p.ordini[Posizione.ORDINE_APERTURA].inviato) {
				        	//prima invio l'ordine di apertura
				        	comandiDaInviare.add(p.ordini[Posizione.ORDINE_APERTURA].comando);
				        	p.ordini[Posizione.ORDINE_APERTURA].inviato = true;
				        	
				        } else if (p.ordini[Posizione.ORDINE_APERTURA].eseguito) {
				        	//metto anche stop profit e stop loss
				        	if (p.ordini[Posizione.STOPLOSS_ATTIVO]==null) {
				        		if (p.ordini[Posizione.STOPLOSS_DESIDERATO].inviato==false) {
					        		System.out.println("STOP LOSS NON ANCORA MESSO, LO METTO");
					        		comandiDaInviare.add(p.ordini[Posizione.STOPLOSS_DESIDERATO].comando);
					        		p.ordini[Posizione.STOPLOSS_DESIDERATO].inviato = true;
				        		}
				        	} else if (p.ordini[Posizione.STOPPROFIT_ATTIVO]==null) {
				        		if (p.ordini[Posizione.STOPPROFIT_DESIDERATO].inviato==false) {
				        			System.out.println("STOP PROFIT NON ANCORA MESSO, LO METTO");
					        		comandiDaInviare.add(p.ordini[Posizione.STOPPROFIT_DESIDERATO].comando);
					        		p.ordini[Posizione.STOPPROFIT_DESIDERATO].inviato = true;
				        		}
				        	}
				        } 
				        
				        //it.remove(); // avoids a ConcurrentModificationException
				    }
				    
				    //invia i messaggi nella coda da inviare
					if (comandiDaInviare.size()>0) {
						String comando = comandiDaInviare.remove(0);
						outToServer.writeBytes(comando+"\n");
						LOG.writeLine(">  "+comando);
						System.out.println(">  "+comando);
					}
				    
					//invio un getposition ogni 10 secondi
					
					if (System.currentTimeMillis()-lastGetPosition>1000) {
						lastGetPosition = System.currentTimeMillis();
						Iterator itt = posizioni.entrySet().iterator();
					    while (itt.hasNext()) {
					        Map.Entry<String, Posizione> pair = (Map.Entry<String, Posizione>)itt.next();
					        String titolo = pair.getKey();
					        Posizione p = pair.getValue();
					        if (p.Q!=0 && p.ordini[Posizione.ORDINE_APERTURA].eseguito) comandiDaInviare.add("GETPOSITION "+p.codalfa);
					    }
					}
					
					/*
					for (Iterator<String> iterator = comandiDaInviare.iterator(); iterator.hasNext();) {
					    String comando= iterator.next();
					    if (comando.contains("PROFIT") || comando.contains("LOSS")) {
					        //esegui solo se l'ordine relativo e' stato eseguito
					    	boolean ordineRelativoEseguito = false;
					    	for (Ordine o : this.ordini) {
					    		//System.out.println(o);
					    		if (o.eseguito) {
					    			if (comando.contains(o.codiceOrdine)) {
					    				ordineRelativoEseguito = true;
					    			}
					    		}
					    	}
					    	if (ordineRelativoEseguito) {
					    		/////outToServer.writeBytes(comando+"\n");
						    	System.out.println(">  "+comando);
								LOG.writeLine(">  "+comando);
						    	iterator.remove();
					    	}
					        
					    } else {
					    	/////outToServer.writeBytes(comando+"\n");
					    	System.out.println(">  "+comando);
							LOG.writeLine(">  "+comando);
					    	iterator.remove();
					    }
					}*/
					
				} catch (Exception e) {
					//forza allarme
					bloccato = true;
				}
				while (bloccato) {
					Toolkit.getDefaultToolkit().beep();
					try {
					    Thread.sleep(500);                 //1000 milliseconds is one second.
					    socketOrdini = new Socket("localhost", 10002);
						socketOrdini.setSoTimeout(500); //la lettura readline non blocca mai per piu' di 500ms
						outToServer = new DataOutputStream(socketOrdini.getOutputStream());   
						inFromServer = new BufferedReader(new InputStreamReader(socketOrdini.getInputStream()));
						bloccato = false;
					} catch(Exception ex) {
						System.out.println("exception!");
						ex.printStackTrace();
					    //Thread.currentThread().interrupt();
					}
				}
			}
			System.out.println("TERMINATOOOOO==============================================");
			socketOrdini.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
