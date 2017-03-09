package testSocket;

import java.math.BigDecimal;

public class MetodoValueVwapRatio implements Metodo {

	GestioneOrdini gi;
	float sogliaVwapRatio = 0.001f, sogliaDeltaVwap = -100f;
	int stopProfit, stopLoss;
	
	public MetodoValueVwapRatio(float sogliaVwapRatio, float sogliaDeltaVwap, int stopProfit, int stopLoss) {
		gi = GestioneOrdini.getInstance();
		this.sogliaVwapRatio = sogliaVwapRatio;
		this.sogliaDeltaVwap = sogliaDeltaVwap;
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
					if (s.vwapRatio <= this.sogliaVwapRatio && s.deltaVwap >= sogliaDeltaVwap) {
						System.out.println("LOOOOONG VALUE");
						gi.apriPosizione(s.codAlfa, 1, new BigDecimal(s.tick.price), this.stopProfit, this.stopLoss);
					}
				}
			}
		}
	}
}

