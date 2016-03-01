package reteSensori.resources;

/**
 * Classe che implementa un protocollo di comunicazione tra i vari
 * sensori e tra la rete e il Gestore. Contiene una stringa che identifica
 * il tipo di messaggio che si vuole passare, una stringa che identifica
 * il messaggio da spedire e un timestamp che identifica quando è stato creato
 * il MessageType (quest'ultimo utilizzato durante la fase di debug).
 * Ogni stringa inviata sarà del tipo:
 * typeMessage#value#timestamp.
 * @author Gianpaolo Caprara
 *
 */
public class MessageType {

	private String typeMessage;
	private String value;
	private long timestamp;
	
	public MessageType(){
		
	}
	
	public MessageType(String type, String v, long time){
		this.typeMessage = type;
		this.value = v;
		this.timestamp = time;
	}
	
	public void setTypeMessage(String type){
		this.typeMessage = type;
	}
	
	public void setValue(String v){
		this.value = v;
	}
	
	public void setTimestamp(long time){
		this.timestamp = time;
	}
	
	public String getTypeMessage(){
		return this.typeMessage;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public long getTimestamp(){
		return this.timestamp;
	}
	
	public String toString(){
		return this.typeMessage + "#" + this.value + "#" + this.timestamp;
	}
	
	public static MessageType fromString(String message){
		String[] partOfMessage = message.split("#");
		MessageType messageType = new MessageType();
		messageType.setTypeMessage(partOfMessage[0]);
		messageType.setValue(partOfMessage[1]);
		messageType.setTimestamp(Long.parseLong(partOfMessage[2]));
		return messageType;
	}
}
