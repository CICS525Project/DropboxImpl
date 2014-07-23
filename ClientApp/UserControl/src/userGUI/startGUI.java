package userGUI;


import dataTransfer.*;
import userMetaData.*;

public class startGUI{
	private fileOptHelper opt = new fileOptHelper();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SignIn frame = new SignIn();
		frame.setVisible(true);
		userOperate.setFolder("/Users/haonanxu/Desktop/download");
		userOperate.setPort(12345);
		userOperate.setHostname("cics525group6S3.cloudapp.net");
		//opt.watchFile(dir);
	}


}
