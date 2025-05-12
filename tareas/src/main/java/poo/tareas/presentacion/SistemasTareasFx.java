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

public class SistemasTareasFx extends Application {
    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init(){
        this.applicationContext = new SpringApplicationBuilder(TareasApplication.class).run();
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(TareasApplication.class.getResource("/templates/index.fxml"));
        loader.setControllerFactory(applicationContext::getBean);
        Scene escena = new Scene(loader.load());
        escena.getStylesheets().add(getClass().getResource("/templates/styles.css").toExternalForm());
        stage.setScene(escena);
        stage.setTitle("Sistema de Tareas y Proyectos");
        stage.show();
    }
    @Override
    public void stop(){
        applicationContext.close();
        Platform.exit();
    }
}
