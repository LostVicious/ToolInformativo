package testSocket;

import java.math.BigDecimal;

public class MetodoContrarianVwapRatio implements Metodo {

	GestioneOrdini gi;
	float soglia = 10f;
	int stopProfit, stopLoss;
	
	public MetodoContrarianVwapRatio(float soglia, int stopProfit, int stopLoss) {
		gi = GestioneOrdini.getInstance();
		this.soglia = soglia;
		this.stopProfit = stopProfit;
		this.stopLoss = stopLoss;
	}
	
	@Override
	public void aggiorna(StockListener s) {
		//entriamo solo se non ha gia' una posizione aperta
		if (gi.posizioni.get(s.codAlfa)==null || gi.posizioni.get(s.codAlfa).Q==0) {
			//tradiamo solo dalle 10.15 in poi
			if (s.ora>10 || (s.ora==10 && s.minuto >= 15) ) {
				//non apriamo trade dopo le 17
				if (s.ora<16 || (s.ora==16 && s.minuto <= 30)) {					
					if (s.vwapRatio >= this.soglia) {
						System.out.println("SHOOOOOOOORT");
						gi.apriPosizione(s.codAlfa, -1, new BigDecimal(s.tick.price), this.stopProfit, this.stopLoss);
					}
				}
			}
		}
	}
}

