package org.foi.nwtis.nmuse_aplikacija_4;

import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.JMSConnectionFactory;
import jakarta.jms.JMSContext;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.nmuse_aplikacija_4.ejb.sb.BankaJmsPoruka;

@MessageDriven(mappedName = "jms/NWTiS_nmuse_1_DR")
public class PrimateljPoruka implements MessageListener {
    
    @Inject
    @JMSConnectionFactory("jms/NWTiS_nmuse_1")
    private JMSContext context;
    
    @EJB
    BankaJmsPoruka bjp;
    
    @Override
    public void onMessage(Message msg) {
        System.out.println(msg);
        try {
            String poruka = ((TextMessage) msg).getText();
            System.out.println("Primatelj poruka: 1" + poruka);
            bjp.spremi(poruka);
            System.out.println("Primatelj poruka: 2" + poruka);
        } catch (JMSException ex) {
            Logger.getLogger(PrimateljPoruka.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
