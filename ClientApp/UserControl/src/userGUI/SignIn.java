package UserControl;

import java.awt.Container;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;

public class SignIn extends JFrame {

	private JPanel contentPane;
	private JTextField uName;
	private JPasswordField pwd;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SignIn frame = new SignIn();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public SignIn() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel lblCloudbox = new JLabel("CloudBox");
		lblCloudbox.setBounds(181, 22, 46, 14);
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
		lblUsername.setBounds(108, 93, 55, 17);
		contentPane.add(lblUsername);

		JLabel lblPassword = new JLabel("Password");
		lblPassword.setBounds(108, 133, 46, 14);
		contentPane.add(lblPassword);

		final JButton btnSignin = new JButton("SignIn");
		btnSignin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (uName == null || uName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"Please enter the Username");
				} else if (pwd == null) {
					JOptionPane.showMessageDialog(null,
							"Please enter the Password");
				} else {
					String name = uName.getText();
					String passw = pwd.getText();
					if (true) {
						try {
							minimizeApp minimizeAppobj = new minimizeApp();
							Container frame = btnSignin.getParent();
							do
								frame = frame.getParent();
							while (!(frame instanceof JFrame));
							((JFrame) frame).hide();
							;

						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else
						JOptionPane.showMessageDialog(null,
								"UserName and Password Not Found");

				}

			}
		});
		btnSignin.setBounds(181, 173, 92, 23);
		contentPane.add(btnSignin);

		JButton btnSignup = new JButton("SignUp");
		btnSignup.setBounds(283, 173, 92, 23);
		contentPane.add(btnSignup);
	}
}
