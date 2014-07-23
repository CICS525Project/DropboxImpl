package userGUI;


import dataTransfer.*;
import userMetaData.*;

public class StartGUI{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SignIn frame = new SignIn();
		frame.setVisible(true);
		UserOperate.setFolder("/Users/haonanxu/Desktop/download");
		UserOperate.setPort(12345);
		UserOperate.setHostname("cics525group6S3.cloudapp.net");
	}


}
