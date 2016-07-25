/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.gimproject.operaciones;

import co.com.gimproject.modelos.Usuario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author cristian.tovar
 */
@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {

    @PersistenceContext(unitName = "GimProjectPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsuarioFacade() {
        super(Usuario.class);
    }

    public Usuario validarLogin (String nombre, String clave) {
        
        try {
            Usuario u = (Usuario) em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario=:nombre and u.clave=:clave").setParameter("nombre", nombre).setParameter("clave", clave).getSingleResult();
            if (u != null) {
                return u;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

}
