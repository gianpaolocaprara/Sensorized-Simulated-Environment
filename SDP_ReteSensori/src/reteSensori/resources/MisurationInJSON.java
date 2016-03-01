package reteSensori.resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che gestisce le misurazioni provenienti dai sensori (compresa anche quella del sink).
 * Formata da una Lista di Stringhe, dove quest'ultime sono delle stringhe in formato JSON che
 * verranno spedite al Gestore per il successivo unmarshalling.
 * @author Gianpaolo Caprara
 *
 */
public class MisurationInJSON {
	
	private static MisurationInJSON istance;
	
	public List<String> misurations;
	
	private MisurationInJSON(){
		misurations = new ArrayList<String>();
	}
	
	public synchronized static MisurationInJSON getInstance(){
		if (istance == null){
			istance = new MisurationInJSON();
		}
		return istance;
	}
	
	/* Aggiunge nell'ArrayList<String> le varie misurazioni in formato JSON da passare successivamente al Gestore. */	
	public synchronized void aggiungi(String t) {
		misurations.add(t);
		notify();
	}

	public synchronized List<String> leggi() {
		/* Finchè la lista non contiene le misurazioni di tutti i sensori attivi il processo si mette in attesa. */
		while (misurations.size() < SinkNode.getInstance().listSocket.size()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/* Mette in una lista la lista di una determinata misurazione. */
		List<String> list = new ArrayList<String>(misurations);

		/* Elimina tutti gli elementi dalla lista una volta letto tutto il contenuto. */
		misurations.clear();

		return list;
	}

}
