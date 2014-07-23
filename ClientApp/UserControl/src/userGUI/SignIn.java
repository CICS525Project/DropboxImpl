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
					UserOperate opt = UserOperate.getInstance();
					try {
						if (opt.signIn(username, password)) {
							try {
								MySystemTray minimizeAppobj = new MySystemTray();
								Container frame = btnSignin.getParent();
								do{
									frame = frame.getParent();
								}while (!(frame instanceof JFrame));
								((JFrame) frame).hide();
								/////
								HashMap<String, Integer>  res = opt.getServerVersion(username);
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
