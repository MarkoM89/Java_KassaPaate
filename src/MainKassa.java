
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.Scanner;



public class MainKassa {

    public static void main(String[] args) {
        Scanner lukija = new Scanner(System.in);
        int toiminto = 0;
        String nimi;
        String tuotenimi = " ";
        int tuoteMaara;
        boolean tuoteLoydetty;
        boolean ostajaLoytyi = false;
        double loppusumma = 0;
        double haettuSaldo;
        int kuittitunnus = 0;
        

        
        ArrayList<Tuote> ostetutTuotteet = new ArrayList<>();
        Properties connConfig = new Properties();
        connConfig.setProperty("user", "root");
        connConfig.setProperty("password", "T13t0k4!?t4");
        

        
        
    	/*
P‰‰toiminto 1: Asiakkaan ostotapahtuma
	
P‰‰toiminto 2: Poistu ohjelmasta
    	 
    	 */
        
        while (toiminto != 2) {
        	
        	System.out.print("\nP‰‰toiminto 1: Ostotapahtuma\n"
        			+ "P‰‰toiminto 2: Poista ohjelmasta\n");
        	toiminto = lukija.nextInt();
        	lukija.nextLine();
        	
        	switch(toiminto) {
    		case 1:
    			
	    	        tuotenimi = " ";
	    	        loppusumma = 0;
	    			
	    			while(!tuotenimi.equals("")) {
	    				
	    				for(Tuote tuote : ostetutTuotteet) {
	    					tuote.tulostaTuote();
	    				}
	    				System.out.println("Yhtens‰: "+loppusumma+ "Ä");
	    				tuoteLoydetty = false;
	    				
	    				
	    				System.out.println("Mit‰ tuotetta ostetaan? ");
	    				tuotenimi = lukija.nextLine();
	    				
	    		        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/kokeilutietokanta", connConfig)) {
	    		             	PreparedStatement stmt = conn.prepareStatement("select * from tuote where tuotenimi=?");
	    		            	stmt.setString(1, tuotenimi);
	    		            	ResultSet tuotetiedot = stmt.executeQuery(); 
	    		                while (tuotetiedot.next()) 
	    		                	
	    							if(tuotetiedot.getString("tuotenimi").equals(tuotenimi)) {
	    								System.out.println("Paljonko ostetaan: ");
	    								tuoteMaara = lukija.nextInt();
	    								lukija.nextLine();
	    								ostetutTuotteet.add(new Tuote(tuotetiedot.getInt("tuotetunniste"), tuotetiedot.getString("tuotenimi"), tuotetiedot.getDouble("yksikkˆhinta"), tuoteMaara));
	    								loppusumma += (tuotetiedot.getDouble("yksikkˆhinta")*tuoteMaara);
	    								tuoteLoydetty = true;
	    							}
	    		                    
	    		        } catch (Exception e) {
	    		            e.printStackTrace();
	    		        }
	    		        
	    	            if (tuoteLoydetty == false && !tuotenimi.equals("")){
	                        System.out.println("Tuotetta ei ole valikoimassa");
	    					}
	    			}
	    			
	    				
			    	System.out.println("Kuka ostaa?");
			    	nimi = lukija.nextLine();
			    	haettuSaldo = 0.0;
			    	ostajaLoytyi = false;
			    	

    		        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/kokeilutietokanta", connConfig)) {
		             	PreparedStatement stmt = conn.prepareStatement("select * from pankki where nimi=?");
		            	stmt.setString(1, nimi);
		            	ResultSet ostajatiedot = stmt.executeQuery(); 
		                while (ostajatiedot.next()) {
		                	
		        			if(ostajatiedot.getString("nimi").equals(nimi)) {
		        				
		        				ostajaLoytyi = true;
		        				haettuSaldo = ostajatiedot.getDouble("saldo");
		        			}
		                }
		                
		                
		                if(ostajaLoytyi == true) {
		                stmt = conn.prepareStatement("UPDATE pankki SET saldo=? WHERE nimi=?");
		                stmt.setDouble(1, (haettuSaldo - loppusumma));
		                stmt.setString(2, nimi);
		                stmt.executeQuery();
		              
		             
		                stmt = conn.prepareStatement("INSERT INTO kuitti (kokonaishinta) VALUES (?)");
		                stmt.setDouble(1, loppusumma);
		                stmt.executeQuery();
		                
		                Statement stmt2 = conn.createStatement();
	    	            ResultSet haettuKuitti = stmt2.executeQuery("SELECT kuittitunnus FROM kuitti ORDER BY kuittitunnus DESC LIMIT 1");
	    	            while(haettuKuitti.next()) {
	    	                
	    	              kuittitunnus = haettuKuitti.getInt("kuittitunnus");
	    	              }
	    	            
	    	            for (Tuote tuote : ostetutTuotteet) {
			                stmt = conn.prepareStatement("INSERT INTO ostettu_tuote (kuittitunnus, tuotetunnus, tuotem‰‰r‰) values (?, ?, ?)");
			                stmt.setInt(1, kuittitunnus);
			                stmt.setInt(2, tuote.haeTuotetunniste());
			                stmt.setInt(3, tuote.haeOstosMaara());
			                stmt.executeQuery();
		    	            }
	    	            
	    	            System.out.println("\n\nKuitti\n");

	    	            for (Tuote tuote : ostetutTuotteet) {
	    	                tuote.tulostaTuote();
	    	            	}

	    	            System.out.println("\nLoppusumma: " +loppusumma+ "Ä");
		                
		                }
		                
		         
		                    
		        } catch (Exception e) {
		            e.printStackTrace();
		        }

	
	    			ostetutTuotteet.clear();
	    			loppusumma = 0;
	    			ostajaLoytyi = false;

    			
    			break;

    		case 2:
    			System.out.println("Poistutaan ohjelmasta");
    			break;

    		case 5:
    			System.out.println("Tulosta korttien tiedot");
    			
    	        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/kokeilutietokanta", connConfig)) {
    	            try (Statement stmt = conn.createStatement()) {
    	                try (ResultSet tilitiedot = stmt.executeQuery("SELECT * FROM pankki")) {
    	                    while (tilitiedot.next()) {
    	                    	
    	                    	System.out.println(tilitiedot.getString("tunniste")+ " " +tilitiedot.getString("nimi")+ " " +tilitiedot.getString("saldo")+ "Ä");
    	                    }
    	                }
    	            }
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	        
    			System.out.println("\n\nTulosta kuitit");
    			
    	        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/kokeilutietokanta", connConfig)) {
    	            try (Statement stmt = conn.createStatement()) {
    	                try (ResultSet kuittitiedot = stmt.executeQuery("SELECT * FROM kuitti")) {
    	                    while (kuittitiedot.next()) {
    	                    	
    	                    	System.out.println(kuittitiedot.getInt("kuittitunnus")+ " " +kuittitiedot.getTimestamp("osto_aika")+ " " +kuittitiedot.getDouble("kokonaishinta")+ "Ä");
    	                    }
    	                }
    	            }
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	        


    			break;
    			
    		default:
    			System.out.println("Vain numerot 1-2");
    		}
        
        }
    }
}