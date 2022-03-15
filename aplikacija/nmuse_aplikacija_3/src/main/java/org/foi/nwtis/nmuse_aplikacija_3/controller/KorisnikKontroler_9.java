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
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_3;
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_6;
import org.foi.nwtis.podaci.Aerodrom;

@Path("dodajAerodrom")
@Controller
public class KorisnikKontroler_9 {

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
    public String dodajAerodrom() {
        request.setAttribute("kontrola", true);
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        KorisniciKlijent_6 kk6 = new KorisniciKlijent_6(icao);
        Response r = kk6.dajAerodrom(Response.class, korisnik, lozinka);

        String status = r.getStatusInfo().getReasonPhrase();

        
        if (status.equals("OK")) {
            String podaci = r.readEntity(String.class);
            Gson gson = new Gson();
            Aerodrom aerodrom = gson.fromJson(podaci, Aerodrom.class);
            KorisniciKlijent_3 kk3 = new KorisniciKlijent_3(korisnik);
            Response r2 = kk3.dodajAerodromKorisnika(aerodrom, Response.class, korisnik, lozinka);
            String status2 = r2.getStatusInfo().getReasonPhrase();

            if (status2.startsWith("OK")) {
                pp.saljiPoruku("Korisnik: "+korisnik+" je dodao aerodrom "+icao+" za pracenje.");
                return "glavniIzbornik.jsp";
            } else {
                Gson gs = new GsonBuilder().setPrettyPrinting().create();
                String tekst = gs.toJson("Greska kod dodavanja aerodroma");
                model.put("greska", tekst);
                return "greska.jsp";
            }
        }
        return "glavniIzbornik.jsp";
    }

}
