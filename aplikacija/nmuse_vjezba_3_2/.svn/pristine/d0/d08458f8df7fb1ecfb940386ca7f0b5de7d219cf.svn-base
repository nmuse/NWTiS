/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.nmuse.vjezba_03.konfiguracije;

import java.util.Properties;

/**
 *
 * @author NWTiS_4
 */
public interface Konfiguracija {
    void ucitajKonfiguraciju() throws NeispravnaKonfiguracija;
    void ucitajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija;
    void spremiKonfiguraciju() throws NeispravnaKonfiguracija;
    void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija;
    void azurirajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija;
    void dodajKonfiguraciju(String nazivDatoteke) throws NeispravnaKonfiguracija;
    void dodajKonfiguraciju(Properties postavke);
    void kopirajKonfiguraciju(Properties postavke);
    Properties dajSvePostavke();
    boolean obrisiSvePostavke();
    String dajPostavku(String kljuc);
    boolean spremiPostavku(String kljuc, String vrijednost);
    boolean azurirajPostavku(String kljuc, String vrijednost);
    boolean postojiPostavka(String kljuc);
    boolean obrisiPostavku(String kljuc);
    
}
