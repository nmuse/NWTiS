package org.foi.nwtis.nmuse_aplikacija_2.dretve;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.AirplanesDAO;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MyAirportsDAO;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MyAirportsLog;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MyAirportsLogDAO;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.rest.klijenti.OSKlijent;
import org.foi.nwtis.rest.podaci.AvionLeti;

public class PreuzimanjeLetovaAviona extends Thread {

    private PostavkeBazaPodataka pbp;
    private OSKlijent osk;
    private String datumPreuzimanja;
    private int trajanjeCiklusa;
    private int trajanjePauze;
    private boolean kraj = false;
    private String openSkyKorisnik;
    private String openSkyLozinka;
    private String preuzimanjeKraj;
    private java.util.Date date;
    private long datumPreuzimanjaMillis;
    private long datumPreuzimanjaDoMillis;
    private MyAirportsLogDAO mdao;
    private List<MyAirportsLog> listaLogova;
    private MyAirportsDAO mojDao;
    private List<MyAirport> listaMojihAerodroma;
    private List<AvionLeti> listaLetova;
    private AirplanesDAO avioniDAO;
    private List<MyAirportsLog> listaTrenutnogLoga;
    private java.sql.Date datum;

    public PreuzimanjeLetovaAviona(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }

    @Override
    public void interrupt() {
        kraj = true;
        super.interrupt();
    }
    
    @Override
    public void run() {

        while (!kraj) {
            System.out.println("Preuzimamo podatke!");
            
            if (datumPreuzimanjaMillis>=datumPreuzimanjaDoMillis) {
                try {
                    System.out.println("Preuzimanje podataka je zavrseno.Pauza 1 dan.");
                    Thread.sleep(86400000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimanjeLetovaAviona.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                long pocetakCiklusa = System.currentTimeMillis();
                System.out.println("Počinje preuzimanje podataka!");
                listaMojihAerodroma = mojDao.dohvatiMojeAerodrome(pbp);
                datum = new java.sql.Date(datumPreuzimanjaMillis);
                System.out.println(datum);
                listaLogova = mdao.dohvatiLog(datum, pbp);
                datumPreuzimanjaDoMillis = datumPreuzimanjaMillis + 86400000;

                listaTrenutnogLoga = new ArrayList<>();

                for (int i = 0; i < listaMojihAerodroma.size(); i++) {
                    listaLetova = new ArrayList<>();
                    boolean naden = false;
                    for (int j = 0; j < listaLogova.size(); j++) {
                        if (listaLogova.get(j).getIdent().equals(listaMojihAerodroma.get(i).getIdent())) {
                            naden = true;
                        }
                    }
                    if (listaTrenutnogLoga != null) {
                        for (int z = 0; z < listaTrenutnogLoga.size(); z++) {
                            if (listaTrenutnogLoga.get(z).getIdent().equals(listaMojihAerodroma.get(i).getIdent())) {
                                naden = true;
                            }
                        }
                    }
                    if (naden == false) {
                        List<AvionLeti> avl = osk.getDepartures(listaMojihAerodroma.get(i).getIdent(), datumPreuzimanjaMillis / 1000, datumPreuzimanjaDoMillis / 1000);
                        for (int k = 0; k < avl.size(); k++) {
                            if (avl.get(k).getEstArrivalAirport() != null) {
                                listaLetova.add(avl.get(k));
                            }
                        }
                        avioniDAO.dodavanjeLetova(listaLetova,datum, pbp);
                        MyAirportsLog noviLog = new MyAirportsLog(datum, listaMojihAerodroma.get(i).getIdent());
                        listaTrenutnogLoga.add(noviLog);

                    }

                }
                mdao.unosLogovaUBazu(listaTrenutnogLoga, pbp);
                datumPreuzimanjaMillis += 86400000;
                long krajCiklusa=System.currentTimeMillis();
                long trajanje_Ciklusa = krajCiklusa-pocetakCiklusa;
                System.out.println("Zavrseno preuzimanje za 1 dan.");
                
                if((trajanjeCiklusa*1000)>trajanje_Ciklusa){
                    try {
                        Thread.sleep((trajanjeCiklusa*1000)-trajanje_Ciklusa);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PreuzimanjeLetovaAviona.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        System.out.println("Preuzimanje podataka zaustavljeno!");
        super.run(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void start() {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku("preuzimanje.status"));
        if (!status) {
            System.out.println("Ne preuzimamo ništa!");
            return;
        }
        this.datumPreuzimanja = pbp.dajPostavku("preuzimanje.pocetak");
        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("preuzimanje.ciklus"));
        this.trajanjePauze = Integer.parseInt(pbp.dajPostavku("preuzimanje.pauza"));
        this.openSkyKorisnik = pbp.dajPostavku("OpenSkyNetwork.korisnik");
        this.openSkyLozinka = pbp.dajPostavku("OpenSkyNetwork.lozinka");
        this.preuzimanjeKraj = pbp.dajPostavku("preuzimanje.kraj");

        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

        try {

            date = sdf.parse(datumPreuzimanja);
            datumPreuzimanjaMillis = date.getTime();
            datumPreuzimanjaDoMillis = datumPreuzimanjaMillis + 86400000;

        } catch (ParseException ex) {
            Logger.getLogger(PreuzimanjeLetovaAviona.class.getName()).log(Level.SEVERE, null, ex);
        }
        osk = new OSKlijent(openSkyKorisnik, openSkyLozinka);
        mdao = new MyAirportsLogDAO();
        mojDao = new MyAirportsDAO();
        avioniDAO = new AirplanesDAO();
        super.start();
    }

}
