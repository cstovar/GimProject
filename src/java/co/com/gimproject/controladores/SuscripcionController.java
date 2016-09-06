package co.com.gimproject.controladores;

import co.com.gimproject.modelos.Suscripcion;
import co.com.gimproject.controladores.util.JsfUtil;
import co.com.gimproject.controladores.util.JsfUtil.PersistAction;
import co.com.gimproject.operaciones.SuscripcionFacade;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
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
    @EJB
    private co.com.gimproject.operaciones.ClienteFacade clienteEjbFacade;
    private List<Suscripcion> items = null;
    private List<Suscripcion> things = null;
    private Suscripcion selected;
    private Suscripcion seleccionado;
    private Date fechainicio;
    private Date fechafin;
    private String diasrestantes;
    private String terminosuscripcion;

    public Date getFechaActual() {
        try {
            Date ahora = new Date();
            fechainicio = ahora;
            selected.setFechaInicio(fechainicio);
            return ahora;
        } catch (Exception e) {
        }
        return null;
    }

    public void fechaFinal() {
        fechafin = new Date();
        try {
            Calendar calendario = Calendar.getInstance();
            calendario.setTime(selected.getFechaInicio());
            if (terminosuscripcion != null) {
                switch (terminosuscripcion) {
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
        } catch (Exception e) {
        }
    }

    public void getDiasRestantes(int i) {
        try {
            final long milisegundospordia = 86400000;
            Date hoy = new Date();
            Calendar calendario = Calendar.getInstance();

            calendario.setTime(items.get(i).getFechaFin());
            Date a = calendario.getTime();

            int año = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH) - 1;
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            Calendar calendariofin = new GregorianCalendar(año, mes, dia);
            Date fecha = new Date(calendariofin.getTimeInMillis());

            long dr = (fecha.getTime() - hoy.getTime()) / milisegundospordia;

            setDiasrestantes("Quedan " + dr);

        } catch (Exception e) {
            e.getMessage();
        }
       
    }

    public void actualizarTablas() {
        things = null; // Invalidate list of thing to trigger re-query.
        items = null; // Invalidate list of items to trigger re-query.
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
        FacesMessage mensaje = new FacesMessage();
        Date ff = ejbFacade.consultarFechaFin(selected.getClienteIdCliente()); // Consultar la fecha de finalizacion
        if (ff.compareTo(getFechaActual()) <= 0) {
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SuscripcionCreated"));
            if (!JsfUtil.isValidationFailed()) {
                actualizarTablas();
                clienteEjbFacade.actualizarTerminoSuscripcion(terminosuscripcion, selected.getClienteIdCliente().getIdCliente());
            }
        } else {
            mensaje.setSeverity(FacesMessage.SEVERITY_ERROR);
            mensaje.setSummary("Lo sentimos, la suscripcion de esté cliente aún no ha terminado!");
        }
        FacesContext.getCurrentInstance().addMessage("Mensaje", mensaje);
        ff = null;
        mensaje = null;
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SuscripcionUpdated"));
        actualizarTablas();
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SuscripcionDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            actualizarTablas();
        }
    }

    public List<Suscripcion> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    public List<Suscripcion> getThingsByFechaFin() {
        if (things == null) {
            things = getFacade().findByFechaFin();
        }
        return things;
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

    public Suscripcion getSeleccionado() {
        return seleccionado;
    }

    public void setSeleccionado(Suscripcion seleccionado) {
        this.seleccionado = seleccionado;
    }

    public String getTerminosuscripcion() {
        return terminosuscripcion;
    }

    public void setTerminosuscripcion(String terminosuscripcion) {
        this.terminosuscripcion = terminosuscripcion;
    }

    public String getDiasrestantes() {
        return diasrestantes;
    }

    public void setDiasrestantes(String diasrestantes) {
        this.diasrestantes = diasrestantes;
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
