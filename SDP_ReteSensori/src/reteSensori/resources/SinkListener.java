package reteSensori.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Classe che implementa il Thread che permette al Sink di ricevere messaggi
 * dai nodi della rete o dal Gestore. A seconda del typeMessage all'interno
 * del MessageType, il Sink saprà quali operazioni dovrà effettuare. Potrà
 * ricevere solo messaggi di elezione o di inserimento misurazione
 * all'interno della struttura dati.
 * @author Gianpaolo Caprara
 *
 */
public class SinkListener extends Thread{
	private Socket connectionSocket;

	public SinkListener(Socket socket) {
		connectionSocket = socket;
	}

	public void run(){
		try{
			BufferedReader fromNode = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String message = fromNode.readLine();
			MessageType fromNetwork = MessageType.fromString(message);
			//String[] messageSplit = message.split("#");
			//MessageType fromNetwork = new MessageType(messageSplit[0] , messageSplit[1], Long.parseLong(messageSplit[2]));
			switch(fromNetwork.getTypeMessage()){
			case "ELECTION":
				String [] appoggio = fromNetwork.getValue().split(";");
				/*
				 * Verifica se il Sink ha ricevuto delle letture di batterie da altri nodi; se non ci sono bisognerà mandare un messaggio di errore al Gestore
				 * (se ne occupa la funzione electionNode())
				 */
				if (appoggio.length > 0){
					for (int i = 0; i<appoggio.length;i++){
						SinkNode.getInstance().listBattery.add(appoggio[i]);
					}
				}else{
					System.out.println(SystemMessage.getString("ERR_NETWORK"));
				}
				SinkNode.getInstance().electionNode();
				break;
			case "MISURATION":
				MisurationInJSON.getInstance().aggiungi(fromNetwork.getValue());
				break;
			}
			connectionSocket.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}