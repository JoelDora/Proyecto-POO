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
import poo.tareas.controlador.NotificacionesControlador.NotificacionListener;
import poo.tareas.modelo.Tarea;
import poo.tareas.servicio.HistorialTareaServicio;
import poo.tareas.servicio.tareaServicio;
import poo.tareas.servicio.PrioridadActualizadorService;
import poo.tareas.servicio.NotificacionService;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.List;
import javafx.scene.control.*;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ComboBox;
import java.time.ZoneId;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javafx.application.Platform;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;


/**
 * Controlador principal de la aplicación de gestión de tareas.
 * Maneja la interfaz de usuario para crear, leer, actualizar y eliminar tareas.
 * Implementa la interfaz Initializable de JavaFX y RecuperacionListener para
 * actualizar la lista de tareas cuando se recupera una tarea eliminada.
 */
@Component
public class IndexControlador implements Initializable, RecuperacionListener, NotificacionListener {
    // Logger para registrar eventos y errores en la aplicación
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    // Inyección de dependencias con Spring
    @Autowired
    private tareaServicio TareaServicio; // Servicio para operaciones CRUD de tareas
    
    @Autowired
    private HistorialTareaServicio historialTareaServicio; // Servicio para gestionar el historial de tareas eliminadas
    
    @Autowired
    private PrioridadActualizadorService prioridadActualizadorService; // Servicio para actualizar prioridades automáticamente

    @Autowired
    private NotificacionService notificacionService; // Servicio para gestionar notificaciones de tareas vencidas
    
    @Autowired
    private ApplicationContext applicationContext; // Contexto de Spring para la creación de beans

    // Componentes de la interfaz de usuario definidos en el archivo FXML
    @FXML
    private TableView<Tarea> tareaTabla; // Tabla para mostrar las tareas

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
    
    @FXML
    private Button notificacionesBoton;
    
    @FXML
    private Circle indicadorNotificaciones;

    // Lista observable para almacenar y mostrar las tareas en la tabla
    private final ObservableList<Tarea> tareaList = FXCollections.observableArrayList();

    // Componentes del formulario para agregar/editar tareas
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

    // Variable para almacenar el ID de la tarea seleccionada cuando se edita
    private Integer idTareaInterno;

    /**
     * Método que se ejecuta al inicializar el controlador.
     * Configura los componentes de la interfaz y carga los datos iniciales.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar el modo de selección de la tabla a una sola fila
        tareaTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Configurar las columnas de la tabla
        configurarColumnas();
        
        // Cargar la lista de tareas en la tabla
        listarTareas();
        
        // Configurar los valores disponibles en los combobox
        estadoCombo.setItems(FXCollections.observableArrayList("Pendiente", "En progreso", "Completada"));
        prioridadCombo.setItems(FXCollections.observableArrayList("Alta", "Media", "Baja"));

        // Seleccionar el primer elemento de cada combobox por defecto
        estadoCombo.getSelectionModel().selectFirst();
        prioridadCombo.getSelectionModel().selectFirst();
        
        // Configurar el indicador de notificaciones
        if (indicadorNotificaciones != null) {
            indicadorNotificaciones.setFill(Paint.valueOf("#E0E0E0")); // Gris claro si no hay notificaciones
            // Verificar si hay notificaciones al iniciar
            verificarNotificacionesPendientes();
        }
        
        // Programar la verificación periódica de notificaciones (cada 60 segundos)
        programarVerificacionNotificaciones();
    }

    /**
     * Programa la verificación periódica de notificaciones.
     */
    private void programarVerificacionNotificaciones() {
        // Crear un hilo que se ejecuta cada 60 segundos
        Thread notificacionesThread = new Thread(() -> {
            while (true) {
                try {
                    // Verificar si hay nuevas tareas vencidas
                    notificacionService.verificarTareasVencidasManual();
                    
                    // Actualizar la interfaz en el hilo de la UI
                    Platform.runLater(this::verificarNotificacionesPendientes);
                    
                    // Esperar 60 segundos
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    logger.error("Error en hilo de notificaciones", e);
                    break;
                }
            }
        });
        
        // Configurar como demonio para que no impida cerrar la aplicación
        notificacionesThread.setDaemon(true);
        notificacionesThread.start();
    }
    
    /**
     * Verifica si hay notificaciones pendientes y actualiza el indicador visual.
     */
    private void verificarNotificacionesPendientes() {
        if (indicadorNotificaciones != null) {
            if (notificacionService.hayNotificacionesNuevas()) {
                // Activar indicador con color rojo si hay notificaciones
                indicadorNotificaciones.setFill(Paint.valueOf("#FF4136"));
                
                // Si la aplicación está activa, mostrar un mensaje pequeño
                if (notificacionesBoton.getScene().getWindow().isFocused()) {
                    mostrarNotificacionToast();
                }
            } else {
                // Desactivar indicador (gris claro) si no hay notificaciones
                indicadorNotificaciones.setFill(Paint.valueOf("#E0E0E0"));
            }
        }
    }
    
    /**
     * Muestra un mensaje toast con información sobre tareas vencidas.
     */
    private void mostrarNotificacionToast() {
        int numeroNotificaciones = notificacionService.getNumeroNotificaciones();
        if (numeroNotificaciones > 0) {
            Alert alerta = new Alert(Alert.AlertType.INFORMATION);
            alerta.setTitle("Tareas Vencidas");
            alerta.setHeaderText(null);
            alerta.setContentText("Hay " + numeroNotificaciones + " tarea(s) vencida(s).\n" +
                                "Haga clic en el botón de notificaciones para verlas.");
            
            // Mostrar y cerrar automáticamente después de 5 segundos
            new Thread(() -> {
                Platform.runLater(() -> alerta.show());
                try {
                    Thread.sleep(5000);
                    Platform.runLater(() -> {
                        if (alerta.isShowing()) {
                            alerta.close();
                        }
                    });
                } catch (InterruptedException e) {
                    logger.error("Error al cerrar notificación toast", e);
                }
            }).start();
        }
    }

    /**
     * Configura las columnas de la tabla asociándolas con las propiedades del modelo Tarea.
     * También establece un estilo especial para la columna de prioridad según su valor.
     */
    private void configurarColumnas(){
        // Asociar cada columna con la propiedad correspondiente de la clase Tarea
        idTareaColumna.setCellValueFactory(new PropertyValueFactory<>("idTarea"));
        nombreTareaColumna.setCellValueFactory(new PropertyValueFactory<>("nombreTarea"));
        responsableColumna.setCellValueFactory(new PropertyValueFactory<>("responsableTarea"));
        descripcionColumna.setCellValueFactory(new PropertyValueFactory<>("descripcionTarea"));
        fechaInicioColumna.setCellValueFactory(new PropertyValueFactory<>("fechaInicioTarea"));
        fechaFinColumna.setCellValueFactory(new PropertyValueFactory<>("fechaFinTarea"));
        estadoColumna.setCellValueFactory(new PropertyValueFactory<>("estadoTarea"));
        prioridadColumna.setCellValueFactory(new PropertyValueFactory<>("prioridadTarea"));
        
        // Configurar el estilo visual de la columna de prioridad según su valor (Alta, Media, Baja)
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

    /**
     * Carga la lista de tareas desde la base de datos y la muestra en la tabla.
     * Limpia la lista actual y la reemplaza con los datos actualizados.
     */
    private void listarTareas(){
        logger.info("Ejecutando listado de tareas");
        
        // Limpiar la lista actual para recargarla
        tareaList.clear();
        
        // Obtener todas las tareas desde el servicio
        List<Tarea> tareas = TareaServicio.listarTareas();
        
        // Agregar las tareas a la lista observable
        tareaList.addAll(tareas);
        
        // Asignar la lista observable a la tabla para mostrarla
        tareaTabla.setItems(tareaList);
        
        // Verificar si hay tareas vencidas y actualizar notificaciones
        verificarNotificacionesPendientes();
    }

    /**
     * Agrega una nueva tarea a la base de datos.
     * Valida que el nombre de la tarea no esté vacío antes de guardarla.
     */
    public void agregarTarea(){
        // Validar que el nombre de la tarea no esté vacío
        if(nombreTareaTexto.getText().isEmpty()) {
            mostrarMensaje("Error Validación", "Debe proporcionar una tarea");
            nombreTareaTexto.requestFocus();
            return;
        }
        else{
            // Crear una nueva tarea con los datos del formulario
            var tarea = new Tarea();
            recolectarDatosFormulario(tarea);
            tarea.setIdTarea(null); // Asegurar que es una tarea nueva (ID = null)
            TareaServicio.guardarTarea(tarea); // Guardar la tarea en la base de datos
            
            // Mostrar mensaje de éxito, limpiar el formulario y actualizar la tabla
            mostrarMensaje("Informacion", "Tarea agregada");
            limpiarFormulario();
            listarTareas();
        }
    }

    /**
     * Carga los datos de la tarea seleccionada en la tabla al formulario para su edición.
     */
    public void cargarTareaFormulario(){
        // Obtener la tarea seleccionada en la tabla
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if(tarea != null){
            // Cargar los datos de la tarea en los campos del formulario
            idTareaInterno = tarea.getIdTarea();
            nombreTareaTexto.setText(tarea.getNombreTarea());
            descripcionTareaTexto.setText(tarea.getDescripcionTarea());
            responsableTareaTexto.setText(tarea.getResponsableTarea());
            estadoCombo.setValue(tarea.getEstadoTarea());
            prioridadCombo.setValue(tarea.getPrioridadTarea());
            
            // Convertir las fechas de java.sql.Date a LocalDate para los DatePicker
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

    /**
     * Recolecta los datos ingresados en el formulario y los asigna a un objeto Tarea.
     * También valida que las fechas hayan sido seleccionadas.
     * 
     * @param tarea Objeto Tarea donde se asignarán los datos recolectados
     */
    private void recolectarDatosFormulario(Tarea tarea){
        // Si existe un ID interno (edición), asignarlo a la tarea
        if(idTareaInterno != null)
            tarea.setIdTarea(idTareaInterno);
            
        // Asignar los valores de los campos de texto
        tarea.setNombreTarea(nombreTareaTexto.getText());
        tarea.setResponsableTarea(responsableTareaTexto.getText());
        tarea.setDescripcionTarea(descripcionTareaTexto.getText());
        
        // Asignar los valores de los combobox
        tarea.setEstadoTarea(estadoCombo.getValue());
        tarea.setPrioridadTarea(prioridadCombo.getValue());

        // Validar y asignar fechas
        if (fechaInicioPicker.getValue() != null && fechaFinPicker.getValue() != null) {
            // Convertir LocalDate a java.sql.Date para guardar en la base de datos
            tarea.setFechaInicioTarea(java.sql.Date.valueOf(fechaInicioPicker.getValue()));
            tarea.setFechaFinTarea(java.sql.Date.valueOf(fechaFinPicker.getValue()));
        } else {
            // Mostrar error si no se han seleccionado ambas fechas
            mostrarMensaje("Error", "Debe seleccionar ambas fechas");
            throw new RuntimeException("Fechas no seleccionadas");
        }
    }

    /**
     * Modifica una tarea existente en la base de datos.
     * Valida que se haya seleccionado una tarea y que el nombre no esté vacío.
     */
    @FXML
    private void modificarTarea(){
        // Verificar que se haya seleccionado una tarea (ID interno no nulo)
        if(idTareaInterno == null) {
            mostrarMensaje("Información", "Debe seleccionar una tarea");
            return;
        }
        
        // Validar que el nombre no esté vacío
        if(nombreTareaTexto.getText().isEmpty()){
            mostrarMensaje("Error Validación", "Debe proporcionar una tarea");
            nombreTareaTexto.requestFocus();
            return;
        }
        
        // Crear objeto tarea y asignarle los datos del formulario
        var tarea = new Tarea();
        recolectarDatosFormulario(tarea);
        
        // Guardar la tarea modificada en la base de datos
        TareaServicio.guardarTarea(tarea);
        
        // Mostrar mensaje de éxito, limpiar formulario y actualizar la tabla
        mostrarMensaje("Información", "Tarea modificada con exito");
        limpiarFormulario();
        listarTareas();
    }

    /**
     * Elimina una tarea seleccionada en la tabla, previa confirmación del usuario.
     * También registra la eliminación en el historial para posible recuperación.
     */
    public void eliminarTarea(){
        // Obtener la tarea seleccionada en la tabla
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
                    // Registrar en el historial antes de eliminar para posible recuperación
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

    /**
     * Limpia todos los campos del formulario y reinicia el estado de edición.
     */
    public void limpiarFormulario(){
        // Reiniciar el ID interno (ya no estamos editando ninguna tarea)
        idTareaInterno = null;
        
        // Limpiar campos de texto
        nombreTareaTexto.clear();
        responsableTareaTexto.clear();
        descripcionTareaTexto.clear();
        
        // Limpiar selecciones de combobox
        estadoCombo.getSelectionModel().clearSelection();
        prioridadCombo.getSelectionModel().clearSelection();
        
        // Limpiar fechas
        fechaInicioPicker.setValue(null);
        fechaFinPicker.setValue(null);
    }

    /**
     * Muestra un mensaje de información o error al usuario.
     * 
     * @param titulo Título de la ventana de diálogo
     * @param mensaje Mensaje a mostrar
     */
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    /**
     * Exporta la lista actual de tareas a un archivo Excel.
     * Permite al usuario seleccionar la ubicación donde guardar el archivo.
     */
    @FXML
    private void exportarExcel() {
        // Crear un diálogo para seleccionar dónde guardar el archivo
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar archivo Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos Excel (*.xlsx)", "*.xlsx"));
        fileChooser.setInitialFileName("tareas.xlsx");

        // Mostrar el diálogo de guardar archivo
        Stage stage = (Stage) tareaTabla.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try (Workbook workbook = new XSSFWorkbook();
                 FileOutputStream fileOut = new FileOutputStream(file)) {

                // Crear una hoja en el libro de Excel
                Sheet sheet = workbook.createSheet("Tareas");

                // Crear la fila de encabezados
                Row header = sheet.createRow(0);
                header.createCell(0).setCellValue("ID");
                header.createCell(1).setCellValue("Nombre");
                header.createCell(2).setCellValue("Descripción");
                header.createCell(3).setCellValue("Responsable");
                header.createCell(4).setCellValue("Estado");
                header.createCell(5).setCellValue("Prioridad");
                header.createCell(6).setCellValue("Fecha Inicio");
                header.createCell(7).setCellValue("Fecha Fin");

                // Agregar los datos de las tareas
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

                // Escribir el libro en el archivo
                workbook.write(fileOut);
                mostrarMensaje("Éxito", "Archivo exportado correctamente:\n" + file.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                mostrarMensaje("Error", "Ocurrió un error al exportar: " + e.getMessage());
            }
        }
    }

    /**
     * Abre una ventana modal para mostrar el historial de tareas eliminadas.
     * Permite recuperar tareas previamente eliminadas.
     */
    @FXML
    private void mostrarHistorial() {
        try {
            // Cargar la vista del historial
            FXMLLoader loader = new FXMLLoader(TareasApplication.class.getResource("/templates/historial.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            // Obtener el controlador y establecer el listener para actualizar la lista cuando se recupere una tarea
            HistorialControlador historialControlador = loader.getController();
            historialControlador.setRecuperacionListener(this);
            
            // Crear una nueva ventana modal
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
    
    /**
     * Método implementado de la interfaz RecuperacionListener.
     * Se llama cuando una tarea es recuperada desde la ventana de historial.
     */
    @Override
    public void onTareaRecuperada() {
        // Actualizar la lista de tareas cuando se recupera una tarea del historial
        listarTareas();
    }

    /**
     * Método implementado de la interfaz NotificacionListener.
     * Se llama cuando una tarea es actualizada desde la ventana de notificaciones.
     */
    @Override
    public void onTareaActualizada() {
        // Actualizar la lista de tareas cuando se modifica una tarea desde las notificaciones
        listarTareas();
    }

    /**
     * Actualiza manualmente las prioridades de las tareas según sus fechas de finalización.
     * Muestra un mensaje con la cantidad de tareas actualizadas y las reglas aplicadas.
     */
    @FXML
    private void actualizarPrioridades() {
        // Llamar al servicio para actualizar las prioridades
        int tareasActualizadas = prioridadActualizadorService.actualizarPrioridadesManual();
        
        // Crear una ventana de diálogo para mostrar el resultado
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle("Prioridades Actualizadas");
        alerta.setHeaderText(null);
        
        if (tareasActualizadas > 0) {
            // Mostrar información sobre las reglas y cantidad de tareas actualizadas
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
    
    /**
     * Abre la ventana de notificaciones para mostrar tareas vencidas.
     */
    @FXML
    private void mostrarNotificaciones() {
        try {
            // Verificar tareas vencidas antes de mostrar la ventana
            notificacionService.verificarTareasVencidasManual();
            
            // Si no hay notificaciones, mostrar mensaje y no abrir la ventana
            if (notificacionService.getNumeroNotificaciones() == 0) {
                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                alerta.setTitle("Notificaciones");
                alerta.setHeaderText(null);
                alerta.setContentText("No hay tareas vencidas en este momento.");
                alerta.showAndWait();
                return;
            }
            
            // Cargar la vista de notificaciones
            FXMLLoader loader = new FXMLLoader(TareasApplication.class.getResource("/templates/notificaciones.fxml"));
            loader.setControllerFactory(applicationContext::getBean);
            Parent root = loader.load();
            
            // Obtener el controlador y establecer el listener para actualizar la lista cuando se actualice una tarea
            NotificacionesControlador notificacionesControlador = loader.getController();
            notificacionesControlador.setNotificacionListener(this);
            
            // Crear una nueva ventana modal
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Tareas Vencidas");
            stage.setScene(new Scene(root));
            stage.show();
            
            // Actualizar el indicador de notificaciones después de mostrar la ventana
            notificacionService.marcarNotificacionesComoVistas();
            verificarNotificacionesPendientes();
            
        } catch (Exception e) {
            logger.error("Error al abrir ventana de notificaciones", e);
            mostrarMensaje("Error", "No se pudo abrir la ventana de notificaciones: " + e.getMessage());
        }
    }
}
