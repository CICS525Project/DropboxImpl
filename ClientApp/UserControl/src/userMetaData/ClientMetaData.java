package userMetaData;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import dataTransfer.FileOptHelper;
import dataTransfer.UserOperate;
import dataTransfer.SessionInfo;

public class ClientMetaData {
	private FileOptHelper fopt;
	private UserOperate uopt; 
	
	public ClientMetaData(){
		fopt = new FileOptHelper();
	}
	/**
	 * 
	 * @param path
	 */
	public boolean checkXML(String path) {
		try {
			String XMLpath = path + File.separator + "file.xml";
			File f = new File(XMLpath);
			if (!f.exists()) {
				System.out.println("file.xml does not exist.");
				PrintWriter writer;
				writer = new PrintWriter(XMLpath, "UTF-8");
				writer.close();
				return true;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * create xml file for files in current folder filelist conatins all file
	 * names in current folder path is the current folder path
	 * 
	 * @param filelist
	 * @param path
	 * @throws Exception
	 * @throws DOMException
	 */
	public void createXML(ArrayList<String> filelist, String path)
			throws DOMException, Exception {
		try {
			String XMLpath = path + File.separator + "file.xml";
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Files");
			rootElement.setAttribute("owner", SessionInfo.getInstance().getUsername());
			doc.appendChild(rootElement);

			for (String name : filelist) {
				Element file = doc.createElement("File");
				rootElement.appendChild(file);
				Element filename = doc.createElement("Filename");
				filename.setTextContent(name);
				file.appendChild(filename);
				Element checkSum = doc.createElement("CheckSum");
				checkSum.setTextContent(fopt.getHashCode(fopt.hashFile(path
						+ File.separator + name)));
				file.appendChild(checkSum);
				Element version = doc.createElement("Version");
				version.setTextContent("1");
				file.appendChild(version);
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(XMLpath));
			transformer.transform(source, result);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	/**
	 * read xml entries for specific files in filenames, return a hashmap
	 * contains <filename, versionNum> pairs path is current file.xml path
	 * filenames contains all filenames whose xml info will be extracted
	 * 
	 * @param path
	 * @param filenames
	 * @return
	 */
	public HashMap<String, String> readXML(String path,
			ArrayList<String> filenames) {
		HashMap<String, String> res = new HashMap<String, String>();
		try {
			path = path + File.separator + "file.xml";
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document doc = metaBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("File");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename")
							.item(0).getTextContent();
					if (filenames.contains(fname)) {
						String vnum = eElement.getElementsByTagName("Version")
								.item(0).getTextContent();
						res.put(fname, vnum);
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * read the version number for one file
	 * @param filename
	 * @return
	 */
	public String readVersionForOne(String filename){
		ArrayList<String> file = new ArrayList<String>();
		file.add(filename);
		HashMap<String, String> allVersion = readXML(SessionInfo.getInstance().getWorkFolder(), file);
		return allVersion.get(filename);
	}
	
	/**
	 * 
	 * @param fn
	 * @return
	 */
	public int compareLcalandRmtVersion(String fn){
	
		uopt = new UserOperate(SessionInfo
				.getInstance().getRemoteDNS(), SessionInfo
				.getInstance().getPortNum());
		HashMap<String, Integer> remoteFileAndVersion = uopt
				.getServerVersion(SessionInfo.getInstance()
						.getUsername());
		String localVersion = readVersionForOne(fn);
		int lv = Integer.parseInt(localVersion);
		int rv = remoteFileAndVersion.get(fn);
		if(lv == rv){
			return 0;
		}else if(lv > rv){
			return 1;
		}else{
			return 2;
		}
	}
	
	/**
	 * read hashcode in the xml file
	 * 
	 * @param files
	 * @param path
	 * @return
	 */
	public HashMap<String, String> readHashCode(String path) {
		HashMap<String, String> res = new HashMap<String, String>();
		try {
			path = path + File.separator + "file.xml";
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document doc = metaBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("File");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename")
							.item(0).getTextContent();
					String vnum = eElement.getElementsByTagName("CheckSum")
							.item(0).getTextContent();
					res.put(fname, vnum);
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * modify information for a xml entry of a specific file filename is the
	 * specific file's name versionNum is the file's version number path is the
	 * current file.xml path
	 * 
	 * @param filename
	 * @param versionNum
	 * @param path
	 */
	public void modifyInfo(String filename, String checkSum, String versionNum,
			String path) {
		try {
			path = path + File.separator + "file.xml";
			System.out.println("modify file path is:  " + path);
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document doc = metaBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			NodeList nodeList = doc.getElementsByTagName("File");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename")
							.item(0).getTextContent();
					if (fname.equals(filename)) {
						eElement.getElementsByTagName("Version").item(0)
								.setTextContent(versionNum);
						eElement.getElementsByTagName("CheckSum").item(0)
								.setTextContent(checkSum);
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
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
	 * add a xml entry for a new added file filename is the new added file's
	 * name path is the xml file's path
	 * 
	 * @param filename
	 * @param path
	 */
	public void addToXML(String filename, String path) {
		try {
			String XMLpath = path + File.separator + "file.xml";
			File xmlFile = new File(XMLpath);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document document = metaBuilder.parse(xmlFile);

			Node root = document.getElementsByTagName("Files").item(0);
			Element file = document.createElement("File");
			root.appendChild(file);
			Element fn = document.createElement("Filename");
			fn.setTextContent(filename);
			file.appendChild(fn);
			Element checkSum = document.createElement("CheckSum");
			checkSum.setTextContent(fopt.getHashCode(fopt.hashFile(path
					+ File.separator + filename)));
			file.appendChild(checkSum);
			Element version = document.createElement("Version");
			version.setTextContent("1");
			file.appendChild(version);
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(XMLpath));
			transformer.transform(domSource, streamResult);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param path
	 * @param filename
	 */
	public void removeOneRecord(String path, String filename){
		try {
			path = path + File.separator + "file.xml";
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document document = metaBuilder.parse(xmlFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("File");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename")
							.item(0).getTextContent();
					if (fname.contains(filename)) {
						eElement.getParentNode().removeChild(eElement);
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(path));
			transformer.transform(domSource, streamResult);
			System.out.println("xml record deleted.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	/**
	 * remove record in the xml file
	 * @param path
	 * @param fileInfolder
	 */
	public void removeRecord(String path, ArrayList<String> fileInfolder) {
		try {
			path = path + File.separator + "file.xml";
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document document = metaBuilder.parse(xmlFile);
			document.getDocumentElement().normalize();
			NodeList nodeList = document.getElementsByTagName("File");
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node nNode = nodeList.item(i);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String fname = eElement.getElementsByTagName("Filename")
							.item(0).getTextContent();
					if (!fileInfolder.contains(fname)) {
						eElement.getParentNode().removeChild(eElement);
					}
				}
			}
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(path));
			transformer.transform(domSource, streamResult);
			System.out.println("xml record deleted.");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param username
	 * @return
	 */
	public boolean checkXMLOwner(String username, String path){
		path = path + File.separator + "file.xml";
		try {
			File xmlFile = new File(path);
			DocumentBuilderFactory metaFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder metaBuilder = metaFactory.newDocumentBuilder();
			Document doc = metaBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			Element root = doc.getDocumentElement();
			String fileOwner = root.getAttribute("owner");
			if(fileOwner.equals(username)){
				return true;
			}
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
}
