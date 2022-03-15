package org.foi.nwtis.nmuse_aplikacija_2.podaci;

import java.sql.Date;

public class MyAirportsLog {
    private Date flightDate;
    private String ident;

    public MyAirportsLog(Date flightDate, String ident) {
        this.flightDate = flightDate;
        this.ident = ident;
    }
    
    public Date getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(Date flightDate) {
        this.flightDate = flightDate;
    }

    public String getIdent() {
        return ident;
    }

    public void setIdent(String ident) {
        this.ident = ident;
    }
    
}
