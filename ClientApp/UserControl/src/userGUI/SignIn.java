package userGUI;


import java.awt.Container;
import java.awt.EventQueue;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import org.w3c.dom.DOMException;

import userMetaData.ClientMetaData;

import com.sun.xml.bind.v2.runtime.reflect.opt.OptimizedAccessorFactory;

import dataTransfer.*;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

public class SignIn extends JFrame {

	private JPanel contentPane;
	private JTextField uName;
	private JPasswordField pwd;
	private String username;
	private String password;

	/**
	 * Create the frame.
	 */
	public SignIn() {
		setTitle("Login");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel lblCloudbox = new JLabel("CloudBox");
		lblCloudbox.setBounds(181, 22, 80, 14);
		contentPane.add(lblCloudbox);

		uName = new JTextField();
		uName.setToolTipText("");
		uName.setBounds(181, 91, 123, 20);
		contentPane.add(uName);
		uName.setColumns(10);

		pwd = new JPasswordField();
		pwd.setBounds(181, 130, 123, 20);
		contentPane.add(pwd);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(108, 93, 70, 17);
		contentPane.add(lblUsername);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(108, 133, 60, 14);
		contentPane.add(lblPassword);

		final JButton btnSignin = new JButton("SignIn");
		btnSignin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (uName == null || uName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"Please enter the Username");
				} else if (pwd.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"Please enter the Password");
				} else {
					username = uName.getText();
					password = pwd.getText();
					// user authorization
					UserOperate uopt = new UserOperate("cics525group6S3.cloudapp.net", 12345);
					ClientMetaData cmd = new ClientMetaData();
					FileOptHelper fopt = new FileOptHelper();
					//initilize session data
					sessionInfo.getInstance().setUsername(username);
					sessionInfo.getInstance().setUserPwd(password);
					sessionInfo.getInstance().setRemoteDNS("cics525group6S3.cloudapp.net");
					sessionInfo.getInstance().setPortNum(12345);
					HashMap<String, String> fileDNS = uopt.getFileInFolderAddress();
					String workpath = sessionInfo.getInstance().getWorkFolder();
					sessionInfo.getInstance().setFileLocations(fileDNS);
					//check if work folder already has xml file.
					if(cmd.checkXML(workpath)){
						try {
							//if not, create a initial file
							cmd.createXML(fopt.getFileInFolder(workpath), workpath);
						} catch (DOMException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					try {
						if (uopt.signIn(username, password)) {
							try {
								MySystemTray minimizeAppobj = new MySystemTray();
								//start thread for folder watcher
								new folderWatcher(sessionInfo.getInstance().getWorkFolder());
								//initializae download queueu
								fopt.initialDownloadQueue();
								System.out.println("download queue size is: " + OperationQueue.getInstance().getDownloadQueue().size());
								/******** start download thread********/
								new DownloadFile();
								/******** create initial upload queue********/
								fopt.initilizeUploadQueue();
								System.out.println("Upload queue size is: " + OperationQueue.getInstance().getUploadQueue().size());
								/******** start upload thread********/
								new UploadFile();
								
								/**close login window **/
								Container frame = btnSignin.getParent();
								do{
									frame = frame.getParent();
								}while (!(frame instanceof JFrame));
								((JFrame) frame).hide();
								/////
								HashMap<String, Integer>  res = uopt.getServerVersion(username);
								System.out.println(res.size());
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						} else {
							JOptionPane.showMessageDialog(null,
									"UserName and Password Not Found");
						}
					} catch (HeadlessException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		btnSignin.setBounds(181, 173, 92, 23);
		contentPane.add(btnSignin);

		JButton btnSignup = new JButton("SignUp");
		btnSignup.setBounds(283, 173, 92, 23);
		contentPane.add(btnSignup);

		btnSignup.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							Container frame = btnSignin.getParent();
							do{
								frame = frame.getParent();
							}while (!(frame instanceof JFrame));
							((JFrame) frame).hide();
							SignUp signUpFrame = new SignUp();
							signUpFrame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}
}
