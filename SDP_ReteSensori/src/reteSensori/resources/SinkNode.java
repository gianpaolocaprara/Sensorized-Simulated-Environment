package reteSensori.resources;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe che implementa il Sink. Estende le funzionalità del Nodo.
 * Il Sink si metterà in ascolto su una porta specifica (5555) per ricevere
 * le misurazioni da parte degli altri sensori (comprese le sue). Quando
 * si accorgerà che il suo livello di batteria è < del 25% allora avvierà
 * la procedura di elezione di un nuovo sink. Tale procedura è effettuata tramite un algoritmo
 * ad anello.
 * @author Gianpaolo Caprara
 *
 */
public class SinkNode extends Nodo{

	@SuppressWarnings("rawtypes")
	protected List listSocket; //lista delle socket attive al momento
	private Socket socket;
	public List<String> listBattery; //lista delle varie batterie dei sensori (utilizzata durante l'elezione)
	private Thread thread;
	private static SinkNode instance;
	private ServerSocket serverSocket;
	
	/* Costruttore per ottenere l'istanza. */
	@SuppressWarnings("rawtypes")
	private SinkNode(){
		listBattery = new ArrayList<String>();
		listSocket = new ArrayList();
		socket = null;
		thread = new Thread(new Runnable() {
			public void run() {
				
				/* Aspetta un tempo frequency trasmission prima di effettuare la prima richiesta delle misurazioni. */
				try {
					Thread.sleep(Nodo.getInstance().frequencyTrasmission);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				/* Fintanto che il suo livello di batteria è > 25%, richiede periodicamente le misurazioni ai vari sensori
				 * per poi spedire il tutto al Gestore(compreso le sue misurazioni). */
				while(Nodo.getInstance().getLevelBattery() >= 25){
					try {
						MessageType message = new MessageType("MISURATION","",System.currentTimeMillis());
						for(int i = 0; i < getInstance().listSocket.size(); i++){
							try {
								getInstance().socket = new Socket("localhost", Integer.parseInt(getInstance().listSocket.get(i).toString()));
								SinkSender sender = new SinkSender(getInstance().socket, message);
								sender.start();
							} catch (UnknownHostException e) {
								getInstance().removeSocketFromList(i,getInstance().listSocket.get(i).toString());
							} catch (IOException e) {
								getInstance().removeSocketFromList(i,getInstance().listSocket.get(i).toString());
							}			
						}
						//qui dovrebbe richiamare la funzione per mandare le misurazioni al Gestore
						getInstance().sendToGateway();
						if(Nodo.getInstance().getLevelBattery() < 25){
							break;
						}
						Thread.sleep(Nodo.getInstance().frequencyTrasmission);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				//una volta che il sink raggiunge meno del 25% deve far partire l'elezione del nuovo sink
				System.out.println(SystemMessage.getString("NEW_ELECTION"));
				getInstance().startSinkElection();
			}

		});
	}

	/* Otttiene l'istanza del sink. */
	public synchronized static SinkNode getInstance(){
		if(instance==null)
			instance = new SinkNode();
		return instance;
	}

	/* Funzione che fa partire il sink. Si è scelto per facilità d'implementazione di tenere traccia delle socket di tutti
	 * e 4 i sensori (compreso il sink stesso). Pertanto ogni messaggio che sarà inviato sarà inviato anche al sink stesso. */
	public void startSink(List<Integer> ports){
		System.out.println(SystemMessage.getString("INFO_SINK"));
		getInstance();

		getInstance().addInListSocket(ports);
		new Thread(new Runnable() {
			public void run() {
				getInstance().startServerSink( 5555 );
			}
		}).start();

		getInstance().thread.start();
	}

	/* Funzione che fa partire la porta di ascolto del sink. Questa porta rimarrà aperta fintanto che
	 * il nodo sia ancora un sink, in maniera tale da ricevere messaggi. Quando riceve una richiesta
	 * di connessione, il sink inizializzerà un SinkListener che sarà in grado di ricevere le informazioni, elaborando
	 * la richiesta. Quando non è più sink, chiude le connessioni in entrata. */
	protected void startServerSink(int i) {
		try {
			serverSocket = new ServerSocket(i);
			System.out.println(SystemMessage.getStringServer("SINK", typologySensor, serverSocket.getLocalPort()));

			while(Nodo.getInstance().sink == true){
				Socket newConnection = serverSocket.accept();
				System.out.println(SystemMessage.getString("NEW_CONNECTION"));

				Thread sinkListener = new SinkListener(newConnection);
				sinkListener.start();
			}
		} catch (IOException e) {
			System.out.println(SystemMessage.getString("CLOSE_SINK"));
		}
	}

	/*
	 * Metodo che fa partire l'elezione di un nuovo sink, inviando un messaggio al suo successivo.
	 */
	private void startSinkElection(){
		getInstance().messageBatteryToFollower();
	}

	/*
	 * Metodo che invia un messaggio di tipo "ELECTION" al suo nodo successivo in maniera tale da
	 * chiedere il suo livello di batteria; tale messaggio verrà poi passato tra i vari nodi della
	 * rete e ritornerà al Sink che analizzerà i vari livelli di batterie ricevute.
	 */
	private void messageBatteryToFollower(){
		MessageType message = new MessageType("ELECTION","",System.currentTimeMillis());
		
		try {
			getInstance().socket = new Socket("localhost", Nodo.getInstance().Nodo_Successivo);
		} catch (IOException e) {
			e.printStackTrace();
		}

		SinkSender sender = new SinkSender(getInstance().socket, message);
		sender.start();
	}
	
	/*
	 * Metodo che elegge un nuovo sink, se possibile. In caso contrario dovrà incaricare il nodo con livello di batteria maggiore di
	 * avvisare il Gestore di fallimento della rete.
	 */
	protected void electionNode(){

		ArrayList<String[]> battery = new ArrayList<String[]>();
		int maxBattery = Nodo.getInstance().getLevelBattery();
		String maxSensorBattery = Nodo.getInstance().typologySensor;
		SinkSender sender = null;

		/*
		 * Verifica se ci sono nodi con i loro livelli di batteria, altrimenti verrà mandato il messaggio di errore al gestore
		 */
		if(getInstance().listBattery.size() != 0){
			for (int i = 0 ; i < getInstance().listBattery.size(); i++){
				String[] appoggio = getInstance().listBattery.get(i).split(":");
				battery.add(appoggio);
			}

			/*
			 * Analizza i vari livelli di batteria e prende in considerazione il sensore con la batteria più alta,
			 * salvandola in due variabili.
			 */
			for (int k = 0 ; k < battery.size(); k++){
				if(Integer.parseInt(battery.get(k)[1]) > maxBattery){
					maxSensorBattery = battery.get(k)[0];
					maxBattery = Integer.parseInt(battery.get(k)[1]);
				}
			}
		}
		
		/*
		 * Se la batteria più alta è maggiore di 25, allora verrà incaricato il sensore associato di diventare il nuovo sink
		 * attraverso un nuovo messaggio di tipo "NEW_SINK"
		 */
		if(maxBattery > 25){
			MessageType message = new MessageType();
			message.setTypeMessage("NEW_SINK");
			message.setTimestamp(System.currentTimeMillis());
			String appoggio = "";
			for (int j= 0; j < getInstance().listSocket.size(); j++){
				appoggio += listSocket.get(j) + ",";
			}
			message.setValue(appoggio);
			
			switch(maxSensorBattery.toLowerCase()){
			case "temperature":
				try {
					getInstance().socket = new Socket("localhost", 1111);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message);
				sender.start();
				break;
			case "light":
				try {
					getInstance().socket = new Socket("localhost", 2222);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message);
				sender.start();
				break;
			case "pir1":
				try {
					getInstance().socket = new Socket("localhost", 3333);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message);
				sender.start();
				break;
			case "pir2":
				try {
					getInstance().socket = new Socket("localhost", 4444);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message);
				sender.start();
				break;
			}
			//pone il suo valore booleano sink a false
			Nodo.getInstance().sink = false;
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else { 
			//nel caso in cui non c'è possibilità di eleggere un nuovo sink, dovrà essere
			//mandato un messaggio di errore di rete
			MessageType message_err = new MessageType();
			message_err.setTypeMessage("NETWORK_ERROR");
			message_err.setValue("Reti di sensore non disponibile.");
			message_err.setTimestamp(System.currentTimeMillis());
			switch(maxSensorBattery.toLowerCase()){
			case "temperature":
				try {
					getInstance().socket = new Socket("localhost", 1111);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message_err);
				sender.start();
				break;
			case "light":
				try {
					getInstance().socket = new Socket("localhost", 2222);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message_err);
				sender.start();
				break;
			case "pir1":
				try {
					getInstance().socket = new Socket("localhost", 3333);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message_err);
				sender.start();
				break;
			case "pir2":
				try {
					getInstance().socket = new Socket("localhost", 4444);
				} catch (IOException e) {
					e.printStackTrace();
				}
				sender = new SinkSender(getInstance().socket, message_err);
				sender.start();
				break;
			}
			//pone il suo valore booleano sink a false
			Nodo.getInstance().sink = false;
		}
	}


	/*
	 * Metodo che quando un nodo fallisce, elimina dalla lista il nodo associato in maniera tale da non contattarlo più;
	 * invia successivamente al gestore l'avviso che il nodo è fallito
	 */
	protected synchronized void removeSocketFromList(int i, String port) {
		MessageType message = new MessageType("NODE_FAILURE","",System.currentTimeMillis());
		switch(port){
		case "1111":
			System.out.println(SystemMessage.getErrConnection("temperatura"));
			getInstance().listSocket.remove(i);
			message.setValue("Temperature");
			break;
		case "2222":
			System.out.println(SystemMessage.getErrConnection("luce"));
			getInstance().listSocket.remove(i);
			message.setValue("Light");
			break;
		case "3333":
			System.out.println(SystemMessage.getErrConnection("PIR1"));
			getInstance().listSocket.remove(i);
			message.setValue("PIR1");
			break;
		case "4444":
			System.out.println(SystemMessage.getErrConnection("PIR2"));
			getInstance().listSocket.remove(i);
			message.setValue("PIR2");
			break;
		}

		try {
			getInstance().socket = new Socket("localhost", 6666);
			SinkSender sender = new SinkSender(getInstance().socket, message);
			System.out.println(SystemMessage.getString("MSG_FAILURE_GW"));
			sender.start();	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Metodo che si connette con il Gateway per mandargli le misurazioni dei vari nodi (compreso le sue)
	 */
	protected void sendToGateway() {
		MessageType message = new MessageType("MISURATIONS","",System.currentTimeMillis());
		try {
			getInstance().socket = new Socket("localhost", 6666);
			SinkSender sender = new SinkSender(getInstance().socket, message);
			sender.start();			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/* Aggiunge le socket all'interno di una lista per poter mandare i vari messaggi. */
	@SuppressWarnings("unchecked")
	private void addInListSocket(List<Integer> values) {
		for (int i = 0; i < values.size(); i++){
			listSocket.add(values.get(i));
		}
	}
}