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

@Path("ukloniPodrucje")
@Controller
public class KorisnikKontroler_7 {

    @FormParam("korisnikTrazi")
    String korisnikTrazi;

    @FormParam("podrucje")
    String podrucje;

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
    public String ukloniPodrucje() {
        request.setAttribute("kontrola", true);
        String korisnik = (String) session.getAttribute("korisnik");
        String lozinka = (String) session.getAttribute("lozinka");
        String sjednica = (String) session.getAttribute("sesija");

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String adresa = pbp.dajPostavku("adresa");
        int port = Integer.parseInt(pbp.dajPostavku("port"));

        String komanda = "REVOKE " + korisnik + " " + sjednica
                + " " + podrucje + " " + korisnikTrazi;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            return "glavniIzbornik.jsp";
        } else {
            Gson gs = new GsonBuilder().setPrettyPrinting().create();
            String tekst = gs.toJson(odgovor);
            model.put("greska", tekst);
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
            Logger.getLogger(KorisnikKontroler_7.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KorisnikKontroler_7.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR XYZ";
    }

}
