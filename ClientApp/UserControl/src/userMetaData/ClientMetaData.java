package userMetaData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import dataTransfer.UserOperate;

public class ClientMetaData {

	/**
	 * create xml file for files in current folder
	 * filelist conatins all file names in current folder
	 * path is the current folder path
	 * @param filelist
	 * @param path
	 */
	public static void createXML(ArrayList<String> filelist, String path)
	{
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Files");
			doc.appendChild(rootElement);
			
			for (String name : filelist) {
				Element file = doc.createElement("File");
				rootElement.appendChild(file);
				Element filename = doc.createElement("Filename");
				filename.setTextContent(name);
				file.appendChild(filename);
				Element version = doc.createElement("Version");
				version.setTextContent("1");
				file.appendChild(version);
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path+"/file.xml"));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (TransformerException tfe) {
			tfe.printStackTrace();
		  }
	}
	
	/**
	 * read xml entries for specific files in filenames, return a hashmap contains <filename, versionNum> pairs
	 * path is current file.xml path
	 * filenames contains all filenames whose xml info will be extracted
	 * @param path
	 * @param filenames
	 * @return
	 */
	public static HashMap<String, String> readXML(String path, ArrayList<String> filenames){
		HashMap<String, String> res = new HashMap<String, String>();
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document doc = metaBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			System.out.println("Root element: " + doc.getDocumentElement().getNodeName());
			
			NodeList nodeList = doc.getElementsByTagName("File");
			System.out.println("------------------");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				System.out.println("Current Element : " + nNode.getNodeName());
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename").item(0).getTextContent();
					if(filenames.contains(fname)){
						String vnum = eElement.getElementsByTagName("Version").item(0).getTextContent();
						res.put(fname, vnum);
					}
					System.out.println("Filename :" + eElement.getElementsByTagName("Filename").item(0).getTextContent());
					System.out.println("Version # :" + eElement.getElementsByTagName("Version").item(0).getTextContent());
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * modify information for a xml entry of a specific file
	 * filename is the specific file's name
	 * versionNum is the file's version number
	 * path is the current file.xml path
	 * @param filename
	 * @param versionNum
	 * @param path
	 */
	public static void modifyInfo(String filename, String versionNum, String path)
	{
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document doc = metaBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("File");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				System.out.println("Current Element : " + nNode.getNodeName());
				if(nNode.getNodeType() == Node.ELEMENT_NODE){
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename").item(0).getTextContent();
					if(fname.equals(filename)){
						eElement.getElementsByTagName("Version").item(0).setTextContent(versionNum);
						System.out.println("Change version number for: " + filename + " to " + versionNum);
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File(path));
			transformer.transform(domSource, streamResult);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * add a xml entry for a new added file
	 * filename is the new added file's name
	 * path is the xml file's path
	 * @param filename
	 * @param path
	 */
	public static void addToXML(String filename, String path){
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document document = metaBuilder.parse(xmlFile);
			
			//document.getDocumentElement().normalize();
			Node root = document.getElementsByTagName("Files").item(0);
			Element file = document.createElement("File");
			root.appendChild(file);
			Element fn = document.createElement("Filename");
			fn.setTextContent(filename);
			file.appendChild(fn);
			Element version = document.createElement("Version");
			version.setTextContent("1");
			file.appendChild(version); 
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(path));
			transformer.transform(domSource, streamResult);
			System.out.println("File added.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		ArrayList<String> reStrings = fileOperate.getFileInFolder("/Users/haonanxu/Desktop/download");
//		createXML(reStrings, "/Users/haonanxu/Desktop/download");
//		//modifyInfo("test.html","1","/Users/haonanxu/Desktop/download/file.xml");
//		//addToXML("tst.txt", "/Users/haonanxu/Desktop/download/file.xml");
//		//reStrings.add("tst.txt");
//		readXML("/Users/haonanxu/Desktop/download/file.xml", reStrings);
//	}

}
