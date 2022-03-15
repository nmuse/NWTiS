package org.foi.nwtis.nmuse_aplikacija_3;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.Queue;
import jakarta.jms.TextMessage;

@Stateless
public class PosiljateljPoruka {

    @Inject
    @JMSConnectionFactory("jms/NWTiS_nmuse_1")
    private JMSContext context;
    
    @Resource(lookup = "jms/NWTiS_nmuse_1_DR")
    Queue requestQueue;
    
    /**
     * 
     * metoda koja salje jms poruku
     * @param poruka 
     */
    public void saljiPoruku (String poruka){
        System.out.println("Posaljitelj poruka: "+poruka);
        TextMessage msg = context.createTextMessage(poruka);
        context.createProducer().send(requestQueue, msg);
    }
    
}