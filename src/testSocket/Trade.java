package testSocket;

import java.io.Serializable;
import java.util.Date;

public class Trade implements Serializable{
		private static final long serialVersionUID = -8801127633384619906L;
		long timestampLong;
		  Date timestamp;
		  String codalfa;
		  float prezzoRiferimento;
		  float prezzoApertura;
		  float price;
		  int volume;
		  int turnover;
		  float vwap;
		  double standardDeviation;
		  int spread;
		  int impact;
		  public Trade(long tL, Date t, String cod, float pR,float pA, float p, int vol, int turn, float vw, double sd, int spr, int imp) {this.timestampLong=tL;this.timestamp=t;this.codalfa=cod;this.prezzoRiferimento=pR;this.prezzoApertura=pA;this.price=p;this.volume=vol;this.turnover=turn;this.vwap=vw;this.standardDeviation=sd;this.spread=spr;this.impact=imp;}
	};
		