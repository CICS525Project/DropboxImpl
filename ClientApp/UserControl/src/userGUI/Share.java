package userGUI;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;

import dataTransfer.ConfigurationData;
import dataTransfer.UserOperate;
import dataTransfer.SessionInfo;

public class Share extends JFrame {

	private JPanel contentPane;
	private JTextField userName;
	private ToolTip myTip;
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
		lblShareAFile.setBounds(189, 16, 82, 14);
		contentPane.add(lblShareAFile);

		ArrayList<String> flist = getFileName();
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(203, 110, 68, 14);
		contentPane.add(lblUsername);

		userName = new JTextField();
		userName.setBounds(272, 107, 86, 20);
		contentPane.add(userName);
		userName.setColumns(10);

		final JButton btnShare = new JButton("Share");
		btnShare.setBounds(203, 184, 89, 23);
		contentPane.add(btnShare);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 56, 155, 177);
		contentPane.add(scrollPane);
		final JList list = new JList(flist.toArray());
		scrollPane.setViewportView(list);
		scrollPane.setViewportView(list);

		final JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				/** close itself and nothing happened **/
				Container frame = cancel.getParent();
				do {
					frame = frame.getParent();
				} while (!(frame instanceof JFrame));
				((JFrame) frame).hide();
			}
		});
		cancel.setBounds(322, 184, 89, 23);
		contentPane.add(cancel);

		btnShare.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if (userName == null || userName.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null,
							"Please enter the username to share the file");
				} else if (list.getSelectedValues() == null
						|| list.getSelectionModel().isSelectionEmpty()) {
					JOptionPane.showMessageDialog(null,
							"Please Select the file to be shared");
				} else {
					UserOperate uopt = new UserOperate(SessionInfo
							.getInstance().getRemoteDNS(), SessionInfo
							.getInstance().getPortNum());
					list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
					Object[] selected = list.getSelectedValues();
					ArrayList<String> sel = new ArrayList<String>();
					String[] selectedItems = new String[selected.length];
					for (int i = 0; i < selected.length; i++) {
						selectedItems[i] = selected[i].toString();
					}
					String name = userName.getText();
					uopt.shareFile(selectedItems, name);
					myTip = new ToolTip();
					myTip.setToolTip(new ImageIcon(ConfigurationData.WARN_IMG), "File is shared successfully!");
					Container frame = btnShare.getParent();
					do {
						frame = frame.getParent();
					} while (!(frame instanceof JFrame));
					((JFrame) frame).hide();
					//pop up here
				}
			}
		});

	}

	private ArrayList<String> getFileName() {
		File folder = new File(SessionInfo.getInstance().getWorkFolder());
		File[] listoffiles = folder.listFiles();
		ArrayList<String> finalFilelist = new ArrayList<String>();
		System.out.println(listoffiles);
		for (int i = 0; i < listoffiles.length; i++) {
			System.out.println(listoffiles[i]);
			File file = new File(listoffiles[i].toString());
			if (file.getName().startsWith(".")) {
				continue;
			} else if(file.getName().equals("file.xml")){
				continue;
			}else{
				finalFilelist.add(file.getName());
			}
		}
		return finalFilelist;
	}
}