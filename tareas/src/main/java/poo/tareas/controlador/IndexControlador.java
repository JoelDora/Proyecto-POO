package poo.tareas.controlador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import poo.tareas.modelo.Tarea;
import poo.tareas.servicio.ITareaServicio;
import poo.tareas.servicio.tareaServicio;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;


@Component
public class IndexControlador implements Initializable {
    private static final Logger logger = LoggerFactory.getLogger(IndexControlador.class);

    @Autowired
    private tareaServicio TareaServicio;

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

    private final ObservableList<Tarea> tareaList = FXCollections.observableArrayList();

    @FXML
    private TextField nombreTareaTexto;

    @FXML
    private TextField descripcionTareaTexto;

    @FXML
    private TextField responsableTareaTexto;

    @FXML
    private TextField estadoTareaTexto;

    @FXML
    private TextField prioridadTexto;

    @FXML
    private TextField fechaInicioTexto;

    @FXML
    private TextField fechaFinTexto;

    private Integer idTareaInterno;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tareaTabla.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        configurarColumnas();
        listarTareas();
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
            estadoTareaTexto.setText(tarea.getEstadoTarea());
            prioridadTexto.setText(tarea.getPrioridadTarea());
            fechaInicioTexto.setText(tarea.getFechaInicioTarea().toString());
            fechaFinTexto.setText(tarea.getFechaFinTarea().toString());
        }
    }

    private void recolectarDatosFormulario(Tarea tarea){
        if(idTareaInterno != null)
            tarea.setIdTarea(idTareaInterno);
        tarea.setNombreTarea(nombreTareaTexto.getText());
        tarea.setResponsableTarea(responsableTareaTexto.getText());
        tarea.setDescripcionTarea(descripcionTareaTexto.getText());
        tarea.setEstadoTarea(estadoTareaTexto.getText());
        tarea.setPrioridadTarea(prioridadTexto.getText());
        
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            tarea.setFechaInicioTarea(sdf.parse(fechaInicioTexto.getText()));
            tarea.setFechaFinTarea(sdf.parse(fechaFinTexto.getText()));
        } catch (ParseException e) {
            mostrarMensaje("Error", "Formato de fecha inválido. Use el formato yyyy-MM-dd");
            throw new RuntimeException(e);
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

    public  void eliminarTarea(){
        var tarea = tareaTabla.getSelectionModel().getSelectedItem();
        if(tarea != null ) {
            logger.info("Registro a eliminar: " + tarea.toString());
            TareaServicio.eliminarTarea(tarea);
            mostrarMensaje("Información" , "Tarea eliminada con éxito: " + tarea.getIdTarea());
            limpiarFormulario();
            listarTareas();
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
        estadoTareaTexto.clear();
        prioridadTexto.clear();
        fechaInicioTexto.clear();
        fechaFinTexto.clear();
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
