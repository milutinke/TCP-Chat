package raf.edu.rs;
	
import javafx.application.Application;
import javafx.stage.Stage;
import raf.edu.rs.controller.Controller;

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Controller.getInstance().connect("localhost", 9777);
			Controller.getInstance().showWindow();
			Controller.getInstance().receiveWelcomeMessage();
			Controller.getInstance().listen();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
