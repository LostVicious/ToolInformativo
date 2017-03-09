package testSocket;

import java.util.Observable;

public class PlayerRealTime extends Observable {
	public void sendLine(String line) {
		//System.out.println(System.currentTimeMillis()+";"+line);
		if (line.compareTo("H")==0) return; //dont log heartbeat signals
		setChanged();
		notifyObservers(System.currentTimeMillis()+";"+line+'\n');
	}
}
