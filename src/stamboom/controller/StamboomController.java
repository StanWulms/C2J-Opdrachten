/*Bijgewerkt door Rick Rongen*/
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import stamboom.domain.Administratie;
import stamboom.storage.*;

public class StamboomController {

    private Administratie admin;
    private IStorageMediator storageMediator;

    /**
     * creatie van stamboomcontroller met lege administratie en onbekend
     * opslagmedium
     */
    public StamboomController() {
        admin = new Administratie();
        storageMediator = new SerializationMediator();
    }

    public Administratie getAdministratie() {
        return admin;
    }

    /**
     * administratie wordt leeggemaakt (geen personen en geen gezinnen)
     */
    public void clearAdministratie() {
        admin = new Administratie();
    }

    /**
     * administratie wordt in geserialiseerd bestand opgeslagen
     *
     * @param bestand
     * @throws IOException
     */
    public void serialize(File bestand) throws IOException {
        //done opgave 2
        //set file property
        Properties p = storageMediator.config();
        //check null, C# would be ?? but doesn't exist in java
        if(p==null)p=new Properties();
        p.setProperty("file", bestand.getAbsolutePath());
        storageMediator.configure(p);
        
        //save to file
        storageMediator.save(admin);//save file
    }

    /**
     * administratie wordt vanuit geserialiseerd bestand gevuld
     *
     * @param bestand
     * @throws IOException
     */
    public void deserialize(File bestand) throws IOException {
        
        //todo opgave 2
         //set file property
        Properties p = storageMediator.config();
        //check null, C# would be ?? but doesn't exist in java
        if(p==null)p=new Properties();
        p.setProperty("file", bestand.getAbsolutePath());
        storageMediator.configure(p);
        
        admin = storageMediator.load();
    }
    
    // opgave 4
    private void initDatabaseMedium() throws IOException {
        if (!(storageMediator instanceof DatabaseMediator)) {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream("database.properties")) {
                props.load(in);
            }
            storageMediator = new DatabaseMediator();
            storageMediator.configure(props);
        }
    }
    
    /**
     * administratie wordt vanuit standaarddatabase opgehaald
     *
     * @throws IOException
     */
    public void loadFromDatabase() throws IOException {
        initDatabaseMedium();
        admin = storageMediator.load();
        //opgave 4
    }

    /**
     * administratie wordt in standaarddatabase bewaard
     *
     * @throws IOException
     */
    public void saveToDatabase() throws IOException {
        initDatabaseMedium();
        //todo opgave 4
        storageMediator.save(admin);
    }

}
