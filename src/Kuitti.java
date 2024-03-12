import java.util.ArrayList;

public class Kuitti {
	
	private String ostaja;
	private ArrayList<String> ostetutTuotteet = new ArrayList<>();
	private double loppusumma;
	
	public Kuitti(String nimi, /*ArrayList<String> tuotteet,*/ double loppusumma) {
		this.ostaja = nimi;
		//this.ostetutTuotteet = tuotteet;
		this.loppusumma = loppusumma;
	}
	
    public void tulostaKuitti(){
		System.out.println("Kuitti\n");
		
		System.out.println("Henkilö: " +this.ostaja+ "\n");
		for(String tuote: ostetutTuotteet) {
			   System.out.println(tuote);
			}
		System.out.println("\nLoppusumma: " +this.loppusumma+ "€");
    }
    
    public void lisaaOstos(String ostettu){
    	this.ostetutTuotteet.add(ostettu);
    }
    			

}
