package co.com.gimproject.controladores;

import co.com.gimproject.modelos.Suscripcion;
import co.com.gimproject.controladores.util.JsfUtil;
import co.com.gimproject.controladores.util.JsfUtil.PersistAction;
import co.com.gimproject.operaciones.SuscripcionFacade;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.bean.ManagedBean;

@Named("suscripcionController")
@SessionScoped
@ManagedBean
public class SuscripcionController implements Serializable {

    @EJB
    private co.com.gimproject.operaciones.SuscripcionFacade ejbFacade;
    private List<Suscripcion> items = null;
    private Suscripcion selected;
    private Date fechainicio;
    private Date fechafin;

    public Date getFechaActual() {
        try {
            Date ahora = new Date();
//        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd");
            fechainicio = ahora;
            selected.setFechaInicio(fechainicio);
            return ahora;
        } catch (Exception e) {
            e.getStackTrace();
        }
        return null;
    }

    public Date getFechaFinal(String termino, Date fechainicio) {
        try {
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(fechainicio);
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
            selected.setFechaFin(fechafin);
            return fechafin;
        } catch (Exception e) {
        }
        return null;
    }

    public SuscripcionController() {
    }

    public Suscripcion getSelected() {
        return selected;
    }

    public void setSelected(Suscripcion selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SuscripcionFacade getFacade() {
        return ejbFacade;
    }

    public Suscripcion prepareCreate() {
        selected = new Suscripcion();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SuscripcionCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SuscripcionUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SuscripcionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Suscripcion> getItems() {
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

    public Suscripcion getSuscripcion(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Suscripcion> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Suscripcion> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public Date getFechafin() {
        return fechafin;
    }

    public void setFechafin(Date fechafin) {
        this.fechafin = fechafin;
    }

    public Date getFechainicio() {
        return fechainicio;
    }

    public void setFechainicio(Date fechainicio) {
        this.fechainicio = fechainicio;
    }

    @FacesConverter(forClass = Suscripcion.class)
    public static class SuscripcionControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SuscripcionController controller = (SuscripcionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "suscripcionController");
            return controller.getSuscripcion(getKey(value));
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
            if (object instanceof Suscripcion) {
                Suscripcion o = (Suscripcion) object;
                return getStringKey(o.getIdSuscripcion());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Suscripcion.class.getName()});
                return null;
            }
        }

    }

}
