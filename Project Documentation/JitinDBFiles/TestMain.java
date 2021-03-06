/**
 * 
 */
package com.cloudbox.db;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author Jitin
 *
 */
public class TestMain {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		try{
			HashMap<String, Integer> fileList=new HashMap<String, Integer>();
			String userName="jitin";
			String serverName="cics525group6S1";
			fileList.put("sample1.txt", 1);
			fileList.put("sample2.txt", 1);
			fileList.put("sample3.txt", 1);
			fileList.put("sample4.txt", 1);
			fileList.put("sample5.txt", 1);
			DBConnection conn=new DBConnection();
			String s=conn.insertRecordforUpload(fileList, userName, serverName);
			System.out.println("Result is "+s);
			HashMap<String, Integer> fileupdateList=new HashMap<String, Integer>();
			fileupdateList.put("sample1.txt", 2);
			fileupdateList.put("sample2.txt", 4);
			fileupdateList.put("sample5.txt", 3);
			String s1=conn.updateVersionForFile(fileupdateList, userName, serverName);
			System.out.println("Result is "+s1);
			HashMap<String, String> fileShareList=new HashMap<String, String>();
			fileShareList.put("sample1.txt", "harry");
			fileShareList.put("sample2.txt", "ignacio");
			fileShareList.put("sample5.txt", "sashi");
			String s2=conn.insertRecordforShare(fileShareList, userName);
			System.out.println("Result is "+s2);
			ArrayList<String> fileSearch=new ArrayList<String>();
			fileSearch.add("sample1.txt");
			fileSearch.add("sample6.txt");
			HashMap<String, String> result=new HashMap<String, String>();
			result=conn.searchForServerName(fileSearch, userName);
			Collection c = result.values();
			Iterator itr = c.iterator();
			while(itr.hasNext()){
			      System.out.println(itr.next());
			  }
		}
		catch (SQLException se){
			se.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
