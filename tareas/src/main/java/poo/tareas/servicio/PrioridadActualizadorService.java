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

/**
 * Servicio encargado de actualizar automáticamente las prioridades de las tareas
 * según la proximidad a su fecha de finalización.
 * 
 * Este servicio implementa tanto actualizaciones programadas (cada día a medianoche)
 * como un método para ejecutar actualizaciones manualmente.
 */
@Service // Anotación que marca esta clase como un servicio de Spring
public class PrioridadActualizadorService {
    // Configuración del sistema de logging para registrar la actividad del servicio
    private static final Logger logger = LoggerFactory.getLogger(PrioridadActualizadorService.class);
    
    // Inyección de dependencia del servicio de tareas para acceder a los métodos CRUD
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
    @Scheduled(cron = "0 0 0 * * ?") // Expresión cron: segundos minutos horas día-del-mes mes día-de-la-semana
    public void actualizarPrioridades() {
        // Registra el inicio del proceso en los logs
        logger.info("Iniciando actualización automática de prioridades de tareas");
        
        // Se obtiene todas las tareas de la base de datos
        List<Tarea> tareas = tareaServicio.listarTareas();
        
        // Se obtiene la fecha actual para calcular los días restantes
        LocalDate hoy = LocalDate.now();
        
        // Contador para registrar cuántas tareas fueron modificadas
        int tareasActualizadas = 0;
        
        // Se itera sobre cada tarea para evaluar y actualizar su prioridad
        for (Tarea tarea : tareas) {
            // Solo procesamos tareas que no estén completadas
            if (!"Completada".equals(tarea.getEstadoTarea())) {
                // Se obtiene la fecha de finalización de la tarea
                Date fechaFin = tarea.getFechaFinTarea();
                
                // Se verifica que la tarea tenga una fecha de finalización válida
                if (fechaFin != null) {
                    // Se convierte la fecha de tipo Date a LocalDate para facilitar cálculos
                    LocalDate fechaFinLD = fechaFin.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                    
                    // Se calculan los días que faltan hasta la fecha límite
                    long diasHastaFin = ChronoUnit.DAYS.between(hoy, fechaFinLD);
                    
                    // Se guarda la prioridad actual para comparar después
                    String prioridadOriginal = tarea.getPrioridadTarea();
                    String nuevaPrioridad = prioridadOriginal;
                    
                    // Se determina la nueva prioridad según los días restantes
                    if (diasHastaFin <= 1) {
                        // Si queda 1 día o menos (urgente)
                        nuevaPrioridad = "Alta";
                    } else if (diasHastaFin <= 3) {
                        // Si quedan entre 2 y 3 días
                        nuevaPrioridad = "Media";
                    } else {
                        // Si quedan más de 3 días
                        nuevaPrioridad = "Baja";
                    }
                    
                    // Se actualiza la prioridad solo si es necesario (optimización)
                    if (!nuevaPrioridad.equals(prioridadOriginal)) {
                        // Se actualiza el valor en el objeto
                        tarea.setPrioridadTarea(nuevaPrioridad);
                        // Se guardan los cambios en la base de datos
                        tareaServicio.guardarTarea(tarea);
                        // Se incrementa el contador de tareas actualizadas
                        tareasActualizadas++;
                        // Se registra el cambio en el log para seguimiento
                        logger.info("Tarea ID {}: Prioridad cambiada de {} a {} (Días restantes: {})", 
                                tarea.getIdTarea(), prioridadOriginal, nuevaPrioridad, diasHastaFin);
                    }
                }
            }
        }
        
        // Se registra la finalización del proceso y el número de tareas afectadas
        logger.info("Actualización de prioridades completada. {} tareas actualizadas", tareasActualizadas);
    }
    
    /**
     * Método para actualizar manualmente las prioridades.
     * Puede ser llamado desde el controlador cuando un usuario solicita
     * actualizar las prioridades fuera del horario programado.
     * 
     * @return Número de tareas cuya prioridad fue actualizada
     */
    public int actualizarPrioridadesManual() {
        // Este método replica la lógica del método automático pero puede ser invocado manualmente
        logger.info("Iniciando actualización manual de prioridades de tareas");
        
        // Se obtiene la lista completa de tareas
        List<Tarea> tareas = tareaServicio.listarTareas();
        // Se obtiene la fecha actual
        LocalDate hoy = LocalDate.now();
        // Contador de tareas actualizadas
        int tareasActualizadas = 0;
        
        // Se recorre cada tarea para evaluar su prioridad
        for (Tarea tarea : tareas) {
            // Solo procesamos tareas no completadas
            if (!"Completada".equals(tarea.getEstadoTarea())) {
                Date fechaFin = tarea.getFechaFinTarea();
                if (fechaFin != null) {
                    // Se convierte la fecha a LocalDate para facilitar cálculos
                    LocalDate fechaFinLD = fechaFin.toInstant()
                                            .atZone(ZoneId.systemDefault())
                                            .toLocalDate();
                    
                    // Se calculan los días restantes hasta la fecha límite
                    long diasHastaFin = ChronoUnit.DAYS.between(hoy, fechaFinLD);
                    
                    // Se almacena la prioridad actual
                    String prioridadOriginal = tarea.getPrioridadTarea();
                    String nuevaPrioridad = prioridadOriginal;
                    
                    // Se aplica la misma lógica de priorización según los días restantes
                    if (diasHastaFin <= 1) {
                        nuevaPrioridad = "Alta";
                    } else if (diasHastaFin <= 3) {
                        nuevaPrioridad = "Media";
                    } else {
                        nuevaPrioridad = "Baja";
                    }
                    
                    // Se actualiza solo si hay cambio en la prioridad
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
        
        // Se registra la finalización y se retorna el número de tareas actualizadas
        logger.info("Actualización manual de prioridades completada. {} tareas actualizadas", tareasActualizadas);
        return tareasActualizadas; // Retorna el conteo para informar al controlador
    }
} 