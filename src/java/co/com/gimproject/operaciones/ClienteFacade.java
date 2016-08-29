/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.gimproject.operaciones;

import co.com.gimproject.modelos.Cliente;
import co.com.gimproject.modelos.Suscripcion;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristian.tovar
 */
@Stateless
public class ClienteFacade extends AbstractFacade<Cliente> {

    @PersistenceContext(unitName = "GimProjectPU")
    private EntityManager em;
    private SuscripcionFacade suscripcionem;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ClienteFacade() {
        super(Cliente.class);
    }

    public boolean crearClienteSuscripcion(Cliente cliente, Suscripcion suscripcion) {
        try {
            List<Cliente> lista = em.createQuery("SELECT c FROM Cliente c WHERE c.identificacion =:identificacion").setParameter("identificacion", cliente.getIdentificacion()).getResultList();
            if (lista.isEmpty()) {
                em.merge(cliente);
                int id = (int) em.createQuery("SELECT c.idCliente FROM Cliente c WHERE c.identificacion=:identificacion").setParameter("identificacion", cliente.getIdentificacion()).getSingleResult();
                cliente.setIdCliente(id);
                suscripcion.setClienteIdCliente(cliente);
                em.merge(suscripcion);
                return true;
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            cliente = null;
            suscripcion = null;
        }
        return false;
    }

    public void actualizarTerminoSuscripcion(String terminoSuscripcion, int id_Cliente) {
        try {
            em.createQuery("UPDATE Cliente c set c.terminoSuscripcion=:terminoSuscripcion WHERE c.idCliente=:id_cliente").setParameter("terminoSuscripcion", terminoSuscripcion).setParameter("id_cliente", id_Cliente).executeUpdate();
        } catch (Exception e) {
            e.getMessage();
        } finally {
        terminoSuscripcion = null;
        id_Cliente = 0;
        }
    }

}
