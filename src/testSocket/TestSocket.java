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
		
		
		//FTSE MIB
		GUI gui = new GUI(player);
		gui.addTitoloInAscolto(new StockListener("A2A",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ATL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("AZM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BGN",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BMED",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BAMI",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BPE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BRE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BZU",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CNHI",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CPR",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ENEL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ENI",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("EXO",30,player, new ArrayList<Metodo>() ));
 		gui.addTitoloInAscolto(new StockListener("FCA",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("RACE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("FBK",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("G",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ISP",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("IT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("LDO",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("LUX",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("MB",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("MONC",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("MS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("PRY",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("PST",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("REC",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SFER",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SPM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SRG",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("STM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("TEN",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("TIT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("TRN",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("UBI",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("UNI",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("US",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("UCG",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("YNAP",30,player, new ArrayList<Metodo>() ));

		
		//FTSE Mid Cap
		gui.addTitoloInAscolto(new StockListener("ACE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ADB",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("AMP",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ANIM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("STS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ASC",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("AST",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("AT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("AGL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("IF",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BPSO",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BNS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BSS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("BC",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CAI",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CASS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CEM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CERV",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CIR",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("CVAL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("DAN",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("DAL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("DLG",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("DIA",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("EIT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ELN",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ENAV",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ERG",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("PRT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("FILA",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("FCT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("GEO",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("HER",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("IGD",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("IMA",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("IP",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("INW",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("IRE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ITM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("JUVE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("MT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("MARR",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("MOL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("OVS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("PLT",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("PIA",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("RWAY",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("RCS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("REY",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SFL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SAL",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SRS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SAVE",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("SIS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("TIP",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("TGYM",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("TOD",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("VAS",30,player, new ArrayList<Metodo>() ));
		gui.addTitoloInAscolto(new StockListener("ZV",30,player, new ArrayList<Metodo>() ));
		
//    	gui.addTitoloInAscolto(new StockListener("SO",30,player, new ArrayList<Metodo>() ));
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
			

//			outToServer.writeBytes("SUB SO\n");
			outToServer.writeBytes("SUB A2A,ATL,ANIM,AZM,BGN,BMED,BAMI,BPE,BRE,BZU,CPR,CNHI,ENEL,ENI,IT,EXO,FCA,FBK,G,ISP,LDO,LUX,MB,MONC,MS,PRY,PST,REC,SFER,SPM,SRG,STM,TEN,TIT,TRN,RACE,UBI,UNI,US,UCG,ACE,ADB,AMP,STS,ASC,AST,AT,AGL,IF,BPSO,BNS,BSS,BC,CAI,CASS,CEM,CERV,CIR,CE,CVAL,DAN,DAL,DLG,DIA,EIT,ELN,ENAV,ERG,PRT,FILA,FCT,GEO,HER,IGD,IMA,IP,INW,IRE,ITM,JUVE,MT,MARR,MOL,OVS,PLT,PIA,RWAY,RCS,REY,SFL,SAL,SRS,SAVE,SIS,TIP,TGYM,TOD,VAS,ZV\n");

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
						outToServer.writeBytes("SUB A2A,ATL,ANIM,AZM,BGN,BMED,BAMI,BPE,BRE,BZU,CPR,CNHI,ENEL,ENI,IT,EXO,FCA,FBK,G,ISP,LDO,LUX,MB,MONC,MS,PRY,PST,REC,SFER,SPM,SRG,STM,TEN,TIT,TRN,RACE,UBI,UNI,US,UCG,ACE,ADB,AMP,STS,ASC,AST,AT,AGL,IF,BPSO,BNS,BSS,BC,CAI,CASS,CEM,CERV,CIR,CE,CVAL,DAN,DAL,DLG,DIA,EIT,ELN,ENAV,ERG,PRT,FILA,FCT,GEO,HER,IGD,IMA,IP,INW,IRE,ITM,JUVE,MT,MARR,MOL,OVS,PLT,PIA,RWAY,RCS,REY,SFL,SAL,SRS,SAVE,SIS,TIP,TGYM,TOD,VAS,ZV\n");
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
