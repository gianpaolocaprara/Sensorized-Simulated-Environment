package gestore.gateway;

import gestore.resources.SystemMessage;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class Initializer implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		new Thread(new GatewayThread()).start();
		System.out.println(SystemMessage.getString("START_GW"));
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		/*
		 * Metodo non utilizzato
		 */
	}


}
