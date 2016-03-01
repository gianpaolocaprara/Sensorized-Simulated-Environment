package client.resources;

/**
 * Classe che restituisce i vari messaggi di sistema da stampare a video (per il client).
 * @author Gianpaolo Caprara
 *
 */
public class SystemMessage {

	public static String getString(String key){
		String result = "";
		switch (key){
		case "WELCOME":
			result = "Benvenuto. Prego, inserisci il tuo nome utente: ";
			break;
		case "RETRY_USER":
			result = "Prego, inserisci un altro nome utente: ";
			break;
		case "RETRY_IP":
			result = "Ip non corretto. Prego, inserisci un altro ip: ";
			break;
		case "RETRY_PORT":
			result = "Porta non corretta. Prego, inserisci un altra porta: ";
			break;
		case "IP":
			result = "Inserisci il tuo IP: ";
			break;
		case "PORT":
			result = "Inserisci la tua porta: ";
			break;
		case "APP":
			result = "Benvenuto nel Resource Manager.";
			break;
		case "INCORRECT_CHOICE":
			result = "Scelta non corretta. Prego, inserisci un'altra scelta: ";
			break;
		case "ERROR":
			result = "Errore improvviso durante il logout.";
			break;
		case "LOGOUT":
			result = "Logout effettuato";
			break;
		case "NEW_CONNECTION":
			result = "Nuova connessione proveniente dal Gestore.";
			break;
		case "RANGE":
			result = "Inserisci i due istanti di tempo seprati da una virgola(,): ";
			break;
		case "RANGE_ERR":
			result = "Intervallo di tempo sbagliato. Riprovare: ";
			break;
		}
		return result;
	}
	
	public static String getMsgConn(int port){
		return "Il client si è messo in ascolto sulla porta " + port;
	}

	public static String getChoice() {
		return  "**********************************************************************\n"
				+ "Scegli una delle seguenti operazioni dalla lista:\n"
				+ "1: Ottieni la temperatura più recente con il suo timestamp\n"
				+ "2: Ottieni la luminosità più recente con il suo timestamp\n"
				+ "3: Ottieni una media delle temperature tra due istanti di tempo\n"
				+ "4: Ottieni una media delle luminosità tra due istanti di tempo\n"
				+ "5: Ottieni la minima e la massima temperatura tra due istanti di tempo\n"
				+ "6: Ottieni la minima e la massima luminosità tra due istanti di tempo\n"
				+ "7: Ottieni la luminosità nell'istante di tempo più vicino in cui la temperatura ha segnato il suo massimo tra due istanti di tempo\n"
				+ "8: Ottieni quante volte è stata rilevata una presenza nella zona ovest della stanza tra due istanti di tempo\n"
				+ "9: Ottieni quante volte è stata rilevata una presenza nella zona est della stanza tra due istanti di tempo\n"
				+ "10: Ottieni quante volte (in media) è stata rilevata una presenza nella stanza tra due istanti di tempo\n"
				+ "11: Logout e chiudi il Resource Manager\n"
				+ "**********************************************************************";
	}
}
