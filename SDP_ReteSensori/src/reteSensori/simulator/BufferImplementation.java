package reteSensori.simulator;
import java.util.ArrayList;
import java.util.List;

import reteSensori.resources.Nodo;
import reteSensori.resources.SystemMessage;

/**
 * Classe che implementa l'interfaccia del Buffer.
 * @author Gianpaolo Caprara
 *
 * @param <T>
 */
public class BufferImplementation<T> implements Buffer<T>{

	@SuppressWarnings("rawtypes")
	private static BufferImplementation instance;

	public List<T> listMisuration;
	private int index = 0;

	private BufferImplementation(){
		listMisuration = new ArrayList<T>();
	}

	@SuppressWarnings("rawtypes")
	public synchronized static BufferImplementation getInstance(){
		if(instance==null)
			instance = new BufferImplementation();
		return instance;
	}

	@Override
	public synchronized void aggiungi(T t) {
		//verifica del vincolo sulla lunghezza della lista (deve essere massimo di 10 elementi)
		if (listMisuration.size() < 10)
		{
			if(Nodo.getInstance().getBattery() >= 2){
				Nodo.getInstance().decrementBattery(2);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				listMisuration.add(t);
			} else{
				Nodo.getInstance().closeSocket();
			}
		} 
		//nel caso in cui la lunghezza massima sia stata raggiunta bisogna salvare il dato nuovo sul dato più vecchio
		else{
			if(Nodo.getInstance().getBattery() >= 2){
				Nodo.getInstance().decrementBattery(2);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				listMisuration.set(index, t);
				index++;
				if(index == 10)
					index = 0;
			}else{
				Nodo.getInstance().closeSocket();
			}
		}
		notify();
	}

	@Override
	public synchronized List<T> leggi() {
		//finchè la lista è vuota il processo si mette in attesa
		while (listMisuration.size() == 0){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//mette in una lista la lista di una determinata misurazione
		List<T> list = new ArrayList<T>(listMisuration); 

		//elimina tutti gli elementi dalla lista una volta letto tutto il contenuto
		listMisuration.clear();

		//resetta la variabile index perchè la lista sarà vuota
		index = 0;
		//restituisce la lista
		return list;
	}

}
