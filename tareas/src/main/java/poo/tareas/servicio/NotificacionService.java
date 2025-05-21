package poo.tareas.servicio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import poo.tareas.modelo.Tarea;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Servicio encargado de verificar tareas vencidas y notificar al usuario.
 * Mantiene un registro de tareas vencidas y proporciona métodos para
 * verificar si hay notificaciones pendientes.
 */
@Service
public class NotificacionService {
    // Configuración del sistema de logging
    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);
    
    // Inyección de dependencia del servicio de tareas
    @Autowired
    private tareaServicio tareaServicio;
    
    // Lista para almacenar las tareas vencidas que no han sido notificadas
    private List<Tarea> tareasVencidasSinNotificar = new ArrayList<>();
    
    // Bandera para indicar si hay notificaciones nuevas
    private boolean hayNotificacionesNuevas = false;
    
    /**
     * Verifica diariamente si hay tareas vencidas y las marca para notificación.
     * Se ejecuta automáticamente cada día a la medianoche.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar a las 00:00 todos los días
    public void verificarTareasVencidas() {
        logger.info("Verificando tareas vencidas...");
        
        // Obtener todas las tareas
        List<Tarea> tareas = tareaServicio.listarTareas();
        LocalDate hoy = LocalDate.now();
        
        int nuevasVencidas = 0;
        
        for (Tarea tarea : tareas) {
            // Solo considerar tareas que no estén completadas
            if (!"Completada".equals(tarea.getEstadoTarea())) {
                Date fechaFin = tarea.getFechaFinTarea();
                
                if (fechaFin != null) {
                    // Convertir a LocalDate para facilitar comparación
                    LocalDate fechaFinLD = fechaFin.toInstant()
                                          .atZone(ZoneId.systemDefault())
                                          .toLocalDate();
                    
                    // Verificar si la fecha de fin ha pasado
                    if (fechaFinLD.isBefore(hoy) || fechaFinLD.isEqual(hoy)) {
                        // Verificar si esta tarea ya está en nuestra lista de notificación
                        if (!contieneTarea(tareasVencidasSinNotificar, tarea.getIdTarea())) {
                            // Actualizar prioridad a "Alta" si está vencida
                            if (!"Alta".equals(tarea.getPrioridadTarea())) {
                                tarea.setPrioridadTarea("Alta");
                                tareaServicio.guardarTarea(tarea);
                                logger.info("Tarea ID {}: Cambiada a prioridad ALTA por vencimiento", tarea.getIdTarea());
                            }
                            
                            // Agregar a la lista de notificaciones
                            tareasVencidasSinNotificar.add(tarea);
                            nuevasVencidas++;
                        }
                    }
                }
            }
        }
        
        // Si hay nuevas tareas vencidas, activar la bandera de notificación
        if (nuevasVencidas > 0) {
            hayNotificacionesNuevas = true;
            logger.info("Se encontraron {} nuevas tareas vencidas para notificar", nuevasVencidas);
        }
    }
    
    /**
     * Verifica si una tarea ya está en la lista de tareas vencidas.
     */
    private boolean contieneTarea(List<Tarea> lista, Integer idTarea) {
        for (Tarea t : lista) {
            if (t.getIdTarea().equals(idTarea)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Método para verificar manualmente si hay tareas vencidas.
     * Este método puede ser llamado desde el controlador en cualquier momento.
     * 
     * @return El número de tareas vencidas encontradas
     */
    public int verificarTareasVencidasManual() {
        logger.info("Verificando tareas vencidas manualmente...");
        
        List<Tarea> tareas = tareaServicio.listarTareas();
        LocalDate hoy = LocalDate.now();
        
        int tareasActualizadas = 0;
        
        for (Tarea tarea : tareas) {
            if (!"Completada".equals(tarea.getEstadoTarea())) {
                Date fechaFin = tarea.getFechaFinTarea();
                
                if (fechaFin != null) {
                    LocalDate fechaFinLD = fechaFin.toInstant()
                                          .atZone(ZoneId.systemDefault())
                                          .toLocalDate();
                    
                    if (fechaFinLD.isBefore(hoy) || fechaFinLD.isEqual(hoy)) {
                        // Cambiar prioridad a Alta si está vencida
                        if (!"Alta".equals(tarea.getPrioridadTarea())) {
                            tarea.setPrioridadTarea("Alta");
                            tareaServicio.guardarTarea(tarea);
                            tareasActualizadas++;
                            
                            // Agregar a las notificaciones si no está ya
                            if (!contieneTarea(tareasVencidasSinNotificar, tarea.getIdTarea())) {
                                tareasVencidasSinNotificar.add(tarea);
                            }
                        }
                    }
                }
            }
        }
        
        if (tareasActualizadas > 0) {
            hayNotificacionesNuevas = true;
        }
        
        logger.info("Verificación manual completada. {} tareas actualizadas", tareasActualizadas);
        return tareasActualizadas;
    }
    
    /**
     * Obtiene la lista de tareas vencidas pendientes de notificación.
     * 
     * @return Lista de tareas vencidas sin notificar
     */
    public List<Tarea> getTareasVencidasSinNotificar() {
        return new ArrayList<>(tareasVencidasSinNotificar);
    }
    
    /**
     * Marca todas las notificaciones como vistas.
     */
    public void marcarNotificacionesComoVistas() {
        hayNotificacionesNuevas = false;
    }
    
    /**
     * Elimina una tarea específica de la lista de notificaciones.
     * 
     * @param idTarea El ID de la tarea a eliminar de las notificaciones
     */
    public void eliminarNotificacion(Integer idTarea) {
        tareasVencidasSinNotificar.removeIf(t -> t.getIdTarea().equals(idTarea));
    }
    
    /**
     * Verifica si hay notificaciones nuevas pendientes.
     * 
     * @return true si hay notificaciones sin ver, false de lo contrario
     */
    public boolean hayNotificacionesNuevas() {
        return hayNotificacionesNuevas;
    }
    
    /**
     * Obtiene el número de notificaciones sin ver.
     * 
     * @return El número de tareas vencidas pendientes de notificación
     */
    public int getNumeroNotificaciones() {
        return tareasVencidasSinNotificar.size();
    }
} 