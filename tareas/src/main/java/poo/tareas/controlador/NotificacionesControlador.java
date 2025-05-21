package poo.tareas.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poo.tareas.modelo.Tarea;
import poo.tareas.servicio.NotificacionService;
import poo.tareas.servicio.tareaServicio;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador para la ventana de notificaciones de tareas vencidas.
 * Muestra una lista de tareas que han vencido y permite marcarlas como completadas.
 */
@Component
public class NotificacionesControlador implements Initializable {
    // Logger para registrar eventos
    private static final Logger logger = LoggerFactory.getLogger(NotificacionesControlador.class);
    
    // Inyección de servicios
    @Autowired
    private NotificacionService notificacionService;
    
    @Autowired
    private tareaServicio tareaServicio;
    
    // Interfaz para comunicar actualizaciones de tareas al controlador principal
    private NotificacionListener notificacionListener;
    
    // Elementos de la interfaz de usuario
    @FXML
    private TableView<Tarea> tareasVencidasTabla;
    
    @FXML
    private TableColumn<Tarea, Integer> idColumna;
    
    @FXML
    private TableColumn<Tarea, String> nombreColumna;
    
    @FXML
    private TableColumn<Tarea, String> fechaFinColumna;
    
    @FXML
    private TableColumn<Tarea, String> responsableColumna;
    
    @FXML
    private Button marcarCompletadaButton;
    
    @FXML
    private Button cerrarButton;
    
    // Lista para almacenar los datos de las tareas vencidas
    private final ObservableList<Tarea> tareasVencidasList = FXCollections.observableArrayList();
    
    /**
     * Inicializa el controlador y configura la tabla.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar la tabla
        tareasVencidasTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Configurar las columnas
        idColumna.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        nombreColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        fechaFinColumna.setCellValueFactory(new PropertyValueFactory<>("fechaFinTarea"));
        responsableColumna.setCellValueFactory(new PropertyValueFactory<>("responsableTarea"));
        
        // Cargar las tareas vencidas
        cargarTareasVencidas();
        
        // Marcar las notificaciones como vistas
        notificacionService.marcarNotificacionesComoVistas();
    }
    
    /**
     * Carga las tareas vencidas desde el servicio de notificaciones.
     */
    private void cargarTareasVencidas() {
        // Limpiar la lista actual
        tareasVencidasList.clear();
        
        // Obtener tareas vencidas del servicio
        tareasVencidasList.addAll(notificacionService.getTareasVencidasSinNotificar());
        
        // Asignar la lista a la tabla
        tareasVencidasTabla.setItems(tareasVencidasList);
    }
    
    /**
     * Marca la tarea seleccionada como completada.
     */
    @FXML
    public void marcarCompletada() {
        // Obtener la tarea seleccionada
        Tarea tarea = tareasVencidasTabla.getSelectionModel().getSelectedItem();
        
        if (tarea != null) {
            // Actualizar el estado de la tarea a "Completada"
            tarea.setEstadoTarea("Completada");
            tareaServicio.actualizarTarea(tarea);
            
            // Eliminar la tarea de la lista de notificaciones
            notificacionService.eliminarNotificacion(tarea.getIdTarea());
            
            // Actualizar la tabla de tareas vencidas
            cargarTareasVencidas();
            
            // Notificar al controlador principal sobre el cambio
            if (notificacionListener != null) {
                notificacionListener.onTareaActualizada();
            }
            
            // Mostrar mensaje de confirmación
            mostrarMensaje("Tarea Completada", "La tarea \"" + tarea.getNombreTarea() + "\" ha sido marcada como completada.");
            
            // Si no quedan tareas vencidas, cerrar la ventana
            if (tareasVencidasList.isEmpty()) {
                cerrarVentana();
            }
        } else {
            mostrarMensaje("Selección Requerida", "Por favor, seleccione una tarea para marcarla como completada.");
        }
    }
    
    /**
     * Cierra la ventana de notificaciones.
     */
    @FXML
    public void cerrarVentana() {
        // Obtener la ventana actual y cerrarla
        Stage stage = (Stage) cerrarButton.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Establece el listener para comunicar cambios al controlador principal.
     */
    public void setNotificacionListener(NotificacionListener listener) {
        this.notificacionListener = listener;
    }
    
    /**
     * Muestra un mensaje de información al usuario.
     */
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    /**
     * Interfaz para comunicar eventos de actualización de tareas.
     */
    public interface NotificacionListener {
        void onTareaActualizada();
    }
} 