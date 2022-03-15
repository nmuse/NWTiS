package org.foi.nwtis.nmuse_aplikacija_1.podaci;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

public class KorisnikDAO {

    public String obradiAddZahtjev(String komanda, PostavkeBazaPodataka pbp) {
        String komandaBezZnakova = komanda.replaceAll("\"", "");
        String[] zahtjev = komandaBezZnakova.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik FROM korisnici WHERE korisnik=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            Connection con;

            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[1]);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");

                if (zahtjev[1].equals(korisnik)) {
                    return "ERROR 18 Korisnik vec postoji";
                }
            }

            dodajKorisnika(zahtjev[1], zahtjev[2], zahtjev[3], zahtjev[4], pbp);

        } catch (SQLException | ClassNotFoundException ex) {

        }

        return "OK";

    }

    public String traziKorisnika(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik,lozinka FROM korisnici WHERE korisnik=? AND lozinka=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[1]);
            s.setString(2, zahtjev[2]);
            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                String lozinka = rs.getString("lozinka");
                if (zahtjev[1].equals(korisnik) && zahtjev[2].equals(lozinka)) {
                    //korisnik postoji i pronaden je
                    return "OK";

                }
            }
            return "ERROR 11 korisnik ili lozinka ne odgovaraju";
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "ERROR 11 korisnik ili lozinka ne odgovaraju";
    }

    public String traziKorisnikaSamoKorisnickoIme(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik FROM korisnici WHERE korisnik=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[1]);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                if (zahtjev[1].equals(korisnik)) {
                    //korisnik postoji i pronaden je
                    return "OK";

                }
            }
            return "ERROR 17 korisnik ne postoji";

        } catch (SQLException | ClassNotFoundException ex) {

        }
        return "ERROR 17 korisnik ne postoji";
    }

    public String traziKorisnikaList(String korisnik2, PostavkeBazaPodataka pbp) {

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik,prezime,ime FROM korisnici WHERE korisnik=?";
        String odgovorVrati = "";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, korisnik2);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                String prezime = rs.getString("prezime");
                String ime = rs.getString("ime");

                if (korisnik2.equals(korisnik)) {

                    odgovorVrati = "OK " + "\"" + korisnik + "\t" + prezime + "\t" + ime + "\"";

                    return odgovorVrati;
                }
            }
            return "ERROR 17 korisnik ne postoji";

        } catch (SQLException | ClassNotFoundException ex) {

        }
        return "ERROR 17 korisnik ne postoji";
    }

    public String traziKorisnikaListAll(PostavkeBazaPodataka pbp) {

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik,prezime,ime FROM korisnici";
        String odgovorVrati = "OK ";
        try {
            Class.forName(pbp.getDriverDatabase(url));
            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                String prezime = rs.getString("prezime");
                String ime = rs.getString("ime");

                odgovorVrati = odgovorVrati + "\"" + korisnik + "\t" + prezime + "\t" + ime + "\" ";
            }
            if (odgovorVrati.isEmpty() == true) {
                return "ERROR 17 nema korisnika";
            }
            return odgovorVrati;

        } catch (SQLException | ClassNotFoundException ex) {

        }
        return "ERROR 17 nema korisnika";
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
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public boolean dodajKorisnika(String korisnicko_ime, String lozinka, String prezime, String ime, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "INSERT INTO korisnici (korisnik, ime, prezime, lozinka) "
                + "VALUES (?, ?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, korisnicko_ime);
                s.setString(2, ime);
                s.setString(3, prezime);
                s.setString(4, lozinka);

                int brojAzuriranja = s.executeUpdate();

                return brojAzuriranja == 1;

            } catch (SQLException ex) {
                Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(KorisnikDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
