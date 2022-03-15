package org.foi.nwtis.nmuse_aplikacija_2.rest;

import jakarta.inject.Inject;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.sql.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.AirplanesDAO;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.AirportsDAO;
import org.foi.nwtis.nmuse_aplikacija_2.podaci.MeteoDAO;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

/**
 *
 * @author
 */
@Path("aerodromi")
public class AirportsResource {

    @Inject
    ServletContext context;

    /*
    curl 'http://localhost:8084/nmuse_zadaca_2_1/rest/korisnici' -H 'korisnik:pero' -H 'lozinka:123456'
    
     */
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrome(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka,
            @QueryParam("naziv") String naziv,
            @QueryParam("drzava") String drzava) {

        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            AirportsDAO aerodromiDAO = new AirportsDAO();
            List<Aerodrom> aerodrom1 = aerodromiDAO.dajAerodromeFilter(naziv, drzava, pbp);

            if (aerodrom1 == null) {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema podataka")
                        .build();
            }

            return Response
                    .status(Response.Status.OK)
                    .entity(aerodrom1)
                    .build();

        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{icao}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajAerodrom(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            AirplanesDAO airplanesdao = new AirplanesDAO();
            Aerodrom aerodrom = airplanesdao.vratiAerodrom(icao, pbp);
            Object object = aerodrom;
            if (object != null) {
                return Response
                        .status(Response.Status.OK)
                        .entity(object)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema podataka")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{icao}/letovi")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajBrojLetovaAerodroma(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            AirplanesDAO airplanesdao = new AirplanesDAO();
            int letovi = airplanesdao.dajBrojLetova(icao, pbp);
            Object object = letovi;

            if (object != null) {
                return Response
                        .status(Response.Status.OK)
                        .entity(object)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema podataka")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{icao}/letovi/{dan}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajLetoveAerodromaZaDan(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao, @PathParam("dan") Date datum) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            AirplanesDAO airplanesdao = new AirplanesDAO();
            List<AvionLeti> letovi = airplanesdao.dajLetoveZaDan(icao, datum, pbp);

            if (letovi != null) {
                return Response
                        .status(Response.Status.OK)
                        .entity(letovi)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema podataka")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{icao}/meteoDan/{dan}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajMeteoZaAerodrom(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao, @PathParam("dan") Date datum) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            MeteoDAO mdao = new MeteoDAO();
            List<MeteoOriginal> letovi = mdao.dohvatiMeteoPodatkeAerodromaZaDan(icao, datum, pbp);

            if (!letovi.isEmpty()) {
                return Response
                        .status(Response.Status.OK)
                        .entity(letovi)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema podataka")
                        .build();
            }
        }
        return Response
                .status(Response.Status.NOT_FOUND)
                .entity(odgovor)
                .build();
    }

    @Path("{icao}/meteoVrijeme/{vrijeme}")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response dajMeteoZaAerodromVrijeme(@HeaderParam("korisnik") String korisnik,
            @HeaderParam("lozinka") String lozinka, @PathParam("icao") String icao, @PathParam("dan") long datum) {
        PostavkeBazaPodataka pbp = (PostavkeBazaPodataka) context.getAttribute("Postavke");

        int port = Integer.parseInt(pbp.dajPostavku("port"));
        String adresa = pbp.dajPostavku("adresa");
        String komanda = "AUTHEN " + korisnik + " " + lozinka;
        String odgovor = izvrsiKomandu(komanda, port, adresa);

        if (odgovor.startsWith("OK")) {

            MeteoDAO mdao = new MeteoDAO();
            List<MeteoOriginal> letovi = mdao.dohvatiMeteoPodatkeAerodromaZaVrijeme(icao, datum, pbp);

            if (!letovi.isEmpty()) {
                return Response
                        .status(Response.Status.OK)
                        .entity(letovi)
                        .build();
            } else {
                return Response
                        .status(Response.Status.NOT_FOUND)
                        .entity("nema podataka")
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
