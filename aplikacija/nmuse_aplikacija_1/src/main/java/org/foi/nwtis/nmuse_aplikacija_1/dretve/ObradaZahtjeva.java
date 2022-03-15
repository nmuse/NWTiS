package org.foi.nwtis.nmuse_aplikacija_1.dretve;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_1.podaci.KorisnikDAO;
import org.foi.nwtis.nmuse_aplikacija_1.podaci.OvlastiDAO;
import org.foi.nwtis.nmuse_aplikacija_1.podaci.Sjednica;

public class ObradaZahtjeva extends Thread {

    int brojDretvi = 0;
    int brojDretve = 0;

    int trajanjeSjednice;
    int maksDretvi;
    int port;
    int maksBrojZahtjeva;
    private boolean kraj = false;
    private PostavkeBazaPodataka pbp;
    private int idSjednice = 0;

    int testnaVarijabla = 0;

    protected List<Sjednica> sjednice = new ArrayList<>();

    ServerSocket ss;

    public ObradaZahtjeva(PostavkeBazaPodataka pbp) {
        this.pbp = pbp;
    }

    public void interrupt() {
        kraj = true;
        try {
            ss.close();
        } catch (IOException ex) {
            System.out.println("server se zaustavio");
            Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.interrupt();
    }

    @Override
    public void run() {
        if (!kraj) {
            pokreniServer();

        }
        super.run();
    }

    public synchronized void start() {
        this.trajanjeSjednice = Integer.parseInt(pbp.dajPostavku("sjednica.trajanje"));
        this.maksDretvi = Integer.parseInt(pbp.dajPostavku("maks.dretvi"));
        this.port = Integer.parseInt(pbp.dajPostavku("port"));
        this.maksBrojZahtjeva = Integer.parseInt(pbp.dajPostavku("maks.zahtjeva"));
        super.start();
    }

    public synchronized void pokreniServer() {
        System.out.println("USAO U POKRENI SERVER");
        try {
            ss = new ServerSocket(port);

            while (true) {
                Socket uticnica = ss.accept();
                if (brojDretvi < maksDretvi) {
                    brojDretvi++;
                    DretvaZahtjeva dz = new DretvaZahtjeva(uticnica, brojDretve++);
                    dz.start();
                } else {
                    izvrsiZahtjev(uticnica, "ERROR 01 nema slobodne dretve");
                }

            }
        } catch (IOException ex) {
            System.out.println("Greska pri pokretanju servera.");
        }
    }

    public synchronized void izvrsiZahtjev(Socket uticnica, String odgovor) {
        try {
            try (
                    OutputStream os = uticnica.getOutputStream();
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");) {

                osw.write(odgovor);
                osw.flush();

                uticnica.shutdownOutput();
                uticnica.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(DretvaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public class DretvaZahtjeva extends Thread {

        int brojDretve = 0;

        Socket uticnica;
        String naziv;

        public DretvaZahtjeva(Socket uticnica, int brojDretve) {
            this.uticnica = uticnica;
            this.brojDretve = brojDretve;
            this.naziv = "nmuse_" + brojDretve;
        }

        @Override
        public void run() {
            System.out.println(this.naziv + " Zapocinje s radom.");
            obradiZahtjev();
            brojDretvi--;
            System.out.println("Broj aktivnih dretvi : " + brojDretvi);
        }

        /**
         * razrada funkcionalnosti obrade zahtjeva
         */
        public synchronized void obradiZahtjev() {
            try (InputStream is = uticnica.getInputStream();
                    OutputStream os = uticnica.getOutputStream();
                    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                    OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");) {
                System.out.println("Dretva" + brojDretve + "Spojen korisnik na adresi: "
                        + uticnica.getInetAddress().getHostAddress());

                StringBuilder sb = new StringBuilder();
                while (true) {
                    int i = isr.read();
                    if (i == -1) {
                        break;
                    }
                    sb.append((char) i);
                }
                uticnica.shutdownInput();
                System.out.println("ZAHTJEV: '" + sb.toString() + "'");

                String odgovor = obradiTrazeniZahtjev(sb);

                osw.write(odgovor);
                osw.flush();
                uticnica.shutdownOutput();
                uticnica.close();
            } catch (IOException ex) {
                System.out.println("Greska prilikom upisa ili ispisa podataka");
            }
        }

        public synchronized String obradiAuthenZahtjev(String komanda, PostavkeBazaPodataka pbp) {
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            if (kdao.traziKorisnika(komanda, pbp).equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        long vrijemeProduzeno = System.currentTimeMillis() + trajanjeSjednice;
                        s.setVrijemeDoKadaVrijedi(vrijemeProduzeno);
                        return ("OK " + s.getId() + " "
                                + vrijemeProduzeno + " " + maksBrojZahtjeva);
                    }

                }
                long vrijemeProduzeno = System.currentTimeMillis() + trajanjeSjednice;
                Sjednica novaSjednica = new Sjednica(idSjednice, podaci[1],
                        System.currentTimeMillis(), vrijemeProduzeno, maksBrojZahtjeva);
                sjednice.add(novaSjednica);
                idSjednice++;
                return ("OK " + (idSjednice - 1) + " "
                        + vrijemeProduzeno + " " + maksBrojZahtjeva);

            } else {
                return "ERROR 11 korisnik ili lozinka ne odgovaraju";
            }
        }

        public synchronized String obradiTrazeniZahtjev(StringBuilder sb) {
            String odgovor = "";
            boolean valja = true;

            try {
                valja = provjeriIspravnostKomandeZahtjeva(sb.toString());
            } catch (IOException ex) {
                Logger.getLogger(ObradaZahtjeva.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (valja == true) {

                if (sb.toString().startsWith("ADD")) {
                    KorisnikDAO kdao2 = new KorisnikDAO();
                    odgovor = kdao2.obradiAddZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("AUTHEN")) {
                    odgovor = obradiAuthenZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("LOGOUT")) {
                    odgovor = obradiLogoutZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("GRANT")) {
                    odgovor = obradiGrantZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("REVOKE")) {
                    odgovor = obradiRevokeZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("RIGHTS")) {
                    odgovor = obradiRightsZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("AUTHOR")) {
                    odgovor = obradiAuthorZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("LIST ")) {
                    odgovor = obradiListZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                if (sb.toString().startsWith("LISTALL")) {
                    odgovor = obradiListAllZahtjev(sb.toString(), pbp);
                    return odgovor;
                }
                return "ERROR 10 neispravna komanda";
            } else {
                return "ERROR 10 neispravna komanda";
            }

        }

        public synchronized String obradiLogoutZahtjev(String komanda, PostavkeBazaPodataka pbp) {

            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            if (kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp).equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            s.setVrijemeDoKadaVrijedi(System.currentTimeMillis());
                            s.setMaksBrojZahtjeva(0);
                            sjednice.remove(s);
                            return "OK";
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return "ERROR 17 nema trazenog korisnika";
            }
        }

        public synchronized String obradiGrantZahtjev(String komanda, PostavkeBazaPodataka pbp) {

            OvlastiDAO odao = new OvlastiDAO();
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            String odgovor = kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp);
            if (odgovor.equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            if (s.getMaksBrojZahtjeva() == 0) {
                                return "ERROR 16 broj zahtjeva je 0";
                            } else if (s.getMaksBrojZahtjeva() > 0) {
                                s.setMaksBrojZahtjeva(s.getMaksBrojZahtjeva() - 1);

                                String odgovor1 = odao.provjeraPodrucja(komanda, pbp);

                                if (odgovor1.equals("aktivno")) {
                                    return "ERROR 13 postoji aktivno podrucje";
                                }
                                if (odgovor1.equals("neaktivno")) {
                                    String odgovor2 = odao.postaviAktivnoPodrucje(komanda, pbp);
                                    return odgovor2;
                                }
                                String odgovor3 = odao.dodajNovuOvlast(komanda, pbp);
                                return odgovor3;
                            }
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return odgovor;
            }
        }

        public synchronized String obradiRevokeZahtjev(String komanda, PostavkeBazaPodataka pbp) {

            OvlastiDAO odao = new OvlastiDAO();
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            String odgovor = kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp);
            if (odgovor.equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            if (s.getMaksBrojZahtjeva() == 0) {
                                return "ERROR 16 broj zahtjeva je 0";
                            } else if (s.getMaksBrojZahtjeva() > 0) {
                                s.setMaksBrojZahtjeva(s.getMaksBrojZahtjeva() - 1);

                                String odgovor1 = odao.provjeraPodrucja(komanda, pbp);

                                if (odgovor1.equals("neaktivno")) {
                                    return "ERROR 18 postoji neaktivno podrucje";
                                }
                                if (odgovor1.equals("aktivno")) {
                                    String odgovor2 = odao.postaviNeaktivnoPodrucje(komanda, pbp);
                                    return odgovor2;
                                }
                                if (odgovor1.equals("nema")) {

                                    return "ERROR 14 ne postoji aktivno podrucje";
                                }
                            }
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return odgovor;
            }
        }

        public synchronized String obradiRightsZahtjev(String komanda, PostavkeBazaPodataka pbp) {

            OvlastiDAO odao = new OvlastiDAO();
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            String odgovor = kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp);
            if (odgovor.equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            if (s.getMaksBrojZahtjeva() == 0) {
                                return "ERROR 16 broj zahtjeva je 0";
                            } else if (s.getMaksBrojZahtjeva() > 0) {
                                s.setMaksBrojZahtjeva(s.getMaksBrojZahtjeva() - 1);

                                List<String> odgovorLista = new ArrayList<>();
                                odgovorLista = odao.provjeraPodrucjaRights(komanda, pbp);

                                if (odgovorLista == null) {
                                    return "ERROR 14 ne postoje aktivna područja";
                                }

                                String odgovorVrati = "";
                                for (String o : odgovorLista) {
                                    odgovorVrati = odgovorVrati + " " + o;
                                }

                                return ("OK" + odgovorVrati);

                            }
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return odgovor;
            }
        }

        public synchronized String obradiListZahtjev(String komanda, PostavkeBazaPodataka pbp) {

            OvlastiDAO odao = new OvlastiDAO();
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            String odgovor = kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp);
            if (odgovor.equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        System.out.println("podaci 2 " + podaci[2] + " get id " + s.getId());
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            if (s.getMaksBrojZahtjeva() == 0) {
                                return "ERROR 16 broj zahtjeva je 0";
                            } else if (s.getMaksBrojZahtjeva() > 0) {
                                s.setMaksBrojZahtjeva(s.getMaksBrojZahtjeva() - 1);

                                odgovor = kdao.traziKorisnikaList(podaci[3], pbp);

                                return odgovor;

                            }
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return odgovor;
            }
        }

        public synchronized String obradiListAllZahtjev(String komanda, PostavkeBazaPodataka pbp) {

            OvlastiDAO odao = new OvlastiDAO();
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            String odgovor = kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp);
            if (odgovor.equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            if (s.getMaksBrojZahtjeva() == 0) {
                                return "ERROR 16 broj zahtjeva je 0";
                            } else if (s.getMaksBrojZahtjeva() > 0) {
                                s.setMaksBrojZahtjeva(s.getMaksBrojZahtjeva() - 1);

                                odgovor = kdao.traziKorisnikaListAll(pbp);

                                return odgovor;

                            }
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return odgovor;
            }
        }

        public synchronized String obradiAuthorZahtjev(String komanda, PostavkeBazaPodataka pbp) {
            OvlastiDAO odao = new OvlastiDAO();
            String[] podaci = komanda.split(" ");
            KorisnikDAO kdao = new KorisnikDAO();
            String odgovor = kdao.traziKorisnikaSamoKorisnickoIme(komanda, pbp);
            if (odgovor.equals("OK")) {
                for (Sjednica s : sjednice) {
                    if (s.getKorisnik().equals(podaci[1])) {
                        if (s.getId() == Integer.parseInt(podaci[2])) {
                            if (s.getMaksBrojZahtjeva() == 0) {
                                return "ERROR 16 broj zahtjeva je 0";
                            } else if (s.getMaksBrojZahtjeva() > 0) {
                                s.setMaksBrojZahtjeva(s.getMaksBrojZahtjeva() - 1);

                                String odgovor1 = odao.provjeraPodrucjaAuthor(komanda, pbp);

                                if (odgovor1.equals("aktivno")) {
                                    return "OK";
                                }
                                if (odgovor1.equals("neaktivno")) {

                                    return "ERROR 14 ne aktivno podrucje";
                                }
                                if (odgovor1.equals("nema")) {

                                    return "ERROR 14 nema aktivno podrucje";
                                }
                            }
                        } else {
                            return "ERROR 15 ne postoji sjednica";
                        }
                    }
                }
                return "ERROR 15 ne postoji sjednica";

            } else {
                return odgovor;
            }
        }

        public synchronized boolean provjeriIspravnostKomandeZahtjeva(String komanda) throws IOException {
            String regex = "(AUTHEN ((\\w)|(-)){3,10} ((\\w)|(-)|(#)|(!)|(\\d)){3,10})|"
                    + "(ADD ((\\w)|(-)){3,10} ((\\w)|(-)|(#)|(!)|(\\d)){3,10} (\"([A-Z][a-z]+)( [A-Z][a-z]+)?\") (\"[A-Z][a-z]+\"))|"
                    + "((LOGOUT|LISTALL) ((\\w)|(-)){3,10} (\\d)+)|"
                    + "((GRANT|REVOKE) ((\\w)|(-)){3,10} (\\d)+ [A-Za-z]+ ((\\w)|(-)){3,10})|"
                    + "((LIST|RIGHTS) ((\\w)|(-)){3,10} (\\d)+ ((\\w)|(-)){3,10})|(AUTHOR ((\\w)|(-)){3,10} (\\d)+ [A-Za-z]+)";

            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(komanda);
            if (matcher.matches() == false) {
                return false;
            }
            return true;
        }

    }

}
