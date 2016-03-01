package reteSensori.resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che implementa il Thread che permette al nodo di ricevere messaggi
 * dai nodi della rete (compreso il Sink) o dal Gestore. A seconda del typeMessage all'interno
 * del MessageType, il nodo saprà quali operazioni dovrà effettuare.
 * @author Gianpaolo Caprara
 *
 */
public class NodeListener extends Thread{
	private Socket connectionSocket;

	public NodeListener(Socket socket) {
		connectionSocket = socket;
	}

	public void run(){
		try{
			BufferedReader fromNode = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String message = fromNode.readLine();
			//String[] messageSplit = message.split("#");
			//MessageType fromNetwork = new MessageType(messageSplit[0] , messageSplit[1], Long.parseLong(messageSplit[2]));
			MessageType fromNetwork = MessageType.fromString(message);
			Socket socket = null;
			NodeSender sender = null;
			switch(fromNetwork.getTypeMessage()){
			case "MISURATION":
				try {
					socket = new Socket("localhost", 5555);
					sender = new NodeSender(socket,fromNetwork);
					sender.start();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "NEW_PREVIOUS":
				Nodo.getInstance().Nodo_Precedente = Integer.parseInt(fromNetwork.getValue());
				System.out.println(SystemMessage.getString("NEW_PREVIOUS"));
				break;
			case "NEW_FOLLOWING":
				Nodo.getInstance().Nodo_Successivo = Integer.parseInt(fromNetwork.getValue());
				System.out.println(SystemMessage.getString("NEW_FOLLOW"));
				break;
			case "ELECTION":
				try {
					if(Nodo.getInstance().sink == false){
						socket = new Socket("localhost", Nodo.getInstance().Nodo_Successivo);
						sender = new NodeSender(socket,fromNetwork);
						sender.start();
					} else {
						socket = new Socket("localhost", 5555);
						sender = new NodeSender(socket,fromNetwork);
						sender.start();
					}
				} catch (UnknownHostException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case "NETWORK_ERROR":
				try{
				socket = new Socket("localhost", 6666);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				sender = new NodeSender(socket,fromNetwork);
				sender.start();
				break;
			case "NEW_SINK":
				String[] appoggio = fromNetwork.getValue().split(",");
				List<Integer> values = new ArrayList<Integer>(); 
				for (int i = 0; i < appoggio.length; i++){
					values.add(Integer.parseInt(appoggio[i]));
				}
				Nodo.getInstance().becameSink(values);
				break;
			}
			connectionSocket.close();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
}