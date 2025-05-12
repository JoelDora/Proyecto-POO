package poo.tareas.modelo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Tarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idTarea;
    private String nombreTarea;
    private String responsableTarea;
    private String descripcionTarea;
    private Date fechaInicioTarea;
    private Date fechaFinTarea;
    private String estadoTarea;
    private String prioridadTarea;
    
    public Integer getIdTarea() {
        return idTarea;
    }

    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    public String getNombreTarea() {
        return nombreTarea;
    }

    public String getResponsableTarea() {
        return responsableTarea;
    }

    public String getDescripcionTarea() {
        return descripcionTarea;
    }

    public Date getFechaInicioTarea() {
        return fechaInicioTarea;
    }

    public Date getFechaFinTarea() {
        return fechaFinTarea;
    }

    public String getEstadoTarea() {
        return estadoTarea;
    }

    public String getPrioridadTarea() {
        return prioridadTarea;
    }

    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    public void setResponsableTarea(String responsableTarea) {
        this.responsableTarea = responsableTarea;
    }

    public void setDescripcionTarea(String descripcionTarea) {
        this.descripcionTarea = descripcionTarea;
    }

    public void setFechaInicioTarea(Date fechaInicioTarea) {
        this.fechaInicioTarea = fechaInicioTarea;
    }

    public void setFechaFinTarea(Date fechaFinTarea) {
        this.fechaFinTarea = fechaFinTarea;
    }

    public void setEstadoTarea(String estadoTarea) {
        this.estadoTarea = estadoTarea;
    }

    public void setPrioridadTarea(String prioridadTarea) {
        this.prioridadTarea = prioridadTarea;
    }
}
