package client.resources;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.util.Scanner;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

/**
 * Classe che contiene il main per far partire il client per poter analizzare i dati provenienti
 * dai sensori (usufruendo del Gestore come intermediario).
 * @author Gianpaolo Caprara
 *
 */
public class UserClient { 

	public static void main(String [] args){
		boolean new_user = false;
		String username = "";
		String ip = "";
		int port = 0;
		Response response = null;
		String choice = "0";
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);
		WebTarget webTarget = client.target(getBaseURI());
		Scanner scanner = new Scanner(System.in);
		
		System.out.println(SystemMessage.getString("WELCOME"));
		/*
		 * Fintanto che l'utente inserisce un nome utente già presente all'interno del sistema, lo stesso 
		 * richiede di inserire un nuovo nome utente.
		 */
		while (new_user == false){
			username = scanner.nextLine();
			response = webTarget.path("/manager").path("/login").path("/" + username).request().accept(MediaType.TEXT_PLAIN).get();
			if(response.getStatus() == 200){
				new_user = true;
			} else{
				System.out.println(response.readEntity(String.class));
				System.out.println(SystemMessage.getString("RETRY_USER"));
			}
		}

		System.out.println(SystemMessage.getString("IP"));
		ip = scanner.nextLine();
		while(ip.isEmpty()){
			System.out.println(SystemMessage.getString("RETRY_IP"));
			ip = scanner.nextLine();
		}
		System.out.println(SystemMessage.getString("PORT"));
		port = Integer.parseInt(scanner.nextLine());
		while(port == 0){
			System.out.println(SystemMessage.getString("RETRY_PORT"));
			port = Integer.parseInt(scanner.nextLine());
		}
		User user = new User(username,ip,port);
		response = webTarget.path("/manager").path("/login").request(MediaType.APPLICATION_XML).accept(MediaType.TEXT_PLAIN).post(Entity.xml(user));
		if(response.getStatus() == 400){
			System.out.println(response.readEntity(String.class));
			System.exit(1);
		}

		/*
		 * Fa partire un server di ascolto per i vari messaggi provenienti dal gestore
		 */
		new Thread(new Runnable() {
			public void run() {
				startServerClient( user.getPort() );
			}
		}).start();

		System.out.println(SystemMessage.getString("APP"));
		/*
		 * Corpo principale del client: stampa le varie operazioni disponibili
		 */
		while(Integer.parseInt(choice) != 11){
			System.out.println(SystemMessage.getChoice());
			choice = scanner.nextLine();
			while(Integer.parseInt(choice) > 11 || Integer.parseInt(choice) < 1) {
				System.out.println(SystemMessage.getString("INCORRECT_CHOICE"));
				System.out.println(SystemMessage.getChoice());
				choice = scanner.nextLine();
			}
			runRequest(choice, webTarget, scanner);
		}
		
		/*
		 * Una volta che il client digita "11", il sistema fa partire la procedura di Logout per poter eliminare l'utente dal sistema.
		 * Una volta terminata l'operazione, chiude il client.
		 */
		response = webTarget.path("/manager").path("/logout").path("/" + user.getName()).request().accept(MediaType.TEXT_PLAIN).delete();
		if(response.getStatus() == 200){
			System.out.println(SystemMessage.getString("LOGOUT"));
			System.exit(0);
		} else{
			System.out.println(SystemMessage.getString("ERROR"));
			System.exit(1);
		}

	}

	/*
	 * Fa partire una serverSocket per far tenere in ascolto il client su una porta in maniera tale da
	 * ricevere i vari messaggi dal gestore di errore di terminazione nodo o fallimento rete
	 */
	protected static void startServerClient(int port) {
		ServerSocket serverSocket = null;

		try {
			serverSocket = new ServerSocket(port);
			System.out.println(SystemMessage.getMsgConn(serverSocket.getLocalPort())) ;
			while(true){
				Socket newConnection = serverSocket.accept();
				System.out.println(SystemMessage.getString("NEW_CONNECTION"));

				Thread clientListener = new ClientListener(newConnection);
				clientListener.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
	}

	/*
	 * Funzione che genera un URI che si riferisce all'indirizzo del Gestore (SDP_Gestore)
	 */
	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:80/SDP_Gestore/rest").build();
	}

	/*
	 * Funzione che, a seconda dell'input dell'utente, sceglie la funzione da richiamare per poter processare la richiesta.
	 */
	private static void runRequest(String request, WebTarget webTarget, Scanner scanner){
		String path1 = "";
		String path2 = "";
		RangeOfValues range = new RangeOfValues();

		switch(Integer.parseInt(request)){
		case 1:
			path1 = "/most_recent";
			path2 = "/Temperature";
			buildRequest(webTarget,path1,path2);
			break;
		case 2:	
			path1 = "/most_recent";
			path2 = "/Light";
			buildRequest(webTarget,path1,path2);
			break;
		case 3:
			range = rangeValues(scanner);
			path1 = "/average";
			path2 = "/Temperature_" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 4:
			range = rangeValues(scanner);
			path1 = "/average";
			path2 = "/Light_" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 5:
			range = rangeValues(scanner);
			path1 = "/min_max";
			path2 = "/Temperature_" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 6:
			range = rangeValues(scanner);
			path1 = "/min_max";
			path2 = "/Light_" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 7:
			range = rangeValues(scanner);
			path1 = "/light_Closer_Temp";
			path2 = range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 8:
			range = rangeValues(scanner);
			path1 = "/presence";
			path2 = "/PIR2_" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 9:
			range = rangeValues(scanner);
			path1 = "/presence";
			path2 = "/PIR1_" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		case 10:
			range = rangeValues(scanner);
			path1 = "/total_presence";
			path2 = "/" + range.getRange1() + "_" + range.getRange2();
			buildRequest(webTarget,path1,path2);
			break;
		}
	}

	/*
	 * Funzione che processa una determinata richiesta, creando la response e restituendo il risultato all'utente.
	 * Ogni richiesta ha la stessa struttura per poter essere richiamata. Abbiamo un path fisso (manager) e due path
	 * variabili, che variano a seconda della funzione richiesta dal client al momento della scelta. A seconda del risutato,
	 * che potrebbe essere anche un errore, il sistema restituisce la risposta.
	 */
	private static void buildRequest(WebTarget webTarget, String path1, String path2){
		Response response = null;
		response = webTarget.path("/manager").path(path1).path(path2).request().accept(MediaType.TEXT_PLAIN).get();
		
		if(response.getStatus() == 200){
			System.out.println(response.readEntity(String.class));
		} else{
			System.out.println(response.readEntity(String.class));
		}
	}

	/*
	 * Funzione che richiede all'utente il range di valori entro i quali il sistema deve poter considerare i valori per poter
	 * calcolare il risultato di una determinata richiesta.
	 */
	private static RangeOfValues rangeValues(Scanner scan) {
		String input = "";
		String[] intervallo;
		RangeOfValues range = new RangeOfValues();
		
		System.out.println(SystemMessage.getString("RANGE"));
		input = scan.nextLine();
		while(!input.contains(",") || input.isEmpty()){
			System.out.println(SystemMessage.getString("RANGE_ERR"));
			input = scan.nextLine();
		}
		
		intervallo = input.split(",");
		if(Integer.parseInt(intervallo[0]) < Integer.parseInt(intervallo[1])){
			range.setRange1(Integer.parseInt(intervallo[0]));
			range.setRange2(Integer.parseInt(intervallo[1]));
		} else {
			range.setRange1(Integer.parseInt(intervallo[1]));
			range.setRange2(Integer.parseInt(intervallo[0]));
		}

		return range;
	}
}
