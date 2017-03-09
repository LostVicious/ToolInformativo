package testSocket;


import java.awt.BorderLayout;
import java.awt.Label;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.Socket;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;

public class TestSocket {
	
	public static void main(String[] args) {		
		String sentence,logfile;   
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		logfile = dateFormat.format(new Date(System.currentTimeMillis()))+"_tool.txt";
		System.out.println("Writing data to: "+logfile);
		
		boolean SCRIVI_FILE_DATI = true;
		
		GestioneOrdini gestioneOrdini = GestioneOrdini.getInstance();
		PlayerRealTime player = new PlayerRealTime();
		
		//gestioneOrdini.apriPosizione("AZM", 500, new BigDecimal("26.53"), 10, 10);
		
		//Player player = new Player();
		GUI gui = new GUI(player);
//		gui.addTitoloInAscolto(new StockListener("LX.EURUSD",0,85,player, Arrays.asList(new MetodoIpercomprato(0.10f,25,22,true,false))));
//		gui.addTitoloInAscolto(new StockListener("A2A",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ANIM",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ATL",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("AZM",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BMED",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BMPS",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BP",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BPE",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BZU",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("CNHI",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("CPR",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("EGPW",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ENEL",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ENI",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("EXO",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("FCA",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("FNC",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("G",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ISP",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("IT",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("LUX",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("MB",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("MONC",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("MS",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("PMI",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("PRY",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("PST",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SFER",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SPM",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SRG",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("STM",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TEN",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TIT",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TOD",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TRN",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("UBI",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("UCG=",1,30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("EIT",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("EM",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ELN",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SO",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("UNI",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("US",25000,85,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("YNAP",25000,85,player, new ArrayList<Metodo>() ));
//		vwapRatioFTSE v = new vwapRatioFTSE();
		 
//		gui.addTitoloInAscolto(new StockListener("A2A",25000,85,player, Arrays.asList( new MetodoValueVwapRatio(0.985f, -0.0025f, 25, 22) )));
//		gui.addTitoloInAscolto(new StockListener("AZM",25596,85,player, Arrays.asList( new MetodoIpercomprato(0.275f,25,22,true,false)  )));
//		gui.addTitoloInAscolto(new StockListener("BP",26457,70,player, Arrays.asList(new MetodoIpercomprato(0.3f,25,22,false,true) , new MetodoContrarianVwapRatio(1.02f, 25, 22) ,  new MetodoValueVwapRatio(0.986f, -0.0075f, 25, 22)   )));
//		gui.addTitoloInAscolto(new StockListener("BMPS",18665,70,player, Arrays.asList(new MetodoIpercomprato(0.3f,25,22,false,true)  , new MetodoContrarianVwapRatio(1.04f, 25, 22)   )));
//		gui.addTitoloInAscolto(new StockListener("LUX",25000,85,player, Arrays.asList( new MetodoValueVwapRatio(0.97f, -0.125f, 25, 22) )));
//		gui.addTitoloInAscolto(new StockListener("SFER",25000,85,player, Arrays.asList( new MetodoValueVwapRatio(0.9825f, -0.02f, 25, 22) )));
//		gui.addTitoloInAscolto(new StockListener("YNAP",40083,85,player, Arrays.asList(new MetodoIpercomprato(0.425f,25,22,true,false)  , new MetodoContrarianVwapRatio(1.025f, 25, 22)   )));
		
		//player.play("C:/Users/PC/Documents/2015-07-02_dati.txt");
		
		//gui.addTitoloInAscolto(new StockListener("BP", 26457, 0.25f, 70, 27, 22, player));
		//gui.addTitoloInAscolto(new StockListener("BMPS", 18665, 0.30f, 70, 65, 50, player));
		//gui.addTitoloInAscolto(new StockListener("YNAP", 40083, 0.425f, 85, 24, 19, player));
		
		//player.play("C:/Users/PC/Documents/2015-06-22_dati.txt");
		/*
		try {
			//gestioneOrdini.revocaAllTitolo("LX.EURGBP");
			gestioneOrdini.apriPosizione("LX.EURGBP", 500, new BigDecimal("0.71130"), 10, 10, true);
			Thread.sleep(1000);
			gestioneOrdini.apriPosizione("LX.EURUSD", -500, new BigDecimal("1.12027"), 10, 10, true);
			Thread.sleep(10000);
			gestioneOrdini.chiudiTutteLePosizioni();
		} catch (Exception e1) {
			e1.printStackTrace();
		}*/
		
		//StockListener asd = new StockListener("AZM", 28452, 0.4f, 70, 65, 50, p);
		//gui.addTitoloInAscolto(new StockListener("LX.EURAUD", 10, 0.3f, 85, 19, 15, player));
		//gui.addTitoloInAscolto( new StockListener("AZM", 36097, 0.4f, 70, 65, 50, p) );
		/*gui.addTitoloInAscolto( new StockListener("BP", 36097, 0.4f, 70, 65, 50, p) );
		gui.addTitoloInAscolto( new StockListener("BMPS", 36097, 0.4f, 70, 65, 50, p) );
		gui.addTitoloInAscolto( new StockListener("YNAP", 36097, 0.4f, 70, 65, 50, p) );
		*/
		
		//StockListener asd = new StockListener("AZM", 36097, 0.4f, 70, 65, 50, p);
		
		//p.play("C:/Users/PC/Documents/2015-06-09_dati.txt");
		/*
		try {
			Thread.sleep(1000000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (true) return;*/
		
		
		
		/*
		int i=0;
		while (i<1) {
			i++;
			try {
				//gestioneOrdini.apriPosizione("FCA", 100, new BigDecimal("13.50"), 20, 10, true);
				//gestioneOrdini.apriPosizione("LX.EURGBP", 5000, new BigDecimal("0.71643"), 4, 7, true);
				//gestioneOrdini.venditaStop(new Ordine("codiceOrdine", "lx.eurusd", 10000, new BigDecimal("1.13340")));
				Thread.sleep(3000);
				gestioneOrdini.revocaAllTitolo("LX.EURGBP");
				//gestioneOrdini.comandiDaInviare.add("ORDERLIST lx.eurusd");
				Thread.sleep(1000000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		gestioneOrdini.chiudiTutteLePosizioni();
		
		if (true) return;*/
		
		DataSaver ds = new DataSaver(logfile);
		try {
			Socket clientSocket = new Socket("localhost", 10001);   
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
			sentence = inFromServer.readLine();   
			if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
			
			outToServer.writeBytes("SUB EIT\n");
			//outToServer.writeBytes("SUB LX.EURUSD,LX.EURAUD,LX.EURGBP,LX.EURJPY,LX.EURRUB,LX.EURCAD,LX.EURCHF,LX.STOXX5,A2A,AZM,BMPS,BP,LUX,SFER,YNAP\n"); 
			//outToServer.writeBytes("SUB LX.FGDAXI,LX.STOXX5,FIB4L,CM.ESZ4,F,G,ENI,LUX,ENEL,MS,UCG,ISP,TIT,BMPS,BP,UBI,A2A,SRG,TRN,MB,PC,TEN,AGL,AZM,EXO,SPM,MED\n");
			if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
			
			Long ultimoDatoBook = System.currentTimeMillis();
			boolean b=true;
			while(b) {
				try {
					sentence = inFromServer.readLine();
//					System.out.println(sentence);
//					System.out.println("scrivi file dati: "+SCRIVI_FILE_DATI);
					if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
					player.sendLine(sentence);
//					System.out.println("indexof: "+(sentence.indexOf("BOOK_5")!=-1));
					if (sentence.indexOf("BOOK_5")!=-1) ultimoDatoBook = System.currentTimeMillis();
//					try {
//						if (sentence.indexOf("BOOK_5")!=-1) ultimoDatoBook = System.currentTimeMillis();
//				  } catch (Exception exc) {
//					  	System.out.println(exc);
//				  }
						
				} catch (Exception e) {
					//forza allarme
					System.out.println("allarme!");
					e.printStackTrace();
//					StockListener stock = new StockListener("EIT",30,player, new ArrayList<Metodo>() );
//					System.out.println(StockListener.qTotaleTrades);
					ultimoDatoBook = 0l;
				}
				
				while (System.currentTimeMillis()-ultimoDatoBook > 36000000) { //se non riceviamo dati book da almeno un minuto
					Toolkit.getDefaultToolkit().beep();
					try {
					    Thread.sleep(500);                 //1000 milliseconds is one second.
					    clientSocket = new Socket("localhost", 10001);   
						outToServer = new DataOutputStream(clientSocket.getOutputStream());   
						inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
						sentence = inFromServer.readLine();   						
						if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
						outToServer.writeBytes("SUB EIT\n");
						if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
						ultimoDatoBook = System.currentTimeMillis();  
					} catch(Exception ex) {
						System.out.println("exception!");
						ex.printStackTrace();
					    //Thread.currentThread().interrupt();
					}
				}
			}
			
			clientSocket.close();
		} catch (Exception e) {
			System.out.println("Eccezione: " + e.getMessage());
		}
		ds.close();
	}
	
}
