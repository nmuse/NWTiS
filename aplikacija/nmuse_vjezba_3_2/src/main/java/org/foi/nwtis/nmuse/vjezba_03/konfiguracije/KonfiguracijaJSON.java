package org.foi.nwtis.nmuse.vjezba_03.konfiguracije;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class KonfiguracijaJSON extends KonfiguracijaApstraktna {

    public KonfiguracijaJSON(String nazivDatoteke) {
        super(nazivDatoteke);
    }

    @Override
    public void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija {

        this.obrisiSvePostavke();
        if (nazivDatoteke == null || nazivDatoteke.length() == 0) {
            throw new NeispravnaKonfiguracija("Nije definiran naziv datoteke!");
        }
        File f = new File(nazivDatoteke);
        Gson gs = new Gson();

        if (f.exists() && f.isFile()) {

            try {
                this.postavke = gs.fromJson(new FileReader(nazivDatoteke), Properties.class);
            } catch (FileNotFoundException ex) {

                System.out.println("Greska prilikom ucitavanja konfiguracije.");
            }
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '" + nazivDatoteke + "' ne postoji!");
        }
    }

    @Override
    public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
        if (datoteka == null || datoteka.length() == 0) {
            throw new NeispravnaKonfiguracija("Nije definiran naziv datoteke!");
        }
        File f = new File(datoteka);
        Gson gs = new Gson();

        if (!f.exists() || (f.exists() && f.isFile())) {
            try {
                FileWriter file = new FileWriter(datoteka);
                String str = gs.toJson(this.postavke);

                file.write(str);
                file.close();

            } catch (IOException ex) {
                System.out.println("Greska prilikom spremanja konfiguracije.");
            }
        } else {
            throw new NeispravnaKonfiguracija("Datoteka pod nazivom: '" + datoteka + "' ne postoji!");
        }
    }

}
