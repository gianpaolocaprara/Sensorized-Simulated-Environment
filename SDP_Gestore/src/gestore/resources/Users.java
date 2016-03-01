package gestore.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe che definisce come devono essere gestiti l'insieme degli utenti.
 * Contiene anche le funzioni per elaborare le richieste provenienti dal client
 * (ed intercettate dall'Interfaccia REST).
 * @author Gianpaolo Caprara
 *
 */
@XmlRootElement(name="users")
@XmlAccessorType (XmlAccessType.FIELD)
public class Users {
	
	@XmlElement (name = "user")
	public List<User> users;
	private static Users instance;

	protected Users(){
		users = new ArrayList<User>();
	}
	
	/* Otttiene l'istanza della lista utenti. */
	public synchronized static Users getInstance(){
		if(instance==null)
			instance = new Users();
		return instance;
	}
	
	//effettua il login dell'utente
	public synchronized boolean loginUser(User user){
		for (int i = 0 ; i < users.size(); i++){
			if (users.get(i).getName().toLowerCase().equals(user.getName().toLowerCase()))
				return false;
		}
		users.add(user);
		return true;
	}
	
	//effettua il logout dell'utente
	public synchronized void logoutUser(String user){
		for (int i = 0; i < users.size(); i++){
			if(users.get(i).getName().toLowerCase().equals(user.toLowerCase())){
				users.remove(i);
				break;
			}
		}
	}
	
	//verifica se è già presente un utente con quel nome all'interno della struttura dati
	public synchronized boolean foundUser(String name){
		for (int i = 0; i < users.size() ; i++){
			if (users.get(i).getName().toLowerCase().equals(name.toLowerCase())){
				return true;
			}			
		}
		return false;
	}
	
	//trova la misurazione più recente, in base al tipo passato in input
	public Misurazione foundMostRecent(String type){
		ArrayList<Misurazione> list = new ArrayList<Misurazione>();
		Misurazione appoggio = new Misurazione(type,"appoggio", 0);
		
		Map<String,ArrayList<Misurazione>> listMisurations = ListMisurations.getInstance().leggi();
		list = listMisurations.get(type);
		//list = ListMisurations.getInstance().misurations.get(type);
		
		for (int i = 0; i < list.size(); i++){
			if(list.get(i).getTimestamp() > appoggio.getTimestamp()){
				appoggio.setType(list.get(i).getType());
				appoggio.setValue(list.get(i).getValue());
				appoggio.setTimestamp(list.get(i).getTimestamp());
			}
		}
		return appoggio;

	}

	//trova la media di un tipo di sensore in un range di tempo, dati entrambi in input
	public double foundAverage(String type, RangeOfValues value) {
		ArrayList<Misurazione> list = new ArrayList<Misurazione>();
		double count = 0;
		int numberMisuration = 0;
		Map<String,ArrayList<Misurazione>> listMisurations = ListMisurations.getInstance().leggi();
		list = listMisurations.get(type);
		//list = ListMisurations.getInstance().misurations.get(type);
		
		for(int i = 0; i < list.size() ; i ++){
			if(list.get(i).getTimestamp() >= value.getRange1() && list.get(i).getTimestamp() <= value.getRange2()){
				count = count +Double.parseDouble(list.get(i).getValue());
				numberMisuration++;
			}		
		}
		
		if(numberMisuration == 0){
			return 0;
		}
		
		return (count/numberMisuration);
	}

	//trova il minimo e il massimo valore
	//di un tipo di sensore in un range di tempo, dati entrambi in input
	public String foundMinMax(String type, RangeOfValues value) {
		ArrayList<Misurazione> list = new ArrayList<Misurazione>();
		String result = "";
		double min = 0;
		double max = 0;
		Map<String,ArrayList<Misurazione>> listMisurations = ListMisurations.getInstance().leggi();
		list = listMisurations.get(type);
		//list = ListMisurations.getInstance().misurations.get(type);
		
		for(int i = 0; i < list.size() ; i ++){
			if(list.get(i).getTimestamp() >= value.getRange1() && list.get(i).getTimestamp() <= value.getRange2()){
				if(i == 0){
					min = Double.parseDouble(list.get(i).getValue());
					max = Double.parseDouble(list.get(i).getValue());
				} else  {
					if(Double.parseDouble(list.get(i).getValue()) < min){
						min = Double.parseDouble(list.get(i).getValue());
					} else {
						if(Double.parseDouble(list.get(i).getValue()) > max){
							max = Double.parseDouble(list.get(i).getValue());
						}
					}
				}
			}		
		}
		
		result = min + "," + max;
		return result;
	}

	//trova il numero di presenza di un tipo di sensore in un range di tempo, dati entrambi in input
	public int foundPresence(String type, RangeOfValues value) {
		ArrayList<Misurazione> list = new ArrayList<Misurazione>();
		int count = 0;
		Map<String,ArrayList<Misurazione>> listMisurations = ListMisurations.getInstance().leggi();
		list = listMisurations.get(type);
		//list = ListMisurations.getInstance().misurations.get(type);
		
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).getTimestamp() >= value.getRange1() && list.get(i).getTimestamp() <= value.getRange2()){
					count++;
			}
		}
		return count;
	}

	//trova il numero totale di presenze nella stanza (in media) in un range di tempo, data in input
	public long foundTotalPresence(RangeOfValues value) {
		ArrayList<Misurazione> list_PIR1 = new ArrayList<Misurazione>();
		ArrayList<Misurazione> list_PIR2 = new ArrayList<Misurazione>();
		int total_presence = 0;
		Map<String,ArrayList<Misurazione>> listMisurations = ListMisurations.getInstance().leggi();
		list_PIR1 = listMisurations.get("PIR1");
		list_PIR2 = listMisurations.get("PIR2");
		//list_PIR1 = ListMisurations.getInstance().misurations.get("PIR1");
		//list_PIR2 = ListMisurations.getInstance().misurations.get("PIR2");

		for(int i = 0; i < list_PIR1.size(); i++){
			if(list_PIR1.get(i).getTimestamp() >= value.getRange1() && list_PIR1.get(i).getTimestamp() <= value.getRange2()){
					total_presence++;		
			}
		}
		
		for(int k = 0; k < list_PIR2.size(); k++){
			if(list_PIR2.get(k).getTimestamp() >= value.getRange1() && list_PIR2.get(k).getTimestamp() <= value.getRange2()){
					total_presence++;
			}
		}
		
		return (total_presence/2);
	}

	//trova la luminosità più vicina alla temperatura registrata più alta in un range di tempo, data in input
	public String foundLightTemp(RangeOfValues value) {
		ArrayList<Misurazione> list_temperature = new ArrayList<Misurazione>();
		ArrayList<Misurazione> list_light = new ArrayList<Misurazione>();
		Misurazione temp_max = new Misurazione("Temperature", "0", 0);
		Misurazione light_closer = new Misurazione("Light", "0" , 0);
		long distance = 0;
		
		Map<String,ArrayList<Misurazione>> listMisurations = ListMisurations.getInstance().leggi();
		list_temperature = listMisurations.get("Temperature");
		list_light = listMisurations.get("Light");
		//list_temperature = ListMisurations.getInstance().misurations.get("Temperature");
		//list_light = ListMisurations.getInstance().misurations.get("Light");
		
		for (int i=0; i < list_temperature.size() ; i++){
			if(list_temperature.get(i).getTimestamp() >= value.getRange1() && list_temperature.get(i).getTimestamp() <= value.getRange2()){
				if(Double.parseDouble(list_temperature.get(i).getValue()) > Double.parseDouble(temp_max.getValue())){
					temp_max.setValue(list_temperature.get(i).getValue());
					temp_max.setTimestamp(list_temperature.get(i).getTimestamp());
				}
			}
		}
		
		for (int k = 0; k < list_light.size() ; k++) {
			if(list_light.get(k).getTimestamp() >= value.getRange1() && list_light.get(k).getTimestamp() <= value.getRange2()){
				if (k == 0){
					distance = Math.abs(list_light.get(k).getTimestamp() - temp_max.getTimestamp());
					light_closer.setValue(list_light.get(k).getValue());
					light_closer.setTimestamp(list_light.get(k).getTimestamp());
				} else if (distance > Math.abs(list_light.get(k).getTimestamp() - temp_max.getTimestamp())){
					distance = Math.abs(list_light.get(k).getTimestamp() - temp_max.getTimestamp());
					light_closer.setValue(list_light.get(k).getValue());
					light_closer.setTimestamp(list_light.get(k).getTimestamp());
				}
			}
		}

		return light_closer.getValue();
	}
}
