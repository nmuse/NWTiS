package org.foi.nwtis.nmuse_aplikacija_2.podaci;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse.konfiguracije.bazaPodataka.PostavkeBazaPodataka;
import org.foi.nwtis.rest.podaci.AvionLeti;
import org.foi.nwtis.rest.podaci.MeteoOriginal;

public class MeteoDAO {

    public boolean unesiMeteoPodatke(List<MeteoOriginal> podaci, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "insert into meteo(ident, coordLon, coordLat, weatherMain,"
                + " weatherDescription, mainTemp,mainFeels_like, mainPressure,"
                + " mainHumidity, mainTemp_min, mainTemp_max, visibility, windSpeed, "
                + "windDeg,windGust, cloudsAll, sysSunrise, sysSunset, timezone, cityId,"
                + " cityName, cod, spremljeno)"
                + " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                    PreparedStatement s = con.prepareStatement(upit)) {
                for (int i = 0; i < podaci.size(); i++) {
                    s.setString(1, podaci.get(i).getIdent());
                    s.setString(2, podaci.get(i).getCoordLon());
                    s.setString(3, podaci.get(i).getCoordLat());
                    s.setString(4, podaci.get(i).getWeatherMain());
                    s.setString(5, podaci.get(i).getWeatherDescription());
                    s.setFloat(6, podaci.get(i).getMainTemp());
                    s.setFloat(7, podaci.get(i).getMainFeels_like());
                    s.setFloat(8, podaci.get(i).getMainPressure());
                    s.setInt(9, podaci.get(i).getMainHumidity());
                    s.setFloat(10, podaci.get(i).getMainTemp_min());
                    s.setFloat(11, podaci.get(i).getMainTemp_max());
                    s.setInt(12, podaci.get(i).getVisibility());
                    if (podaci.get(i).getWindSpeed() != null) {
                        s.setFloat(13, podaci.get(i).getWindSpeed());
                    } else {
                        s.setFloat(13, 0);
                    }
                    s.setInt(14, podaci.get(i).getWindDeg());
                    if (podaci.get(i).getWindGust() != null) {
                        s.setFloat(15, podaci.get(i).getWindGust());
                    } else {
                        s.setFloat(15, 0);
                    }
                    s.setInt(16, podaci.get(i).getCloudsAll());
                    s.setInt(17, podaci.get(i).getSysSunrise());
                    s.setInt(18, podaci.get(i).getSysSunset());
                    s.setInt(19, podaci.get(i).getTimezone());
                    s.setInt(20, podaci.get(i).getCityId());
                    s.setString(21, podaci.get(i).getCityName());
                    s.setInt(22, podaci.get(i).getCod());
                    int brojAzuriranja = s.executeUpdate();
                }
                return true;
            } catch (SQLException ex) {
                Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MeteoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public List<MeteoOriginal> dohvatiMeteoPodatkeAerodromaZaDan(String icao, Date datum,PostavkeBazaPodataka pbp){
      
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT `ident`, `dt`, `coordLon`, `coordLat`, `weatherId`, `weatherMain`,"
                + " `weatherDescription`, `base`, `mainTemp`, `mainFeels_like`, `mainPressure`,"
                + " `mainHumidity`, `mainTemp_min`, `mainTemp_max`, `mainSea_level`,"
                + " `mainGrnd_level`, `visibility`, `windSpeed`, `windDeg`, `windGust`,"
                + " `cloudsAll`, `rain1h`, `rain3h`, `snow1h`, `snow3h`, `sysType`, `sysId`,"
                + " `sysMessage`, `sysCountry`, `sysSunrise`, `sysSunset`, `timezone`, `cityId`,"
                + " `cityName`, `cod`, `jsonMeteo`, `spremljeno` FROM `meteo` WHERE ident = ? AND date(spremljeno)=?";
        
        List<MeteoOriginal> lista = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, icao);
                s.setDate(2, datum);
                ResultSet rs = s.executeQuery();

                
                
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    long dt = rs.getLong("dt");
                    String coordLon = rs.getString("coordLon");
                    String coordLat = rs.getString("coordLat");
                    int weatherId = rs.getInt("weatherId");
                    String weatherMain = rs.getString("weatherMain");
                    String weatherDescription = rs.getString("weatherDescription");
                    String base=rs.getString("base");
                    Float mainTemp = rs.getFloat("mainTemp");
                    Float mainFeels_like = rs.getFloat("mainFeels_like");
                    Float mainPressure = rs.getFloat("mainPressure");
                    int mainHumidity = rs.getInt("mainHumidity");
                    Float mainTemp_min = rs.getFloat("mainTemp_min");
                    Float mainTemp_max = rs.getFloat("mainTemp_max");
                    Float mainSea_level = rs.getFloat("mainSea_level");
                    Float mainGrnd_level = rs.getFloat("mainGrnd_level");
                    int visibility = rs.getInt("visibility");
                    Float windSpeed = rs.getFloat("windSpeed");
                    int windDeg = rs.getInt("windDeg");
                    Float windGust = rs.getFloat("windGust");
                    int cloudsAll = rs.getInt("cloudsAll");
                    Float rain1h = rs.getFloat("rain1h");
                    Float rain3h = rs.getFloat("rain3h");
                    Float snow1h = rs.getFloat("snow1h");
                    Float snow3h = rs.getFloat("snow3h");
                    int sysType = rs.getInt("sysType");
                    int sysId = rs.getInt("sysId");
                    float sysMessage = rs.getFloat("sysMessage");
                    String sysCountry = rs.getString("sysCountry");
                    int sysSunrise = rs.getInt("sysSunrise");
                    int sysSunset = rs.getInt("sysSunset");
                    int timezone = rs.getInt("timezone");
                    int cityId = rs.getInt("cityId");
                    String cityName = rs.getString("cityName");
                    int cod = rs.getInt("cod");
                    String jsonMeteo = rs.getString("jsonMeteo");
                    Timestamp spremljeno = rs.getTimestamp("spremljeno");
                    
                    MeteoOriginal mo = new MeteoOriginal(ident, dt, coordLon, coordLat, weatherId, weatherMain, 
                            weatherDescription, "",base,  mainTemp, mainFeels_like, mainPressure, mainHumidity, 
                            mainTemp_min, mainTemp_max, mainSea_level, mainGrnd_level, visibility, windSpeed, 
                            windDeg, windGust, cloudsAll, rain1h, rain3h, snow1h, snow3h, sysType, sysId, sysMessage, 
                            sysCountry, sysSunrise, sysSunset, timezone, cityId, cityName, cod, jsonMeteo);

                    lista.add(mo);
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

    public List<MeteoOriginal> dohvatiMeteoPodatkeAerodromaZaVrijeme(String icao, long datum,PostavkeBazaPodataka pbp){
      
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT `ident`, `dt`, `coordLon`, `coordLat`, `weatherId`, `weatherMain`,"
                + " `weatherDescription`, `base`, `mainTemp`, `mainFeels_like`, `mainPressure`,"
                + " `mainHumidity`, `mainTemp_min`, `mainTemp_max`, `mainSea_level`,"
                + " `mainGrnd_level`, `visibility`, `windSpeed`, `windDeg`, `windGust`,"
                + " `cloudsAll`, `rain1h`, `rain3h`, `snow1h`, `snow3h`, `sysType`, `sysId`,"
                + " `sysMessage`, `sysCountry`, `sysSunrise`, `sysSunset`, `timezone`, `cityId`,"
                + " `cityName`, `cod`, `jsonMeteo`, `spremljeno` FROM `meteo` WHERE ident = ? AND date(spremljeno)>=? limit 1";
        
        Timestamp vrijeme = new Timestamp(datum);
        
        
        List<MeteoOriginal> lista = new ArrayList<>();
        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka); PreparedStatement s = con.prepareStatement(upit)) {
                s.setString(1, icao);
                s.setTimestamp(2, vrijeme);
                ResultSet rs = s.executeQuery();

                
                
                while (rs.next()) {
                    String ident = rs.getString("ident");
                    long dt = rs.getLong("dt");
                    String coordLon = rs.getString("coordLon");
                    String coordLat = rs.getString("coordLat");
                    int weatherId = rs.getInt("weatherId");
                    String weatherMain = rs.getString("weatherMain");
                    String weatherDescription = rs.getString("weatherDescription");
                    String base=rs.getString("base");
                    Float mainTemp = rs.getFloat("mainTemp");
                    Float mainFeels_like = rs.getFloat("mainFeels_like");
                    Float mainPressure = rs.getFloat("mainPressure");
                    int mainHumidity = rs.getInt("mainHumidity");
                    Float mainTemp_min = rs.getFloat("mainTemp_min");
                    Float mainTemp_max = rs.getFloat("mainTemp_max");
                    Float mainSea_level = rs.getFloat("mainSea_level");
                    Float mainGrnd_level = rs.getFloat("mainGrnd_level");
                    int visibility = rs.getInt("visibility");
                    Float windSpeed = rs.getFloat("windSpeed");
                    int windDeg = rs.getInt("windDeg");
                    Float windGust = rs.getFloat("windGust");
                    int cloudsAll = rs.getInt("cloudsAll");
                    Float rain1h = rs.getFloat("rain1h");
                    Float rain3h = rs.getFloat("rain3h");
                    Float snow1h = rs.getFloat("snow1h");
                    Float snow3h = rs.getFloat("snow3h");
                    int sysType = rs.getInt("sysType");
                    int sysId = rs.getInt("sysId");
                    float sysMessage = rs.getFloat("sysMessage");
                    String sysCountry = rs.getString("sysCountry");
                    int sysSunrise = rs.getInt("sysSunrise");
                    int sysSunset = rs.getInt("sysSunset");
                    int timezone = rs.getInt("timezone");
                    int cityId = rs.getInt("cityId");
                    String cityName = rs.getString("cityName");
                    int cod = rs.getInt("cod");
                    String jsonMeteo = rs.getString("jsonMeteo");
                    Timestamp spremljeno = rs.getTimestamp("spremljeno");
                    
                    MeteoOriginal mo = new MeteoOriginal(ident, dt, coordLon, coordLat, weatherId, weatherMain, 
                            weatherDescription, "",base,  mainTemp, mainFeels_like, mainPressure, mainHumidity, 
                            mainTemp_min, mainTemp_max, mainSea_level, mainGrnd_level, visibility, windSpeed, 
                            windDeg, windGust, cloudsAll, rain1h, rain3h, snow1h, snow3h, sysType, sysId, sysMessage, 
                            sysCountry, sysSunrise, sysSunset, timezone, cityId, cityName, cod, jsonMeteo);

                    lista.add(mo);
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