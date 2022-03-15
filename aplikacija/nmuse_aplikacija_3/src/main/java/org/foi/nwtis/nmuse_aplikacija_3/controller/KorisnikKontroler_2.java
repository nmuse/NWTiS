package org.foi.nwtis.nmuse_aplikacija_3.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.foi.nwtis.nmuse_aplikacija_3.podaci.KorisniciKlijent_1;
import org.foi.nwtis.podaci.Korisnik;

@Path("registracijaKorisnika")
@Controller
public class KorisnikKontroler_2 {

    @FormParam("korisnik")
    String korime;

    @FormParam("lozinka")
    String lozinka;

    @FormParam("lozinka2")
    String lozinka2;

    @FormParam("ime")
    String ime;

    @FormParam("prezime")
    String prezime;

    @FormParam("email")
    String email;

    @Inject
    private Models model;

    /*
    curl -X OPTIONS http://localhost:8084/nmuse_zadaca_2_1/rest/korisnici/ > korisnici.wadl
     */
    @POST
    public String registracijaKorisnika() {
        
        Korisnik korisnik = new Korisnik(korime, lozinka, prezime, ime, email, 0);
        KorisniciKlijent_1 kk = new KorisniciKlijent_1();
        Response odgovor = kk.dodajKorisnika(korisnik, Response.class);
        String o = odgovor.getStatusInfo().getReasonPhrase();
        if (!o.equals("OK")) {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            String tekst = gs.toJson("Registracija nije uspjela");
            model.put("greska", tekst);
            return "greskaRegistracija.jsp";
        } else {
            return "prijavaKorisnika.jsp";
            
        }
}
}
