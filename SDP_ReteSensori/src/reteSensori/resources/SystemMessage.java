package reteSensori.resources;

/**
 * Classe che restituisce i vari messaggi di sistema da stampare a video (per la rete di Sensori).
 * @author Gianpaolo Caprara
 *
 */
public class SystemMessage {

	public static String getString(String key){
		String result = "";
		switch (key){
		case "LEVEL_BATTERY":
			result = "Livello di batteria: " + Nodo.getInstance().getLevelBattery() + "%";
			break;
		case "MISURATION":
			result = "Nuova misurazione calcolata!" + getString("LEVEL_BATTERY") + ".";
			break;
		case "MISURATION_TO_GW":
			result = "Spedisco le misurazioni al Gateway.";
			break;
		case "MSG_ERR_NODE":
			result = "Avviso di nodo scarico al Gateway.";
			break;
		case "ELECTION_TO_FOLLOW":
			result = "Invio al mio successivo della richiesta di elezione di un nuovo sink.";
			break;
		case "MISURATION_TO_SENSOR":
			result = "Contatto i sensori per le misurazioni.";
			break;
		case "INFO_SINK":
			result = "Sono il sink.";
			break;
		case "NEW_SINK":
			result = "Contatto il nuovo sink.";
			break;
		case "NEW_ELECTION":
			result = "Non posso più essere il sink. Elezione di un nuovo sink.";
			break;
		case "NEW_CONNECTION":
			result = "Nuova connessione!";
			break;
		case "CLOSE_SINK":
			result = "Connessione in entrata del sink chiusa. D'ora in poi non comunicherò più con il Gestore.";
			break;
		case "MSG_FAILURE_GW":
			result = "Invio del fallimento al Gestore in corso.";
			break;
		case "ERR_NETWORK":
			result = "Nessun altro nodo con livelli di batteria sufficienti per essere sink. Messaggio di errore di rete al Gestore in corso.";
			break;
		case "NO_NODE":
			result = "Non c'è un sensore con quel nome. Accesso negato.";
			break;
		case "EXIT_NODE":
			result = "Terminazione del nodo.";
			break;
		case "CLOSE_CONNECTION":
			result = "Il server chiude le comunicazioni in entrata.";
			break;
		case "MISURATION_BY_SINK":
			result = "Misurazioni richieste dal sink. Invio in corso.";
			break;
		case "ANALIZE_BATTERY":
			result = "Analizzo le informazioni ricevute dai sensori.";
			break;
		case "NEW_PREVIOUS":
			result = "Il mio nuovo nodo precedente e' " + Nodo.getInstance().Nodo_Precedente + ".";
			break;
		case "NEW_FOLLOW":
			result = "Il mio nuovo nodo successivo e' " + Nodo.getInstance().Nodo_Successivo + ".";
			break;

		}
		return result;
	}
	
	public static String getStringServer(String key, String type, int port){
		String result = "";
		switch (key){
		case "SINK":
			result = "Il sink " + type + " si è messo in ascolto sulla porta " + port + ".";
			break;
		case "SENSOR":
			result = "Il sensore " + type + " si è messo in ascolto sulla porta " + port + ".";
			break;
		}
		
		return result;
	}
	
	public static String getErrConnection(String key){
		return "Connessione impossibile da fare con il nodo " + key + ".";
	}
	
	public static String getStringStartSensor(String key){
		return "Partenza sensore " + key + ".";
	}
}
