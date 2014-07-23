package userGUI;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPasswordField;

import dataTransfer.userOperate;


public class SignUp extends JFrame {

	private JPanel contentPane;
	private JTextField userName;
	private JPasswordField nPassword;
	private JPasswordField cPassword;

	/**
	 * Create the frame.
	 */
	public SignUp() {
		setTitle("Create Account");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(100, 62, 59, 14);
		contentPane.add(lblUsername);

		userName = new JTextField();
		userName.setBounds(177, 59, 86, 20);
		contentPane.add(userName);
		userName.setColumns(10);

		JLabel lblNewPassword = new JLabel("New Password");
		lblNewPassword.setBounds(82, 98, 70, 14);
		contentPane.add(lblNewPassword);

		JLabel lblConfirmPassword = new JLabel("Confirm Password");
		lblConfirmPassword.setBounds(64, 133, 91, 14);
		contentPane.add(lblConfirmPassword);

		JButton btnCreateAccount = new JButton("Create Account");
		btnCreateAccount.setBounds(177, 172, 138, 23);
		contentPane.add(btnCreateAccount);

		btnCreateAccount.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub

				if(userName  == null || userName.getText().isEmpty()){ 
					JOptionPane.showMessageDialog(null,
							"Please enter the username");
				}
				else if (nPassword  == null || nPassword.getText().isEmpty()){
					JOptionPane.showMessageDialog(null,
							"Please enter the new password");
				}
				else if(cPassword == null || cPassword.getText().isEmpty()){
					JOptionPane.showMessageDialog(null,
							"Please enter the confirm password");
				}
				else if(!nPassword.getText().equals(cPassword.getText())){
					JOptionPane.showMessageDialog(null,
							"Please check both passwords are equal");
				}
				else
				{

					userOperate opt = userOperate.getInstance();
					System.out.println(userName.getText() + " " + nPassword.getText());
					try {
						if(opt.signIn(userName.getText(), nPassword.getText())){
							JOptionPane.showMessageDialog(null,
									"You have created your account.");
						}else{
							JOptionPane.showMessageDialog(null,
									"Sorry, your account is not created successfully. Please try again.");
						}
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});


		nPassword = new JPasswordField();
		nPassword.setBounds(177, 95, 86, 20);
		contentPane.add(nPassword);

		cPassword = new JPasswordField();
		cPassword.setBounds(177, 130, 86, 20);
		contentPane.add(cPassword);

		final JButton btnBack = new JButton("Back");
		btnBack.setBounds(82, 172, 70, 23);
		contentPane.add(btnBack);

		btnBack.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Container frame = btnBack.getParent();
				do{
					frame = frame.getParent();
				}while (!(frame instanceof JFrame));
				((JFrame) frame).hide();	
				SignIn signInObj = new SignIn();
				signInObj.setVisible(true);
			}
		});


	}
}