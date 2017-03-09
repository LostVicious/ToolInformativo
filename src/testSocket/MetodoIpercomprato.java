package testSocket;

import java.math.BigDecimal;

public class MetodoIpercomprato implements Metodo {

	GestioneOrdini gi;
	int stopProfit, stopLoss;
	boolean longare = false, shortare = false;
	float scostamento = 99f;
	
	public MetodoIpercomprato(float scostamento, int stopProfit, int stopLoss, boolean longare, boolean shortare) {
		gi = GestioneOrdini.getInstance();
		this.scostamento = scostamento;
		this.stopProfit = stopProfit;
		this.stopLoss = stopLoss;
		this.longare = longare;
		this.shortare = shortare;
	}
	
	@Override
	public void aggiorna(StockListener s) {
		if (s.nTradeGrossi>=5) {
			//entriamo solo se non ha gia' una posizione aperta
			if (gi.posizioni.get(s.codAlfa)==null || gi.posizioni.get(s.codAlfa).Q==0) {
				//tradiamo solo dalle 10.15 in poi
				if (s.ora>10 || (s.ora==10 && s.minuto >= 15) ) {
					//non apriamo trade dopo le 17
					if (s.ora<16 || (s.ora==16 && s.minuto <= 30)) {
						//devono essere passati almeno 6 minuto dall'ultima Q
						if (s.tick.timestamp.getTime()-s.timestampUltimaQ>360000) {
							s.timestampUltimoCheck = s.tick.timestamp.getTime();
							//System.out.println("longare:"+longare+" shortare:"+shortare+" iper:"+s.ipercomprato+" scostamento:"+this.scostamento+ " dvWap"+s.deltaVwap);
							//controllo sull'ipercomprato
							
							if (longare && s.deltaVwap>0 && (s.ipercomprato >= 0.5f+this.scostamento)) {
								System.out.println("LOOOOOONG");
								gi.apriPosizione(s.codAlfa, 1, new BigDecimal(s.tick.price), this.stopProfit, this.stopLoss);
							}
							else if (shortare && s.deltaVwap<0 && (s.ipercomprato <= 0.5f-this.scostamento)) {
								System.out.println("SHOOOOOOOORT");
								gi.apriPosizione(s.codAlfa, -1, new BigDecimal(s.tick.price), this.stopProfit, this.stopLoss);
							}
						}
					}
				}
			}
		}
	}
	
}
