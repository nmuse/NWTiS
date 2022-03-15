package org.foi.nwtis.nmuse_aplikacija_2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Korisnik;

/**
 *
 * @author
 */
@Path("korisnici")
public class KorisniciResource {

    @Inject
    ServletContext context;

    /*
    curl 'http://localhost:8084/nmuse_zadaca_2_1/rest/korisnici' -H 'korisnik:pero' -H 'lozinka:123456'
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnike(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");

        System.out.println("Korisnik: " + korisnik);

//        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            String[] zahtjev = odgovor.split(" ");
            komanda = "LISTALL " + korisnik + " " + zahtjev[1];
            odgovor = izvrsiKomandu(komanda, port, adresa);
            System.out.println("odgovor je " + odgovor);
            odgovor = odgovor.replaceAll("OK ", "");
            odgovor = odgovor.replaceAll("\"", "");
            odgovor = odgovor.replaceAll("\t", " ");
            String[] podaci = odgovor.split(" ");

            List<Korisnik> listaKorisnika = new ArrayList<>();
            for (int i = 0; i < podaci.length; i++) {
                Korisnik noviKorisnik = new Korisnik(podaci[i], "", podaci[i + 1], podaci[i + 2], "", 0);
                i = i + 2;
                listaKorisnika.add(noviKorisnik);
            }
            return Response
                    .status(Response.Status.OK)
                    .entity(listaKorisnika)
                    .build();

        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(odgovor)
                    .build();
        }

    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajKorisnika(Korisnik noviKorisnik) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");

        String komanda = "ADD " + noviKorisnik.getKorisnik() + " " + noviKorisnik.getLozinka() + " \""
                + noviKorisnik.getPrezime() + "\" \"" + noviKorisnik.getIme() + "\"";

        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.equals("OK")) {
            return Response
                    .status(Response.Status.OK)
                    .entity(odgovor)
                    .build();
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(odgovor)
                    .build();
        }
    }

    @Path("{korisnik}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnika(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("korisnik") String pkorisnik) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");

        System.out.println("Korisnik: " + korisnik);

//        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);
        String odgovor2="";
        Korisnik noviKorisnik;
        if (odgovor.startsWith("OK")) {
            String[] zahtjev = odgovor.split(" ");
            komanda = "LIST " + korisnik + " " + zahtjev[1] + " " + pkorisnik;
            odgovor = izvrsiKomandu(komanda, port, adresa);
            System.out.println("odgovor je " + odgovor);
            
            odgovor2 = odgovor.replaceAll("OK ", "");
            odgovor2 = odgovor2.replaceAll("\"", "");
            odgovor2 = odgovor2.replaceAll("\t", " ");
            String[] podaci = odgovor2.split(" ");

            noviKorisnik = new Korisnik(podaci[0], "", podaci[1], podaci[2], "", 0);

            if (!odgovor.startsWith("OK")) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity(odgovor)
                        .build();

            } else {
                return Response
                        .status(Response.Status.OK)
                        .entity(noviKorisnik)
                        .build();
            }

        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(odgovor)
                    .build();
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
            Logger.getLogger(KorisniciResource.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(KorisniciResource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "ERROR XYZ";
    }
}
