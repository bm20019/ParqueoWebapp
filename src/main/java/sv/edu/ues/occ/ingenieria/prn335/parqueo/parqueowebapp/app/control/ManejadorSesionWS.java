/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.websocket.Session;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author home
 */
@Named
@ApplicationScoped
public class ManejadorSesionWS implements Serializable {

    final Set<Session> SESIONES = new HashSet<>();

    public void agregar(Session s) {
        this.SESIONES.add(s);
    }

    public void eliminar(Session s) {
        this.SESIONES.remove(s);
    }

    public Set<Session> getSesiones() {
        return SESIONES;
    }
}
