package userGUI;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;

public class Share extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public Share() {
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setLayout(null);
		setContentPane(contentPane);

		JLabel lblShareAFile = new JLabel("Share a File");
		lblShareAFile.setBounds(189, 28, 82, 14);
		contentPane.add(lblShareAFile);

		ArrayList<String> flist = getFileName();
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(201, 110, 61, 14);
		contentPane.add(lblUsername);

		textField = new JTextField();
		textField.setBounds(272, 107, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);

		JButton btnShare = new JButton("Share");
		btnShare.setBounds(269, 148, 89, 23);
		contentPane.add(btnShare);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 56, 155, 177);
		contentPane.add(scrollPane);
		JList list = new JList(flist.toArray());
		scrollPane.setViewportView(list);
		scrollPane.setViewportView(list);

	}

	private ArrayList<String> getFileName() {
		File folder = new File("/Users/haonanxu/Desktop/download");
		File[] listoffiles = folder.listFiles();
		ArrayList<String> finalFilelist = new ArrayList<String>();
		System.out.println(listoffiles);
		for (int i = 0; i < listoffiles.length; i++) {
			System.out.println(listoffiles[i]);
			File file = new File(listoffiles[i].toString());
			if (file.getName().startsWith(".")) {
				continue;
			} else {
				finalFilelist.add(file.getName());
			}
		}
		return finalFilelist;
	}
}
