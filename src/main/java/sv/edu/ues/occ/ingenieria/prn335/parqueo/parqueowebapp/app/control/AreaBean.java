/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.control;

import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import sv.edu.ues.occ.ingenieria.prn335.parqueo.parqueowebapp.app.entity.Area;

/**
 *
 * @author home
 */
@Stateless
@LocalBean
public class AreaBean extends AbstractDataAccess<Area> implements Serializable {

    @PersistenceContext(unitName = "ParqueoPU")
    EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public AreaBean() {
        super(Area.class);
    }

    public int contarByIdPadre(final Integer idPadre) {
        if (idPadre != null && em != null) {
            Query q = em.createNamedQuery("Area.coundByIdPadre");
            q.setParameter("idAreaPadre", idPadre);
            return ((Long) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public List<Area> findByIdPadre(final Integer idPadre, int primero, int tamanio) {
        if (primero >= 0 && tamanio > 0 && em != null) {
            Query q;
            if (idPadre == null) {
                q = em.createNamedQuery("Area.findRaices");
            } else {
                q = em.createNamedQuery("Area.findByIdPadre");
                q.setParameter("idAreaPadre", idPadre);
            }
            q.setFirstResult(primero);
            q.setMaxResults(tamanio);
            return q.getResultList();
        }
        return Collections.EMPTY_LIST;
    }

}
