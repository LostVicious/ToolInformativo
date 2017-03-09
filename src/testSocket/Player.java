package testSocket;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Date;
import java.util.Observable;

public class Player extends Observable implements Runnable {
	String path = "---";
	Boolean playing = false;
	
	public void play(String filePath) {
		this.path = filePath;
		setChanged();
		notifyObservers("CLEAR");
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		try {
			BufferedReader br = new BufferedReader( new FileReader(path) );
			String line;
			boolean vero=true;
			line = br.readLine();
			Long millisInit = Long.parseLong(line.split(";")[0]);
			Date giornoDaCaricare = new Date(millisInit);
			//System.out.println("CARICO GIORNO: "+giornoDaCaricare);
			
			Long sysMillisInit = System.currentTimeMillis();
			//System.out.println(line);
			this.playing = true;
			while (playing) {
				line = br.readLine();
				Long milli = Long.parseLong(line.split(";")[0]);
				Long timeToPlay = sysMillisInit + (milli-millisInit);
				/*while (System.currentTimeMillis()<timeToPlay) {
				//while ((System.currentTimeMillis()-sysMillisInit)/2 < (milli-millisInit) ) {
					if (timeToPlay-System.currentTimeMillis()>5000) {
						//if wait >5 seconds jump to it directly
						//cur-sysmillinit=millis-millisinit
						sysMillisInit=System.currentTimeMillis()-milli+millisInit;
						break;
					}
				}*/
				//System.out.println(line);
				setChanged();
				notifyObservers(line);
			}
		} catch (Exception e) {
			System.out.println("caricamento terminato.");
			setChanged();
			notifyObservers("CARICAMENTO_TERMINATO");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
