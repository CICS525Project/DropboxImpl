package userGUI;


import dataTransfer.*;
import userMetaData.*;

public class StartGUI{

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SignIn frame = new SignIn();
		frame.setVisible(true);
		sessionInfo.getInstance().setWorkFolder("/Users/haonanxu/Desktop/download");
	}


}
