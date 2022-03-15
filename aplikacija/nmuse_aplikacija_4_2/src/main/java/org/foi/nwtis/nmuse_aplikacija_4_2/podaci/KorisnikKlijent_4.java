/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.foi.nwtis.nmuse_aplikacija_4_2.podaci;

/**
 * Jersey REST client generated for REST resource:aerodromi
 * [aerodromi/{icao}/meteoVrijeme/{vrijeme}]<br>
 * USAGE:
 * <pre>
 *        KorisnikKlijent_4 client = new KorisnikKlijent_4();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author NWTiS_4
 */
public class KorisnikKlijent_4 {

    private jakarta.ws.rs.client.WebTarget webTarget;
    private jakarta.ws.rs.client.Client client;
    private static final String BASE_URI = "http://localhost:8084/nmuse_aplikacija_2/rest/";

    public KorisnikKlijent_4(String icao, String vrijeme) {
        client = jakarta.ws.rs.client.ClientBuilder.newClient();
        String resourcePath = java.text.MessageFormat.format("aerodromi/{0}/meteoVrijeme/{1}", new Object[]{icao, vrijeme});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    public void setResourcePath(String icao, String vrijeme) {
        String resourcePath = java.text.MessageFormat.format("aerodromi/{0}/meteoVrijeme/{1}", new Object[]{icao, vrijeme});
        webTarget = client.target(BASE_URI).path(resourcePath);
    }

    /**
     * @param responseType Class representing the response
     * @return response object (instance of responseType class)@param korisnik header parameter[REQUIRED]
     * @param lozinka header parameter[REQUIRED]
     */
    public <T> T dajMeteoZaAerodromVrijeme(Class<T> responseType, String korisnik, String lozinka) throws jakarta.ws.rs.ClientErrorException {
        return webTarget.request(jakarta.ws.rs.core.MediaType.APPLICATION_JSON).header("korisnik", korisnik).header("lozinka", lozinka).get(responseType);
    }

    public void close() {
        client.close();
    }
    
}
