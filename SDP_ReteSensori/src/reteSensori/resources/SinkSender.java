package reteSensori.resources;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

/**
 * Classe che implementa il Thread che permette al Sink di inviare messaggi
 * ai nodi della rete o al Gestore. A seconda del typeMessage all'interno
 * del MessageType, il Sink saprà quali operazioni dovrà effettuare.
 * @author Gianpaolo Caprara
 *
 */
public class SinkSender extends Thread {

	private DataOutputStream outToNode;
	private Socket connection = null;
	private MessageType messageFromNetwork = new MessageType();


	public SinkSender(Socket socket, MessageType msg) {
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
			case "MISURATIONS":
				Nodo.getInstance().decrementBattery(5*4);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				System.out.println(SystemMessage.getString("MISURATION_TO_GW"));
				messageToSend = toJSONString();
				outToNode.writeBytes(messageToSend.toString() + "\n");
				break;
			case "NODE_FAILURE":
				Nodo.getInstance().decrementBattery(5*4);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				System.out.println(SystemMessage.getString("MSG_ERR_NODE"));
				messageToSend.setTypeMessage("NODE_FAILURE");
				messageToSend.setValue("Il nodo " + messageFromNetwork.getValue() + " non è più disponibile.");
				messageToSend.setTimestamp(System.currentTimeMillis());
				outToNode.writeBytes(messageToSend.toString() + "\n");
				break;
			case "ELECTION":
				Nodo.getInstance().decrementBattery(5);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				System.out.println(SystemMessage.getString("ELECTION_TO_FOLLOW"));
				outToNode.writeBytes(messageFromNetwork.toString() + "\n");
				break;
			case "MISURATION":
				Nodo.getInstance().decrementBattery(5);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				System.out.println(SystemMessage.getString("MISURATION_TO_SENSOR"));
				outToNode.writeBytes(messageFromNetwork.toString() + "\n");
				break;
			case "NEW_SINK":
				Nodo.getInstance().decrementBattery(5);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				System.out.println(SystemMessage.getString("NEW_SINK"));
				outToNode.writeBytes(messageFromNetwork.toString() + "\n");
				break;
			case "NETWORK_ERROR":
				Nodo.getInstance().decrementBattery(5);
				System.out.println(SystemMessage.getString("LEVEL_BATTERY"));
				System.out.println(SystemMessage.getString("ERR_NETWORK"));
				outToNode.writeBytes(messageFromNetwork.toString() + "\n");
				break;
			}
			
			connection.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Costruisce un messageType di tipo "MISURATIONS" 
	 * che ha come valore tutte le misurazioni dei vari sensori da passare al Gestore.
	 */
	public MessageType toJSONString(){
		MessageType msg = new MessageType();
		msg.setTypeMessage("MISURATIONS");
		List<String> list = MisurationInJSON.getInstance().leggi();
		for(int k = 0; k < list.size(); k++){
			if(k == 0){
				msg.setValue(list.get(k));
			} else {
				String otherValue = msg.getValue();
				msg.setValue(otherValue + ";" + list.get(k)); 
			}
		}
		msg.setTimestamp(System.currentTimeMillis());

		return msg;
	}
}