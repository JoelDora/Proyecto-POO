package poo.tareas.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import poo.tareas.modelo.Tarea;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

@Service
public class PrioridadActualizadorService {
    private static final Logger logger = LoggerFactory.getLogger(PrioridadActualizadorService.class);
    
    @Autowired
    private tareaServicio tareaServicio;
    
    /**
     * Actualiza las prioridades de las tareas según la cercanía a la fecha de finalización.
     * - Prioridad Alta: 1 día o menos para la fecha de finalización
     * - Prioridad Media: 3 días o menos para la fecha de finalización
     * - Prioridad Baja: más de 3 días para la fecha de finalización
     * 
     * Este método se ejecuta automáticamente cada día a la medianoche.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar diariamente a la medianoche
    public void actualizarPrioridades() {
        logger.info("Iniciando actualización automática de prioridades de tareas");
        
        List<Tarea> tareas = tareaServicio.listarTareas();
        LocalDate hoy = LocalDate.now();
        int tareasActualizadas = 0;
        
        for (Tarea tarea : tareas) {
            // Solo procesamos tareas que no estén marcadas como completadas
            if (!"Completada".equals(tarea.getEstadoTarea())) {
                Date fechaFin = tarea.getFechaFinTarea();
                if (fechaFin != null) {
                    LocalDate fechaFinLD = fechaFin.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                    
                    // Calcular los días hasta la fecha de finalización
                    long diasHastaFin = ChronoUnit.DAYS.between(hoy, fechaFinLD);
                    
                    String prioridadOriginal = tarea.getPrioridadTarea();
                    String nuevaPrioridad = prioridadOriginal;
                    
                    // Determinar la nueva prioridad basada en los días restantes
                    if (diasHastaFin <= 1) {
                        nuevaPrioridad = "Alta";
                    } else if (diasHastaFin <= 3) {
                        nuevaPrioridad = "Media";
                    } else {
                        nuevaPrioridad = "Baja";
                    }
                    
                    // Actualizar la prioridad si cambió
                    if (!nuevaPrioridad.equals(prioridadOriginal)) {
                        tarea.setPrioridadTarea(nuevaPrioridad);
                        tareaServicio.guardarTarea(tarea);
                        tareasActualizadas++;
                        logger.info("Tarea ID {}: Prioridad cambiada de {} a {} (Días restantes: {})", 
                                tarea.getIdTarea(), prioridadOriginal, nuevaPrioridad, diasHastaFin);
                    }
                }
            }
        }
        
        logger.info("Actualización de prioridades completada. {} tareas actualizadas", tareasActualizadas);
    }
    
    /**
     * Método para actualizar manualmente las prioridades.
     * Puede ser llamado desde el controlador.
     * 
     * @return Número de tareas actualizadas
     */
    public int actualizarPrioridadesManual() {
        logger.info("Iniciando actualización manual de prioridades de tareas");
        
        List<Tarea> tareas = tareaServicio.listarTareas();
        LocalDate hoy = LocalDate.now();
        int tareasActualizadas = 0;
        
        for (Tarea tarea : tareas) {
            // Solo procesamos tareas que no estén marcadas como completadas
            if (!"Completada".equals(tarea.getEstadoTarea())) {
                Date fechaFin = tarea.getFechaFinTarea();
                if (fechaFin != null) {
                    LocalDate fechaFinLD = fechaFin.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                    
                    // Calcular los días hasta la fecha de finalización
                    long diasHastaFin = ChronoUnit.DAYS.between(hoy, fechaFinLD);
                    
                    String prioridadOriginal = tarea.getPrioridadTarea();
                    String nuevaPrioridad = prioridadOriginal;
                    
                    // Determinar la nueva prioridad basada en los días restantes
                    if (diasHastaFin <= 1) {
                        nuevaPrioridad = "Alta";
                    } else if (diasHastaFin <= 3) {
                        nuevaPrioridad = "Media";
                    } else {
                        nuevaPrioridad = "Baja";
                    }
                    
                    // Actualizar la prioridad si cambió
                    if (!nuevaPrioridad.equals(prioridadOriginal)) {
                        tarea.setPrioridadTarea(nuevaPrioridad);
                        tareaServicio.guardarTarea(tarea);
                        tareasActualizadas++;
                        logger.info("Tarea ID {}: Prioridad cambiada de {} a {} (Días restantes: {})", 
                                tarea.getIdTarea(), prioridadOriginal, nuevaPrioridad, diasHastaFin);
                    }
                }
            }
        }
        
        logger.info("Actualización manual de prioridades completada. {} tareas actualizadas", tareasActualizadas);
        return tareasActualizadas;
    }
} 