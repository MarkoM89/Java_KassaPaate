
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;



public class MainKassa {

    public static void main(String[] args) {
        Scanner lukija = new Scanner(System.in);
        int toiminto = 0;
        String nimi;
        int tuotenimi = -1;
        int tuoteMaara;
        boolean tuoteLoydetty;
        boolean ostajaLoytyi = false;
        double loppusumma = 0;
        double maksettavana;
        double haettuSaldo;
        int kuittitunnus = 0;
        int maksutapa;
        double kateinen;
        


        ArrayList<Tuote> ostetutTuotteet = new ArrayList<>();
        Properties connConfig = new Properties();
        connConfig.setProperty("user", "käyttäjä");
        connConfig.setProperty("password", "salasana");
        

        
        
    	/*
Päätoiminto 1: Asiakkaan ostotapahtuma
	
Päätoiminto 2: Poistu ohjelmasta
    	 
    	 */
        
        while (toiminto != 2) {
        	
        	System.out.print("\nPäätoiminto 1: Ostotapahtuma\n"
        			+ "Päätoiminto 2: Poista ohjelmasta\n");
        	toiminto = lukija.nextInt();
        	lukija.nextLine();
        	
        	switch(toiminto) {
    		case 1:
    			
	    	        tuotenimi = -1;
	    	        loppusumma = 0;
	    			
	    			while(tuotenimi != 0) {
	    				
	    				for(Tuote tuote : ostetutTuotteet) {
	    					tuote.tulostaTuote();
	    				}
	    				System.out.println("Yhtensä: "+loppusumma+ "€");
	    				tuoteLoydetty = false;
	    				
	    				
	    				System.out.println("Mitä tuotetta ostetaan? ");
	    				tuotenimi = lukija.nextInt();
						lukija.nextLine();
	    				
	    		        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/pankki_kauppa", connConfig)) {
	    		             	PreparedStatement stmt = conn.prepareStatement("select * from tuote where tuotetunniste=?");
	    		            	stmt.setInt(1, tuotenimi);
	    		            	ResultSet tuotetiedot = stmt.executeQuery(); 
	    		                while (tuotetiedot.next()) 
	    		                	
	    							if(tuotetiedot.getInt("tuotetunniste") == tuotenimi) {

										System.out.println(tuotetiedot.getString("tuotenimi")+ " " +tuotetiedot.getDouble("yksikköhinta"));
	    								System.out.println("Paljonko ostetaan: ");
	    								tuoteMaara = lukija.nextInt();
	    								lukija.nextLine();
	    								ostetutTuotteet.add(new Tuote(tuotetiedot.getInt("tuotetunniste"), tuotetiedot.getString("tuotenimi"), tuotetiedot.getDouble("yksikköhinta"), tuoteMaara));
	    								loppusumma += (tuotetiedot.getDouble("yksikköhinta")*tuoteMaara);
	    								tuoteLoydetty = true;
	    							}
	    		                    
	    		        } catch (Exception e) {
	    		            e.printStackTrace();
	    		        }
	    		        
	    	            if (tuoteLoydetty == false && tuotenimi != 0){
	                        System.out.println("Tuotetta ei ole valikoimassa");
	    					}
	    			}
	    			
	    			
	    			
	    			maksettavana = loppusumma;
	    			
	    			while(maksettavana > 0) {
	    				
	    			System.out.println("Maksettavana " +maksettavana+ "€");
	    			System.out.println("Valitse maksutapa");
	    			maksutapa = lukija.nextInt();
	    			lukija.nextLine();
	    			
	    			if(maksutapa == 1) {
				    	System.out.println("Kuka ostaa?");
				    	nimi = lukija.nextLine();
				    	haettuSaldo = 0.0;
			    	

	    		        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/pankki_kauppa", connConfig)) {
			             	PreparedStatement stmt = conn.prepareStatement("select * from pankki where nimi=?");
			            	stmt.setString(1, nimi);
			            	ResultSet ostajatiedot = stmt.executeQuery(); 
			                while (ostajatiedot.next()) {
			                	
			        			if(ostajatiedot.getString("nimi").equals(nimi)) {
			        				
			        				ostajaLoytyi = true;
			        				haettuSaldo = ostajatiedot.getDouble("saldo");
			        			}
			                }
	    		        }
	    		        catch (Exception e) {
	    		            e.printStackTrace();
	    		        }
		                
		                
				                if(ostajaLoytyi == true) {
				                	
				                try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/pankki_kauppa", connConfig)) {
				                PreparedStatement stmt = conn.prepareStatement("UPDATE pankki SET saldo=? WHERE nimi=?");
				                stmt.setDouble(1, (haettuSaldo - maksettavana));
				                stmt.setString(2, nimi);
				                stmt.executeQuery();
				                maksettavana = 0;
				                }
				                catch (Exception e) {
			    		            e.printStackTrace();
			    		        }
				                }
	    			
    		        
	    				}  
		    			
		    			if(maksutapa == 2) {
		    				System.out.print("Käteinen: ");
		    				kateinen = lukija.nextDouble();
		    				lukija.nextLine();
		    				maksettavana -= kateinen;
		    				}
	    			}
	    			
	    			
	    			
	    				try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/pankki_kauppa", connConfig)) {
	    				PreparedStatement stmt = conn.prepareStatement("INSERT INTO kuitti (kokonaishinta) VALUES (?)");
		                stmt.setDouble(1, loppusumma);
		                stmt.executeQuery();
		                
		                Statement stmt2 = conn.createStatement();
	    	            ResultSet haettuKuitti = stmt2.executeQuery("SELECT kuittitunnus FROM kuitti ORDER BY kuittitunnus DESC LIMIT 1");
	    	            while(haettuKuitti.next()) {
	    	                
	    	              kuittitunnus = haettuKuitti.getInt("kuittitunnus");
	    	              }
	    	            
	    	            for (Tuote tuote : ostetutTuotteet) {
			                stmt = conn.prepareStatement("INSERT INTO ostettu_tuote (kuittitunnus, tuotetunnus, tuotemäärä) values (?, ?, ?)");
			                stmt.setInt(1, kuittitunnus);
			                stmt.setInt(2, tuote.haeTuotetunniste());
			                stmt.setInt(3, tuote.haeOstosMaara());
			                stmt.executeQuery();
		    	            }
	    	            
	    	            System.out.println("\n\nKuitti\n");

	    	            for (Tuote tuote : ostetutTuotteet) {
	    	                tuote.tulostaTuote();
	    	            	}

	    	            System.out.println("\nLoppusumma: " +loppusumma+ "€");
		                
		                }
		                
        	
		                    
	    			catch (Exception e) {
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
    			
    	        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/pankki_kauppa", connConfig)) {
    	            try (Statement stmt = conn.createStatement()) {
    	                try (ResultSet tilitiedot = stmt.executeQuery("SELECT * FROM pankki")) {
    	                    while (tilitiedot.next()) {
    	                    	
    	                    	System.out.println(tilitiedot.getString("tunniste")+ " " +tilitiedot.getString("nimi")+ " " +tilitiedot.getString("saldo")+ "€");
    	                    }
    	                }
    	            }
    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	        
    			System.out.println("\n\nTulosta kuitit");
    			
    	        try (Connection conn = DriverManager.getConnection("jdbc:mariadb://127.0.0.1:3306/pankki_kauppa", connConfig)) {
    	            try (Statement stmt = conn.createStatement()) {
    	                try (ResultSet kuittitiedot = stmt.executeQuery("SELECT * FROM kuitti")) {
    	                    while (kuittitiedot.next()) {
    	                    	
    	                    	System.out.println(kuittitiedot.getInt("kuittitunnus")+ " " +kuittitiedot.getTimestamp("osto_aika")+ " " +kuittitiedot.getDouble("kokonaishinta")+ "€");
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