package client.resources;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Classe User: definisce come deve essere composto l'utente per operazioni di Login e Logout.
 * Serve inoltre per poter tenere traccia di tutti gli utenti registrati al sistema per inviare i messaggi di errore.
 * @author Gianpaolo Caprara
 *
 */
@XmlRootElement(name="user")
public class User {
	
    private String name;
    private String ip;
    private Integer port;

    public User(){
    	
    }
    
    public User(String name, String ip, Integer port) {
        this.name = name;
        this.port = port;
        this.ip=ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
