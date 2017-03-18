package testSocket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;



public class StockDatabaseMYSQL {
	public Connection conn;
	DateFormat tsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	PreparedStatement insertTick = null, insertBook = null, insertBookGiusto = null;
	
	public StockDatabaseMYSQL() {
		try {
	        Class.forName("com.mysql.jdbc.Driver");
	        conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=zxcvbnm");
	        
	        //creo il database se non esiste
	        Statement s = conn.createStatement();
	        int Result = s.executeUpdate("CREATE DATABASE IF NOT EXISTS hedgefund;");
	        //seleziono il database
	        conn.setCatalog("hedgefund");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public void doQuery(String query) {
		try {
			Statement stmt = conn.createStatement();
			boolean error = stmt.execute(query);
			if (error) System.out.println("Error in query: "+query);
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error in query: "+query);
			e.printStackTrace();
		}
	}
	
	public void doUpdate(String query) {
		try {
			Statement stmt = conn.createStatement();
			int i = stmt.executeUpdate(query);
			if (i==-1) System.out.println("Error in query: "+query);
			stmt.close();
			//conn.commit();
		} catch (SQLException e) {
			if (e.getErrorCode()==1062) {
				//ignora, ha provato a inserire un duplicato
			} else {
				System.out.println("Error in query: "+query);
				e.printStackTrace();
			}
		}
	}

	
	public void insertTick(Tick tick) {
			doUpdate("insert into toolinformativo(codalfa,timestamp,price,volume,buy,market) values('"+tick.codalfa+"','"+tsFormat.format(tick.timestamp)+"',"+tick.price+","+tick.volume+","+tick.buy+","+tick.market+");");
	}

	public void inserisciTrade(Trade t) {
		doUpdate("insert into trades(codalfa,timestamp,price,volume,turnover,vwap,standarddeviation,spread,impact) values('"+t.codalfa+"','"+tsFormat.format(t.timestamp)+"',"+t.price+","+t.volume+","+t.turnover+","+t.vwap+","+t.standardDeviation+","+t.spread+","+t.impact+");");
}

	
	public void inserisciIndicatori(Indicatori I) {
		doUpdate("insert into indicatoridaily(codalfa,timestamp,totalturnover,turnover30,numberoftrades30,averageturnover30,marketorderdelta30,marketbuypercentage30,standarddeviation,spread30,bookimpact30,bookimpactbuy30,bookimpactsell30) values('"+I.codAlfa+"','"+tsFormat.format(I.timestampIndicatori)+"',"+I.totalTurnover+","+I.turnover+","+I.numberoftrades+","+I.averageturnover+","+I.marketorderdelta+","+I.marketbuypercentage+","+I.standardDeviation+","+I.bidAskSpread+","+I.bookImpact+","+I.bookImpactBuy+","+I.bookImpactSell+");");
}
	
}
