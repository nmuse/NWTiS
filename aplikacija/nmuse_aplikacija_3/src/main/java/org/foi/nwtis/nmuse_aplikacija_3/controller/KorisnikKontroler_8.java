package org.foi.nwtis.nmuse_aplikacija_3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_5;
import org.foi.nwtis.podaci.Korisnik;

@Path("prateAerodrom")
@Controller
public class KorisnikKontroler_8 {

    @FormParam("icao")
    String icao;

    @Inject
    private Models model;

    @Inject
    private HttpSession session;

    @Inject
    private HttpServletRequest request;

    @Inject
    ServletContext context;

    /*
    curl -X OPTIONS http://localhost:8084/nmuse_zadaca_2_1/rest/korisnici/ > korisnici.wadl
     */
    @POST
    public String dajKorisnikeAerodroma() {
        
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        KorisniciKlijent_5 kk5 = new KorisniciKlijent_5(icao);
        Response r = kk5.dajKorisnikeAerodroma(Response.class, korisnik, lozinka);

        String status = r.getStatusInfo().getReasonPhrase();

        List<Korisnik> korisniciLista = new ArrayList<>();
        if (status.equals("OK")) {
            String podaci = r.readEntity(String.class);
            Gson gson = new Gson();
            Korisnik[] korisnici = gson.fromJson(podaci, Korisnik[].class);
            korisniciLista = Arrays.asList(korisnici);
        } else {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            String tekst = gs.toJson("Nema pretplacenih korisnika");
            model.put("greska", tekst);
            return "greska.jsp";

        }
        model.put("korisnici", korisniciLista);

        return "pregledKorisnika.jsp";
    }

}
