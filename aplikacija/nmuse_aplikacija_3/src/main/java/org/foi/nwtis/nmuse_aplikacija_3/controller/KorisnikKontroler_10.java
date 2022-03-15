package org.foi.nwtis.nmuse_aplikacija_3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.ejb.EJB;
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
import org.foi.nwtis.nmuse_aplikacija_3.PosiljateljPoruka;
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_7;

@Path("ukloniAerodrom")
@Controller
public class KorisnikKontroler_10 {

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

    @EJB
    PosiljateljPoruka pp;

    /*
    curl -X OPTIONS http://localhost:8084/nmuse_zadaca_2_1/rest/korisnici/ > korisnici.wadl
     */
    @POST
    public String ukloniAerodrom() {
        request.setAttribute("kontrola", true);
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        KorisniciKlijent_7 kk7 = new KorisniciKlijent_7(korisnik, icao);
        Response r = kk7.izbrisiAerodrom(Response.class, korisnik, lozinka);

        String status = r.getStatusInfo().getReasonPhrase();

        if (status.equals("OK")) {
            pp.saljiPoruku("Korisnik: " + korisnik + " je uklonio aerodrom " + icao + " iz pracenja.");
            return "glavniIzbornik.jsp";
        } else {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            String tekst = gs.toJson("Greska kod brisanja aerodroma");
            model.put("greska", tekst);
            return "greska.jsp";
        }

    }

}
