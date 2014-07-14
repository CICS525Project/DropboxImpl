package UserControl;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;



public class userDetails {
	
	@FXML 
	private Button createACC;
	@FXML
	private TextField uname;
	@FXML
	private PasswordField npwd;
	@FXML
	private PasswordField cpwd;
	@FXML
	private Label msg;
	
	@FXML
	public void createaccount(ActionEvent event) throws IOException{
		if(uname  == null || uname.getText().isEmpty()){ 
			msg.setText("Please enter the username");
		}
		else if (npwd  == null || npwd.getText().isEmpty()){
			msg.setText("Please enter the new password");
		}
		else if(cpwd == null || cpwd.getText().isEmpty()){
			msg.setText("Please enter the confirm password");
		}
		else if(!npwd.getText().equals(cpwd.getText())){
			msg.setText("Please enter both passwords equal values");
		}
		else{
			if(UpAndDownLoad.signUp(uname.getText(), npwd.getText())){
				msg.setText("Account created successfully");
			}else{
				msg.setText("Account creation failed");
			}
		}
	}
	
}
