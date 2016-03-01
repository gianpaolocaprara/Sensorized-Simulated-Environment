package client.resources;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Thread di ascolto del Client. Rimane in ascolto per stampare messaggi di errore
 * di nodo scarico o rete non più disponibile.
 * @author Gianpaolo Caprara
 *
 */
public class ClientListener extends Thread {
	private Socket connectionSocket;

	public ClientListener(Socket socket) {
		connectionSocket = socket;
	}
	
	public void run(){
		try {
			BufferedReader fromManager = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			String message = fromManager.readLine();
			System.out.println(message);
			connectionSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
