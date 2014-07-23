package userGUI;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import dataTransfer.*;
import userMetaData.*;

public class startGUI{
	private fileOptHelper opt = new fileOptHelper();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SignIn frame = new SignIn();
		frame.setVisible(true);
		Path dir = Paths.get("/Users/haonanxu/Desktop/download");
		//opt.watchFile(dir);
	}


}
