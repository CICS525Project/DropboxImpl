package UserControl;

import java.awt.Button;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class Filechoose {
	
	private String mypath;
	
	@FXML
	private TextField path;
	
	@FXML
	private TextField dpath;
	
	@FXML
	private ComboBox glist;
	

	@FXML
	public void choose() {
		Stage newstage = new Stage();
		FileChooser chooser = new FileChooser();
		File temp = chooser.showOpenDialog(newstage);
		if (temp != null) {
			mypath = temp.getAbsolutePath();
			path.setText(mypath);
		}
	}
	
	@FXML
	public void select() {
		Stage newstage = new Stage();
		DirectoryChooser dirChoose = new DirectoryChooser();
		File temp = dirChoose.showDialog(newstage);
		if (temp != null) {
			mypath = temp.getAbsolutePath();
			dpath.setText(mypath);
		}
	}
	
	@FXML
	public void listname() throws IOException{
		ArrayList<String> getlist;
		System.out.println("function called");
		getlist = UpAndDownLoad.getRemoteList();
		ObservableList obList = FXCollections.observableList(getlist);
		glist.setItems(obList);
	}
	
	@FXML
	public void upload() throws IOException {
		
		UpAndDownLoad.upoLoadFile(mypath);
	}
	
	@FXML
	public void download() throws IOException{
		String pname = (String) glist.getValue();
		System.out.println("selected " + pname);
		String path = dpath.getText();
		UpAndDownLoad.downLoadFile(path, pname);
	}
	@FXML
	public void logout() throws IOException{
		UpAndDownLoad.closeConnection();
		Parent root = FXMLLoader.load(getClass().getResource("loginpage.fxml"));
		Scene scene = new Scene(root);
		GUICtl.getStage().setScene(scene);
		GUICtl.getStage().show();
		//close window here and back to the main window
	}
}
