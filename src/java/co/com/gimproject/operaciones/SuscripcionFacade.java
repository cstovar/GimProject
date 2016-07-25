/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.gimproject.operaciones;

import co.com.gimproject.modelos.Suscripcion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristian.tovar
 */
@Stateless
public class SuscripcionFacade extends AbstractFacade<Suscripcion> {

    @PersistenceContext(unitName = "GimProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SuscripcionFacade() {
        super(Suscripcion.class);
    }
    
}
