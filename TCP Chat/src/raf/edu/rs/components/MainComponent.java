package raf.edu.rs.components;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import raf.edu.rs.controller.Controller;

public class MainComponent {
	private static TextArea textArea = new TextArea();

	public static void show() {
		Stage stage = new Stage();
		
		VBox vBox = new VBox();
		vBox.setPadding(new Insets(5));

		Label label = new Label();
		label.setText("Poruke: ");
		vBox.getChildren().add(label);

		textArea.setPrefSize(500, 256);
		textArea.setWrapText(true);

		vBox.getChildren().add(textArea);

		Label label2 = new Label();
		label2.setText("Unesi poruku: ");
		vBox.getChildren().add(label2);

		TextField textField = new TextField();
		textField.setPromptText("Unesi poruku");

		textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER) {
					if (textField.getText().length() <= 0)
						return;

					Controller.getInstance().send(textField.getText());
					
					if(textField.getText().equalsIgnoreCase("disconnect") || textField.getText().equalsIgnoreCase("exit") || textField.getText().equalsIgnoreCase("quit")) {
						Controller.getInstance().setClosed(true);
						stage.close();
						return;
					}
					
					textField.clear();
				}
			}
		});

		vBox.getChildren().add(textField);

		Button button = new Button("Posalji");
		button.setPrefWidth(500);

		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (textField.getText().length() <= 0)
					return;

				Controller.getInstance().send(textField.getText());
				
				if(textField.getText().equalsIgnoreCase("disconnect") || textField.getText().equalsIgnoreCase("exit") || textField.getText().equalsIgnoreCase("quit")) {
					Controller.getInstance().setClosed(true);
					stage.close();
					return;
				}
				
				textField.clear();
			}
		});

		vBox.getChildren().add(button);

		Scene scene = new Scene(vBox, 500, 300);

		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("TCP Pricaonica");
		stage.show();
		
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
				Controller.getInstance().send("disconnect");
				Controller.getInstance().setClosed(true);
				Controller.getInstance().close();
			}
		});
	}

	public static TextArea getTextArea() {
		return textArea;
	}
}
