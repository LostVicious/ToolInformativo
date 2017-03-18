package testSocket;

import java.util.Date;

public class Indicatori {
	public String codAlfa;
	public Date timestampIndicatori;
	public int totalTurnover = 0;
	public int turnover = 0;
	public int numberoftrades = 0;
	public int averageturnover = 0;
	public int marketorderdelta = 0;
	public float marketbuypercentage = 0.5f;
	public double standardDeviation = 0f;
	public float bidAskSpread = 1f;
	public float bookImpact = 1f;
	public float bookImpactBuy = 1f;
	public float bookImpactSell = 1f;
	
	public Indicatori(String codalfa, Date timestampIndicatori,int totalTurnover,int turnover,int numberoftrades,int averageturnover,int marketorderdelta,float marketbuypercentage,double standardDeviation,float bidAskSpread,float bookImpact,float bookImpactBuy,float bookImpactSell) {
		this.codAlfa = codalfa;
		this.timestampIndicatori = timestampIndicatori;
		this.totalTurnover = totalTurnover;
		this.turnover = turnover;
		this.numberoftrades = numberoftrades;
		this.averageturnover = averageturnover;
		this.marketorderdelta = marketorderdelta;
		this.marketbuypercentage = marketbuypercentage;
		this.standardDeviation = standardDeviation;
		this.bidAskSpread = bidAskSpread;
		this.bookImpact = bookImpact;
		this.bookImpactBuy = bookImpactBuy;
		this.bookImpactSell = bookImpactSell;
	}
}
