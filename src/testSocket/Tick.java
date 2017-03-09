package testSocket;

import java.util.Date;

public class Tick {
	public String codalfa;
	public Date timestamp;
	public String price;
	public int volume;
	public boolean buy;
	public boolean market;
	
	public Tick(String codalfa, Date timestamp,String price,int volume,boolean buy,boolean market) {
		this.codalfa = codalfa;
		this.timestamp = timestamp;
		this.price = price;
		this.volume = volume;
		this.buy = buy;
		this.market = market;
	}
}
