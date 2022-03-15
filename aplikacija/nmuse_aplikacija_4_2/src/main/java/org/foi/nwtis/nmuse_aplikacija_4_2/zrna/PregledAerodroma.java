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
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_4_2.podaci.KorisnikKlijent_1;
import org.foi.nwtis.nmuse_aplikacija_4_2.podaci.KorisnikKlijent_2;
import org.foi.nwtis.nmuse_aplikacija_4_2.podaci.KorisnikKlijent_3;
import org.foi.nwtis.nmuse_aplikacija_4_2.podaci.KorisnikKlijent_4;
import org.foi.nwtis.nmuse_aplikacija_4_2.slusaci.SlusacAplikacije;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

@Named(value = "pregledAerodroma")
@ViewScoped
public class PregledAerodroma implements Serializable {

    private List<Aerodrom> mojiAerodromi;

    @Getter
    @Setter
    private List<AvionLeti> letoviAviona;

    @Getter
    @Setter
    private Date datumVrijeme;

    @Getter
    @Setter
    private List<MeteoOriginal> meteoPodaciDatum;
    
    @Getter
    @Setter
    private List<MeteoOriginal> meteoPodaciVrijeme;

    @Getter
    @Setter
    private String odabranAerodrom;

    @Getter
    @Setter
    private Date datumAerodrom;

    @Inject
    ServletContext context;

    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpSession session;

    private int port;
    private String adresa;

    public PregledAerodroma() {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) SlusacAplikacije.getServletContext().getAttribute("Postavke");
        port = Integer.parseInt(pbp.dajPostavku("port"));
        adresa = pbp.dajPostavku("adresa");
        letoviAviona = new ArrayList<>();
        meteoPodaciDatum = new ArrayList<>();
        meteoPodaciVrijeme = new ArrayList<>();
    }

    public List<Aerodrom> getMojiAerodromi() {
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        KorisnikKlijent_1 kk1 = new KorisnikKlijent_1(korisnik);

        Response r = kk1.vratiAerodromeKorisnika(Response.class, korisnik, lozinka);
        String status = r.getStatusInfo().getReasonPhrase();

        if (status.equals("OK")) {
            Gson gson = new Gson();
            String s = r.readEntity(String.class);
            Aerodrom[] aerodromiPolje = gson.fromJson(s, Aerodrom[].class);
            mojiAerodromi = Arrays.asList(aerodromiPolje);
        }
        return mojiAerodromi;
    }

    public String dohvatiLetove() {
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datum = formatter.format(datumAerodrom);

        KorisnikKlijent_2 kk2 = new KorisnikKlijent_2(odabranAerodrom, datum);
        Response r = kk2.dajLetoveAerodromaZaDan(Response.class, korisnik, lozinka);

        String status = r.getStatusInfo().getReasonPhrase();

        if (status.equals("OK")) {
            String json = r.readEntity(String.class);
            Gson gson = new Gson();
            AvionLeti[] avl = gson.fromJson(json, AvionLeti[].class);
            letoviAviona = Arrays.asList(avl);

        }

        return "";
    }

    public String dohvatiMeteoDan() {
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        Format formatter = new SimpleDateFormat("yyyy-MM-dd");
        String datum = formatter.format(datumAerodrom);

        KorisnikKlijent_3 kk3 = new KorisnikKlijent_3(odabranAerodrom, datum);
        Response r = kk3.dajMeteoZaAerodrom(Response.class, korisnik, lozinka);
        String status = r.getStatusInfo().getReasonPhrase();

        if (status.equals("OK")) {
            String json = r.readEntity(String.class);
            Gson gson = new Gson();
            MeteoOriginal[] meteoPolje = gson.fromJson(json, MeteoOriginal[].class);
            meteoPodaciDatum = Arrays.asList(meteoPolje);
        }
        return "";
    }
    public String dohvatiMeteoVrijeme(){
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        long vrijeme= datumVrijeme.getTime();

        KorisnikKlijent_4 kk4 = new KorisnikKlijent_4(odabranAerodrom, String.valueOf(vrijeme));
        Response r = kk4.dajMeteoZaAerodromVrijeme(Response.class, korisnik, lozinka);
        String status = r.getStatusInfo().getReasonPhrase();

        if (status.equals("OK")) {
            String json = r.readEntity(String.class);
            Gson gson = new Gson();
            MeteoOriginal[] meteoPolje = gson.fromJson(json, MeteoOriginal[].class);
            meteoPodaciVrijeme = Arrays.asList(meteoPolje);
        }
        return "";
    }

}
