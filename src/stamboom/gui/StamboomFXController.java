/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stamboom.gui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import stamboom.controller.StamboomController;
import stamboom.domain.Administratie;
import stamboom.domain.Geslacht;
import stamboom.domain.Gezin;
import stamboom.domain.Persoon;
import stamboom.util.StringUtilities;

/**
 *
 * @author frankpeeters
 */
public class StamboomFXController extends StamboomController implements Initializable {

//<editor-fold defaultstate="collapsed" desc="@FXML items">
    //MENUs en TABs
    @FXML MenuBar menuBar;
    @FXML MenuItem miNew;
    @FXML MenuItem miOpen;
    @FXML MenuItem miSave;
    @FXML CheckMenuItem cmDatabase;
    @FXML MenuItem createPsuedo;
    @FXML MenuItem miClose;
    @FXML Tab tabPersoon;
    @FXML Tab tabGezin;
    @FXML Tab tabPersoonInvoer;
    @FXML Tab tabGezinInvoer;
    
    //PERSOON
    @FXML ComboBox cbPersonen;
    @FXML TextField tfPersoonNr;
    @FXML TextField tfVoornamen;
    @FXML TextField tfTussenvoegsel;
    @FXML TextField tfAchternaam;
    @FXML TextField tfGeslacht;
    @FXML TextField tfGebDatum;
    @FXML TextField tfGebPlaats;
    @FXML ComboBox cbOuderlijkGezin;
    @FXML ListView lvAlsOuderBetrokkenBij;
    @FXML Button btStamboom;
    
    //INVOER GEZIN
    @FXML ComboBox cbOuder1Invoer;
    @FXML ComboBox cbOuder2Invoer;
    @FXML TextField tfHuwelijkInvoer;
    @FXML TextField tfScheidingInvoer;
    @FXML Button btOKGezinInvoer;
    @FXML Button btCancelGezinInvoer;
    
    //INVOER PERSOON
    @FXML TextField tfNVoornaam;
    @FXML TextField tfnTussenVoegsel;
    @FXML TextField tfnAchternaam;
    @FXML TextField tfNGeslacht;
    @FXML TextField tfnGebDat;
    @FXML TextField tfNGebPlaats;
    @FXML ComboBox cbxNOuderlijkGezin;
    @FXML Button btnMaakPersoon;
    @FXML Button btnAnnuleren;
    
    //GEZIN
    @FXML ComboBox cbxKiesGezin;
    @FXML ComboBox cbxKiesGezinslid;
    @FXML TextField tfGNummer;
    @FXML TextField tfGVoornamen;
    @FXML TextField tfGTussenVoegsel;
    @FXML TextField tfGAchternaam;
    @FXML TextField tfGGeslacht;
    @FXML TextField tfGGebDat;
    @FXML TextField tfGGebPlaats;
    @FXML ComboBox cbxGOuderlijkGezin;
    @FXML ListView lvGBetrokkenBij;
//</editor-fold>

    //opgave 4
    private boolean withDatabase;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initComboboxes();
        withDatabase = false;
    }

    public void createPsuedo(){
        System.out.println("Creating psuedo!");
        //from stamboom.domain.StamboomTest.java in test packages
        Administratie adm = getAdministratie();
        Persoon piet = adm.addPersoon(Geslacht.MAN, new String[]{"Piet"}, "Swinkels",
                "", new GregorianCalendar(1924, Calendar.APRIL, 23), "Den Haag", null);
        Persoon teuntje = adm.addPersoon(Geslacht.VROUW, new String[]{"Teuntje"}, "Vries", "de",
                new GregorianCalendar(1927, Calendar.MAY, 5), "Doesburg", null);
        Gezin teuntjeEnPiet = adm.addOngehuwdGezin(teuntje, piet);
        Persoon gijs = adm.addPersoon(Geslacht.MAN, new String[]{"Gijs", "Jozef"}, "Swinkels",
                "", new GregorianCalendar(1944, Calendar.APRIL, 21), "Geldrop", teuntjeEnPiet);
        Persoon ferdinand = adm.addPersoon(Geslacht.MAN, new String[]{"Ferdinand", "Karel", "Helene"}, "Vuiter", "de",
                new GregorianCalendar(1901, Calendar.JULY, 14), "Amsterdam", null);
        Persoon annalouise = adm.addPersoon(Geslacht.VROUW, new String[]{"Annalouise", "Isabel", "Teuntje"}, "Vuiter", "de",
                new GregorianCalendar(1902, Calendar.OCTOBER, 1), "Amsterdam", null);
        Gezin ferdinandEnAnnalouise = adm.addHuwelijk(ferdinand, annalouise,
                new GregorianCalendar(1921, Calendar.MAY, 5));
        Persoon louise = adm.addPersoon(Geslacht.VROUW, new String[]{"Louise", "Isabel", "Helene"}, "Vuiter", "de",
                new GregorianCalendar(1927, Calendar.JANUARY, 15), "Amsterdam", ferdinandEnAnnalouise);
        Gezin louiseAlleen = adm.addOngehuwdGezin(louise, null);
        Persoon mary = adm.addPersoon(Geslacht.VROUW, new String[]{"mary"}, "Vuiter",
                "de", new GregorianCalendar(1943, Calendar.MAY, 25), "Rotterdam", louiseAlleen);
        Gezin gijsEnMary = adm.addOngehuwdGezin(gijs, mary);
        Persoon jaron = adm.addPersoon(Geslacht.MAN, new String[]{"Jaron"}, "Swinkels",
                "", new GregorianCalendar(1962, Calendar.JULY, 22), "Velp", gijsEnMary);
        initComboboxes();
    }
    
    private void initComboboxes() {
        Administratie a = getAdministratie();
        cbPersonen.setItems(a.getOPersonen());
        cbOuderlijkGezin.setItems(a.getOGezinnen());
        cbOuder1Invoer.setItems(a.getOPersonen());
        cbOuder2Invoer.setItems(a.getOPersonen());
        cbxKiesGezin.setItems(a.getOGezinnen());
    }

    public void selectPersoon(Event evt) {
        Persoon persoon = (Persoon) cbPersonen.getSelectionModel().getSelectedItem();
        showPersoon(persoon);
    }

    private void showPersoon(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfPersoonNr.setText(persoon.getNr() + "");
            tfVoornamen.setText(persoon.getVoornamen());
            tfTussenvoegsel.setText(persoon.getTussenvoegsel());
            tfAchternaam.setText(persoon.getAchternaam());
            tfGeslacht.setText(persoon.getGeslacht().toString());
            tfGebDatum.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvAlsOuderBetrokkenBij.setItems(persoon.getOAlsOuderBetrokkenIn());
        }
    }
    
    private void showPersoonGezin(Persoon persoon) {
        if (persoon == null) {
            clearTabPersoon();
        } else {
            tfGNummer.setText(persoon.getNr() + "");
            tfGVoornamen.setText(persoon.getVoornamen());
            tfGTussenVoegsel.setText(persoon.getTussenvoegsel());
            tfGAchternaam.setText(persoon.getAchternaam());
            tfGGeslacht.setText(persoon.getGeslacht().toString());
            tfGGebDat.setText(StringUtilities.datumString(persoon.getGebDat()));
            tfGGebPlaats.setText(persoon.getGebPlaats());
            if (persoon.getOuderlijkGezin() != null) {
                cbxGOuderlijkGezin.getSelectionModel().select(persoon.getOuderlijkGezin());
            } else {
                cbxGOuderlijkGezin.getSelectionModel().clearSelection();
            }

            //todo opgave 3
            lvGBetrokkenBij.setItems(persoon.getOAlsOuderBetrokkenIn());
        }
    }

    public void setOuders(Event evt) {
        if (tfPersoonNr.getText().isEmpty()) {
            return;
        }
        Gezin ouderlijkGezin = (Gezin) cbOuderlijkGezin.getSelectionModel().getSelectedItem();
        if (ouderlijkGezin == null) {
            return;
        }

        int nr = Integer.parseInt(tfPersoonNr.getText());
        Persoon p = getAdministratie().getPersoon(nr);
        if(getAdministratie().setOuders(p, ouderlijkGezin)){
            showDialog("Success", ouderlijkGezin.toString()
                + " is nu het ouderlijk gezin van " + p.getNaam());
        }
        
    }

    public void selectGezin(Event evt) {
        // opgave 3
        if(cbxKiesGezin.getValue()==null){
            Logger.getLogger(StamboomFXController.class.getName()).log(Level.WARNING, "No gezin selected!");
            clearTabGezin();
            return;
        }
        Gezin g = (Gezin) cbxKiesGezin.getValue();
        showGezin(g);
    }
    
    public void selectGezinslid(Event evt){
        Persoon persoon = (Persoon) cbxKiesGezinslid.getSelectionModel().getSelectedItem();
        showPersoonGezin(persoon);
    }
    

    private void showGezin(Gezin gezin) {
        // opgave 3
        ObservableList kinderen = FXCollections.observableArrayList(gezin.getKinderen());
        kinderen.add(gezin.getOuder1());
        if(gezin.getOuder2()!=null)
            kinderen.add(gezin.getOuder2());
        
        cbxKiesGezinslid.setItems(kinderen);
    }

    public void setHuwdatum(Event evt) {
        // todo opgave 3
        
    }

    public void setScheidingsdatum(Event evt) {
        // todo opgave 3

    }

    public void cancelPersoonInvoer(Event evt) {
        // opgave 3
        clearTabPersoonInvoer();
    }

    public void okPersoonInvoer(Event evt) throws ParseException {
        // opgave 3
        
        String voornamen = tfNVoornaam.getText();
        String tussenVoegsel = tfnTussenVoegsel.getText();
        String achternaam = tfnAchternaam.getText();
        String geslacht = tfNGeslacht.getText();
        String gebDat = tfnGebDat.getText();
        String gebPlaats = tfNGebPlaats.getText();
        Gezin ouderlijkGezin = (Gezin)cbxNOuderlijkGezin.getValue();
       
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d-M-y");
        cal.setTime(sdf.parse(gebDat));
        
        getAdministratie().addPersoon(geslacht.equalsIgnoreCase("M")?Geslacht.MAN:Geslacht.VROUW, voornamen.split(" "), achternaam, tussenVoegsel, cal, gebPlaats, ouderlijkGezin);
        clearTabPersoonInvoer();
    }

    public void okGezinInvoer(Event evt) {
        Persoon ouder1 = (Persoon) cbOuder1Invoer.getSelectionModel().getSelectedItem();
        if (ouder1 == null) {
            showDialog("Warning", "eerste ouder is niet ingevoerd");
            return;
        }
        Persoon ouder2 = (Persoon) cbOuder2Invoer.getSelectionModel().getSelectedItem();
        Calendar huwdatum;
        try {
            huwdatum = StringUtilities.datum(tfHuwelijkInvoer.getText());
        } catch (IllegalArgumentException exc) {
            showDialog("Warning", "huwelijksdatum :" + exc.getMessage());
            return;
        }
        Gezin g;
        if (huwdatum != null) {
            g = getAdministratie().addHuwelijk(ouder1, ouder2, huwdatum);
            if (g == null) {
                showDialog("Warning", "Invoer huwelijk is niet geaccepteerd");
            } else {
                Calendar scheidingsdatum;
                try {
                    scheidingsdatum = StringUtilities.datum(tfScheidingInvoer.getText());
                    if(scheidingsdatum != null){
                        getAdministratie().setScheiding(g, scheidingsdatum);
                    }
                } catch (IllegalArgumentException exc) {
                    showDialog("Warning", "scheidingsdatum :" + exc.getMessage());
                }
            }
        } else {
            g = getAdministratie().addOngehuwdGezin(ouder1, ouder2);
            if (g == null) {
                showDialog("Warning", "Invoer ongehuwd gezin is niet geaccepteerd");
            }
        }

        clearTabGezinInvoer();
    }

    public void cancelGezinInvoer(Event evt) {
        clearTabGezinInvoer();
    }

    
    public void showStamboom(Event evt) {
        // opgave 3
        Persoon p = (Persoon)cbPersonen.getValue();
        Dialog d = new Dialog(this.getStage(),"Stamboom",p.stamboomAlsString());
        d.show();
        
    }

    public void createEmptyStamboom(Event evt) {
        this.clearAdministratie();
        clearTabs();
        initComboboxes();
    }

    
    public void openStamboom(Event evt) {
        if(withDatabase){
            try {
                loadFromDatabase();
                initComboboxes();
            } catch (IOException ex) {
                Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            // opgave 3
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showOpenDialog(getStage());
            if(file!=null){
                try {
                    deserialize(file);
                    initComboboxes();
                } catch (IOException ex) {
                    Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog d = new Dialog(getStage(),"Error opening file","An error occured while oppening the requested file");
                    d.show();
                }
            }
        }
    }

    
    public void saveStamboom(Event evt) {
        if(withDatabase){
            try {
                saveToDatabase();
            } catch (IOException ex) {
                Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
            // todo opgave 3
            FileChooser fileChooser = new FileChooser();
            File file = fileChooser.showSaveDialog(getStage());
            if(file!=null){
                try {
                    serialize(file);
                } catch (IOException ex) {
                    Logger.getLogger(StamboomFXController.class.getName()).log(Level.SEVERE, null, ex);
                    Dialog d = new Dialog(getStage(),"Error saving file","An error occured while saving the requested file");
                    d.show();
                }
            }
        }
    }

    
    public void closeApplication(Event evt) {
        saveStamboom(evt);
        getStage().close();
    }

   
    public void configureStorage(Event evt) {
        withDatabase = cmDatabase.isSelected();
        if(withDatabase){
            
        }else{
            
        }
    }

 
    public void selectTab(Event evt) {
        Object source = evt.getSource();
        if (source == tabPersoon) {
            clearTabPersoon();
        } else if (source == tabGezin) {
            clearTabGezin();
        } else if (source == tabPersoonInvoer) {
            clearTabPersoonInvoer();
        } else if (source == tabGezinInvoer) {
            clearTabGezinInvoer();
        }
    }

    private void clearTabs() {
        clearTabPersoon();
        clearTabPersoonInvoer();
        clearTabGezin();
        clearTabGezinInvoer();
    }

 
    private void clearTabPersoonInvoer() {
        //opgave 3
        //cbPersonen.getSelectionModel().clearSelection();
        tfNVoornaam.clear();
        tfnTussenVoegsel.clear();
        tfnAchternaam.clear();
        tfNGeslacht.clear();
        tfnGebDat.clear();
        tfNGebPlaats.clear();
        cbxNOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.getSelectionModel().clearSelection();
    }

    
    private void clearTabGezinInvoer() {
        //opgave 3
        cbOuder1Invoer.getSelectionModel().clearSelection();
        cbOuder2Invoer.getSelectionModel().clearSelection();
        tfHuwelijkInvoer.clear();
        tfScheidingInvoer.clear();
    }

    private void clearTabPersoon() {
        //cbPersonen.getSelectionModel().clearSelection();
        tfPersoonNr.clear();
        tfVoornamen.clear();
        tfTussenvoegsel.clear();
        tfAchternaam.clear();
        tfGeslacht.clear();
        tfGebDatum.clear();
        tfGebPlaats.clear();
        cbOuderlijkGezin.getSelectionModel().clearSelection();
        lvAlsOuderBetrokkenBij.getSelectionModel().clearSelection();
    }

    
    private void clearTabGezin() {
        //cbxKiesGezin.getSelectionModel().clearSelection();
        //cbxKiesGezinslid.getSelectionModel().clearSelection();
        tfGNummer.clear();
        tfGVoornamen.clear();
        tfGTussenVoegsel.clear();
        tfGAchternaam.clear();
        tfGGeslacht.clear();
        tfGGebDat.clear();
        tfGGebPlaats.clear();
        cbxGOuderlijkGezin.getSelectionModel().clearSelection();
        lvGBetrokkenBij.getSelectionModel().clearSelection();
    }

    private void showDialog(String type, String message) {
        Stage myDialog = new Dialog(getStage(), type, message);
        myDialog.show();
    }

    private Stage getStage() {
        return (Stage) menuBar.getScene().getWindow();
    }

}
