/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.com.gimproject.operaciones;

import co.com.gimproject.modelos.Usuario;
import co.com.gimproject.modelos.Email;
import java.util.UUID;
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

    public Usuario validarLogin(String nombre, String clave) {

        try {
            Usuario u = (Usuario) em.createQuery("SELECT u FROM Usuario u WHERE u.nombreUsuario=:nombre and u.clave=:clave").setParameter("nombre", nombre).setParameter("clave", clave).getSingleResult();
            if (u != null) {
                return u;
            }
        } catch (Exception e) {
        }
        return null;
    }

    public boolean cambioContrasena(String nombre) {
        try {
            String correo = (String) em.createQuery("Select u.correo from Usuario u where u.nombreUsuario=:nombre").setParameter("nombre", nombre).getSingleResult();

            if (correo != null) {
                String random = UUID.randomUUID().toString();
                String nuevaClave = random.substring(0,9);
                Email email = new Email();
                String clave = "28032016Regimp";
                String de = "regimpequipo@outlook.com";
                String mensaje = "Por solicitud del usuario recibimos una petición de cambio de contraseña, A continuación procederemos a facilitarsela \n"
                        + "Contraseña: ".concat(nuevaClave);
                String asunto = "Cambio de contraseña regimp";
                boolean resultado = email.enviarCorreo(de, clave, correo, mensaje, asunto);
                if (resultado) {
                    String cifrado = Encripcion.Encriptar.encriptaEnMD5(nuevaClave);
                    em.createQuery("UPDATE Usuario u set u.clave=:contrasena WHERE u.nombreUsuario=:nombre").setParameter("contrasena", cifrado).setParameter("nombre", nombre).executeUpdate();
                    return true;
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
        return false;
    }

}
