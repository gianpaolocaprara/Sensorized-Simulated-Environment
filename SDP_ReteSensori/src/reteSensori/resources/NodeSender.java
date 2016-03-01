package reteSensori.resources;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import reteSensori.simulator.BufferImplementation;
import reteSensori.simulator.Misurazione;

/**
 * Classe che implementa il Thread che permette al nodo di inviare messaggi
 * ai nodi della rete (compreso il Sink) o al Gestore. A seconda del typeMessage all'interno
 * del MessageType, il nodo saprà quali operazioni dovrà effettuare.
 * @author Gianpaolo Caprara
 *
 */
public class NodeSender extends Thread {

	private DataOutputStream outToNode;
	private Socket connection = null;
	private List<Misurazione> list = new ArrayList<Misurazione>();
	private MessageType messageFromNetwork = new MessageType();
	
	public NodeSender(Socket socket, MessageType msg) {
		connection = socket;
		messageFromNetwork = msg;
		try {
			outToNode = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run(){
		MessageType messageToSend = new MessageType();
		try {
			switch(messageFromNetwork.getTypeMessage()){
			case "MISURATION":
				System.out.println(SystemMessage.getString("MISURATION_BY_SINK"));
				Nodo.getInstance().decrementBattery(5);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				messageToSend = toJSON();
				outToNode.writeBytes(messageToSend.toString() + "\n");
				break;
			case "NEW_PREVIOUS":
				outToNode.writeBytes(messageFromNetwork + "\n");
				break;
			case "NEW_FOLLOWING":
				outToNode.writeBytes(messageFromNetwork + "\n");
				break;
			case "ELECTION":
				if(Nodo.getInstance().sink == false){
					System.out.println(SystemMessage.getString("ELECTION_TO_FOLLOW"));
					messageToSend.setTypeMessage(messageFromNetwork.getTypeMessage());
					messageToSend.setValue(messageFromNetwork.getValue() + Nodo.getInstance().typologySensor + ":" + Nodo.getInstance().getLevelBattery() + ";");
					messageToSend.setTimestamp(System.currentTimeMillis());
					outToNode.writeBytes(messageToSend.toString() + "\n");
				} else {
					System.out.println(SystemMessage.getString("ANALIZE_BATTERY"));
					outToNode.writeBytes(messageFromNetwork + "\n");
				}
				break;
			case "NETWORK_ERROR":
				MessageType messageErr = new MessageType();
				messageErr.setTypeMessage("NETWORK_ERROR");
				messageErr.setValue("La rete non è più disponibile.");
				messageErr.setTimestamp(System.currentTimeMillis());
				String messageToManager = messageErr.toString();
				outToNode.writeBytes(messageToManager + "\n");
				break;
			}
			
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Metodo che crea un MessageType di tipo "MISURATION"
	 * che contiene come valore le varie misurazioni del sensore in formato JSON.
	 * Tale stringa verrà passata successivamente al Sink che la salverà all'interno
	 * della struttura dati "MisurationInJSON"
	 */
	@SuppressWarnings("unchecked")
	public MessageType toJSON(){
		MessageType msg = new MessageType();
		msg.setTypeMessage("MISURATION");
		msg.setTimestamp(System.currentTimeMillis());
		
		list = BufferImplementation.getInstance().leggi();
		JSONObject listJSON = new JSONObject();
		JSONArray misurations = new JSONArray();
		for (Misurazione m : list) {
			JSONObject misuration = new JSONObject();
			try {
				misuration.put("type", m.getType());
				misuration.put("value", m.getValue());
				misuration.put("timestamp", m.getTimestamp());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			misurations.put(misuration);
		}
		try {
			listJSON.put("listJSONmisuration", misurations);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		msg.setValue(listJSON.toString());
		return msg;
	}
}
