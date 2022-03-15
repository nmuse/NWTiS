package org.foi.nwtis.nmuse_aplikacija_4_2.zrna;

import jakarta.faces.context.FacesContext;
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
import lombok.Getter;
import lombok.Setter;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_4_2.slusaci.SlusacAplikacije;

@Named(value = "prijavaZrno")
@ViewScoped
public class Prijava implements Serializable {

    @Getter
    @Setter
    private String korisnik;

    @Getter
    @Setter
    private String lozinka;

    @Inject
    ServletContext context;

    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpSession session;

    private int port;
    private String adresa;

    public Prijava() {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) SlusacAplikacije.getServletContext().getAttribute("Postavke");
        port = Integer.parseInt(pbp.dajPostavku("port"));
        adresa = pbp.dajPostavku("adresa");
    }

    public String prijaviKorisnika() {

        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (!odgovor.contains("OK")) {
            System.out.println("prijava nije dobra");
            korisnik = "";
            lozinka="";
            return "prijava";
        } else {
            System.out.println("dobra prijava");
            session = request.getSession(false);

            if (session != null && session.getAttribute("korisnik") == null) {
                session.setAttribute("korisnik", korisnik);
                session.setAttribute("lozinka", lozinka);
                String[] polje = odgovor.split(" ");
                session.setAttribute("sesija", polje[1]);
                return "izbornik";
            } else {
                System.out.println("korisnik je već ulogiran");
                return "izbornik";
            }
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
            Logger.getLogger(Prijava.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Prijava.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR XYZ";
    }
}
