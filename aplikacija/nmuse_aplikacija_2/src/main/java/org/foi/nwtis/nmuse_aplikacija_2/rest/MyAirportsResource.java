package org.foi.nwtis.nmuse_aplikacija_2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MyAirportsDAO;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;

/**
 *
 * @author
 */
@Path("mojiAerodromi")
public class MyAirportsResource {

    @Inject
    ServletContext context;

    /*
    curl 'http://localhost:8084/nmuse_zadaca_2_1/rest/korisnici' -H 'korisnik:pero' -H 'lozinka:123456'
    
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajLetoveAerodroma(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");

        System.out.println("Korisnik: " + korisnik);

//        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            MyAirportsDAO myairportsdao = new MyAirportsDAO();
            List<Aerodrom> aerodromi = myairportsdao.vratiLetoveAerodroma(pbp);

            if (aerodromi == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema letova")
                        .build();
            }

            return Response
                    .status(Response.Status.OK)
                    .entity(aerodromi)
                    .build();

        } else {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity(odgovor)
                    .build();

        }
    }

    @Path("{icao}/prate")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajKorisnikeAerodroma(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");

        System.out.println("Korisnik: " + korisnik);

//        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);
        List<Korisnik> korisniciPrate = new ArrayList<>();
        if (odgovor.startsWith("OK")) {
            MyAirportsDAO myairportsdao = new MyAirportsDAO();
            List<String> korisniciAerodroma = myairportsdao.dohvatiKorisnikeAerodroma(icao, pbp);

            String[] zahtjev = odgovor.split(" ");
            komanda = "LISTALL " + korisnik + " " + zahtjev[1];
            odgovor = izvrsiKomandu(komanda, port, adresa);

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

            for (String a : korisniciAerodroma) {
                for (Korisnik k : listaKorisnika) {
                    if (a.equals(k.getKorisnik())) {
                        Korisnik novi = new Korisnik(k.getKorisnik(), k.getLozinka(), k.getPrezime(), k.getIme(), "", 0);
                        korisniciPrate.add(novi);
                    }
                }
            }

            if (korisniciPrate == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nema podataka")
                        .build();
            }

            return Response
                    .status(Response.Status.OK)
                    .entity(korisniciPrate)
                    .build();

        }
        return Response
                .status(Response.Status.OK)
                .entity(korisniciPrate)
                .build();
    }

    @Path("{korisnik}/prati")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response vratiAerodromeKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka, @PathParam("korisnik") String paramKorisnik) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");

        System.out.println("Korisnik: " + korisnik);

//        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);
        List<Korisnik> korisniciPrate = new ArrayList<>();
        if (odgovor.startsWith("OK")) {

            MyAirportsDAO myairportsdao = new MyAirportsDAO();
            List<Aerodrom> aerodromi = myairportsdao.dajAerodromeKorisnika(paramKorisnik, pbp);

            if (aerodromi != null) {
                return Response
                        .status(Response.Status.OK)
                        .entity(aerodromi)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nema aerodroma")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{korisnik}/prati")
    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response dodajAerodromKorisnika(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka, @PathParam("korisnik") String paramKorisnik, Aerodrom aerodrom) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            MyAirportsDAO myairportsdao = new MyAirportsDAO();
            boolean dodan = myairportsdao.dodajAerodrom(paramKorisnik, aerodrom, pbp);

            if (dodan == true) {
                return Response
                        .status(Response.Status.OK)
                        .entity("Dodan")
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nije dodan")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{korisnik}/prati/{icao}")
    @DELETE
    @Produces({MediaType.APPLICATION_JSON})
    public Response izbrisiAerodrom(@HeaderParam("korisnik") String korisnik, @HeaderParam("lozinka") String lozinka, @PathParam("korisnik") String paramKorisnik, @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {
            MyAirportsDAO myairportsdao = new MyAirportsDAO();
            boolean izbrisan = myairportsdao.izbrisiAerodrom(icao, paramKorisnik, pbp);
            if (izbrisan == true) {
                return Response
                        .status(Response.Status.OK)
                        .entity("Izbrisan")
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("Nije izbrisan")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
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
