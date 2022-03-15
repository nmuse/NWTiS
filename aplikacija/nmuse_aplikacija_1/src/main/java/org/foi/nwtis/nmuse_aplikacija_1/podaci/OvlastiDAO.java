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

public class OvlastiDAO {

    
    public String provjeraPodrucja(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik,podrucje,status FROM ovlasti WHERE korisnik=? AND podrucje=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[4]);
            s.setString(2, zahtjev[3]);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                String podrucje = rs.getString("podrucje");
                String status = rs.getString("status");

                if (zahtjev[4].equals(korisnik)) {

                    if (zahtjev[3].equals(podrucje)) {
                        return status;
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException ex) {
            
        }

        return "nema";

    }
    
    
    public String provjeraPodrucjaAuthor(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik,podrucje,status FROM ovlasti WHERE korisnik=? AND podrucje=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[1]);
            s.setString(2, zahtjev[3]);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                String podrucje = rs.getString("podrucje");
                String status = rs.getString("status");

                if (zahtjev[1].equals(korisnik)) {

                    if (zahtjev[3].equals(podrucje)) {
                        return status;
                    }
                }
            }

        } catch (SQLException | ClassNotFoundException ex) {
            
        }

        return "nema";

    }
    

    public List<String> provjeraPodrucjaRights(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "SELECT korisnik,podrucje,status FROM ovlasti WHERE korisnik=?";
        List<String> listaPodrucja = new ArrayList<String>();
        try {
            Class.forName(pbp.getDriverDatabase(url));
            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[3]);

            ResultSet rs = s.executeQuery();

            while (rs.next()) {
                String korisnik = rs.getString("korisnik");
                String podrucje = rs.getString("podrucje");
                String status = rs.getString("status");

                if (zahtjev[3].equals(korisnik)) {

                    if (status.equals("aktivno")) {
                        listaPodrucja.add(podrucje);
                    }
                }
            }
            if (listaPodrucja.isEmpty()) {
                return null;
            }
            return listaPodrucja;

        } catch (SQLException | ClassNotFoundException ex) {

            /*
            if (status.equals("aktivno")) {
                            //stavit da je neaktivno
                            return "OK";
                        } else if (status.equals("neaktivno")) {
                            
                            upit2 = "UPDATE ovlasti SET status=\"aktivno\" WHERE korisnik=? AND podrucje = ?";
                            System.out.println("1111111");
                            s2 = con.prepareStatement(upit2);
                            System.out.println("2222222");
                            s.setString(1, zahtjev[4]);
                            s.setString(2, zahtjev[3]);
                            
                            rs1 = s2.executeQuery();
                            
                            return "OK";
                        } else {
                            

                            String upit3 = "INSERT INTO ovlasti (korisnik, podrucje, status) VALUES (?, ?, ?)";
                            s = con.prepareStatement(upit2);

                            s.setString(1, zahtjev[4]);
                            s.setString(2, zahtjev[3]);
                            s.setString(3, "aktivno");

                           // rs2 = s.executeQuery();
                            return "OK";
                        }
             */
        }

        return null;

    }

    public String postaviAktivnoPodrucje(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "UPDATE ovlasti SET status='aktivno' WHERE korisnik=? AND podrucje = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, zahtjev[4]);
                s.setString(2, zahtjev[3]);

                int rs = s.executeUpdate();
                return "OK";
            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "ERROR 18 greska u postavljanju aktivnog podrucja (iz neaktivnog)";
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String postaviNeaktivnoPodrucje(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "UPDATE ovlasti SET status='neaktivno' WHERE korisnik=? AND podrucje = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, zahtjev[4]);
                s.setString(2, zahtjev[3]);

                int rs = s.executeUpdate();
                return "OK";
            } catch (SQLException ex) {
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
            return "ERROR 18 greska u postavljanju neaktivnog podrucja (iz aktivnog)";
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String dodajNovuOvlast(String komanda, PostavkeBazaPodataka pbp) {

        String[] zahtjev = komanda.split(" ");

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "INSERT INTO ovlasti (korisnik, podrucje, status) VALUES (?, ?, ?)";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            Connection con;
            con = DriverManager.getConnection(url, bpkorisnik, bplozinka);

            PreparedStatement s = con.prepareStatement(upit);

            s.setString(1, zahtjev[4]);
            s.setString(2, zahtjev[3]);
            s.setString(3, "aktivno");

            int rs = s.executeUpdate();
            return "OK";
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR 18 greska u dodavanju ovlasti";
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
            return "ERROR 17 korisnik ne postoji";

        } catch (SQLException | ClassNotFoundException ex) {

        }
        return "ERROR 17 korisnik ne postoji";
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
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OvlastiDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
