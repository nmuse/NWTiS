package org.foi.nwtis.nmuse_aplikacija_4_2.zrna;

import com.google.gson.Gson;
import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.core.Response;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_4_2.podaci.KorisnikKlijent_5;
import org.foi.nwtis.nmuse_aplikacija_4_2.slusaci.SlusacAplikacije;
import org.foi.nwtis.podaci.Korisnik;

@Named(value = "pregledKorisnika")
@ViewScoped
public class PregledKorisnika implements Serializable {

    @Inject
    ServletContext context;

    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpSession session;

    private int port;
    private String adresa;
    private boolean preuzeto = false;

    List<Korisnik> listaKorisnika;

    @Getter
    @Setter
    List<Korisnik> filtriranaLista;

    public PregledKorisnika() {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) SlusacAplikacije.getServletContext().getAttribute("Postavke");
        port = Integer.parseInt(pbp.dajPostavku("port"));
        adresa = pbp.dajPostavku("adresa");
        listaKorisnika = new ArrayList<>();
    }

    public List<Korisnik> getListaKorisnika() {

        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");

        if (!preuzeto) {

            KorisnikKlijent_5 kk5 = new KorisnikKlijent_5();
            Response r = kk5.dajKorisnike(Response.class, korisnik, lozinka);
            String status = r.getStatusInfo().getReasonPhrase();

            if (status.equals("OK")) {
                Gson gson = new Gson();
                String s = r.readEntity(String.class);
                Korisnik[] korisniciPolje = gson.fromJson(s, Korisnik[].class);
                listaKorisnika = Arrays.asList(korisniciPolje);
            }
            preuzeto = true;
            return listaKorisnika;
        }
        return new ArrayList<>();
    }

}
