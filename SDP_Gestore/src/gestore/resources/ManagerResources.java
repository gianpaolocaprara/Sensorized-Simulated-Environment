package gestore.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.JAXBElement;

/**
 * Classe che implementa le funzioni che possono essere utilizzate dal client. In pratica
 * è l'Interfaccia REST del Gestore, che accetta le richieste dal client, le elabora, e 
 * restituisce la risposta. Composta da tanti metodi quante sono le funzionalità che
 * il client può usufruire.
 * @author Gianpaolo Caprara
 *
 */
@Path("/manager")
public class ManagerResources {

	/*
	 * Verifica se un utente è presente all'interno della lista di utenti.
	 */
	@GET
	@Path("/login/{name}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response existsUser(@PathParam("name") String name){
		if(Users.getInstance().foundUser(name)){
			System.out.println(SystemMessage.getString("USER_FOUND"));
			return Response.status(Response.Status.FOUND).entity(SystemMessage.getString("USER_FOUND")).build();
		}
		return Response.ok().build();
	}
	
	/*
	 * Login tecnico (metodo Post perchè deve inserire all'interno di una struttura dati i valori dell'utente).
	 */
	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.TEXT_PLAIN)
	public Response loginUser(JAXBElement<User> user){
		System.out.println(SystemMessage.getString("CREATE_USER"));
		if(Users.getInstance().loginUser(user.getValue())){
			System.out.println(SystemMessage.getString("USER_OK"));
			return Response.ok().build();
		}
		System.out.println(SystemMessage.getString("USER_ERR"));
		return Response.status(Response.Status.BAD_REQUEST).entity(SystemMessage.getString("USER_ERR")).build();
	}
	
	/*
	 * Logout tecnico (metodo Delete perchè l'utente deve essere eliminato dal sistema).
	 */
	@DELETE
	@Path("/logout/{user}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response deleteUser(@PathParam("user") String user) {
		Users.getInstance().logoutUser(user);
		System.out.println(SystemMessage.getString("LOGOUT"));
			return Response.ok().build();
	}
	
	
	/*
	 * Funzione che richiama il metodo per ricavare il valore più recente (temperatura o luminosità).
	 */
	@GET
	@Path("/most_recent/{type}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response recentValue(@PathParam("type") String sensor_type){
		Misurazione result;
		String response ="";

		result = Users.getInstance().foundMostRecent(sensor_type);
		
		if(result.getTimestamp() != 0){
			response = "Il valore più recente di " + result.getType() + " è: " + result.getValue() + ".Il suo timestamp è: " + result.getTimestamp();
			return Response.ok(response).build();
		}
			response = "Non c'è un valore di " + result.getType();
			return Response.status(Status.NOT_FOUND).entity(response).build();
	}
	
	/*
	 * Funzione che richiama il metodo per ricavare la media dei valori (temperatura o luminosità).
	 */
	@GET
	@Path("/average/{request}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response averageValue(@PathParam("request") String request){
		String[] requests = request.split("_");
		RangeOfValues range = new RangeOfValues(Integer.parseInt(requests[1]),Integer.parseInt(requests[2]));
		double average = 0;
		String response ="";
	
		average = Users.getInstance().foundAverage(requests[0], range);

		if(average != 0){
			response = "La media di " + requests[0] + " tra gli istanti di tempo " + range.getRange1() + " e " + range.getRange2() +
					" è: " + average;
			return Response.ok(response).build();
		}
			response = "Non c'è una media tra gli istanti di tempo " + range.getRange1() + " e " + range.getRange2();
			return Response.status(Status.NOT_FOUND).entity(response).build();
	}
	
	/*
	 * Funzione che richiama il metodo per ricavare i valori massimi e minimi (temperatura o luminosità).
	 */
	@GET
	@Path("/min_max/{request}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response minAndMaxValue(@PathParam("request") String request){
		String[] requests = request.split("_");
		RangeOfValues range = new RangeOfValues(Integer.parseInt(requests[1]),Integer.parseInt(requests[2]));
		String values = "";
		String valuesArray[] = null;
		
		String response ="";
		
		values = Users.getInstance().foundMinMax(requests[0], range);
		valuesArray = values.split(",");
		
		if(Double.parseDouble(valuesArray[0]) != 0 || Double.parseDouble(valuesArray[1]) != 0){
			response = "Il massimo di  " + requests[0] + " è: " + valuesArray[1] +".\nIl minimo di  " + requests[0] + " è:" + valuesArray[0] + ".";

			return Response.ok(response).build();
		}
			response = "Non ci sono valori tra gli istanti di tempo " + range.getRange1() + " e " + range.getRange2();
			return Response.status(Status.NOT_FOUND).entity(response).build();
	}
	
	/*
	 * Funzione che richiama il metodo per ricavare la luminosità più vicina al massimo
	 * della temperatura in due istanti di tempo
	 */
	@GET
	@Path("/light_Closer_Temp/{request}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response lightCloserTemp(@PathParam("request") String request){
		String[] requests = request.split("_");
		RangeOfValues range = new RangeOfValues(Integer.parseInt(requests[0]),Integer.parseInt(requests[1]));
		String values;
		String response ="";
				
		values = Users.getInstance().foundLightTemp(range);

		if(values != null){
			response = "La luminosità più vicina al massimo della temperatura riscontrata tra i due intervalli scelti e': " + values + ".";

			return Response.ok(response).build();
		}
			response = "Non c'è una luminosità calcolata tra l'intervallo " + range.getRange1() + " e " + range.getRange2();
			return Response.ok(response).build();
	}
	
	/*
	 * Funzione che richiama il metodo per ricavare il numero di presenze all'interno di una parte della stanza (PIR1 o PIR2).
	 */
	@GET
	@Path("/presence/{request}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response presenceValue(@PathParam("request") String request){
		String[] requests = request.split("_");
		RangeOfValues range = new RangeOfValues(Integer.parseInt(requests[1]),Integer.parseInt(requests[2]));
		int values;
		
		String response ="";
		
		values = Users.getInstance().foundPresence(requests[0], range);

		if(values != 0){
			response = "Il numero di presenze del tipo " + requests[0] + " è: " + values + ".";

			return Response.ok(response).build();
		}
			response = "Non ci sono presenze tra gli istanti di tempo " + range.getRange1() + " e " + range.getRange2();
			return Response.ok(response).build();
	}
	
	/*
	 * Funzione che richiama il metodo per ricavare il numero di presenze totali (in media) all'interno della stanza complessiva.
	 */
	@GET
	@Path("/total_presence/{request}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response presenceTotalValue(@PathParam("request") String request){
		String[] requests = request.split("_");
		RangeOfValues range = new RangeOfValues(Integer.parseInt(requests[0]),Integer.parseInt(requests[1]));
		long values;
		String response ="";
		
		values = Users.getInstance().foundTotalPresence(range);

		if(values != 0){
			response = "Il numero di presenze complessive (in media) è: " + values + ".";

			return Response.ok(response).build();
		}
			response = "Non ci sono presenze tra gli istanti di tempo " + range.getRange1() + " e " + range.getRange2();
			return Response.ok(response).build();
	}
	
}
