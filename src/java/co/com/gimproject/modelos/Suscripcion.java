
package co.com.gimproject.modelos;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author cristian.tovar
 */
@Entity
@Table(name = "suscripcion")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Suscripcion.findAll", query = "SELECT s FROM Suscripcion s"),
    @NamedQuery(name = "Suscripcion.findByIdSuscripcion", query = "SELECT s FROM Suscripcion s WHERE s.idSuscripcion = :idSuscripcion"),
    @NamedQuery(name = "Suscripcion.findByFechaInicio", query = "SELECT s FROM Suscripcion s WHERE s.fechaInicio = :fechaInicio"),
    @NamedQuery(name = "Suscripcion.findByFechaFin", query = "SELECT s FROM Suscripcion s WHERE s.fechaFin = :fechaFin"),
    @NamedQuery(name = "Suscripcion.findByEstado", query = "SELECT s FROM Suscripcion s WHERE s.estado = :estado")})
public class Suscripcion implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "Id_Suscripcion")
    private Integer idSuscripcion;
    @Column(name = "Fecha_Inicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;
    @Column(name = "Fecha_Fin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
    @Column(name = "Estado")
    private Boolean estado;
    @JoinColumn(name = "Cliente_Id_Cliente", referencedColumnName = "Id_Cliente")
    @ManyToOne(optional = false)
    private Cliente clienteIdCliente;

    public Suscripcion() {
    }

    public Suscripcion(Integer idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public Integer getIdSuscripcion() {
        return idSuscripcion;
    }

    public void setIdSuscripcion(Integer idSuscripcion) {
        this.idSuscripcion = idSuscripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public Boolean getEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }

    public Cliente getClienteIdCliente() {
        return clienteIdCliente;
    }

    public void setClienteIdCliente(Cliente clienteIdCliente) {
        this.clienteIdCliente = clienteIdCliente;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idSuscripcion != null ? idSuscripcion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Suscripcion)) {
            return false;
        }
        Suscripcion other = (Suscripcion) object;
        if ((this.idSuscripcion == null && other.idSuscripcion != null) || (this.idSuscripcion != null && !this.idSuscripcion.equals(other.idSuscripcion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "co.com.gimproject.modelos.Suscripcion[ idSuscripcion=" + idSuscripcion + " ]";
    }
    
}
