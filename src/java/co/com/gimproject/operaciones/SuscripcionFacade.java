/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.gimproject.operaciones;

import co.com.gimproject.modelos.Cliente;
import co.com.gimproject.modelos.Suscripcion;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristian.tovar
 */
@Stateless
public class SuscripcionFacade extends AbstractFacade<Suscripcion> {

    Calendar fecha;
    Date ahora;
    Date dosdias;
    private ClienteFacade clienteem;

    @PersistenceContext(unitName = "GimProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Suscripcion> findByFechaFin() {
        try {
            ahora = new Date();
            fecha = Calendar.getInstance();
            fecha.setTime(ahora);
            fecha.add(Calendar.DAY_OF_YEAR, 3);
            dosdias = fecha.getTime();
            return em.createQuery("SELECT s from Suscripcion s where s.fechaFin BETWEEN CURRENT_DATE and ?1").setParameter(1, dosdias).getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public Date consultarFechaFin(Cliente cliente) {
        try {
        Date fecha_fin = (Date) em.createQuery("SELECT MAX(s.fechaFin) FROM Suscripcion s WHERE s.clienteIdCliente=:id_cliente").setParameter("id_cliente", cliente).getSingleResult();
        return fecha_fin;
        } catch (Exception e) {
            e.getMessage();
        }
        return null;
    }

    public SuscripcionFacade() {
        super(Suscripcion.class);
    }

}
