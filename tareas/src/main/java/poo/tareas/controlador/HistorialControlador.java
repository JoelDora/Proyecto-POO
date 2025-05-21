package poo.tareas.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poo.tareas.modelo.HistorialTarea;
import poo.tareas.modelo.Tarea;
import poo.tareas.servicio.HistorialTareaServicio;
import poo.tareas.servicio.tareaServicio;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Controlador para la vista del historial de tareas eliminadas.
 * Esta clase gestiona la interacción del usuario con la vista de historial,
 * permitiendo visualizar, recuperar y eliminar definitivamente tareas previamente borradas.
 */
@Component
public class HistorialControlador implements Initializable {

    /**
     * Servicio que gestiona las operaciones relacionadas con el historial de tareas.
     * Se inyecta automáticamente con Spring.
     */
    @Autowired
    private HistorialTareaServicio historialTareaServicio;
    
    /**
     * Servicio que gestiona las operaciones relacionadas con las tareas.
     * Se inyecta automáticamente con Spring.
     */
    @Autowired
    private tareaServicio TareaServicio;
    
    /**
     * Interfaz que define un método para notificar a la ventana principal
     * cuando se ha recuperado una tarea del historial.
     * Implementa el patrón Observer para la comunicación entre ventanas.
     */
    public interface RecuperacionListener {
        void onTareaRecuperada();
    }
    
    /**
     * Instancia del listener que se notificará cuando se recupere una tarea.
     */
    private RecuperacionListener recuperacionListener;
    
    /**
     * Establece el listener que será notificado cuando se recupere una tarea.
     * @param listener El objeto que implementa la interfaz RecuperacionListener
     */
    public void setRecuperacionListener(RecuperacionListener listener) {
        this.recuperacionListener = listener;
    }

    // Elementos de la interfaz gráfica (inyectados por JavaFX)
    
    /**
     * Tabla que muestra las tareas en el historial.
     */
    @FXML
    private TableView<HistorialTarea> historialTabla;

    /**
     * Columna para el ID de registro en el historial.
     */
    @FXML
    private TableColumn<HistorialTarea, Integer> idHistorialColumna;

    /**
     * Columna para el ID original de la tarea antes de ser eliminada.
     */
    @FXML
    private TableColumn<HistorialTarea, Integer> idOriginalColumna;

    /**
     * Columna para el nombre de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, String> nombreColumna;

    /**
     * Columna para la descripción de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, String> descripcionColumna;

    /**
     * Columna para el responsable de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, String> responsableColumna;

    /**
     * Columna para el estado de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, String> estadoColumna;

    /**
     * Columna para la prioridad de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, String> prioridadColumna;

    /**
     * Columna para la fecha de inicio de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, Date> fechaInicioColumna;

    /**
     * Columna para la fecha de finalización de la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, Date> fechaFinColumna;

    /**
     * Columna para la fecha en que se eliminó la tarea.
     */
    @FXML
    private TableColumn<HistorialTarea, Date> fechaEliminacionColumna;

    /**
     * Columna para el tipo de acción realizada (ej. "Eliminación").
     */
    @FXML
    private TableColumn<HistorialTarea, String> accionColumna;

    /**
     * Botón para cerrar la ventana de historial.
     */
    @FXML
    private Button cerrarBoton;
    
    /**
     * Botón para recuperar una tarea seleccionada del historial.
     */
    @FXML
    private Button recuperarBoton;
    
    /**
     * Botón para eliminar definitivamente una tarea del historial.
     */
    @FXML
    private Button eliminarDefinitivoBoton;

    /**
     * Lista observable que contiene los elementos del historial mostrados en la tabla.
     * Se utiliza ObservableList para que la interfaz de usuario se actualice automáticamente con los cambios.
     */
    private final ObservableList<HistorialTarea> historialList = FXCollections.observableArrayList();

    /**
     * Método inicializador que se ejecuta automáticamente después de que
     * se hayan inyectado todos los elementos FXML.
     * @param url La ubicación usada para resolver rutas relativas para el objeto raíz
     * @param resourceBundle Los recursos localizados
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarHistorial();
    }

    /**
     * Configura las columnas de la tabla asignando los valores de las propiedades
     * del modelo HistorialTarea a cada columna correspondiente.
     * También configura el formato visual de la columna de prioridad.
     */
    private void configurarColumnas() {
        // Asocia cada columna con la propiedad correspondiente del modelo HistorialTarea
        idHistorialColumna.setCellValueFactory(new PropertyValueFactory<>("idHistorial"));
        idOriginalColumna.setCellValueFactory(new PropertyValueFactory<>("idTareaOriginal"));
        nombreColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        descripcionColumna.setCellValueFactory(new PropertyValueFactory<>("descripcionTarea"));
        responsableColumna.setCellValueFactory(new PropertyValueFactory<>("responsableTarea"));
        estadoColumna.setCellValueFactory(new PropertyValueFactory<>("estadoTarea"));
        prioridadColumna.setCellValueFactory(new PropertyValueFactory<>("prioridadTarea"));
        fechaInicioColumna.setCellValueFactory(new PropertyValueFactory<>("fechaInicioTarea"));
        fechaFinColumna.setCellValueFactory(new PropertyValueFactory<>("fechaFinTarea"));
        fechaEliminacionColumna.setCellValueFactory(new PropertyValueFactory<>("fechaEliminacion"));
        accionColumna.setCellValueFactory(new PropertyValueFactory<>("accion"));
        
        // Personaliza la apariencia de la columna de prioridad según su valor
        prioridadColumna.setCellFactory(column -> {
            return new TableCell<HistorialTarea, String>() {
                @Override
                protected void updateItem(String prioridad, boolean empty) {
                    super.updateItem(prioridad, empty);
                    
                    // Si la celda está vacía o el valor es nulo, no mostrar nada
                    if (empty || prioridad == null) {
                        setText(null);
                        setStyle("");
                        getStyleClass().clear();
                    } else {
                        // Mostrar el texto de la prioridad
                        setText(prioridad);
                        
                        // Limpiar estilos anteriores para evitar acumulación
                        getStyleClass().removeAll("prioridad-alta", "prioridad-media", "prioridad-baja");
                        
                        // Aplicar el estilo CSS correspondiente según la prioridad
                        if (prioridad.equals("Alta")) {
                            getStyleClass().add("prioridad-alta");
                        } else if (prioridad.equals("Media")) {
                            getStyleClass().add("prioridad-media");
                        } else if (prioridad.equals("Baja")) {
                            getStyleClass().add("prioridad-baja");
                        }
                    }
                }
            };
        });
    }

    /**
     * Carga los datos del historial desde el servicio y los muestra en la tabla.
     * Este método borra la lista actual y la rellena con datos actualizados.
     */
    private void cargarHistorial() {
        // Limpiar la lista actual para evitar duplicados
        historialList.clear();
        // Obtener datos del servicio y añadirlos a la lista observable
        historialList.addAll(historialTareaServicio.listarHistorial());
        // Asignar la lista a la tabla para mostrarla
        historialTabla.setItems(historialList);
    }
    
    /**
     * Maneja el evento de clic en el botón "Recuperar".
     * Restaura una tarea eliminada desde el historial al sistema activo.
     * Solicita confirmación al usuario antes de proceder.
     */
    @FXML
    private void recuperarTarea() {
        // Obtener la tarea seleccionada en la tabla
        HistorialTarea historialSeleccionado = historialTabla.getSelectionModel().getSelectedItem();
        if (historialSeleccionado != null) {
            // Crear diálogo de confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar recuperación");
            confirmacion.setHeaderText("¿Está seguro de recuperar esta tarea?");
            confirmacion.setContentText("Tarea: " + historialSeleccionado.getNombreTarea());
            
            // Configurar botones personalizados
            ButtonType botonSi = new ButtonType("Sí, recuperar");
            ButtonType botonNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            confirmacion.getButtonTypes().setAll(botonSi, botonNo);
            
            // Mostrar diálogo y procesar la respuesta
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == botonSi) {
                    // Llamar al servicio para convertir el historial en una tarea activa
                    Tarea tareaRecuperada = historialTareaServicio.recuperarTarea(historialSeleccionado);
                    
                    // Guardar la tarea recuperada en el sistema
                    TareaServicio.guardarTarea(tareaRecuperada);
                    
                    // Actualizar la vista del historial
                    cargarHistorial();
                    
                    // Notificar a la ventana principal sobre el cambio
                    if (recuperacionListener != null) {
                        recuperacionListener.onTareaRecuperada();
                    }
                    
                    // Mostrar mensaje de éxito
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Tarea recuperada");
                    exito.setHeaderText(null);
                    exito.setContentText("La tarea ha sido recuperada exitosamente.");
                    exito.showAndWait();
                }
            });
        } else {
            // Mostrar alerta si no hay selección
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selección requerida");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, seleccione una tarea para recuperar.");
            alerta.showAndWait();
        }
    }
    
    /**
     * Maneja el evento de clic en el botón "Eliminar definitivamente".
     * Elimina permanentemente una tarea del historial sin posibilidad de recuperación.
     * Solicita confirmación al usuario antes de proceder debido a la naturaleza irreversible.
     */
    @FXML
    private void eliminarDefinitivo() {
        // Obtener la tarea seleccionada en la tabla
        HistorialTarea historialSeleccionado = historialTabla.getSelectionModel().getSelectedItem();
        if (historialSeleccionado != null) {
            // Crear diálogo de confirmación con mensaje de advertencia
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación definitiva");
            confirmacion.setHeaderText("¡ATENCIÓN! Esta acción no se puede deshacer");
            confirmacion.setContentText("¿Está seguro de eliminar definitivamente la tarea: " + 
                    historialSeleccionado.getNombreTarea() + "?\n\nEsta acción eliminará permanentemente la tarea del historial y no podrá recuperarla.");
            
            // Botones personalizados con estilo de advertencia
            ButtonType botonEliminar = new ButtonType("Eliminar definitivamente");
            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            confirmacion.getButtonTypes().setAll(botonEliminar, botonCancelar);
            
            // Mostrar diálogo y procesar la respuesta
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == botonEliminar) {
                    // Llamar al servicio para eliminar definitivamente
                    historialTareaServicio.eliminarDefinitivamente(historialSeleccionado);
                    
                    // Actualizar la vista del historial
                    cargarHistorial();
                    
                    // Mostrar mensaje de éxito
                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Tarea eliminada definitivamente");
                    exito.setHeaderText(null);
                    exito.setContentText("La tarea ha sido eliminada permanentemente del historial.");
                    exito.showAndWait();
                }
            });
        } else {
            // Mostrar alerta si no hay selección
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selección requerida");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, seleccione una tarea para eliminar definitivamente.");
            alerta.showAndWait();
        }
    }

    /**
     * Cierra la ventana actual del historial.
     * Se asocia con el evento de clic en el botón "Cerrar".
     */
    @FXML
    private void cerrarVentana() {
        // Obtener la referencia a la ventana actual y cerrarla
        Stage stage = (Stage) cerrarBoton.getScene().getWindow();
        stage.close();
    }
} 