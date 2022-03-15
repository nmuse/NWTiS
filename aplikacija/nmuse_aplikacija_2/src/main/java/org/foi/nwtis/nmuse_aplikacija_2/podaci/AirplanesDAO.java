package org.foi.nwtis.nmuse_aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.Lokacija;

public class AirplanesDAO {

    public void dodavanjeLetova(List<AvionLeti> letoviAviona, Date datum, PostavkeBazaPodataka pbp) {

        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();

        String upit = "INSERT INTO airplanes (icao24,firstSeen,estDepartureAirport,"
                + "lastSeen,estArrivalAirport,callsign,"
                + "estDepartureAirportHorizDistance,"
                + "estDepartureAirportVertDistance,estArrivalAirportHorizDistance,"
                + "estArrivalAirportVertDistance,departureAirportCandidatesCount,"
                + "arrivalAirportCandidatesCount,spremljeno,flightDate) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,?)";
        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                for (int i = 0; i < letoviAviona.size(); i++) {
                    s.setString(1, letoviAviona.get(i).getIcao24());
                    s.setInt(2, letoviAviona.get(i).getFirstSeen());
                    s.setString(3, letoviAviona.get(i).getEstDepartureAirport());
                    s.setInt(4, letoviAviona.get(i).getLastSeen());
                    s.setString(5, letoviAviona.get(i).getEstArrivalAirport());
                    s.setString(6, letoviAviona.get(i).getCallsign());
                    s.setInt(7, letoviAviona.get(i).getEstDepartureAirportHorizDistance());
                    s.setInt(8, letoviAviona.get(i).getEstDepartureAirportVertDistance());
                    s.setInt(9, letoviAviona.get(i).getEstArrivalAirportHorizDistance());
                    s.setInt(10, letoviAviona.get(i).getEstArrivalAirportVertDistance());
                    s.setInt(11, letoviAviona.get(i).getDepartureAirportCandidatesCount());
                    s.setInt(12, letoviAviona.get(i).getArrivalAirportCandidatesCount());
                    s.setDate(13, datum);
                    int brojAzuriranja = s.executeUpdate();
                }

            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Aerodrom vratiAerodrom(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ident,name,iso_country,coordinates FROM airports WHERE ident=?";

        try {
            Class.forName(pbp.getDriverDatabase(url));

            try (
                    Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {

                s.setString(1, icao);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    String name = rs.getString("name");
                    String iso_country = rs.getString("iso_country");
                    String coordinates = rs.getString("coordinates");
                    List<String> lat_lon = Arrays.asList(coordinates.split(", "));
                    String lat = lat_lon.get(0);
                    String lon = lat_lon.get(1);
                    Lokacija lokacija = new Lokacija(lat, lon);
                    Aerodrom aerodrom = new Aerodrom(ident, name, iso_country, lokacija);
                    return aerodrom;
                }

            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public int dajBrojLetova(String icao, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "select count(estDepartureAirport) as letovi from airplanes where estDepartureAirport=?";
        int letovi = 0;

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, icao);
                ResultSet rs = s.executeQuery();
                while (rs.next()) {
                    letovi = rs.getInt("letovi");
                }
                return letovi;
            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public List<AvionLeti> dajLetoveZaDan(String icao, Date datum, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT icao24, firstSeen, estDepartureAirport, lastSeen,"
                + " estArrivalAirport, callsign, estDepartureAirportHorizDistance,"
                + " estDepartureAirportVertDistance, estArrivalAirportHorizDistance,"
                + " estArrivalAirportVertDistance, departureAirportCandidatesCount,"
                + " arrivalAirportCandidatesCount"
                + " FROM airplanes WHERE estDepartureAirport = ? and flightDate = ?";
        List<AvionLeti> lista = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, icao);
                s.setDate(2, datum);
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String icao24 = rs.getString("icao24");
                    int firstSeen = rs.getInt("firstSeen");
                    String estDepartureAirport = rs.getString("estDepartureAirport");
                    int lastSeen = rs.getInt("lastSeen");
                    String estArrivalAirport = rs.getString("estArrivalAirport");
                    String callsign = rs.getString("callsign");
                    int estDepartureAirportHorizDistance = rs.getInt("estDepartureAirportHorizDistance");
                    int estDepartureAirportVertDistance = rs.getInt("estDepartureAirportVertDistance");
                    int estArrivalAirportHorizDistance = rs.getInt("estArrivalAirportHorizDistance");
                    int estArrivalAirportVertDistance = rs.getInt("estArrivalAirportVertDistance");
                    int departureAirportCandidatesCount = rs.getInt("departureAirportCandidatesCount");
                    int arrivalAirportCandidatesCount = rs.getInt("arrivalAirportCandidatesCount");

                    AvionLeti avl = new AvionLeti(icao24, firstSeen, estDepartureAirport, lastSeen, estArrivalAirport,
                            callsign, estDepartureAirportHorizDistance, estDepartureAirportVertDistance, estArrivalAirportHorizDistance,
                            estArrivalAirportVertDistance, departureAirportCandidatesCount, arrivalAirportCandidatesCount);
                    lista.add(avl);
                }

                return lista;
            } catch (SQLException ex) {
                Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(AirplanesDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
