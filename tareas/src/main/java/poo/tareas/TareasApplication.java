package poo.tareas;

import javafx.application.Application;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import poo.tareas.presentacion.SistemasTareasFx;

@SpringBootApplication
public class TareasApplication {

	public static void main(String[] args) {
		Application.launch(SistemasTareasFx.class, args);
	}

}
