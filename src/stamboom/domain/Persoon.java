/*Bijgewerkt door Rick Rongen*/
package stamboom.domain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import stamboom.util.StringUtilities;

public class Persoon implements java.io.Serializable {

    // ********datavelden**************************************
    private final int nr;
    private final String[] voornamen;
    private final String achternaam;
    private final String tussenvoegsel;
    private final Calendar gebDat;
    private final String gebPlaats;
    private Gezin ouderlijkGezin;
    private final List<Gezin> alsOuderBetrokkenIn;
    private transient ObservableList<Gezin> oAlsOuderBetrokkenIn;
    private final Geslacht geslacht;

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
        oAlsOuderBetrokkenIn = FXCollections.observableList(alsOuderBetrokkenIn);
    }
    
    // ********constructoren***********************************
    /**
     * er wordt een persoon gecreeerd met persoonsnummer persNr en met als
     * voornamen vnamen, achternaam anaam, tussenvoegsel tvoegsel, geboortedatum
     * gebdat, gebplaats, geslacht g en een gegeven ouderlijk gezin (mag null
     * (=onbekend) zijn); NB. de eerste letter van een voornaam, achternaam en
     * gebplaats wordt naar een hoofdletter omgezet, alle andere letters zijn
     * kleine letters; het tussenvoegsel is zo nodig in zijn geheel
     * geconverteerd naar kleine letters.
     *
     */
    Persoon(int persNr, String[] vnamen, String anaam, String tvoegsel,
            Calendar gebdat, String gebplaats, Geslacht g, Gezin ouderlijkGezin) {
        //todo opgave 1
        //throw new UnsupportedOperationException();
        nr = persNr;
        voornamen = vnamen;
        for (int i = 0; i < vnamen.length; i++) {
            vnamen[i] = ToFirstCap(vnamen[i]);
        }
        achternaam = ToFirstCap(anaam);
        tussenvoegsel = tvoegsel.toLowerCase();
        gebDat = gebdat;
        gebPlaats = ToFirstCap(gebplaats);
        geslacht = g;
        this.ouderlijkGezin = ouderlijkGezin;
        alsOuderBetrokkenIn = new ArrayList<>();
        oAlsOuderBetrokkenIn = FXCollections.observableList(alsOuderBetrokkenIn);
    }

    // ********methoden****************************************
    /**
     * @return de achternaam van deze persoon
     */
    
    private String ToFirstCap(String input){
        return input.trim().substring(0,1).toUpperCase() + input.trim().substring(1).toLowerCase();
    }
    
    public String getAchternaam() {
        return achternaam;
    }

    /**
     * @return de geboortedatum van deze persoon
     */
    public Calendar getGebDat() {
        return gebDat;
    }

    /**
     *
     * @return de geboorteplaats van deze persoon
     */
    public String getGebPlaats() {
        return gebPlaats;
    }

    /**
     *
     * @return het geslacht van deze persoon
     */
    public Geslacht getGeslacht() {
        return geslacht;
    }

    /**
     *
     * @return de voorletters van de voornamen; elke voorletter wordt gevolgd
     * door een punt
     */
    public String getInitialen() {
        StringBuilder builder = new StringBuilder();
        //append each item
        for (String initiaal : voornamen) {
            builder.append(initiaal.toCharArray()[0]);
            builder.append(".");
        }
        //create string
        return builder.toString();
    }

    /**
     *
     * @return de initialen gevolgd door een eventueel tussenvoegsel en
     * afgesloten door de achternaam; initialen, voorzetsel en achternaam zijn
     * gescheiden door een spatie
     */
    public String getNaam() {
        //create full name
        return getInitialen() + " " + tussenvoegsel +(tussenvoegsel.length() > 0 ? " " : "") + achternaam;
    }

    /**
     * @return het nummer van deze persoon
     */
    public int getNr() {
        return nr;
    }

    /**
     * @return het ouderlijk gezin van deze persoon, indien bekend, anders null
     */
    public Gezin getOuderlijkGezin() {
        return ouderlijkGezin;
    }

    /**
     * @return het tussenvoegsel van de naam van deze persoon (kan een lege
     * string zijn)
     */
    public String getTussenvoegsel() {
        return tussenvoegsel;
    }

    /**
     * @return alle voornamen onderling gescheiden door een spatie
     */
    public String getVoornamen() {
        StringBuilder init = new StringBuilder();
        for (String s : voornamen) {
            init.append(s).append(' ');
        }
        return init.toString().trim();
    }

    /**
     * @return de standaardgegevens van deze mens: naam (geslacht) geboortedatum
     */
    public String standaardgegevens() {
        return getNaam() + " (" + getGeslacht() + ") " + StringUtilities.datumString(gebDat);
    }

    @Override
    public String toString() {
        return standaardgegevens();
    }

    /**
     * @return de gezinnen waar deze persoon bij betrokken is
     */
    public List<Gezin> getAlsOuderBetrokkenIn() {
        return (List<Gezin>) Collections.unmodifiableList(alsOuderBetrokkenIn);
    }

    /**
     * Als het ouderlijk gezin van deze persoon nog onbekend is dan wordt deze
     * persoon een kind van ouderlijkGezin en tevens wordt deze persoon als kind
     * in dat gezin geregistreerd Als de ouders bij aanroep al bekend zijn,
     * verandert er niets
     *
     * @param ouderlijkGezin
     * @return of ouderlijk gezin kon worden toegevoegd
     */
    boolean setOuders(Gezin ouderlijkGezin) {
        if(this.ouderlijkGezin == null){
            ouderlijkGezin.breidUitMet(this);
            this.ouderlijkGezin = ouderlijkGezin;
            return true;
        }
        return false;
    }

    /**
     * @return voornamen, eventueel tussenvoegsel en achternaam, geslacht,
     * geboortedatum, namen van de ouders, mits bekend, en nummers van de
     * gezinnen waarbij deze persoon betrokken is (geweest)
     */
    public String beschrijving() {
        StringBuilder sb = new StringBuilder();

        sb.append(standaardgegevens());

        if (ouderlijkGezin != null) {
            sb.append("; 1e ouder: ").append(ouderlijkGezin.getOuder1().getNaam());
            if (ouderlijkGezin.getOuder2() != null) {
                sb.append("; 2e ouder: ").append(ouderlijkGezin.getOuder2().getNaam());
            }
        }
        if (!alsOuderBetrokkenIn.isEmpty()) {
            sb.append("; is ouder in gezin ");

            for (Gezin g : alsOuderBetrokkenIn) {
                sb.append(g.getNr()).append(" ");
            }
        }

        return sb.toString();
    }

    /**
     * als g nog niet bij deze persoon staat geregistreerd wordt g bij deze
     * persoon geregistreerd en anders verandert er niets
     *
     * @param g een nieuw gezin waarin deze persoon een ouder is
     *
     */
    void wordtOuderIn(Gezin g) {
        if (!alsOuderBetrokkenIn.contains(g)) {
            oAlsOuderBetrokkenIn.add(g);
        }
    }

    /**
     *
     *
     * @param andereOuder mag null zijn
     * @return het ongehuwde gezin met de andere ouder ; mits bestaand anders
     * null
     */
    public Gezin heeftOngehuwdGezinMet(Persoon andereOuder) {
        for(Gezin gezin : alsOuderBetrokkenIn){
            if(gezin.getOuder1() == andereOuder || gezin.getOuder2() == andereOuder) return gezin;
        }
        return null;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum getrouwd is, anders false
     */
    public boolean isGetrouwdOp(Calendar datum) {
        for (Gezin gezin : alsOuderBetrokkenIn) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param datum
     * @return true als de persoon kan trouwen op datum, hierbij wordt rekening
     * gehouden met huwelijken in het verleden en in de toekomst
     * Alleen meerderjarige (18+) personen kunnen trouwen.
     */
    public boolean kanTrouwenOp(Calendar datum) {
        Calendar meerderjarigDatum = ((GregorianCalendar)this.gebDat.clone());
        meerderjarigDatum.add(Calendar.YEAR, 18);
        if(datum.compareTo(meerderjarigDatum) < 1){
            return false;
        }

        for (Gezin gezin : alsOuderBetrokkenIn) {
            if (gezin.heeftGetrouwdeOudersOp(datum)) {
                return false;
            } else {
                Calendar huwdatum = gezin.getHuwelijksdatum();
                if (huwdatum != null && huwdatum.after(datum)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     *
     * @param datum
     * @return true als persoon op datum gescheiden is, anders false
     */
    public boolean isGescheidenOp(Calendar datum) {
        //if(alsOuderBetrokkenIn.stream().anyMatch((g)->g.isHuwelijkOp(datum))) return true;//stream version
        for(Gezin g : this.alsOuderBetrokkenIn){
            if(g.isHuwelijkOp(datum))return true;
        }
        return false;
    }

    /**
     * ********* de rest wordt in opgave 2 verwerkt ****************
     */
    /**
     *
     * @return het aantal personen in de stamboom van deze persoon (ouders,
     * grootouders etc); de persoon zelf telt ook mee
     */
    public int afmetingStamboom() {
        //todo opgave 2
        int i = 1;
        
        if(ouderlijkGezin!=null){
            i+=ouderlijkGezin.getOuder1().afmetingStamboom();//ouder 1
            if(ouderlijkGezin.getOuder2() != null) i += ouderlijkGezin.getOuder2().afmetingStamboom();//eventuele ouder 2
        }
                
        return i;
    }

    /**
     * de lijst met de items uit de stamboom van deze persoon wordt toegevoegd
     * aan lijst, dat wil zeggen dat begint met de toevoeging van de
     * standaardgegevens van deze persoon behorende bij generatie g gevolgd door
     * de items uit de lijst met de stamboom van de eerste ouder (mits bekend)
     * en gevolgd door de items uit de lijst met de stamboom van de tweede ouder
     * (mits bekend) (het generatienummer van de ouders is steeds 1 hoger)
     *
     * @param lijst
     * @param g >=0, het nummer van de generatie waaraan deze persoon is
     * toegewezen;
     */
    void voegJouwStamboomToe(ArrayList<PersoonMetGeneratie> lijst, int g) {
        lijst.add(new PersoonMetGeneratie(toString(),g));
        if(ouderlijkGezin!=null){
            ouderlijkGezin.getOuder1().voegJouwStamboomToe(lijst, g+1);//ouder 1
            if(ouderlijkGezin.getOuder2()!=null)ouderlijkGezin.getOuder2().voegJouwStamboomToe(lijst, g+1);//eventuele ouder 2
        }
        
    }

    /**
     *
     * @return de stamboomgegevens van deze persoon in de vorm van een String:
     * op de eerste regel de standaardgegevens van deze persoon, gevolgd door de
     * stamboomgegevens van de eerste ouder (mits bekend) en gevolgd door de
     * stamboomgegevens van de tweede ouder (mits bekend); formattering: iedere
     * persoon staat op een aparte regel en afhankelijk van het
     * generatieverschil worden er per persoon 2*generatieverschil spaties
     * ingesprongen;
     *
     * bijv voor M.G. Pieterse met ouders, grootouders en overgrootouders,
     * inspringen is in dit geval met underscore gemarkeerd: <br>
     *
     * M.G. Pieterse (VROUW) 5-5-2004<br>
     * __L. van Maestricht (MAN) 27-6-1953<br>
     * ____A.G. von Bisterfeld (VROUW) 13-4-1911<br>
     * ______I.T.M.A. Goldsmid (VROUW) 22-12-1876<br>
     * ______F.A.I. von Bisterfeld (MAN) 27-6-1874<br>
     * ____H.C. van Maestricht (MAN) 17-2-1909<br>
     * __J.A. Pieterse (MAN) 23-6-1964<br>
     * ____M.A.C. Hagel (VROUW) 12-0-1943<br>
     * ____J.A. Pieterse (MAN) 4-8-1923<br>
     */
    public String stamboomAlsString() {
        StringBuilder builder = new StringBuilder();
        //todo opgave 2
        ArrayList<PersoonMetGeneratie> tree = new ArrayList<>();
        voegJouwStamboomToe(tree, 0);
        for(PersoonMetGeneratie p : tree){
            for(int i = 0; i < p.getGeneratie(); i++) builder.append("  ");
            builder.append(p.getPersoonsgegevens()).append(System.lineSeparator());
        }
        return builder.toString();
    }
    
    public ObservableList<Gezin> getOAlsOuderBetrokkenIn(){
        return (ObservableList<Gezin>) FXCollections.unmodifiableObservableList(oAlsOuderBetrokkenIn);
    }
}
