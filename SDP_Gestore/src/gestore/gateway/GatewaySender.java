package gestore.gateway;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Thread di invio del Gestore. Invia ai vari client connessi al sistema
 * i messaggi di errore provenienti dalla Rete di Sensori.
 * @author Gianpaolo Caprara
 *
 */
public class GatewaySender extends Thread{

	private Socket connection;
	private DataOutputStream outToClient;
	private String message;

	public GatewaySender(Socket socket, String msg) {
		connection = socket;
		message = msg;
		try {
			outToClient = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Spedisce i messaggi di errore ai client del sistema
	 */
	public void run(){
		try {
			outToClient.writeBytes(message);
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
