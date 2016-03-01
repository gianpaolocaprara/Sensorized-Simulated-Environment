package reteSensori.resources;

import java.io.IOException;

/**
 * Classe che fa partire la rete di Sensori. Vengono dati come argomenti
 * l'id del nodo che deve essere fatto partire come sink; la frequenza di trasmissione del sink,
 * i livelli di batteria di ogni singolo nodo.
 * @author Gianpaolo Caprara
 *
 */
public class ReteSensori {

	public static void main(String[] args) throws IOException {
		String id_sink = args[0];
		String frequency_trasmission = args[1];
		String battery_temperature = args[2];
		String battery_light = args[3];
		String battery_PIR1 = args[4];
		String battery_PIR2 = args[5];
		Process p,s,t,r;

		switch(id_sink.toLowerCase()){
		case "temperature":
			try {
				s = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar light " + battery_light +  " false " + frequency_trasmission});
				s.waitFor();
				t = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir1 "+ battery_PIR1 + " false "  + frequency_trasmission});
				t.waitFor();
				r = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir2 " + battery_PIR2 + " false "  + frequency_trasmission} );
				r.waitFor();
				p = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar temperature " + battery_temperature + " true " + frequency_trasmission});
				p.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case "light":
			try {
				p = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar temperature " + battery_temperature + " false " + frequency_trasmission});
				p.waitFor();
				t = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir1 "+ battery_PIR1 + " false "  + frequency_trasmission});
				t.waitFor();
				r = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir2 " + battery_PIR2 + " false "  + frequency_trasmission} );
				r.waitFor();
				s = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar light " + battery_light +  " true " + frequency_trasmission});
				s.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case "pir1":
			try {
				p = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar temperature " + battery_temperature + " false " + frequency_trasmission});
				p.waitFor();
				s = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar light " + battery_light +  " false " + frequency_trasmission});
				s.waitFor();
				r = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir2 " + battery_PIR2 + " false "  + frequency_trasmission} );
				r.waitFor();
				t = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir1 "+ battery_PIR1 + " true "  + frequency_trasmission});
				t.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		case "pir2":
			try {
				p = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar temperature " + battery_temperature + " false " + frequency_trasmission});
				p.waitFor();
				s = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar light " + battery_light +  " false " + frequency_trasmission});
				s.waitFor();
				t = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir1 "+ battery_PIR1 + " false "  + frequency_trasmission});
				t.waitFor();
				r = Runtime.getRuntime().exec(new String[]{"cmd","/c","start","cmd","/k","java -jar nodo.jar pir2 " + battery_PIR2 + " true "  + frequency_trasmission} );
				r.waitFor();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			break;
		}
	}
}
