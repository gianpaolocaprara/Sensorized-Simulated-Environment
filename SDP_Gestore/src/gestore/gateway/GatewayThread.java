package gestore.gateway;

import gestore.resources.SystemMessage;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server multithread del Gestore. Rimane in ascolto per accogliere eventuali richieste di connessione dal Sink
 * (o dal nodo con batteria più alta, nel caso in cui la Rete di Sensori non fosse più disponibile).
 * @author Gianpaolo Caprara
 *
 */
public class GatewayThread implements Runnable {

	@Override
	public void run() {
		ServerSocket welcomeSocket;
		try {
			welcomeSocket = new ServerSocket(6666);
			//si mette in ascolto fintanto che non arriva una richiesta da un client
			while(true) {   
				//accetta la connessione proveniente dal sink stabilisce una socket che verrà passata ad un Thread
				Socket connectionSocket = welcomeSocket.accept(); 

				System.out.println(SystemMessage.getString("NEW_CONNECTION"));
				// Creazione di un thread e passaggio della established socket
				Thread gatewayListener =  new GatewayListener(connectionSocket);

				// Avvio del thread
				gatewayListener.start();
			} 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
