/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.boundary.jsf;

import jakarta.faces.event.ActionEvent;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.boundary.ws.NotificadorEspacioEndpoint;

/**
 *
 * @author home
 */
@Named
@ViewScoped
public class FrmCobrar implements Serializable{
   
    @Inject
    NotificadorEspacioEndpoint ne;
    public void btnCobrarHandler(ActionEvent ae){

        try {
            //Logicas de cobrar
            ne.propagarMensaje("Parqueo libre! buxo!!");
        } catch (IOException ex) {
            Logger.getLogger(FrmCobrar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
