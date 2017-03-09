package testSocket;

import java.math.BigDecimal;

public class Ordine {
	String codiceOrdine;
	String codalfa;
	int Q;
	//BigDecimal pi = new BigDecimal("3.14159265358979323846");
	BigDecimal prezzo;
	boolean inviato = false;
	boolean confermato = false;
	boolean eseguito = false;
	boolean revocato = false;
	int tipoOrdine = -1;
	String comando = "";

	float perdita_massima = 300;

	public static int AL_MEGLIO = 0;
	public static int LIMITE = 1;
	public static int STOP = 2;
	
	//tipo ordine puo' essere ALMEGLIO LIMITE o STOP
	public Ordine(int tipoOrdine, String codiceOrdine, String codalfa, int q, BigDecimal prezzo) {
		this.codiceOrdine = codiceOrdine;
		this.codalfa = codalfa;
		Q = q;
		this.prezzo = prezzo;
		this.tipoOrdine = tipoOrdine;
		
		if (tipoOrdine == AL_MEGLIO) {
			this.setOperazioneAlMeglio();
		} else if (tipoOrdine == LIMITE) {
			this.setOperazioneLimite();
		} else if (tipoOrdine == STOP) {
			this.setOperazioneStop();
		}
		
	}
	
	private void setOperazioneAlMeglio() {
		comando = Q>=0 ? "ACQMARKET " : "VENMARKET "; 
		comando = comando + codiceOrdine+","+codalfa+","+Math.abs(Q);
	}
	
	private void setOperazioneLimite() {
		comando = Q>=0 ? "ACQAZ " : "VENAZ "; 
		comando = comando+codiceOrdine+","+codalfa+","+Math.abs(Q)+","+prezzo;
	}
	
	private void setOperazioneStop() {
		comando = Q>0 ? "ACQSTOPLIMIT " : "VENSTOPLIMIT ";
		BigDecimal prezzoLimite =  Q>0 ? prezzo.add( GestioneOrdini.tickSizeFromPrice(prezzo).multiply(new BigDecimal(10) )) : prezzo.subtract( GestioneOrdini.tickSizeFromPrice(prezzo).multiply(new BigDecimal(10)));
		//prezzo limite,prezzo segnale
		comando = comando+codiceOrdine+","+codalfa+","+Math.abs(Q)+","+prezzoLimite+","+prezzo;
	}

	@Override
	public String toString() {
		return "Ordine [codiceOrdine=" + codiceOrdine + ", codalfa=" + codalfa
				+ ", Q=" + Q + ", prezzo=" + prezzo + "]";
	}
	

}
