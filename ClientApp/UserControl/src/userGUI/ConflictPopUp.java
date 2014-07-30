package userGUI;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.*;

import dataTransfer.DownloadFile;
import dataTransfer.OperationQueue;

public class ConflictPopUp extends JFrame {
	private JPanel contentPane;
	private JButton upDate;
	private JButton downLoad;
	private JButton cancel;
	private JLabel warnTx;
	private JLabel titleJLabel;
	private String warnMsg;

	public ConflictPopUp(String warn, int i, String fn) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 100);
		contentPane = new JPanel();
		setContentPane(contentPane);
		getContentPane().setLayout(null);
		this.warnMsg = warn;
		setupGUI(i, fn);
	}

	private void setupGUI(int i, String fn) {

		final int choice = i;
		final String filen = fn;
		upDate = new JButton();
		upDate.setLocation(29, 237);
		upDate.setSize(80, 40);
		upDate.setText("UpDate");
		//when client click upload
		upDate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//user wants to upload conflict with existing dowload
				//user choose upload
				if(choice == 1){
					//discard download
					OperationQueue.getInstance().removeDownAddUp(filen);
				}
				//user wants to upload conflict with existing upload
				//user choose download
				if(choice == 3){
					//discard upload 
					OperationQueue.getInstance().remiveUploadAddUpload(filen);
				}
				for (Frame frame : Frame.getFrames()) {
					if (frame.isActive()) {
						WindowEvent windowClosing = new WindowEvent(frame,
								WindowEvent.WINDOW_CLOSING);
						frame.dispatchEvent(windowClosing);
					}
				}
			}
		});
		

		downLoad = new JButton();
		downLoad.setLocation(138, 237);
		downLoad.setSize(80, 40);
		downLoad.setText("Download");
		downLoad.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//user wants to download conflict with existing upload
				//user chooses dowload
				if(choice == 1){
					//discard upload
					OperationQueue.getInstance().removeUpAddDown(filen);
				}
				//user wants to download conflict with existing download
				//user choose download
				if(choice == 2){
					OperationQueue.getInstance().removeDownAddDown(filen);
				}
				DownloadFile downloader = new DownloadFile();
				downloader.start();
				for (Frame frame : Frame.getFrames()) {
					if (frame.isActive()) {
						WindowEvent windowClosing = new WindowEvent(frame,
								WindowEvent.WINDOW_CLOSING);
						frame.dispatchEvent(windowClosing);
					}
				}
			}
		});
		
		cancel = new JButton();
		cancel.setLocation(253, 237);
		cancel.setSize(80, 40);
		cancel.setText("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				for (Frame frame : Frame.getFrames()) {
					if (frame.isActive()) {
						WindowEvent windowClosing = new WindowEvent(frame,
								WindowEvent.WINDOW_CLOSING);
						frame.dispatchEvent(windowClosing);
					}
				}
			}
		});
		
		//if download conflict with an existing upload action
		if (i == 1) {
			contentPane.add(upDate);
			contentPane.add(downLoad);
			contentPane.add(cancel);
		}
		//if download conflict with an existing download action
		if(i == 2){
			warnMsg = "The file is already in the download task queue.";
			cancel.setText("OK");
//			downLoad.setLocation(50, 237);
			cancel.setLocation(150, 237);
//			contentPane.add(downLoad);
			contentPane.add(cancel);
		}
		//if upload conflict with a existing upload action
		if(i == 3){
			warnMsg = "the file is already in the upload task queue.";
			cancel.setText("OK");
//			upDate.setLocation(50, 237);
			cancel.setLocation(150, 237);
//			contentPane.add(upDate);
			contentPane.add(cancel);
		}
		warnTx = new JLabel();
		warnTx.setLocation(42, 124);
		warnTx.setSize(300, 50);
		warnTx.setText(warnMsg);
		warnTx.setFont(new Font("STKaiti", Font.PLAIN, 24));
		contentPane.add(warnTx);

		titleJLabel = new JLabel();
		titleJLabel.setLocation(150, 25);
		titleJLabel.setSize(150, 50);
		titleJLabel.setText("Warning");
		titleJLabel.setFont(new Font("Impact", Font.PLAIN, 24));
		contentPane.add(titleJLabel);

		setTitle("WARNING");
		setSize(400, 400);
		setForeground(new Color(-16777216));
		setVisible(true);
		setResizable(true);

	}
	public static void main(String args[]) {
		new ConflictPopUp("Test",3, "1");
	}
}
