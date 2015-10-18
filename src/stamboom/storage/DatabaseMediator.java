/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.storage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.sqlite.SQLiteConfig;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;

public class DatabaseMediator implements IStorageMediator {

    private Properties props;
    private Connection conn;

    @Override
    public Administratie load() throws IOException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            initConnection();
            //opgave 4
            Administratie admin = new Administratie();
            Statement s = conn.createStatement();
            //get persons
            ResultSet set = s.executeQuery("SELECT persoonsNummer, achternaam, voornamen, tussenvoegsel, geboortedatum, geboorteplaats, geslacht FROM personen");
            while(set.next()){
                Date d = format.parse(set.getString(5));
                Calendar c = Calendar.getInstance();
                c.setTime(d);
                admin.addPersoon(set.getString(7).equals("M")?Geslacht.MAN:Geslacht.VROUW, set.getString(3).split(" "), set.getString(2), set.getString(4), c, set.getString(6), null);
            }
            set.close();
            //get familys
            set = s.executeQuery("SELECT gezinsnummer, ouder1, ouder2, huwelijksdatum, scheidingsdatum FROM gezinnen");
            while(set.next()){
                Persoon ouder1 = admin.getPersoon(set.getInt(2));
                int ouder2Nr = set.getInt(3);
                Persoon ouder2;
                if(set.wasNull()){
                    ouder2 = null;
                }else{
                    ouder2 = admin.getPersoon(ouder2Nr);
                }
                Calendar huwdatum, scheidingsdatum;
                String huwdat = set.getString(4);
                if(set.wasNull()){
                    huwdatum=scheidingsdatum=null;
                    admin.addOngehuwdGezin(ouder1, ouder2);
                }else{
                    huwdatum = Calendar.getInstance();
                    huwdatum.setTime(format.parse(huwdat));
                    
                    Gezin g = admin.addHuwelijk(ouder1, ouder2, huwdatum);
                    
                    String scheidDat = set.getString(5);
                    if(set.wasNull()){
                        scheidingsdatum=null;
                    }else{
                        scheidingsdatum = Calendar.getInstance();
                        scheidingsdatum.setTime(format.parse(scheidDat));
                        admin.setScheiding(g, scheidingsdatum);
                    }
                }
                
            }
            set.close();
            //bind parents to children
            set = s.executeQuery("SELECT persoonsnummer, ouders FROM personen WHERE ouders IS NOT NULL");
            while(set.next()){
                Persoon p = admin.getPersoon(set.getInt(1));
                Gezin o = admin.getGezin(set.getInt(2));
                admin.setOuders(p, o);
            }
            closeConnection();
            return admin;
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public void save(Administratie admin) throws IOException {
        try {
            initConnection();
            //opgave 4
            Statement statement = conn.createStatement();
            try {
                statement.executeUpdate("UPDATE personen SET ouders = null");//delete relations
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseMediator.class.getName()).log(Level.INFO, null, ex);
            }
            try {
                statement.executeUpdate("DROP TABLE gezinnen");
                statement.executeUpdate("DROP TABLE personen");
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseMediator.class.getName()).log(Level.INFO, null, ex);
            }
            statement.executeUpdate("CREATE TABLE personen(\n" +
                    "	persoonsNummer 	INTEGER	PRIMARY KEY,\n" +
                    "	achternaam 	TEXT	NOT NULL,\n" +
                    "	voornamen 	TEXT	NOT NULL,\n" +
                    "	tussenvoegsel	TEXT	NOT NULL,\n" +
                    "	geboortedatum 	TEXT	NOT NULL,\n" +
                    "	geboorteplaats	TEXT	NOT NULL,\n" +
                    "	geslacht	TEXT 	NOT NULL,\n" +
                    "	ouders		INTEGER	NULL,\n" +
                    "   FOREIGN KEY (ouders) REFERENCES gezinnen(gezinsNummer))");
            //"	FOREIGN KEY (ouders) REFERENCES gezinnen(gezinsNummer)\n"")");
            statement.executeUpdate("CREATE TABLE gezinnen(\n" +
                    "	gezinsNummer	INTEGER PRIMARY KEY,\n" +
                    "	ouder1		INTEGER NOT NULL,\n" +
                    "	ouder2		INTEGER NULL,\n" +
                    "	huwelijksdatum	TEXT 	NULL,\n" +
                    "	scheidingsdatum	TEXT	NULL,\n" +
                    "	FOREIGN KEY (ouder1) REFERENCES personen(persoonsNummer),\n" +
                    "	FOREIGN KEY	(ouder2) REFERENCES personen(persoonsNummer))");
            //statement.executeUpdate("ALTER TABLE personen ADD CONSTRAINT fkPersonen ");
            
            SimpleDateFormat fm = new SimpleDateFormat("yyyy-MM-dd");
            
            //add all personons
            for(Persoon p : admin.getPersonen()){
                String q = "INSERT INTO personen (persoonsNummer, achternaam, voornamen, tussenvoegsel, geboortedatum, geboorteplaats, geslacht) VALUES (?,?,?,?,?,?,?)";
                PreparedStatement s = conn.prepareStatement(q);
                s.setInt(1, p.getNr());
                s.setString(2, p.getAchternaam());
                s.setString(3, p.getVoornamen());
                s.setString(4, p.getTussenvoegsel());
                s.setString(5, fm.format(p.getGebDat().getTime()));
                s.setString(6, p.getGebPlaats());
                s.setString(7, p.getGeslacht() == Geslacht.MAN ?"M":"V");
                s.executeUpdate();
            }
            
            //add all familys
            for(Gezin g : admin.getGezinnen()){
                String q = "INSERT INTO gezinnen (gezinsNummer, ouder1, ouder2, huwelijksdatum, scheidingsdatum) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement s = conn.prepareStatement(q);
                s.setInt(1, g.getNr());
                s.setInt(2,g.getOuder1().getNr());
                if(g.getOuder2()==null){
                    s.setNull(3, java.sql.Types.INTEGER); 
                }else {
                    s.setInt(3, g.getOuder2().getNr());
                }
                if(g.getHuwelijksdatum()==null){
                    s.setNull(4, java.sql.Types.VARCHAR); 
                }else{
                    s.setString(4,fm.format(g.getHuwelijksdatum().getTime()));
                }
                if(g.getScheidingsdatum()==null){
                    s.setNull(5, java.sql.Types.VARCHAR); 
                }else{
                    s.setString(5,fm.format(g.getScheidingsdatum().getTime()));
                }
                s.executeUpdate();
            }
            
            //connect persons to parent familys
            for(Persoon p : admin.getPersonen()){
                String q = "UPDATE personen SET ouders = ? WHERE persoonsNummer = ?";
                if(p.getOuderlijkGezin()==null)continue;
                PreparedStatement s = conn.prepareStatement(q);
                s.setInt(1, p.getOuderlijkGezin().getNr());
                s.setInt(2,p.getNr());
                s.executeUpdate();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseMediator.class.getName()).log(Level.SEVERE, null, ex);
        }
        closeConnection();
    }

    /**
     * Laadt de instellingen, in de vorm van een Properties bestand, en controleert
     * of deze in de correcte vorm is, en er verbinding gemaakt kan worden met
     * de database.
     * @param props
     * @return
     */
    @Override
    public final boolean configure(Properties props) {
        this.props = props;
        if (!isCorrectlyConfigured()) {
            System.err.println("props mist een of meer keys");
            return false;
        }

        try {
            initConnection();
            return true;
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println(ex.getMessage());
            this.props = null;
            return false;
        } finally {
            closeConnection();
        }
    }

    @Override
    public Properties config() {
        return props;
    }

    @Override
    public boolean isCorrectlyConfigured() {
        if (props == null) {
            return false;
        }
        if (!props.containsKey("driver")) {
            return false;
        }
        if (!props.containsKey("url")) {
            return false;
        }
        if (!props.containsKey("username")) {
            return false;
        }
        if (!props.containsKey("password")) {
            return false;
        }
        return true;
    }

    private void initConnection() throws SQLException, ClassNotFoundException {
        
        Class.forName(props.getProperty("driver"));
                
        conn = DriverManager.getConnection(props.getProperty("url"), props.getProperty("username"), props.getProperty("password"));        
        //opgave 4
    }

    private void closeConnection() {
        try {
            conn.close();
            conn = null;
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
