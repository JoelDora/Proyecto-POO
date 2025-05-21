package poo.tareas.presentacion;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import poo.tareas.TareasApplication;

import java.io.IOException;

/**
 * Clase principal que inicia la aplicación de JavaFX integrada con Spring Boot.
 * Esta clase se encarga de inicializar tanto el contexto de Spring como la interfaz de usuario JavaFX.
 * Extiende de Application, que es la clase base para aplicaciones JavaFX.
 */
public class SistemasTareasFx extends Application {
    // Contexto de aplicación Spring que nos permitirá acceder a los beans y servicios
    private ConfigurableApplicationContext applicationContext;

    /**
     * Método que se ejecuta durante la inicialización de la aplicación JavaFX.
     * Aquí se inicializa el contexto de Spring Boot antes de que se muestre la interfaz gráfica.
     */
    @Override
    public void init(){
        // Se crea y ejecuta la aplicación Spring Boot utilizando el builder
        this.applicationContext = new SpringApplicationBuilder(TareasApplication.class).run();
    }

    /**
     * Método principal que inicia la interfaz gráfica de usuario.
     * Se ejecuta después del método init() y es responsable de configurar y mostrar la ventana principal.
     * 
     * @param stage El contenedor principal (ventana) de la aplicación JavaFX
     * @throws Exception Si ocurre algún error durante la carga de los recursos FXML
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Se crea un cargador FXML que utilizará los recursos de nuestra aplicación
        FXMLLoader loader = new FXMLLoader(TareasApplication.class.getResource("/templates/index.fxml"));
        
        // Se configura el cargador para que obtenga los controladores desde el contexto de Spring
        // Lo que permite la inyección de dependencias en los controladores FXML
        loader.setControllerFactory(applicationContext::getBean);
        
        // Se crea la escena principal cargando el archivo FXML
        Scene escena = new Scene(loader.load());
        
        // Se añade la hoja de estilos CSS a la escena
        escena.getStylesheets().add(getClass().getResource("/templates/styles.css").toExternalForm());
        
        // Se configura el escenario (Stage) con la escena creada
        stage.setScene(escena);
        
        // Se establece el título de la ventana
        stage.setTitle("Sistema de Tareas y Proyectos");
        
        // Se muestra la ventana
        stage.show();
    }
    
    /**
     * Método que se ejecuta cuando la aplicación se cierra.
     * Se encarga de liberar recursos y finalizar correctamente tanto JavaFX como Spring Boot.
     */
    @Override
    public void stop(){
        // Cierra el contexto de Spring para liberar recursos
        applicationContext.close();
        
        // Termina la aplicación JavaFX
        Platform.exit();
    }
}
