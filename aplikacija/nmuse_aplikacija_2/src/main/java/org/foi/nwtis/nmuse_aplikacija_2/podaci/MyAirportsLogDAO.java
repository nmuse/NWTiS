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
import org.foi.nwtis.podaci.Korisnik;

public class MyAirportsLogDAO {
    
    
    public List<MyAirportsLog> dohvatiLog(Date datumSkidanja, PostavkeBazaPodataka pbp) {
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bpkorisnik = pbp.getUserUsername();
        String bplozinka = pbp.getUserPassword();
        String upit = "SELECT ident, flightdate FROM myairportslog WHERE flightdate = ?";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            List<MyAirportsLog> listaLogova=new ArrayList<>();
            try (
                     Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                     PreparedStatement s = con.prepareStatement(upit)) {

                s.setDate(1, datumSkidanja);
                
                ResultSet rs = s.executeQuery();

                while (rs.next()) {
                    String ident = rs.getString("ident");
                    Date flightdate = rs.getDate("flightdate");

                    MyAirportsLog l = new MyAirportsLog(flightdate,ident);
                    listaLogova.add(l);
                }
                return listaLogova;
            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MyAirportsDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void unosLogovaUBazu(List<MyAirportsLog> listaLogova, PostavkeBazaPodataka pbp) {
        
        String bpkorisnik = pbp.getUserUsername();
        String url = pbp.getServerDatabase() + pbp.getUserDatabase();
        String bplozinka = pbp.getUserPassword();
        
        
        String upit = "INSERT INTO myairportslog "
                + "(ident,flightdate,spremljeno) "
                + "VALUES (?,?,CURRENT_TIMESTAMP)";

        try {
            Class.forName(pbp.getDriverDatabase(url));
            try (Connection con = DriverManager.getConnection(url, bpkorisnik, bplozinka);
                 PreparedStatement s = con.prepareStatement(upit)){
                for (int i = 0; i < listaLogova.size(); i++) {
                    s.setString(1, listaLogova.get(i).getIdent());
                    s.setDate(2, (java.sql.Date) listaLogova.get(i).getFlightDate());
                    int brojAzuriranja = s.executeUpdate();
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (ClassNotFoundException ex) {
            
            Logger.getLogger(MyAirportsLogDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}