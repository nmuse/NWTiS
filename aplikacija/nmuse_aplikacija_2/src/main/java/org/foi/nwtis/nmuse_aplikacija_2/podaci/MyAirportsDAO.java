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
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.podaci.MyAirport;
import org.foi.nwtis.rest.podaci.Lokacija;

public class MyAirportsDAO {

    public List<MyAirport> dohvatiMojeAerodrome(PostavkeBazaPodataka pbp) {

        String bplozinka = pbp.getUserPassword();
        String bpkorisnik = pbp.getUserUsername();
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();

        String upit = "SELECT ident, username, spremljeno FROM myairports";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<MyAirport> listaAerodroma = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    Statement s = con.createStatement();
                    ResultSet rs = s.executeQuery(upit)) {
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String username = rs.getString("username");
                    MyAirport aerodrom = new MyAirport(ident, username, Boolean.TRUE);
                    listaAerodroma.add(aerodrom);
                }

                return listaAerodroma;
            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<Aerodrom> vratiLetoveAerodroma(PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "select distinct airports.ident, name, iso_country, coordinates from airports join myairports ON myairports.ident=airports.ident";
        List<Aerodrom> letoviAerodroma = new ArrayList<>();

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {

                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String name = rs.getString("name");
                    String iso_country = rs.getString("iso_country");
                    String coordinates = rs.getString("coordinates");
                    List<String> lat_lon = Arrays.asList(coordinates.split(", "));
                    String lat = lat_lon.get(0);
                    String lon = lat_lon.get(1);
                    Lokacija lokacija = new Lokacija(lat, lon);
                    Aerodrom aerodrom = new Aerodrom(ident, name, iso_country, lokacija);
                    letoviAerodroma.add(aerodrom);
                }
                return letoviAerodroma;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public List<String> dohvatiKorisnikeAerodroma(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "select username from myairports where ident=?";
        List<String> korisniciAerodroma = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, icao);
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String username = rs.getString("username");
                    korisniciAerodroma.add(username);
                }
                return korisniciAerodroma;

            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean dodajAerodrom(String korime, Aerodrom aerodrom, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "insert into myairports (ident,username,spremljeno) values (?,?,CURRENT_TIMESTAMP)";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, aerodrom.getIcao());
                s.setString(2, korime);
                
                List<MyAirport> lista=dohvatiMojeAerodrome(pbp);
                for(MyAirport m:lista){
                    if(m.getIdent().equals(aerodrom.getIcao())){
                        if(m.getUsername().equals(korime)){
                            return false;
                        }
                    }
                }
                
                int brojAzuriranja = s.executeUpdate();
                System.out.println("444444444444444444broj azuriranja   "+brojAzuriranja+" jeli azuriran 4444444");
                
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean izbrisiAerodrom(String icao, String korime, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "delete from myairports where username=? and ident=?";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, korime);
                s.setString(2, icao);
                int brojAzuriranja = s.executeUpdate();
                if (brojAzuriranja != 0) {
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public List<Aerodrom> dajAerodromeKorisnika(String korime, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "select airports.ident,airports.name,airports.iso_country,airports.coordinates from airports join myairports on airports.ident=myairports.ident where myairports.username=?";
        List<Aerodrom> aerodromi = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, korime);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String name = rs.getString("name");
                    String iso_country = rs.getString("iso_country");
                    String coordinates = rs.getString("coordinates");
                    List<String> lat_lon = Arrays.asList(coordinates.split(", "));
                    String lat = lat_lon.get(0);
                    String lon = lat_lon.get(1);
                    Lokacija lokacija = new Lokacija(lat, lon);
                    Aerodrom aerodrom = new Aerodrom(ident, name, iso_country, lokacija);
                    aerodromi.add(aerodrom);
                }
                return aerodromi;
            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
