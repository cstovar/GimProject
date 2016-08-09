package co.com.gimproject.controladores;

import co.com.gimproject.modelos.Cliente;
import co.com.gimproject.controladores.util.JsfUtil;
import co.com.gimproject.controladores.util.JsfUtil.PersistAction;
import co.com.gimproject.modelos.Suscripcion;
import co.com.gimproject.operaciones.ClienteFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.List;
import java.util.ResourceBundle;
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
import javax.faces.validator.ValidatorException;
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
    private UploadedFile foto;

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
            e.getMessage();
        }
        return null;
    }

    public void fechaFinal() {
        Date fechafin = new Date();
        try {

            String termino = selected.getTerminoSuscripcion();
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(nuevasuscripcion.getFechaInicio());
            //SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
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
        System.out.println("Entro");
        selected.setFoto(foto.getContents());
        boolean si = ejbFacade.crearClienteSuscripcion(selected, nuevasuscripcion);
        if (si) {

            ResourceBundle.getBundle("/Bundle").getString("ClienteCreated");
            items = null; // invalidate list of items to trigger re-query
        }
        ResourceBundle.getBundle("/Bundle").getString("ClienteNotCreated");
    }

    public void validarFoto(Object value) {
        List<FacesMessage> msgs = new ArrayList<FacesMessage>();
        foto = (UploadedFile) value;
        int fileByte = foto.getContents().length;
        if (fileByte > 15360) {
            msgs.add(new FacesMessage("Imagen demasiado grande, maximo 15Kb"));
        }
        if (!(foto.getContentType().startsWith("image"))) {
            msgs.add(new FacesMessage("not an Image file"));
        }
        if (!msgs.isEmpty()) {
            throw new ValidatorException(msgs);
        }
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

    public UploadedFile getFoto() {
        return foto;
    }

    public void setFoto(UploadedFile foto) {
        this.foto = foto;
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
