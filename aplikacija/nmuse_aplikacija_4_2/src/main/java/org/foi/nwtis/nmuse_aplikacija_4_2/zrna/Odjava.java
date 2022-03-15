package org.foi.nwtis.nmuse_aplikacija_4_2.zrna;

import jakarta.inject.Named;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_4_2.slusaci.SlusacAplikacije;

@Named(value = "odjavaZrno")
@ViewScoped
public class Odjava implements Serializable {

    @Inject
    ServletContext context;

    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpSession session;

    private int port;
    private String adresa;

    public Odjava() {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) SlusacAplikacije.getServletContext().getAttribute("Postavke");
        port = Integer.parseInt(pbp.dajPostavku("port"));
        adresa = pbp.dajPostavku("adresa");
    }

    public String odjaviKorisnika() {
        System.out.println("odjavljuje se korisnik");
        String korisnik = (String) session.getAttribute("korisnik");
        String sjednica = (String) session.getAttribute("sesija");
        String komanda = "LOGOUT " + korisnik + " " + sjednica;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            System.out.println("dobra odjava");
            session.removeAttribute("korisnik");
            session.removeAttribute("lozinka");
            session.removeAttribute("sesija");
            return "prijava";
        } else {
            System.out.println("nije dobra odjava " + odgovor);
            return "izbornik";
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
            Logger.getLogger(Odjava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Odjava.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR XYZ";
    }
}
