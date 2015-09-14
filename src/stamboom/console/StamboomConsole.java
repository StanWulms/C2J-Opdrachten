/*Bijgewerkt door Rick Rongen*/
package stamboom.console;

import java.io.File;
import stamboom.domain.*;
import java.util.*;
import stamboom.util.StringUtilities;
import stamboom.controller.StamboomController;

public class StamboomConsole {

    // **********datavelden**********************************************
    private final Scanner input;
    private final StamboomController controller;

    // **********constructoren*******************************************
    public StamboomConsole(StamboomController controller) {
        input = new Scanner(System.in);
        this.controller = controller;
        this.startMenu();
    }

    // ***********methoden***********************************************
    public void startMenu() {
        MenuItem choice = kiesMenuItem();
        while (choice != MenuItem.EXIT) {
            switch (choice) {
                case NEW_PERS:
                    invoerNieuwePersoon();
                    break;
                case NEW_ONGEHUWD_GEZIN:
                    invoerNieuwGezin();
                    break;
                case NEW_HUWELIJK:
                    invoerHuwelijk();
                    break;
                case SCHEIDING:
                    invoerScheiding();
                    break;
                case SHOW_PERS:
                    toonPersoonsgegevens();
                    break;
                case SHOW_GEZIN:
                    toonGezinsgegevens();
                    break;
                case LOAD_FILE:
                    loadFile();
                    break;
                case SAVE_FILE:
                    saveFile();
                    break;
                case SHOW_TREE:
                    showTree();
                    break;
                case CREATE_PSUEDO:
                    createPsuedo();
                    break;
            }
            choice = kiesMenuItem();
        }
    }

    Administratie getAdmin() {
        return controller.getAdministratie();
    }

    void createPsuedo(){
        //from stamboom.domain.StamboomTest.java in test packages
        Administratie adm = controller.getAdministratie();
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
        System.out.println("8 psuedo personen aangemaakt met relatie naar elkaar");
    }
    
    void showTree(){
        int nr = 0;
        while(nr < 1){
            nr = readInt("persoons nummer");
            if(nr<1)continue;//quit if less then 1
            Persoon p = controller.getAdministratie().getPersoon(nr);
            System.out.println(p.stamboomAlsString());
        }
    }
    
    void loadFile(){
        String filename = null;
        while(filename==null){
            filename = readString("wat is de bestandsnaam");
            
            File f = new File(filename);
            if(!f.isFile() || !f.canRead()) continue;//file doesn't exist or can not be read
            
            try
            {
                //load the file
                controller.deserialize(f);
            }catch(Exception e){
                if(readString("Fout tijdens lezen, opnieuw proberen?[Y/N]").equalsIgnoreCase("Y")){
                    continue;//retry
                }else{
                    break;//quit
                }
            }
        }
    }
    
    void saveFile(){
        String filename = null;
        while(filename==null){
            filename = readString("wat is de bestandsnaam");
            File f;
            try 
            {
                f = new File(filename);
            }catch(Exception ex){
                filename = null;
                continue;
            }
            try
            {
                //save the file
                controller.serialize(f);
            }catch(Exception e){
                if(readString("Fout tijdens opslaan, opnieuw proberen?[Y/N]").equalsIgnoreCase("Y")){
                    continue;//retry
                }else{
                    break;//quit
                }
            }
        }
    }
    
    
    
    void invoerNieuwePersoon() {
        Geslacht geslacht = null;
        while (geslacht == null) {
            String g = readString("wat is het geslacht (m/v)");
            if (g.toLowerCase().charAt(0) == 'm') {
                geslacht = Geslacht.MAN;
            }
            if (g.toLowerCase().charAt(0) == 'v') {
                geslacht = Geslacht.VROUW;
            }
        }

        String[] vnamen;
        vnamen = readString("voornamen gescheiden door spatie").split(" ");

        String tvoegsel;
        tvoegsel = readString("tussenvoegsel");

        String anaam;
        anaam = readString("achternaam");

        Calendar gebdat;
        gebdat = readDate("geboortedatum");

        String gebplaats;
        gebplaats = readString("geboorteplaats");

        Gezin ouders;
        toonGezinnen();
        String gezinsString = readString("gezinsnummer van eventueel ouderlijk gezin");
        if (gezinsString.equals("")) {
            ouders = null;
        } else {
            ouders = getAdmin().getGezin(Integer.parseInt(gezinsString));
        }

        getAdmin().addPersoon(geslacht, vnamen, anaam, tvoegsel, gebdat,
                gebplaats, ouders);
    }

    void invoerNieuwGezin() {
        System.out.println("wie is de eerste partner?");
        Persoon partner1 = selecteerPersoon();
        if (partner1 == null) {
            System.out.println("onjuiste invoer eerste partner");
            return;
        }
        System.out.println("wie is de tweede partner?");
        Persoon partner2 = selecteerPersoon();
        Gezin gezin = getAdmin().addOngehuwdGezin(partner1, partner2);
        if (gezin == null) {
            System.out.println("gezin is niet geaccepteerd");
        }
    }

    void invoerHuwelijk() {
        System.out.println("wie is de eerste partner?");
        Persoon partner1 = selecteerPersoon();
        if (partner1 == null) {
            System.out.println("onjuiste invoer eerste partner");
            return;
        }
        System.out.println("wie is de tweede partner?");
        Persoon partner2 = selecteerPersoon();
        if (partner2 == null) {
            System.out.println("onjuiste invoer tweede partner");
            return;
        }
        Calendar datum = readDate("datum van huwelijk");
        Gezin g = getAdmin().addHuwelijk(partner1, partner2, datum);
        if (g == null) {
            System.out.println("huwelijk niet voltrokken");
        }
    }

    void invoerScheiding() {
        selecteerGezin();
        int gezinsNr = readInt("kies gezinsnummer");
        input.nextLine();
        Calendar datum = readDate("datum van scheiding");
        Gezin g = getAdmin().getGezin(gezinsNr);
        if (g != null) {
            boolean gelukt = getAdmin().setScheiding(g,datum);
            if (!gelukt) {
                System.out.println("scheiding niet geaccepteerd");
            }
        } else {
            System.out.println("gezin onbekend");
        }
    }

    Persoon selecteerPersoon() {
        String naam = readString("wat is de achternaam");
        ArrayList<Persoon> personen = getAdmin().getPersonenMetAchternaam(naam);
        for (Persoon p : personen) {
            System.out.println(p.getNr() + "\t" + p.getNaam() + " " + datumString(p.getGebDat()));
        }
        int invoer = readInt("selecteer persoonsnummer");
        input.nextLine();
        Persoon p = getAdmin().getPersoon(invoer);
        return p;
    }

    Gezin selecteerGezin() {
        String naam = readString("gezin van persoon met welke achternaam");
        ArrayList<Persoon> kandidaten = getAdmin().getPersonenMetAchternaam(naam);
        for (Persoon p : kandidaten) {
            List<Gezin> gezinnen = p.getAlsOuderBetrokkenIn();
            System.out.print(p.getNr() + "\t" + p.getNaam() + " " + datumString(p.getGebDat()));
            System.out.print(" gezinnen: ");
            for (Gezin gezin : gezinnen){
                System.out.print(" " + gezin.getNr());
            }
            System.out.println();
        }
        int invoer = readInt("selecteer gezinsnummer");
        input.nextLine();
        return getAdmin().getGezin(invoer);
    }

    MenuItem kiesMenuItem() {
        System.out.println();
        for (MenuItem m : MenuItem.values()) {
            System.out.println(m.ordinal() + "\t" + m.getOmschr());
        }
        System.out.println();
        int maxNr = MenuItem.values().length - 1;
        int nr = readInt("maak een keuze uit 0 t/m " + maxNr);
        while (nr < 0 || nr > maxNr) {
            nr = readInt("maak een keuze uit 0 t/m " + maxNr);
        }
        input.nextLine();
        return MenuItem.values()[nr];
    }

    void toonPersoonsgegevens() {
        Persoon p = selecteerPersoon();
        if (p == null) {
            System.out.println("persoon onbekend");
        } else {
            System.out.println(p.beschrijving());
        }
    }

    void toonGezinsgegevens() {
        Gezin g = selecteerGezin();
        if (g == null) {
            System.out.println("gezin onbekend");
        } else {
            System.out.println(g.beschrijving());
        }
    }

    void toonGezinnen() {
        int nr = 1;
        Gezin r = getAdmin().getGezin(nr);
        while (r != null) {
            System.out.println(r.toString());
            nr++;
            r = getAdmin().getGezin(nr);
        }
    }

    static void printSpaties(int n) {
        System.out.print(StringUtilities.spaties(n));
    }

    Calendar readDate(String helptekst) {
        String datumString = readString(helptekst + "; voer datum in (dd-mm-jjjj)");
        try {
            return StringUtilities.datum(datumString);
        } catch (IllegalArgumentException exc) {
            System.out.println(exc.getMessage());
            return readDate(helptekst);
        }
    }

    int readInt(String helptekst) {
        boolean invoerOk = false;
        int invoer = -1;
        while (!invoerOk) {
            try {
                System.out.print(helptekst + " ");
                invoer = input.nextInt();
                invoerOk = true;
            } catch (InputMismatchException exc) {
                System.out.println("Let op, invoer moet een getal zijn!");
                input.nextLine();
            }

        }
        return invoer;
    }

    String readString(String helptekst) {
        System.out.print(helptekst + " ");
        String invoer = input.nextLine();
        return invoer;
    }

    String datumString(Calendar datum) {
        return StringUtilities.datumString(datum);
    }

    public static void main(String[] arg) {
        StamboomController controller = new StamboomController();

        StamboomConsole console = new StamboomConsole(controller);
        //console.startMenu();
    }
}
