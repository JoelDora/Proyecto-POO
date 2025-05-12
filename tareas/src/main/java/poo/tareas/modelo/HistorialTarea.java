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
public class HistorialTarea {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idHistorial;
    private Integer idTareaOriginal;
    private String nombreTarea;
    private String responsableTarea;
    private String descripcionTarea;
    private Date fechaInicioTarea;
    private Date fechaFinTarea;
    private String estadoTarea;
    private String prioridadTarea;
    private Date fechaEliminacion;
    private String accion;
    
    // Constructor para crear un historial a partir de una tarea
    public HistorialTarea(Tarea tarea, String accion) {
        this.idTareaOriginal = tarea.getIdTarea();
        this.nombreTarea = tarea.getNombreTarea();
        this.responsableTarea = tarea.getResponsableTarea();
        this.descripcionTarea = tarea.getDescripcionTarea();
        this.fechaInicioTarea = tarea.getFechaInicioTarea();
        this.fechaFinTarea = tarea.getFechaFinTarea();
        this.estadoTarea = tarea.getEstadoTarea();
        this.prioridadTarea = tarea.getPrioridadTarea();
        this.fechaEliminacion = new Date(); // Fecha actual
        this.accion = accion;
    }
} 