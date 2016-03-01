package gestore.resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Classe che viene utilizzata per poter salvare tutte le misurazioni provenienti dal Sink dei vari Sensori.
 * Viene utilizzato un HashMap costituito da una Stringa, che identifica la tipologia del sensore, e da un
 * ArrayList<\Misurazione>\ che tiene traccia delle misurazioni più recenti di quel determinato tipo di sensore.
 * @author Gianpaolo Caprara
 *
 */
public class ListMisurations {

	private static ListMisurations instance;

	public Map<String,ArrayList<Misurazione>> misurations;

	protected ListMisurations(){
		misurations = new HashMap<>();
	}

	/* Otttiene l'istanza della lista delle misurazioni. */
	public synchronized static ListMisurations getInstance(){
		if(instance==null)
			instance = new ListMisurations();
		return instance;
	}

	/* Aggiunge le varie misurazioni di quella determinata tipologia di sensore. */
	public synchronized void aggiungi(String type, ArrayList<Misurazione> mis) {
		//verifica se è sono già contenuti delle misurazioni di quel tipo;
		//in caso affermativo unisce le misurazioni nuove nella HashMap
		if (misurations.containsKey(type)){
			for (int i = 0; i < mis.size(); i++){
				misurations.get(type).add(mis.get(i));			
			}
			notify();	
		}else {
			misurations.put(type, mis);
			notify();	
		}
	}

	public synchronized Map<String,ArrayList<Misurazione>> leggi() {
		/* Finchè la lista è vuota il processo si mette in attesa. */
		while (misurations.size() == 0){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		/* Mette in una HashMap le varie misurazioni lette. */
		Map<String,ArrayList<Misurazione>> list = new HashMap<String,ArrayList<Misurazione>>(misurations);

		/* Elimina tutti gli elementi dell'HashMap una volta letto tutto il contenuto. */
		/*misurations.clear();*/

		return list;
	}

}
