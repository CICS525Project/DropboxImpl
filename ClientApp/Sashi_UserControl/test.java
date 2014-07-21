package UserControl;

import java.io.File;

public class test {
	public void fileLst(){
		File folder = new File("C:/Users/Sashiraj/Desktop/Upload");
		File[] listoffiles = folder.listFiles();
		for(int i=0;i<listoffiles.length;i++){
			System.out.println(listoffiles[i]);
		}
	}

//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		
//		test test=new test();
//		test.fileLst();
//		
//
//	}

}
