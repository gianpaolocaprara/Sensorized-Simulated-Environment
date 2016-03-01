package client.resources;

/**
 * Classe utilizzata per poter tenere traccia del range di valori inseriti dal client
 * durante l'elaborazione di una richiesta. In tal maniera si diminuisce il sovraccarico 
 * informativo da/per il Gestore.
 * @author Gianpaolo Caprara
 *
 */
public class RangeOfValues {
	
	int range1;
	int range2;
	
	public RangeOfValues(){
		
	}
	
	public RangeOfValues (int r1,int r2){
		this.range1 = r1;
		this.range2 = r2;
	}
	
	public void setRange1(int r1){
		this.range1 = r1;
	}
	
	public void setRange2(int r2){
		this.range2 = r2;
	}
	
	public int getRange1(){
		return range1;
	}
	
	public int getRange2(){
		return range2;
	}
}
