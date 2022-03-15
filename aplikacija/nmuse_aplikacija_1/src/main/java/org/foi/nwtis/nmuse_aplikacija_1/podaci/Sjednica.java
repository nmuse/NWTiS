package org.foi.nwtis.nmuse_aplikacija_1.podaci;

/**
 * klasa koja sadrzi podatke o sjednici korisnika
 *
 * @author Nikola Muse
 */
public class Sjednica {

    static public enum StatusSjednice {
        AKTIVNA, NEAKTIVNA
    }

    private int id;
    private String korisnik;
    private long vrijemeKreiranja;
    private long vrijemeDoKadaVrijedi;
    private int maksBrojZahtjeva;

    public Sjednica(int id, String korisnik, long vrijemeKreiranja,
            long vrijemeDoKadaVrijedi, int maksBrojZahtjeva) {
        this.id = id;
        this.korisnik = korisnik;
        this.vrijemeKreiranja = vrijemeKreiranja;
        this.vrijemeDoKadaVrijedi = vrijemeDoKadaVrijedi;
        this.maksBrojZahtjeva = maksBrojZahtjeva;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKorisnik() {
        return korisnik;
    }

    public void setKorisnik(String korisnik) {
        this.korisnik = korisnik;
    }

    public long getVrijemeKreiranja() {
        return vrijemeKreiranja;
    }

    public void setVrijemeKreiranja(long vrijemeKreiranja) {
        this.vrijemeKreiranja = vrijemeKreiranja;
    }

    public long getVrijemeDoKadaVrijedi() {
        return vrijemeDoKadaVrijedi;
    }

    public void setVrijemeDoKadaVrijedi(long vrijemeDoKadaVrijedi) {
        this.vrijemeDoKadaVrijedi = vrijemeDoKadaVrijedi;
    }

    public int getMaksBrojZahtjeva() {
        return maksBrojZahtjeva;
    }

    public void setMaksBrojZahtjeva(int maksBrojZahtjeva) {
        this.maksBrojZahtjeva = maksBrojZahtjeva;
    }

    

}
