package org.foi.nwtis.nmuse_aplikacija_4.ejb.sb;

import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import java.util.ArrayList;
import java.util.List;


@Startup @Singleton
public class BankaJmsPoruka {
    
    private List<String> svePoruke= new ArrayList<>();
    
    public List<String> dajPoruke(){
        return svePoruke;
    }
    
    public void spremi(String poruka){
        svePoruke.add(poruka);
        System.out.println("BankaJmsPoruka : "+poruka);
    }
    
}
