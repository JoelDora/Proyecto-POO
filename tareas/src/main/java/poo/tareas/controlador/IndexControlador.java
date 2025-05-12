package poo.tareas.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import poo.tareas.TareasApplication;
import poo.tareas.controlador.HistorialControlador.RecuperacionListener;
import poo.tareas.modelo.Tarea;
import poo.tareas.servicio.HistorialTareaServicio;
import poo.tareas.servicio.tareaServicio;
import poo.tareas.servicio.PrioridadActualizadorService;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import java.time.ZoneId;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


@Component
public class IndexControlador implements Initializable, RecuperacionListener {
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    @Autowired
    private tareaServicio TareaServicio;
    
    @Autowired
    private HistorialTareaServicio historialTareaServicio;
    
    @Autowired
    private PrioridadActualizadorService prioridadActualizadorService;
    
    @Autowired
    private ApplicationContext applicationContext;

    @FXML
    private TableView<Tarea> tareaTabla;

    @FXML
    private TableColumn<Tarea, Integer> idTareaColumna;

    @FXML
    private TableColumn<Tarea, String> nombreTareaColumna;

    @FXML
    private TableColumn<Tarea, String> descripcionColumna;

    @FXML
    private TableColumn<Tarea, String> responsableColumna;

    @FXML
    private TableColumn<Tarea, String> estadoColumna;

    @FXML
    private TableColumn<Tarea, String> prioridadColumna;

    @FXML
    private TableColumn<Tarea, LocalDate> fechaInicioColumna;

    @FXML
    private TableColumn<Tarea, LocalDate> fechaFinColumna;

    @FXML
    private Button historialBoton;

    @FXML
    private Button actualizarPrioridadesBoton;

    private final ObservableList<Tarea> tareaList = FXCollections.observableArrayList();

    @FXML
    private TextField nombreTareaTexto;

    @FXML
    private TextField descripcionTareaTexto;

    @FXML
    private TextField responsableTareaTexto;

    @FXML
    private ComboBox<String> estadoCombo;

    @FXML
    private ComboBox<String> prioridadCombo;

    @FXML
    private DatePicker fechaInicioPicker;

    @FXML
    private DatePicker fechaFinPicker;

    private Integer idTareaInterno;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        configurarColumnas();
        listarTareas();
        estadoCombo.setItems(FXCollections.observableArrayList("Pendiente", "En progreso", "Completada"));
        prioridadCombo.setItems(FXCollections.observableArrayList("Alta", "Media", "Baja"));

        estadoCombo.getSelectionModel().selectFirst(); // Opcional
        prioridadCombo.getSelectionModel().selectFirst(); // Opcional

    }

    private void configurarColumnas(){
        idTareaColumna.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        nombreTareaColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        responsableColumna.setCellValueFactory(new PropertyValueFactory<>("responsableTarea"));
        descripcionColumna.setCellValueFactory(new PropertyValueFactory<>("descripcionTarea"));
        fechaInicioColumna.setCellValueFactory(new PropertyValueFactory<>("fechaInicioTarea"));
        fechaFinColumna.setCellValueFactory(new PropertyValueFactory<>("fechaFinTarea"));
        estadoColumna.setCellValueFactory(new PropertyValueFactory<>("estadoTarea"));
        prioridadColumna.setCellValueFactory(new PropertyValueFactory<>("prioridadTarea"));
        
        // Configurar el color de la columna de prioridad según su valor
        prioridadColumna.setCellFactory(column -> {
            return new TableCell<Tarea, String>() {
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

    private void listarTareas(){
        logger.info("Ejecutando listado de tareas");
        tareaList.clear();
        tareaList.addAll(TareaServicio.listarTareas());
        tareaTabla.setItems(tareaList);
    }

    public void agregarTarea(){
        if(nombreTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error Validación", "Debe proporcionar una tarea");
            nombreTareaTexto.requestFocus();
            return;
        }
        else{
            var tarea = new Tarea();
            recolectarDatosFormulario(tarea);
            tarea.setIdTarea(null);
            TareaServicio.guardarTarea(tarea);
            mostrarMensaje("Informacion", "Tarea agregada");
            limpiarFormulario();
            listarTareas();
        }
    }

    public void cargarTareaFormulario(){
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if(tarea != null){
            idTareaInterno = tarea.getIdTarea();
            nombreTareaTexto.setText(tarea.getNombreTarea());
            descripcionTareaTexto.setText(tarea.getDescripcionTarea());
            responsableTareaTexto.setText(tarea.getResponsableTarea());
            estadoCombo.setValue(tarea.getEstadoTarea());
            prioridadCombo.setValue(tarea.getPrioridadTarea());
            fechaInicioPicker.setValue(tarea.getFechaInicioTarea()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());

            fechaFinPicker.setValue(tarea.getFechaFinTarea()
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate());

        }
    }

    private void recolectarDatosFormulario(Tarea tarea){
        if(idTareaInterno != null)
            tarea.setIdTarea(idTareaInterno);
        tarea.setNombreTarea(nombreTareaTexto.getText());
        tarea.setResponsableTarea(responsableTareaTexto.getText());
        tarea.setDescripcionTarea(descripcionTareaTexto.getText());
        tarea.setEstadoTarea(estadoCombo.getValue());
        tarea.setPrioridadTarea(prioridadCombo.getValue());

        if (fechaInicioPicker.getValue() != null && fechaFinPicker.getValue() != null) {
            tarea.setFechaInicioTarea(java.sql.Date.valueOf(fechaInicioPicker.getValue()));
            tarea.setFechaFinTarea(java.sql.Date.valueOf(fechaFinPicker.getValue()));
        } else {
            mostrarMensaje("Error", "Debe seleccionar ambas fechas");
            throw new RuntimeException("Fechas no seleccionadas");
        }

    }

    @FXML
    private void modificarTarea(){
        if(idTareaInterno == null) {
            mostrarMensaje("Información", "Debe seleccionar una tarea");
            return;
        }
        if(nombreTareaTexto.getText().isEmpty()){
            mostrarMensaje("Error Validación", "Debe proporcionar una tarea");
            nombreTareaTexto.requestFocus();
            return;
        }
        var tarea = new Tarea();
        recolectarDatosFormulario(tarea);
        TareaServicio.guardarTarea(tarea);
        mostrarMensaje("Información", "Tarea modificada con exito");
        limpiarFormulario();
        listarTareas();
    }

    public void eliminarTarea(){
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if(tarea != null ) {
            // Mostrar diálogo de confirmación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar esta tarea?");
            confirmacion.setContentText("Tarea: " + tarea.getNombreTarea());
            
            // Botones personalizados
            ButtonType botonSi = new ButtonType("Sí, eliminar");
            ButtonType botonNo = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
            
            confirmacion.getButtonTypes().setAll(botonSi, botonNo);
            
            // Esperar respuesta del usuario
            confirmacion.showAndWait().ifPresent(respuesta -> {
                if (respuesta == botonSi) {
                    // Registrar en el historial antes de eliminar
                    historialTareaServicio.registrarEliminacion(tarea);
                    
                    // Proceder con la eliminación
                    logger.info("Registro a eliminar: " + tarea.toString());
                    TareaServicio.eliminarTarea(tarea);
                    mostrarMensaje("Información", "Tarea eliminada con éxito: " + tarea.getIdTarea());
                    limpiarFormulario();
                    listarTareas();
                }
            });
        }
        else {
            mostrarMensaje("Error", "No se ha seleccionado ninguna tarea");
        }
    }

    public void limpiarFormulario(){
        idTareaInterno = null;
        nombreTareaTexto.clear();
        responsableTareaTexto.clear();
        descripcionTareaTexto.clear();
        estadoCombo.getSelectionModel().clearSelection();
        prioridadCombo.getSelectionModel().clearSelection();
        fechaInicioPicker.setValue(null);
        fechaFinPicker.setValue(null);

    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void exportarExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("tareas.xlsx");

        Stage stage = (Stage) tareaTabla.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                Sheet sheet = workbook.createSheet("Tareas");

                // Encabezados
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("ID");
                header.createCell(1).setCellValue("Nombre");
                header.createCell(2).setCellValue("Descripción");
                header.createCell(3).setCellValue("Responsable");
                header.createCell(4).setCellValue("Estado");
                header.createCell(5).setCellValue("Prioridad");
                header.createCell(6).setCellValue("Fecha Inicio");
                header.createCell(7).setCellValue("Fecha Fin");

                // Contenido
                int rowIndex = 1;
                for (Tarea tarea : tareaTabla.getItems()) {
                    Row row = sheet.createRow(rowIndex++);
                    row.createCell(0).setCellValue(tarea.getIdTarea());
                    row.createCell(1).setCellValue(tarea.getNombreTarea());
                    row.createCell(2).setCellValue(tarea.getDescripcionTarea());
                    row.createCell(3).setCellValue(tarea.getResponsableTarea());
                    row.createCell(4).setCellValue(tarea.getEstadoTarea());
                    row.createCell(5).setCellValue(tarea.getPrioridadTarea());
                    row.createCell(6).setCellValue(tarea.getFechaInicioTarea().toString());
                    row.createCell(7).setCellValue(tarea.getFechaFinTarea().toString());
                }

                workbook.write(fileOut);
                mostrarMensaje("Éxito", "Archivo exportado correctamente:\n" + file.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensaje("Error", "Ocurrió un error al exportar: " + e.getMessage());
            }
        }
    }

    @FXML
    private void mostrarHistorial() {
        try {
            FXMLLoader loader = new FXMLLoader(TareasApplication.class.getResource("/templates/historial.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            // Obtener el controlador y establecer el listener
            HistorialControlador historialControlador = loader.getController();
            historialControlador.setRecuperacionListener(this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Historial de Tareas Eliminadas");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            logger.error("Error al abrir ventana de historial", e);
            mostrarMensaje("Error", "No se pudo abrir la ventana de historial: " + e.getMessage());
        }
    }
    
    @Override
    public void onTareaRecuperada() {
        // Actualizar la lista de tareas cuando se recupera una tarea
        listarTareas();
    }

    @FXML
    private void actualizarPrioridades() {
        int tareasActualizadas = prioridadActualizadorService.actualizarPrioridadesManual();
        
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Prioridades Actualizadas");
        alerta.setHeaderText(null);
        
        if (tareasActualizadas > 0) {
            alerta.setContentText("Se han actualizado " + tareasActualizadas + " tareas basadas en sus fechas de finalización.\n\n" +
                    "Reglas:\n" +
                    "- 1 día o menos: Prioridad Alta\n" +
                    "- 3 días o menos: Prioridad Media\n" +
                    "- Más de 3 días: Prioridad Baja");
            
            // Refrescar la tabla para mostrar los cambios
            listarTareas();
        } else {
            alerta.setContentText("No se requirieron cambios en las prioridades de las tareas.");
        }
        
        alerta.showAndWait();
    }
}
