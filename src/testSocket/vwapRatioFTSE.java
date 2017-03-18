package testSocket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

public class vwapRatioFTSE {
	static public Connection conn = null;
	static HashMap<String, Float> pesi;
	
	public vwapRatioFTSE() {
		
	}
	
	public static float getVwapFTSE(ArrayList<StockListener> stocks) {
		if (conn==null) {
			pesi = new HashMap<String, Float>();
			try {
		        Class.forName("com.mysql.jdbc.Driver");
		        conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=zxcvbnm");
		        
		        conn.createStatement();
		        conn.setCatalog("hedgefund");
		        
		        Statement stmt = conn.createStatement();
				String query = "select codalfa,peso FROM pesi_ftse";
				ResultSet rs = stmt.executeQuery(query);
				while(rs.next()) {
					pesi.put(rs.getString("codalfa"), rs.getFloat("peso"));
				}
		        
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		float somma=0, sommaPesi=0;
		for (StockListener s : stocks) {
			if (pesi.containsKey(s.codAlfa)) {
				somma+=pesi.get(s.codAlfa)*s.vwapRatio;
				sommaPesi+=pesi.get(s.codAlfa);
			}
		}
		return somma/sommaPesi;
	}
	
}
