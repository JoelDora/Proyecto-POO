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

@Component
public class HistorialControlador implements Initializable {

    @Autowired
    private HistorialTareaServicio historialTareaServicio;
    
    @Autowired
    private tareaServicio TareaServicio;
    
    // Interfaz para notificar a la ventana principal que se ha recuperado una tarea
    public interface RecuperacionListener {
        void onTareaRecuperada();
    }
    
    private RecuperacionListener recuperacionListener;
    
    public void setRecuperacionListener(RecuperacionListener listener) {
        this.recuperacionListener = listener;
    }

    @FXML
    private TableView<HistorialTarea> historialTabla;

    @FXML
    private TableColumn<HistorialTarea, Integer> idHistorialColumna;

    @FXML
    private TableColumn<HistorialTarea, Integer> idOriginalColumna;

    @FXML
    private TableColumn<HistorialTarea, String> nombreColumna;

    @FXML
    private TableColumn<HistorialTarea, String> descripcionColumna;

    @FXML
    private TableColumn<HistorialTarea, String> responsableColumna;

    @FXML
    private TableColumn<HistorialTarea, String> estadoColumna;

    @FXML
    private TableColumn<HistorialTarea, String> prioridadColumna;

    @FXML
    private TableColumn<HistorialTarea, Date> fechaInicioColumna;

    @FXML
    private TableColumn<HistorialTarea, Date> fechaFinColumna;

    @FXML
    private TableColumn<HistorialTarea, Date> fechaEliminacionColumna;

    @FXML
    private TableColumn<HistorialTarea, String> accionColumna;

    @FXML
    private Button cerrarBoton;
    
    @FXML
    private Button recuperarBoton;
    
    @FXML
    private Button eliminarDefinitivoBoton;

    private final ObservableList<HistorialTarea> historialList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarColumnas();
        cargarHistorial();
    }

    private void configurarColumnas() {
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
        
        // Configurar el color de la columna de prioridad según su valor
        prioridadColumna.setCellFactory(column -> {
            return new TableCell<HistorialTarea, String>() {
                @Override
                protected void updateItem(String prioridad, boolean empty) {
                    super.updateItem(prioridad, empty);
                    
                    if (empty || prioridad == null) {
                        setText(null);
                        setStyle("");
                        getStyleClass().clear();
                    } else {
                        setText(prioridad);
                        
                        // Limpiar clases de estilo anteriores
                        getStyleClass().removeAll("prioridad-alta", "prioridad-media", "prioridad-baja");
                        
                        // Aplicar estilo según la prioridad
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

    private void cargarHistorial() {
        historialList.clear();
        historialList.addAll(historialTareaServicio.listarHistorial());
        historialTabla.setItems(historialList);
    }
    
    @FXML
    private void recuperarTarea() {
        HistorialTarea historialSeleccionado = historialTabla.getSelectionModel().getSelectedItem();
        if (historialSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar recuperación");
            confirmacion.setHeaderText("¿Está seguro de recuperar esta tarea?");
            confirmacion.setContentText("Tarea: " + historialSeleccionado.getNombreTarea());
            
            ButtonType botonSi = new ButtonType("Sí, recuperar");
            ButtonType botonNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            confirmacion.getButtonTypes().setAll(botonSi, botonNo);
            
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == botonSi) {
                    // Recuperar la tarea
                    Tarea tareaRecuperada = historialTareaServicio.recuperarTarea(historialSeleccionado);
                    
                    // Guardar la tarea recuperada
                    TareaServicio.guardarTarea(tareaRecuperada);
                    
                    // Recargar la lista de historial
                    cargarHistorial();
                    
                    // Notificar a la ventana principal
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
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selección requerida");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, seleccione una tarea para recuperar.");
            alerta.showAndWait();
        }
    }
    
    @FXML
    private void eliminarDefinitivo() {
        HistorialTarea historialSeleccionado = historialTabla.getSelectionModel().getSelectedItem();
        if (historialSeleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación definitiva");
            confirmacion.setHeaderText("¡ATENCIÓN! Esta acción no se puede deshacer");
            confirmacion.setContentText("¿Está seguro de eliminar definitivamente la tarea: " + 
                    historialSeleccionado.getNombreTarea() + "?\n\nEsta acción eliminará permanentemente la tarea del historial y no podrá recuperarla.");
            
            // Botones personalizados con estilo de advertencia
            ButtonType botonEliminar = new ButtonType("Eliminar definitivamente");
            ButtonType botonCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            confirmacion.getButtonTypes().setAll(botonEliminar, botonCancelar);
            
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == botonEliminar) {
                    // Eliminar definitivamente del historial
                    historialTareaServicio.eliminarDefinitivamente(historialSeleccionado);
                    
                    // Recargar la lista de historial
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
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Selección requerida");
            alerta.setHeaderText(null);
            alerta.setContentText("Por favor, seleccione una tarea para eliminar definitivamente.");
            alerta.showAndWait();
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) cerrarBoton.getScene().getWindow();
        stage.close();
    }
} 