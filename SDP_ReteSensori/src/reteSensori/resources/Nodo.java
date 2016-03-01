package reteSensori.resources;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import reteSensori.simulator.*;

/**
 * Classe che implementa il nodo. A seconda dei parametri passati in input
 * (provenienti dall'inizializzazione della rete di sensori), avvierà
 * il sensore adatto e si metterà in ascolto su una porta specifica, per ricevere
 * messaggi da parte degli altri sensori. Il nodo, all'atto della sua attivazione,
 * terrà traccia del suo nodo successivo e del suo nodo precedente, in maniera tale da
 * poter implementare l'algoritmo ad anello, utile per l'elezione del sink.
 * Quando si accorgerà che il suo livello di batteria è 0, allora avviserà il suo nodo successivo
 * e il suo nodo precedente, all'interno dell'anello, di modificare i riferimenti ai nodi. Il sink
 * che proverà ad effettuare una connessione con il nodo scarico, manderà un messaggio di fallimento
 * nodo.
 * @author Gianpaolo Caprara
 *
 */
public class Nodo{

	protected String typologySensor;
	protected long batteryLevel;
	protected long copyBatteryLevel;
	protected boolean sink;
	protected int frequencyTrasmission;
	private Thread thread;
	private static Nodo instance;
	protected Simulator simulator;
	private ServerSocket serverSocket;
	protected int Nodo_Precedente;
	protected int Nodo_Successivo;

	/* Costruttore per ottenere l'istanza per la prima volta. */
	protected Nodo(){
		typologySensor = "";
		batteryLevel = 0;
		copyBatteryLevel = 0;
		sink = false;
		frequencyTrasmission = 0;
		thread = null;
		simulator = null;
		serverSocket = null;
		Nodo_Precedente = 0;
		Nodo_Successivo = 0;
	}

	/* Otttiene l'istanza del nodo. */
	public synchronized static Nodo getInstance(){
		if(instance==null)
			instance = new Nodo();
		return instance;
	}

	/* A seconda degli argomenti che vengno passati in fase di inizializzazione
	 * il nodo saprà quale sensore deve far partire. */
	public static void main(String[] args) throws IOException  {
		getInstance();

		getInstance().setTypologySensor(args[0]);
		getInstance().setBatteryLevel(Integer.parseInt(args[1]));
		getInstance().setSink(Boolean.parseBoolean(args[2]));
		getInstance().setFrequencyTrasmission(Integer.parseInt(args[3]));

		/* Fa partire il sensore. */
		getInstance().startSensor(getInstance().typologySensor.toLowerCase());
	}

	/* A seconda del tipo di sensore che riceve, fa partire il thread adatto;
	 * successivamente inizializza la porta di ascolto del nodo con un nuovo thread.
	 * Infine, nel caso il nodo è anche sink, fa partire il sink con la funzione startSink()
	 * della classe SinkNode. */
	@SuppressWarnings("unchecked")
	private void startSensor(String s) throws UnknownHostException, IOException{
		List<Integer> socket_port = new ArrayList<Integer>();
		socket_port.add(1111);
		socket_port.add(2222);
		socket_port.add(3333);
		socket_port.add(4444);

		switch (s){
		case "temperature":
			System.out.println(SystemMessage.getStringStartSensor("temperatura"));
			getInstance().simulator = new TemperatureSimulator(BufferImplementation.getInstance());
			getInstance().thread = new Thread(getInstance().simulator);
			new Thread(new Runnable() {
				public void run() {
					getInstance().startServerNode( 1111 );
				}
			}).start();
			getInstance().Nodo_Successivo = 2222;
			getInstance().Nodo_Precedente = 4444;
			if (getInstance().sink == true){
				SinkNode.getInstance().startSink(socket_port);
			}
			break;
		case "light":
			System.out.println(SystemMessage.getStringStartSensor("luminosità"));
			getInstance().simulator = new LightSimulator(BufferImplementation.getInstance());
			getInstance().thread = new Thread(getInstance().simulator);
			new Thread(new Runnable() {
				public void run() {
					getInstance().startServerNode( 2222 );
				}
			}).start();
			getInstance().Nodo_Successivo = 3333;
			getInstance().Nodo_Precedente = 1111;
			if (getInstance().sink == true){
				SinkNode.getInstance().startSink(socket_port);
			}
			break;
		case "pir1":
			System.out.println(SystemMessage.getStringStartSensor("PIR1"));
			getInstance().simulator = new PIR1Simulator(BufferImplementation.getInstance());
			getInstance().thread = new Thread(getInstance().simulator);
			new Thread(new Runnable() {
				public void run() {
					getInstance().startServerNode( 3333 );
				}
			}).start();
			getInstance().Nodo_Successivo = 4444;
			getInstance().Nodo_Precedente = 2222;
			if (getInstance().sink == true){
				SinkNode.getInstance().startSink(socket_port);	
			}
			break;
		case "pir2":
			System.out.println(SystemMessage.getStringStartSensor("PIR2"));
			getInstance().simulator = new PIR2Simulator(BufferImplementation.getInstance());
			getInstance().thread = new Thread(getInstance().simulator);

			new Thread(new Runnable() {
				public void run() {
					getInstance().startServerNode( 4444 );
				}
			}).start();
			getInstance().Nodo_Successivo = 1111;
			getInstance().Nodo_Precedente = 3333;
			if (getInstance().sink == true){
				SinkNode.getInstance().startSink(socket_port);
			}
			break;
		default:
			System.out.println(SystemMessage.getString("NO_NODE"));
		}

		/* Parte il thread del simulatore. */
		getInstance().thread.start();
	}

	/* Funzione che fa partire la porta di ascolto del nodo. Questa porta rimarrà aperta fintanto che
	 * il nodo non abbia batteria sufficiente ( > 0 ) per poter ricevere messaggi. Quando riceve una richiesta
	 * di connessione, il nodo inizializzerà un NodeListener che sarà in grado di ricevere le informazioni, elaborando
	 * la richiesta. Quando la batteria è scarica chiude le comunicazioni in entrata. */
	protected void startServerNode(int i) {
		try {
			getInstance().serverSocket = new ServerSocket(i);
			System.out.println(SystemMessage.getStringServer("SENSOR", typologySensor, serverSocket.getLocalPort()));

			while(getInstance().batteryLevel > 0){
				Socket newConnection = serverSocket.accept();
				System.out.println(SystemMessage.getString("NEW_CONNECTION"));

				Thread nodeListener = new NodeListener(newConnection);
				nodeListener.start();
			}
		} catch (IOException e) {
			System.out.println(SystemMessage.getString("EXIT_NODE"));
		}
	}

	/*
	 * Funzione che contatta i nodi successivo e precedente per permettere di modificare
	 * le loro porte. Verrà chiamata quando il nodo è scarico.
	 */
	public void contactNode(String request, int portToConnect, int portToChange){
		Socket socket = null;
		NodeSender sender = null;
		MessageType message = new MessageType(request,String.valueOf(portToChange),System.currentTimeMillis());
		try {
			socket = new Socket("localhost", portToConnect);
		} catch (IOException e) {
			e.printStackTrace();
		}
		sender = new NodeSender(socket, message);
		sender.start();	
	}

	/*
	 * Funzione che chiude il simulatore associato, avvia la procedura di cambio porte
	 * per il suo nodo precedente e il suo nodo successivo e chiude la socket su cui il sensore
	 * era in ascolto.
	 */
	public synchronized void closeSocket(){
		getInstance().simulator.stopMeGently();

		contactNode("NEW_PREVIOUS",getInstance().Nodo_Successivo,getInstance().Nodo_Precedente);
		contactNode("NEW_FOLLOWING",getInstance().Nodo_Precedente,getInstance().Nodo_Successivo);
		System.out.println(SystemMessage.getString("CLOSE_CONNECTION"));

		try {
			getInstance().serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Funzione che fa partire il sink quando un nodo viene eletto come sink.
	 */
	public void becameSink(List<Integer> values) {
		Nodo.getInstance().sink = true;
		SinkNode.getInstance().startSink(values);
	}


	/* Decrementa il livello di batteria ogni volta che il nodo effettua una operazione: 
	 * - Verrà decrementato di 2 se effettuerà una lettura di misurazione; 
	 * - Verrà decrementato di 5 se effettuerà un invio messaggio ad un altro nodo (o sink); 
	 * - Verrà decrementato di 5*4 quando invierà un messaggio al Gestore (solo nel caso del sink). */
	public synchronized void decrementBattery(int i) {
		getInstance().batteryLevel = getInstance().batteryLevel - i;
	}

	/* Metodi set. */
	public void setTypologySensor(String s){
		getInstance().typologySensor = s;
	}

	public void setBatteryLevel(int i){
		getInstance().batteryLevel = i;
		getInstance().copyBatteryLevel = i;
	}


	public void setSink (boolean b){
		getInstance().sink = b;
	}

	public void setFrequencyTrasmission(int i){
		getInstance().frequencyTrasmission = i;
	}
	
	/* Metodi get. */
	public long getBattery(){
		return getInstance().batteryLevel;
	}

	public int getLevelBattery(){
		return (int)((Nodo.getInstance().batteryLevel * 100) / Nodo.getInstance().copyBatteryLevel);
	}

}