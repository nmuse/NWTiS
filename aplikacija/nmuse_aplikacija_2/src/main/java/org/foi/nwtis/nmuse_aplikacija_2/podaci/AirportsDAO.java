package org.foi.nwtis.nmuse_aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Airport;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.rest.podaci.Lokacija;

public class AirportsDAO {
    
    
    public Airport dajAirport(String ident, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "select ident,type,name,elevation_ft,continent,iso_country,iso_region,"
                + "municipality,gps_code,iata_code,local_code,"
                + "coordinates from airports where ident=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                 PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, ident);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident1 = rs.getString("ident");
                    String type = rs.getString("type");
                    String name = rs.getString("name");
                    String elevation_ft = rs.getString("elevation_ft");
                    String continent = rs.getString("continent");
                    String iso_country = rs.getString("iso_country");
                    String iso_region = rs.getString("iso_region");
                    String municipality = rs.getString("municipality");
                    String gps_code = rs.getString("gps_code");
                    String iata_code = rs.getString("iata_code");
                    String local_code = rs.getString("local_code");
                    String coordinates = rs.getString("coordinates");
                    Airport a = new Airport(ident1, type, name, elevation_ft, continent, iso_country,
                            iso_region, municipality, gps_code, iata_code, local_code, coordinates);
					return a;
                }
            } catch (SQLException ex) {
                Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    

    public List<Aerodrom> dajAerodromeFilter(String naziv, String drzava, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "";
        List<Aerodrom> aerodromiFilter = new ArrayList<>();
        if (naziv == null && drzava == null) {
            upit = "SELECT ident, name, iso_country, coordinates FROM airports";
        } else if (naziv != null && drzava != null) {
            upit = "SELECT ident, name, iso_country, coordinates FROM airports WHERE name LIKE ? AND iso_country = ?";
        } else if (naziv != null && drzava == null) {
            upit = "SELECT ident, name, iso_country, coordinates FROM airports WHERE name LIKE ?";
        } else if (naziv == null && drzava != null) {
            upit = "SELECT ident, name, iso_country, coordinates FROM airports WHERE iso_country = ?";
        }

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                if (naziv != null && drzava != null) {
                    naziv = "%" + naziv + "%";
                    s.setString(1, naziv);
                    s.setString(2, drzava);
                } else if (naziv == null && drzava != null) {
                    s.setString(1, drzava);
                } else if (naziv != null && drzava == null) {
                    naziv = "%" + naziv + "%";
                    s.setString(1, naziv);
                }

                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String name = rs.getString("name");
                    String iso_country = rs.getString("iso_country");
                    String coordinates = rs.getString("coordinates");
                    List<String> lat_lon = Arrays.asList(coordinates.split(", "));
                    Lokacija lokacija = new Lokacija(lat_lon.get(0), lat_lon.get(1));
                    Aerodrom aerodrom = new Aerodrom(ident, name, iso_country, lokacija);
                    aerodromiFilter.add(aerodrom);
                }
                return aerodromiFilter;

            } catch (SQLException ex) {
                Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public Korisnik dohvatiKorisnika(String korisnik, String lozinka, Boolean prijava, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ime, prezime, korisnik, lozinka, emailAdresa, vrijemeKreiranja, vrijemePromjene "
                + "FROM korisnici WHERE korisnik = ?";

        if (prijava) {
            upit += " and lozinka = ?";
        }

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnik);
                if (prijava) {
                    s.setString(2, lozinka);
                }
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String korisnik1 = rs.getString("korisnik");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("emailAdresa");
                    Timestamp kreiran = rs.getTimestamp("vrijemeKreiranja");
                    Timestamp promjena = rs.getTimestamp("vrijemePromjene");

                    Korisnik k = new Korisnik(korisnik1, "******", prezime, ime, email, kreiran, promjena, 0);
                    return k;
                }

            } catch (SQLException ex) {
                Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<Korisnik> dohvatiSveKorisnike(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ime, prezime, emailAdresa, korisnik, lozinka, vrijemeKreiranja, vrijemePromjene FROM korisnici";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            List<Korisnik> korisnici = new ArrayList<>();

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {

                while (rs.next()) {
                    String korisnik1 = rs.getString("korisnik");
                    String ime = rs.getString("ime");
                    String prezime = rs.getString("prezime");
                    String email = rs.getString("emailAdresa");
                    Timestamp kreiran = rs.getTimestamp("vrijemeKreiranja");
                    Timestamp promjena = rs.getTimestamp("vrijemePromjene");
                    Korisnik k = new Korisnik(korisnik1, "******", prezime, ime, email, kreiran, promjena, 0);

                    korisnici.add(k);
                }
                return korisnici;

            } catch (SQLException ex) {
                Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean azurirajKorisnika(Korisnik k, String lozinka, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "UPDATE korisnici SET ime = ?, prezime = ?, emailAdresa = ?, lozinka = ?, "
                + "vrijemePromjene = CURRENT_TIMESTAMP WHERE korisnik = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.getIme());
                s.setString(2, k.getPrezime());
                s.setString(3, k.getEmailAdresa());
                s.setString(4, lozinka);
                s.setString(5, k.getKorisnik());

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean dodajKorisnika(Korisnik k, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO korisnici (ime, prezime, emailAdresa, korisnik, lozinka, vrijemeKreiranja, vrijemePromjene) "
                + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, k.getIme());
                s.setString(2, k.getPrezime());
                s.setString(3, k.getEmailAdresa());
                s.setString(4, k.getKorisnik());
                s.setString(5, k.getLozinka());

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    

}
