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
		
		boolean SCRIVI_FILE_DATI = false;
		
		PlayerRealTime player = new PlayerRealTime();
		
		
		//Player player = new Player();
		GUI gui = new GUI(player);
//		gui.addTitoloInAscolto(new StockListener("A2A",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ANIM",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("AZM",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BMED",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BAMI",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BPE",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("BZU",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("CNHI",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ENAV",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ENEL",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ENI",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("EXO",30,player, new ArrayList<Metodo>() ));
 //		gui.addTitoloInAscolto(new StockListener("FCA",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("RACE",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("G",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("ISP",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("LDO",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("LUX",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("MB",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("MONC",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("MS",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("PRY",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("PST",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SFER",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SPM",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("SRG",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("STM",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TEN",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TIT",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TOD",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("TRN",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("UBI",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("UNI",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("UCG",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("YNAP",30,player, new ArrayList<Metodo>() ));
//		
		
    	gui.addTitoloInAscolto(new StockListener("SO",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("EM",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("LX.EURUSD",30,player, new ArrayList<Metodo>() ));
//		gui.addTitoloInAscolto(new StockListener("LX.USDJPY",30,player, new ArrayList<Metodo>() ));

//		vwapRatioFTSE v = new vwapRatioFTSE();
		 

		
		DataSaver ds = new DataSaver(logfile);
		try {
			Socket clientSocket = new Socket("localhost", 10001);   
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());   
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));   
			sentence = inFromServer.readLine();   
			if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
			
			outToServer.writeBytes("SUB SO\n");
	//		outToServer.writeBytes("SUB A2A,ANIM,AZM,BMED,BAMI,BPE,BZU,CNHI,ENAV,ENEL,ENI,EXO,FCA,G,ISP,LDO,LUX,MB,MONC,MS,PRY,PST,SFER,SPM,SRG,STM,TEN,TIT,TOD,TRN,RACE,UBI,UNI,UCG\n");
			if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
			
			Long ultimoDatoBook = System.currentTimeMillis();
			boolean b=true;
			while(b) {
				try {
					sentence = inFromServer.readLine();
					if ( SCRIVI_FILE_DATI ) ds.writeLine(sentence);
					player.sendLine(sentence);
					if (sentence.indexOf("BOOK_5")!=-1) ultimoDatoBook = System.currentTimeMillis();

						
				} catch (Exception e) {
					//forza allarme
					System.out.println("allarme!");
					e.printStackTrace();
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
						outToServer.writeBytes("SUB A2A,ANIM,AZM,BMED,BAMI,BPE,BZU,CNHI,ENAV,ENEL,ENI,EXO,FCA,G,ISP,LDO,LUX,MB,MONC,MS,PRY,PST,SFER,SPM,SRG,STM,TEN,TIT,TOD,TRN,RACE,UBI,UNI,UCG\n");
//						outToServer.writeBytes("SUB GE\n");
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
