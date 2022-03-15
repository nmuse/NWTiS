package org.foi.nwtis.nmuse_aplikacija_3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_1;
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_4;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;

@Path("korisnik")
@Controller
public class KorisnikKontroler_1 {

    @Inject
    private HttpServletRequest request;

    @Inject
    private Models model;

    @Inject
    private HttpSession session;

    @Inject
    ServletContext context;

    @Path("prijavaKorisnika")
    @GET
    @View("prijavaKorisnika.jsp")
    public void prijavaKorisnika() {
        return;
    }

    @Path("registracijaKorisnika")
    @GET
    @View("registracijaKorisnika.jsp")
    public void registracijaKorisnika() {
        return;
    }

    @Path("komanda")
    @GET
    @View("komanda.jsp")
    public void slanjeKomande() {
        return;
    }

    @Path("glavniIzbornik")
    @GET
    @View("glavniIzbornik.jsp")
    public void glavniIzbornik() {
        return;
    }

    @Path("radSpodrucjima")
    @GET
    public String radSpodrucjima() {
        
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String adresa = pbp.dajPostavku("adresa");
        int port = Integer.parseInt(pbp.dajPostavku("port"));
        List<Korisnik> korisniciLista = new ArrayList<>();
        String komanda = "AUTHOR " + korisnik + " " + sjednica + " " + "administracija";
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            
            KorisniciKlijent_1 kk1 = new KorisniciKlijent_1();
            Response r = kk1.dajKorisnike(Response.class, korisnik, lozinka);

            String podaci = r.readEntity(String.class);
            Gson gson = new Gson();
            Korisnik[] korisnici = gson.fromJson(podaci, Korisnik[].class);
            korisniciLista = Arrays.asList(korisnici);
            model.put("korisnici", korisniciLista);
            return "radSpodrucjima.jsp";
        } else {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            String tekst = gs.toJson(odgovor);
            model.put("greska", tekst);
            return "greska.jsp";
        }
    }

    @Path("aerodromi")
    @GET
    public String aerodromi() {

        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String adresa = pbp.dajPostavku("adresa");
        int port = Integer.parseInt(pbp.dajPostavku("port"));

        String komanda = "AUTHOR " + korisnik + " " + sjednica + " " + "administracijaAerodroma";
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            KorisniciKlijent_4 kk4 = new KorisniciKlijent_4();
            Response r = kk4.dajLetoveAerodroma(Response.class, korisnik, lozinka);
            String status = r.getStatusInfo().getReasonPhrase();
            if (status.equals("OK")) {
                String aerodromi = r.readEntity(String.class);
                Gson gson = new Gson();
                Aerodrom[] poljeAerodromi = gson.fromJson(aerodromi, Aerodrom[].class);
                List<Aerodrom> listaAerodroma = Arrays.asList(poljeAerodromi);
                model.put("aerodromi", listaAerodroma);
            }
        } else {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            String tekst = gs.toJson(odgovor);
            model.put("greska", tekst);
            return "greska.jsp";
        }
        return "radSaerodromima.jsp";
    }

    @Path("odjava")
    @GET
    public String odjava() {
        
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String adresa = pbp.dajPostavku("adresa");
        int port = Integer.parseInt(pbp.dajPostavku("port"));

        String komanda = "LOGOUT " + korisnik + " " + sjednica;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            session.removeAttribute("korisnik");
            session.removeAttribute("lozinka");
            session.removeAttribute("sesija");
            return "odjava.jsp";
        } else {
            model.put("greska", "greska kod odjave");
            return "greska.jsp";

        }

    }

    private synchronized String izvrsiKomandu(String komanda, int port, String adresa) {
        try (Socket uticnica = new Socket(adresa, port);
                InputStream is = uticnica.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                OutputStream os = uticnica.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");) {

            osw.write(komanda);
            osw.flush();
            uticnica.shutdownOutput();

            StringBuilder sb = new StringBuilder();
            while (true) {
                int i = isr.read();
                if (i == -1) {
                    break;
                }
                sb.append((char) i);
            }
            uticnica.shutdownInput();
            uticnica.close();

            return sb.toString();

        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(KorisnikKontroler_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KorisnikKontroler_1.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR XYZ";
    }

}
