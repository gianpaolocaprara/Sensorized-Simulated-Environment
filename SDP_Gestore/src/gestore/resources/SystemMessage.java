package gestore.resources;

/**
 * Classe che restituisce i vari messaggi di sistema da stampare a video (per il Gestore).
 * @author Gianpaolo Caprara
 *
 */
public class SystemMessage {

	public static String getString(String key){
		String result = "";
		switch (key){
		case "START_GW":
			result = "Avvio server del Gestore.";
			break;
		case "NODE_FAILURE":
			result = "Sto avvisando che uno dei nodi è scarico a tutti gli utenti connessi al Gestore in questo momento.";
			break;
		case "NETWORK_ERROR":
			result = "Sto avvisando che la rete non è più disponibile.";
			break;
		case "NEW_CONNECTION":
			result = "Nuova connessione!";
			break;
		case "CREATE_USER":
			result = "Creazione utente in corso.";
			break;
		case "USER_OK":
			result = "Utente creato con successo.";
			break;
		case "USER_ERR":
			result = "Errore durante la creazione dell'utente nel sistema.";
			break;
		case "USER_FOUND":
			result = "Utente già presente nel sistema.";
			break;
		case "LOGOUT":
			result = "Logout effettuato.";
			break;
		}
		return result;
	}
}

