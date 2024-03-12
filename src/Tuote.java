

public class Tuote {
	
	private int tuotetunniste;
	private String nimi;
	private double hinta;
	private int ostosMaara;
	
	public Tuote (int tuotetunniste, String nimi, double hinta, int ostosMaara) {
		this.tuotetunniste = tuotetunniste;
		this.nimi = nimi;
		this.hinta = hinta;
		this.ostosMaara = ostosMaara;
	}
	
	public int haeTuotetunniste () {
		return this.tuotetunniste;
	}
	
	public String haeNimi () {
		return this.nimi;
	}
	
	public double haeHinta() {
		return this.hinta;
	}
	
	public int haeOstosMaara() {
		return this.ostosMaara;
	}
	
	
	public void tulostaTuote() {
		System.out.println(this.nimi+ " " +this.hinta+ "€");
	}

}
