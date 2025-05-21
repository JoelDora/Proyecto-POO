package poo.tareas.modelo;

// Importaciones necesarias para la entidad JPA y funcionalidades de Lombok
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import java.util.Date;

/**
 * Clase que representa una Tarea en el sistema.
 * Esta clase es una entidad JPA que se mapea a una tabla en la base de datos.
 * Se utilizan anotaciones de Lombok para reducir el código.
 */
@Entity // Marca esta clase como una entidad JPA que se mapea a una tabla
@Data // Anotación de Lombok que genera getters, setters, equals, hashCode y toString
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@ToString // Genera el método toString()
public class Tarea {
    @Id // Identifica el campo como clave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Genera valor automáticamente (auto-increment)
    private Integer idTarea; // Identificador único de la tarea
    private String nombreTarea; // Nombre descriptivo de la tarea
    private String responsableTarea; // Persona responsable de completar la tarea
    private String descripcionTarea; // Descripción detallada de la tarea
    private Date fechaInicioTarea; // Fecha de inicio planificada para la tarea
    private Date fechaFinTarea; // Fecha límite para completar la tarea
    private String estadoTarea; // Estado actual de la tarea 
    private String prioridadTarea; // Prioridad de la tarea 
    
    /**
     * Obtiene el identificador único de la tarea.
     * @return El ID de la tarea como un Integer
     */
    public Integer getIdTarea() {
        return idTarea;
    }

    /**
     * Establece el identificador único de la tarea.
     * @param idTarea El nuevo ID de la tarea
     */
    public void setIdTarea(Integer idTarea) {
        this.idTarea = idTarea;
    }

    /**
     * Obtiene el nombre de la tarea.
     * @return El nombre de la tarea
     */
    public String getNombreTarea() {
        return nombreTarea;
    }

    /**
     * Obtiene el responsable asignado a la tarea.
     * @return El nombre del responsable de la tarea
     */
    public String getResponsableTarea() {
        return responsableTarea;
    }

    /**
     * Obtiene la descripción detallada de la tarea.
     * @return La descripción de la tarea
     */
    public String getDescripcionTarea() {
        return descripcionTarea;
    }

    /**
     * Obtiene la fecha de inicio programada para la tarea.
     * @return La fecha de inicio de la tarea
     */
    public Date getFechaInicioTarea() {
        return fechaInicioTarea;
    }

    /**
     * Obtiene la fecha límite para completar la tarea.
     * @return La fecha de finalización de la tarea
     */
    public Date getFechaFinTarea() {
        return fechaFinTarea;
    }

    /**
     * Obtiene el estado actual de la tarea.
     * @return El estado de la tarea
     */
    public String getEstadoTarea() {
        return estadoTarea;
    }

    /**
     * Obtiene la prioridad asignada a la tarea.
     * @return La prioridad de la tarea
     */
    public String getPrioridadTarea() {
        return prioridadTarea;
    }

    /**
     * Establece el nombre de la tarea.
     * @param nombreTarea El nuevo nombre de la tarea
     */
    public void setNombreTarea(String nombreTarea) {
        this.nombreTarea = nombreTarea;
    }

    /**
     * Establece el responsable de la tarea.
     * @param responsableTarea El nuevo responsable de la tarea
     */
    public void setResponsableTarea(String responsableTarea) {
        this.responsableTarea = responsableTarea;
    }

    /**
     * Establece la descripción de la tarea.
     * @param descripcionTarea La nueva descripción de la tarea
     */
    public void setDescripcionTarea(String descripcionTarea) {
        this.descripcionTarea = descripcionTarea;
    }

    /**
     * Establece la fecha de inicio de la tarea.
     * @param fechaInicioTarea La nueva fecha de inicio
     */
    public void setFechaInicioTarea(Date fechaInicioTarea) {
        this.fechaInicioTarea = fechaInicioTarea;
    }

    /**
     * Establece la fecha límite para completar la tarea.
     * @param fechaFinTarea La nueva fecha de finalización
     */
    public void setFechaFinTarea(Date fechaFinTarea) {
        this.fechaFinTarea = fechaFinTarea;
    }

    /**
     * Establece el estado de la tarea.
     * @param estadoTarea El nuevo estado de la tarea
     */
    public void setEstadoTarea(String estadoTarea) {
        this.estadoTarea = estadoTarea;
    }

    /**
     * Establece la prioridad de la tarea.
     * @param prioridadTarea La nueva prioridad de la tarea
     */
    public void setPrioridadTarea(String prioridadTarea) {
        this.prioridadTarea = prioridadTarea;
    }
}
