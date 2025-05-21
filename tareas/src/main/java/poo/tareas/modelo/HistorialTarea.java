/**
 * Paquete que contiene las clases del modelo de datos para la aplicación de tareas
 */
 package poo.tareas.modelo;

// Importaciones necesarias para la entidad JPA y otras funcionalidades
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
 * Clase que representa el historial de cambios de una tarea.
 * Esta entidad almacena el estado de una tarea cuando se realiza alguna acción sobre ella,
 * como eliminación, modificación, etc., permitiendo mantener un registro histórico.
 * 
 * @Entity - Define esta clase como una entidad JPA que se mapeará a una tabla en la base de datos
 * @Data - Anotación de Lombok que genera automáticamente getters, setters, equals, hashCode y toString
 * @NoArgsConstructor - Genera un constructor sin argumentos requerido por JPA
 * @AllArgsConstructor - Genera un constructor con todos los argumentos
 * @ToString - Genera el método toString() para representación en texto del objeto
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HistorialTarea {
    /**
     * Identificador único del registro de historial
     * @Id - Indica que este campo es la clave primaria
     * @GeneratedValue - Configura la estrategia de generación automática de valores para la clave primaria
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;
    
    /**
     * Identificador de la tarea original a la que hace referencia este registro de historial
     */
    private Integer idTareaOriginal;
    
    /**
     * Nombre de la tarea en el momento de la acción
     */
    private String nombreTarea;
    
    /**
     * Responsable asignado a la tarea en el momento de la acción
     */
    private String responsableTarea;
    
    /**
     * Descripción detallada de la tarea en el momento de la acción
     */
    private String descripcionTarea;
    
    /**
     * Fecha de inicio programada para la tarea en el momento de la acción
     */
    private Date fechaInicioTarea;
    
    /**
     * Fecha de finalización programada para la tarea en el momento de la acción
     */
    private Date fechaFinTarea;
    
    /**
     * Estado de la tarea en el momento de la acción (ej: pendiente, en progreso, completada)
     */
    private String estadoTarea;
    
    /**
     * Nivel de prioridad asignado a la tarea en el momento de la acción
     */
    private String prioridadTarea;
    
    /**
     * Fecha y hora en que se realizó la acción sobre la tarea
     */
    private Date fechaEliminacion;
    
    /**
     * Tipo de acción realizada sobre la tarea (ej: crear, modificar, eliminar)
     */
    private String accion;
    
    /**
     * Constructor que crea un registro de historial a partir de una tarea existente
     * y la acción realizada sobre ella. Este constructor facilita la creación de registros
     * históricos copiando todos los datos relevantes de la tarea original.
     * 
     * @param tarea La tarea de la cual se creará el registro histórico
     * @param accion El tipo de acción realizada sobre la tarea (crear, modificar, eliminar, etc.)
     */
    public HistorialTarea(Tarea tarea, String accion) {
        // Se copian todos los atributos de la tarea original
        this.idTareaOriginal = tarea.getIdTarea();
        this.nombreTarea = tarea.getNombreTarea();
        this.responsableTarea = tarea.getResponsableTarea();
        this.descripcionTarea = tarea.getDescripcionTarea();
        this.fechaInicioTarea = tarea.getFechaInicioTarea();
        this.fechaFinTarea = tarea.getFechaFinTarea();
        this.estadoTarea = tarea.getEstadoTarea();
        this.prioridadTarea = tarea.getPrioridadTarea();
        // Se registra la fecha actual como momento de la acción
        this.fechaEliminacion = new Date();
        // Se guarda el tipo de acción realizada
        this.accion = accion;
    }
} 