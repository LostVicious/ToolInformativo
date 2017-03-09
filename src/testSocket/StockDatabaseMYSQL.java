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
			doUpdate("INSERT INTO TOOLINFORMATIVO(codalfa,timestamp,price,volume,buy,market) VALUES('"+tick.codalfa+"','"+tsFormat.format(tick.timestamp)+"',"+tick.price+","+tick.volume+","+tick.buy+","+tick.market+");");
	}

}
