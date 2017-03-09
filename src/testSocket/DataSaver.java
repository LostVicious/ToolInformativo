package testSocket;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DataSaver {
	private File logfile;
	private FileWriter fileWriter;
	
	public DataSaver(String path) {
		this.logfile = new File(path); 
		if(!logfile.exists()){
			try {
				logfile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//true = append file
		try {
			fileWriter = new FileWriter(logfile.getAbsolutePath(),true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void writeLine(String line) {
		//System.out.println(System.currentTimeMillis()+";"+line);
		if (line.compareTo("H")==0) return; //dont log heartbeat signals
		try {
	        fileWriter.write(System.currentTimeMillis()+";"+line+'\n');
	        fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void finalize() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close() {
		try {
			fileWriter.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
