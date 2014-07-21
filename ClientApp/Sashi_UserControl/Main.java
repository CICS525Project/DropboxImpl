package UserControl;

import java.io.IOException;
import java.net.MalformedURLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
		Scene scene = new Scene(root);
		GUICtl.getStage().setScene(scene);
		GUICtl.getStage().show();
		//UpAndDownLoad.linkToServer("cics525group6.cloudapp.net", 12345);
	}

	public static void main(String[] args) {
		launch(args);
//		try {
//			//minimizeApp mapp = new minimizeApp();
//		} catch (MalformedURLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
