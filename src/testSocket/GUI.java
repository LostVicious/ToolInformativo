package testSocket;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Label;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.print.attribute.AttributeSet;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class GUI implements Observer {
	private Label logLabel;
	private JFrame frame;
	private JTextArea logArea;
	private Observable p;
	JPanel barraTop;
	JPanel destra; 
	StyledDocument doc;
	javax.swing.text.AttributeSet green;
	
	long lastUIupdate = 0;
	boolean showGUI = false;
	
	ArrayList<StockListener> titoliInAscolto = new ArrayList<StockListener>();
	
	GUI(Observable p) {
		this.p = p;
		p.addObserver(this);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//JLabel nomeFileAperto = new JLabel("(no data file)");
		
		if (showGUI) {
		
			frame = new JFrame("HEDGE BRAIN");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
//		JButton bottoneStop = new JButton("CHIUDI POSIZIONI");
//		bottoneStop.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				int result = JOptionPane.showConfirmDialog(frame, "Vuoi davvero chiudere tutte le posizioni?");
//				if (result == JOptionPane.OK_OPTION) {
//					System.out.println("OOOOOOOOOOOK");
//					GestioneOrdini gestioneOrdini = GestioneOrdini.getInstance();
//					gestioneOrdini.chiudiTutteLePosizioni();
//				}
//			}
//		});
		
//		JButton bottoneApri = new JButton("Apri posizione");
//		bottoneApri.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				GestioneOrdini gestioneOrdini = GestioneOrdini.getInstance();
//				//gestioneOrdini.operazioneLimite(new Ordine(gestioneOrdini.generaCodiceOrdine("AZM")+"PROFIT", "AZM", 100, new BigDecimal("26.00")) );
//				//gestioneOrdini.operazioneStop(new Ordine(gestioneOrdini.generaCodiceOrdine("AZM")+"LOSS", "AZM", -100, new BigDecimal("25.50") ) );
//				gestioneOrdini.apriPosizione("LX.EURUSD", 10, new BigDecimal("1.10568"), 4, 4);
//				//gestioneOrdini.apriPosizione("LX.EURGBP", -10, new BigDecimal("0.70840"), 10, 10);
//			}
//		});/**/
		
		JButton ordina = new JButton("Ordina per Market Delta");
			ordina.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Collections.sort(titoliInAscolto, (StockListener s1, StockListener s2) ->{
						if (s1.marketorderdelta>s2.marketorderdelta) return 1;
						else return -1;
					});
				}
			});/**/
			
			barraTop = new JPanel();
			barraTop.setLayout(new BoxLayout(barraTop, BoxLayout.Y_AXIS));
			//barraTop.add(nomeFileAperto);
			frame.add(barraTop,BorderLayout.NORTH);
			JPanel centrale = new JPanel();
			centrale.setBackground(Color.BLACK);
			//centrale.add(bottoneStop);
			centrale.add(ordina);
			
			JTextPane bookArea = new JTextPane();
			bookArea.setEditable(false);
			bookArea.setFont(new Font("Verdana", 1, 12));
			bookArea.setBackground(Color.BLACK);
			JScrollPane jsp = new JScrollPane(bookArea);
			StyleContext sc = StyleContext.getDefaultStyleContext();
			green = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, Color.green);
			barraTop.add(jsp);
			doc = bookArea.getStyledDocument();
			
			frame.getContentPane().add(centrale,BorderLayout.CENTER);
			destra = new JPanel();
			frame.add(destra,BorderLayout.EAST);
			//frame.pack();
			frame.setSize(700, 400);
			frame.setVisible(true);
		}
	}
	
	public void addTitoloInAscolto(StockListener s) {
		titoliInAscolto.add(s);
		aggiornaTitoli();
	}
	
	public void aggiornaTitoli() {
		//barraTop.removeAll();
		
		try {doc.remove(0, doc.getLength());} catch (Exception e) {}
		//JLabel spiegazione = new JLabel("<html><pre>CodAlfa\tQeuro  \tIpercomprato \tVWAP \tvwapRatio</pre></html>" );
		try {doc.insertString(doc.getLength(),"Titolo \tContrTot \tContr30 \t#Trades \tContr.Medio Delta \tBuyPerc. \tStdDev \tSpread \tImpact\n",green);} catch (Exception e) {}
		//barraTop.add(spiegazione);
		for (int i=0;i<titoliInAscolto.size();i++) {
			StockListener s = titoliInAscolto.get(i);
			//JLabel nomeFileAperto = new JLabel("<html><pre>"+ s.codAlfa + "\t" + s.Qeuro + "\t    " + s.ipercomprato + "\t\t" + s.vwap  + "\t" + s.vwapRatio +" </pre></html>" );
			String stringa = ""+ s.codAlfa + "\t" +s.totalTurnover+"1t"+ s.turnover + "\t\t\t\t \t   " + s.numberoftrades + "\t\t" + s.averageturnover + "\t\t" + s.marketorderdelta  + "\t\t\t" + s.marketbuypercentage + "\t" + s.standardDeviation+ "\t" + s.bidAskSpread + "\t" + s.bookImpact+"\n";
			stringa = String.format("%s \t%d \t%d \t%d \t%d \t%+d \t%.2f \t%.5f \t%.2f \t%.2f \n", s.codAlfa, s.totalTurnover, s.turnover, s.numberoftrades, s.averageturnover ,  s.marketorderdelta, s.marketbuypercentage, s.standardDeviation, s.bidAskSpread, s.bookImpact);
			try {doc.insertString(doc.getLength(),stringa,green);} catch (Exception e) {}
			
			if (!showGUI) System.out.println(stringa);
			//barraTop.add(nomeFileAperto);
			
			//frame.getContentPane().validate();
			//frame.getContentPane().repaint();
			
			//barraTop.validate();
			//barraTop.repaint();
		}
		/*
		float spm=0,spmaxa=0;
		for (StockListener s : titoliInAscolto) {
			if (s.codAlfa.compareTo("SPM")==0) spm=s.lastPrice;
			if (s.codAlfa.compareTo("SPMAXA")==0) spmaxa=s.lastPrice;
		}
		float val = (spm/((spmaxa/22.0f)+0.362f) -1)*100;
		try {doc.insertString(doc.getLength(),"Parità SPM: "+val,green);} catch (Exception e) {e.printStackTrace();}
		*/
		
		//try {doc.insertString(doc.getLength(),"vwap FTSE:  "+vwapRatioFTSE.getVwapFTSE(titoliInAscolto)+"\n",green);} catch (Exception e) {e.printStackTrace();}
//		frame.setTitle("vwap FTSE:  "+vwapRatioFTSE.getVwapFTSE(titoliInAscolto));
		
		if (showGUI) frame.setTitle("Tool Informativo");
	}
	
	public void aggiornaPosizioni() {
		if (showGUI) {
			destra.removeAll();
			GestioneOrdini gi = GestioneOrdini.getInstance();
			for (Map.Entry<String, Posizione> entry : gi.posizioni.entrySet()) {
				destra.add(new JLabel("<html><pre>"+entry.getKey()+ " " + entry.getValue().Q+"</pre></html>"));
				frame.getContentPane().validate();
				frame.getContentPane().repaint();
			}
		}
	}
	
	@Override
	public void update(Observable o, Object arg) {
		if (System.currentTimeMillis()-lastUIupdate<200) return;
		lastUIupdate = System.currentTimeMillis();
		aggiornaTitoli();
		aggiornaPosizioni();
		
		String s = (String)arg;
		if (s.compareTo("CARICAMENTO_TERMINATO")==0) {
			//carichiamo il prossimo file
		}
		/*logArea.append((String)arg+'\n');
		logArea.setCaretPosition(logArea.getText().length());*/
	}

}
