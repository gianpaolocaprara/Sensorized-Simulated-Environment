package gestore.gateway;

import gestore.resources.ListMisurations;
import gestore.resources.MessageType;
import gestore.resources.Misurazione;
import gestore.resources.SystemMessage;
import gestore.resources.Users;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Thread di ascolto del Gestore. Può accogliere le misurazioni dal Sink
 * (facendo un parsing per trasformare la stringa JSON in un ArrayList di Misurazione,
 * o per eventuali messaggi di errore di nodo scarico o rete non più disponibile.
 * In quest'ultimo caso verranno mandati ai vari client attraverso l'utilizzo del Gateway Sender.
 * @author Gianpaolo Caprara
 *
 */
public class GatewayListener extends Thread {
	private Socket connectionSocket;

	public GatewayListener(Socket socket) {
		connectionSocket = socket;
	}

	public void run(){
		try{
			BufferedReader fromSink = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String message = fromSink.readLine();
			MessageType fromNetwork = MessageType.fromString(message);
			//String[] messageSplit = message.split("#");
			//MessageType fromNetwork = new MessageType(messageSplit[0] , messageSplit[1], Long.parseLong(messageSplit[2]));
			switch (fromNetwork.getTypeMessage()){
			case "MISURATIONS":
				fromJSON(fromNetwork.getValue());
				break;
			case "NODE_FAILURE":
				System.out.println(SystemMessage.getString("NODE_FAILURE"));
				sendErrToUser(fromNetwork.getValue());
				break;
			case "NETWORK_ERROR":
				System.out.println(SystemMessage.getString("NETWORK_ERROR"));
				sendErrToUser(fromNetwork.getValue());
				break;
			}
			connectionSocket.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * Funzione che avvisa tutti gli utenti di nodo scarico o di rete non disponibile.
	 */
	public void sendErrToUser(String msg){
		Socket socket = null;
		GatewaySender sender = null;
		for (int i = 0; i < Users.getInstance().users.size() ; i++){
			try {
				socket = new Socket(Users.getInstance().users.get(i).getIp(),Users.getInstance().users.get(i).getPort());
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			sender = new GatewaySender(socket,msg);
			sender.start();
		}
	}
	
	/*
	 * Funzione che trasforma la stringa che contiene tutte le misurazioni in JSON e le salva all'interno
	 * della ListMisurations che contiene tutte le misurazioni dei sensori.
	 */
	public void fromJSON(String JSONstring){
		String[] singleMisurationJSON = JSONstring.split(";");
		for (int k = 0; k < singleMisurationJSON.length; k++){
			System.out.println(singleMisurationJSON[k]);
			try {
				ArrayList<Misurazione> misurations = new ArrayList<Misurazione>();
				JSONObject input = new JSONObject(singleMisurationJSON[k]);
				JSONArray array;
				array = input.getJSONArray("listJSONmisuration");
				for (int i=0; i<array.length(); i++){
					JSONObject current = array.getJSONObject(i);
					String type = current.getString("type");
					String value = current.getString("value");
					long timestamp = current.getLong("timestamp");
					Misurazione m = new Misurazione(type,value,timestamp);
					misurations.add(m);
				}
				ListMisurations.getInstance().aggiungi(misurations.get(0).getType(), misurations);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}	
	}
}
