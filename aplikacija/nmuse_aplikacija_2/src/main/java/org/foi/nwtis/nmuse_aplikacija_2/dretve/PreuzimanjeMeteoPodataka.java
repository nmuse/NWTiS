package org.foi.nwtis.nmuse_aplikacija_2.dretve;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.AirportsDAO;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MeteoDAO;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MyAirportsDAO;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

public class PreuzimanjeMeteoPodataka extends Thread {

    private PostavkeBazaPodataka pbp;

    private String datumPreuzimanja;
    private int trajanjeCiklusa;
    private int trajanjePauze;
    private boolean kraj = false;
    private MyAirportsDAO mojDao;
    private List<MyAirport> listaMojihAerodroma;
    private List<MeteoOriginal> listaMeteoPodataka;
    private java.sql.Date datum;
    private AirportsDAO adao;
    private OWMKlijent owmKlijent;
    private String owmApiKey;
    private MeteoDAO meteodao;

    public PreuzimanjeMeteoPodataka(PostavkeBazaPodataka pbp) {
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

            long pocetakCiklusa = System.currentTimeMillis();
            System.out.println("Počinje preuzimanje podataka!");
            listaMojihAerodroma = mojDao.dohvatiMojeAerodrome(pbp);
            listaMeteoPodataka = new ArrayList<>();

            for (int i = 0; i < listaMojihAerodroma.size(); i++) {
                List<String> lat_long = dajLokaciju(listaMojihAerodroma.get(i).getIdent());
                MeteoOriginal mo = owmKlijent.getRealTimeWeatherOriginal(lat_long.get(1), lat_long.get(0));
                mo.setIdent(listaMojihAerodroma.get(i).getIdent());
                listaMeteoPodataka.add(mo);

            }
            meteodao.unesiMeteoPodatke(listaMeteoPodataka, pbp);

            long krajCiklusa = System.currentTimeMillis();
            long trajanje_Ciklusa = krajCiklusa - pocetakCiklusa;

            if ((trajanjeCiklusa * 1000) > trajanje_Ciklusa) {
                try {
                    Thread.sleep((trajanjeCiklusa * 1000) - trajanje_Ciklusa);
                } catch (InterruptedException ex) {
                    Logger.getLogger(PreuzimanjeMeteoPodataka.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        System.out.println("Preuzimanje podataka zaustavljeno!");
        super.run(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public synchronized void start() {
        boolean status = Boolean.parseBoolean(pbp.dajPostavku("meteo.preuzimanje.status"));
        if (!status) {
            System.out.println("Ne preuzimamo ništa!");
            return;
        }

        this.trajanjeCiklusa = Integer.parseInt(pbp.dajPostavku("meteo.preuzimanje.ciklus"));
        this.owmApiKey = pbp.dajPostavku("OpenWeather.apikey");

        owmKlijent = new OWMKlijent(owmApiKey);

        mojDao = new MyAirportsDAO();

        adao = new AirportsDAO();
        meteodao = new MeteoDAO();

        super.start();
    }

    private List<String> dajLokaciju(String ident) {
        Airport a = adao.dajAirport(ident, pbp);
        String lat_long = a.getCoordinates();
        return Arrays.asList(lat_long.split(", "));
    }

}
