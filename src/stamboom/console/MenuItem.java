/*Bijgewerkt door Rick Rongen*/
package stamboom.console;

public enum MenuItem {

    EXIT("exit"),
    NEW_PERS("registreer persoon"),
    NEW_ONGEHUWD_GEZIN("registreer ongehuwd gezin"),
    NEW_HUWELIJK("registreer huwelijk"),
    SCHEIDING("registreer scheiding"),
    SHOW_PERS("toon gegevens persoon"),
    SHOW_GEZIN("toon gegevens gezin"),
    LOAD_FILE("laad een bestand"),
    SAVE_FILE("sla bestand op"),
    SHOW_TREE("geef de stamboom weer"),
    CREATE_PSUEDO("maak psuedo gegevens aan");
    
    private final String omschr;

    private MenuItem(String omschr) {
        this.omschr = omschr;
    }

    /**
     * @return  the omschr
     * @uml.property  name="omschr"
     */
    public String getOmschr() {
        return omschr;
    }
}
