package testSocket;

public class Posizione {
	String codalfa;
	int Q;
	
	Ordine ordini[] = new Ordine[5];
	
	public static int ORDINE_APERTURA = 0;
	public static int STOPLOSS_ATTIVO = 1;
	public static int STOPLOSS_DESIDERATO = 2;
	public static int STOPPROFIT_ATTIVO = 3;
	public static int STOPPROFIT_DESIDERATO = 4;
	
	public Posizione(String codalfa, int q) {
		this.codalfa = codalfa;
		Q = q;
	}
	
	public void confermaOrdine(String codiceOrdine) {
		for (int i=0;i<ordini.length;i++) {
			Ordine o = ordini[i];
			if (o!= null && o.codiceOrdine.compareTo(codiceOrdine)==0) {
				o.confermato = true;
				if (i==STOPLOSS_DESIDERATO) {
					ordini[STOPLOSS_ATTIVO] = o;
				}
				if (i==STOPPROFIT_DESIDERATO) {
					ordini[STOPPROFIT_ATTIVO] = o;
				}
			}
		}
	}
	
	public void segnaEseguito(String codiceOrdine) {
		for (int i=0;i<ordini.length;i++) {
			Ordine o = ordini[i];
			if (o!= null && o.codiceOrdine.compareTo(codiceOrdine)==0) {
				o.eseguito = true;
				if (i==STOPLOSS_DESIDERATO) {
					ordini[STOPLOSS_ATTIVO] = o;
				}
				if (i==STOPPROFIT_DESIDERATO) {
					ordini[STOPPROFIT_ATTIVO] = o;
				}
			}
		}
	}
	
	public void segnaAnnullato(String codiceOrdine) {
		for (int i=0;i<ordini.length;i++) {
			Ordine o = ordini[i];
			if (o!= null && o.codiceOrdine.compareTo(codiceOrdine)==0) {
				o.revocato = true;
				if (i==STOPLOSS_ATTIVO) {
					if (ordini[STOPLOSS_DESIDERATO].codiceOrdine.compareTo(codiceOrdine)==0) {
						//e' stato revocato del tutto lo stopLoss, popup
						GestioneOrdini.popup("ATTENZIONE NO STOP LOSS SU "+codalfa);
						//provo a rimetterlo
						ordini[STOPLOSS_DESIDERATO].inviato = false;
					}
					ordini[STOPLOSS_ATTIVO] = null;
				}
				if (i==STOPPROFIT_ATTIVO) {
					ordini[STOPPROFIT_ATTIVO] = null;
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "Posizione [codalfa=" + codalfa + ", Q=" + Q + "]";
	}
	
}
