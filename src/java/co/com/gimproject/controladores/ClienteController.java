package co.com.gimproject.controladores;

import co.com.gimproject.modelos.Cliente;
import co.com.gimproject.controladores.util.JsfUtil;
import co.com.gimproject.controladores.util.JsfUtil.PersistAction;
import co.com.gimproject.controladores.util.UtilJsf;
import co.com.gimproject.modelos.Suscripcion;
import co.com.gimproject.operaciones.ClienteFacade;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.imageio.ImageIO;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@Named("clienteController")
@SessionScoped
@ManagedBean
public class ClienteController implements Serializable {

    @EJB
    private co.com.gimproject.operaciones.ClienteFacade ejbFacade;
    private List<Cliente> items = null;
    private Cliente selected;
    private SuscripcionController suscripcionselected;
    private Suscripcion nuevasuscripcion;
    private Date fechainicio;
    private String ImagenCliente;
    private UploadedFile imagensubida;
    private InputStream is;
    private BufferedImage img;

    public ClienteController() {
    }

    public Cliente getSelected() {
        return selected;
    }

    public void setSelected(Cliente selected) {
        this.selected = selected;
    }

    public Date getFechaActual() {
        try {
            nuevasuscripcion = new Suscripcion();
            Date ahora = new Date();
            fechainicio = ahora;
            nuevasuscripcion.setFechaInicio(fechainicio);
            return ahora;
        } catch (Exception e) {
        } 
        return null;
    }

    public void fechaFinal() {
        Date fechafin = new Date();
        try {

            String termino = selected.getTerminoSuscripcion();
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(nuevasuscripcion.getFechaInicio());
            if (termino != null) {
                switch (termino) {
                    case "Un Mes":
                        calendario.add(Calendar.MONTH, 1);
                        fechafin = calendario.getTime();
                        break;
                    case "Dos Meses":
                        calendario.add(Calendar.MONTH, 2);
                        fechafin = calendario.getTime();
                        break;
                    case "Tres Meses":
                        calendario.add(Calendar.MONTH, 3);
                        fechafin = calendario.getTime();
                        break;
                    case "Seis Meses":
                        calendario.add(Calendar.MONTH, 6);
                        fechafin = calendario.getTime();
                        break;
                    default:
                        break;
                }
            }
            nuevasuscripcion.setFechaFin(fechafin);
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void crear() {

        boolean si = ejbFacade.crearClienteSuscripcion(selected, nuevasuscripcion);
        if (si) {
            ResourceBundle.getBundle("/Bundle").getString("ClienteCreated");
            items = null; // invalidate list of items to trigger re-query
            suscripcionselected = new SuscripcionController();
            selected = null;
            ImagenCliente = null;
            is = null;
            img = null;
        }
        ResourceBundle.getBundle("/Bundle").getString("ClienteNotCreated");
    }

    public void subirImagen(FileUploadEvent event) {
        FacesMessage mensaje = new FacesMessage();
        try {
            is = event.getFile().getInputstream();
            selected.setFoto(IOUtils.toByteArray(is));
            ImagenCliente = UtilJsf.guardaBlobEnFicheroTemporal(selected.getFoto(), event.getFile().getFileName());
            if (ImagenCliente != null) {
                mensaje.setSeverity(FacesMessage.SEVERITY_INFO);
                mensaje.setSummary("Imagen cargada correctamente");
            } else {
                mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
                mensaje.setSummary("Problemas al subir la imagen");
            }

        } catch (Exception e) {

        } 
        FacesContext.getCurrentInstance().addMessage("Mensaje", mensaje);
    }

    public String mostrarImagen(byte[] foto) throws IOException {
        try {
            img = ImageIO.read(new ByteArrayInputStream(foto));
            ImagenCliente = UtilJsf.guardaBlobEnFicheroTemporal(foto, UUID.randomUUID().toString());
            return ImagenCliente;
        } catch (Exception e) {
            e.getMessage();
        } finally {
            ImagenCliente = null;
            img = null;
        }
        return null;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ClienteFacade getFacade() {
        return ejbFacade;
    }

    public Cliente prepareCreate() {
        selected = new Cliente();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ClienteCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ClienteUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ClienteDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Cliente> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Cliente getCliente(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Cliente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cliente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public Suscripcion getNuevasuscripcion() {
        return nuevasuscripcion;
    }

    public void setNuevasuscripcion(Suscripcion nuevasuscripcion) {
        this.nuevasuscripcion = nuevasuscripcion;
    }

    public String getImagenCliente() {
        return ImagenCliente;
    }

    public void setImagenCliente(String ImagenCliente) {
        this.ImagenCliente = ImagenCliente;
    }

    public UploadedFile getImagensubida() {
        return imagensubida;
    }

    public void setImagensubida(UploadedFile imagensubida) {
        this.imagensubida = imagensubida;
    }

    public BufferedImage getImg() {
        return img;
    }

    public void setImg(BufferedImage img) {
        this.img = img;
    }

    @FacesConverter(forClass = Cliente.class)
    public static class ClienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ClienteController controller = (ClienteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "clienteController");
            return controller.getCliente(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Cliente) {
                Cliente o = (Cliente) object;
                return getStringKey(o.getIdCliente());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cliente.class.getName()});
                return null;
            }
        }

    }

}
