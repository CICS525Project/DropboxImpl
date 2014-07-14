package UserControl;

import java.io.IOException;
import java.net.Socket;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Login {
	@FXML
	private TextField usrname;
	@FXML
	private PasswordField pwd;
	@FXML
	private Button signin;
	@FXML
	private Button signup;
	@FXML
	private Button choose;
	@FXML
	private Button upload;
	@FXML
	private Stage PrevStage;
	@FXML
	private Label msg;

	private Socket curClient;

	public void signin(ActionEvent event) throws IOException {

		if (usrname == null || usrname.getText().isEmpty()) {
			msg.setText("Please enter the username");
		} else if (pwd == null || pwd.getText().isEmpty()) {
			msg.setText("Please enter the password");
		} else {
			String name = usrname.getText();
			String passw = pwd.getText();
			System.out.println("the user name and pwd are : " + name + " "
					+ passw);
			if (UpAndDownLoad.signIn(name, passw)) {
				Parent root = FXMLLoader.load(getClass().getResource(
						"upload.fxml"));
				Scene scene = new Scene(root);
				Stage MainStage = new Stage();
				MainStage.setScene(scene);
				MainStage.show();
			} else {
				msg.setText("username and password not found");
			}

		}

	}

	public void signup(ActionEvent event) throws IOException {

		Parent root = FXMLLoader.load(getClass()
				.getResource("userDetails.fxml"));
		Scene scene = new Scene(root);
		GUICtl.mainStage.setScene(scene);
		GUICtl.mainStage.show();
	}
	public void SetStage(Stage Stage) {
		this.PrevStage = Stage;
	}
}
