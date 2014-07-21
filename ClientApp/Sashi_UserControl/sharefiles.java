package UserControl;

import java.awt.Button;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import javax.swing.ImageIcon;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class sharefiles implements Initializable {

	@FXML
	private TextField uname;

	@FXML
	private Button share;

	@FXML
	private Label msg;

	@FXML
	private ListView<String> fileslist;

	public String mypath;
	private static DataInputStream streamReader;
	private static DataOutputStream streamWriter;

	public static boolean signIn(String uname, String upass) throws IOException {
		// linkToServer("cics525group6.cloudapp.net", 12345);
		String msg = "auth," + uname + "," + upass;
		streamWriter.writeUTF(msg);
		String res = streamReader.readUTF();
		if (res.equals("true")) {
			return true;
		}
		return true;
	}

	public static boolean sharefile(String uname) throws IOException {
		// linkToServer("cics525group6.cloudapp.net", 12345);
		String msg = "auth," + uname;
		streamWriter.writeUTF(msg);
		String res = streamReader.readUTF();
		if (res.equals("true")) {
			return true;
		}
		return false;
	}

	public void fileslist() {
		//String pattern = Pattern.quote(System.getProperty("file.separator"));
		File folder = new File("C:/Users/Sashiraj/Desktop/Upload");
		File[] listoffiles = folder.listFiles();
		ArrayList<String> finalFilelist=new ArrayList<String>();
		System.out.println(listoffiles);
		for(int i=0;i<listoffiles.length;i++){
			System.out.println(listoffiles[i]);
			File file = new File(listoffiles[i].toString());
			finalFilelist.add(file.getName());
		}
		ObservableList obList = FXCollections.observableArrayList(finalFilelist);
		fileslist.setItems(obList);
		fileslist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	public void share(ActionEvent event) throws MalformedURLException {
		if (uname == null || uname.getText().isEmpty()) {
			msg.setText("Please enter the username to share the file");
		} else if (fileslist.getSelectionModel().getSelectedItem() == null
				|| fileslist.getSelectionModel().isEmpty()) {
			msg.setText("Please Select the file to be shared");
		} else {


			ListView<String> listview = new ListView<String>();
			ArrayList<String> sel = new ArrayList<String>(fileslist.getSelectionModel().getSelectedItems());
			for(String s:sel){
				System.out.println(s);
			}
			String name = uname.getText();
			System.out.println(name);
           // minimizeApp minimizeAppobj = new minimizeApp();
            
           
		}

	}

	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		fileslist();
	}

}
