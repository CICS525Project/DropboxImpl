package userGUI;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;

import com.sun.glass.events.MouseEvent;

import dataTransfer.ConfigurationData;

import java.awt.PopupMenu;

public class MySystemTray {
	ToolTip myTip;
	
	public MySystemTray() throws MalformedURLException {
		// TODO Auto-generated constructor stub
		myTip = new ToolTip();
		if(SystemTray.isSupported()){
			//the image should be put in the bin folder of the application
			URL url = this.getClass().getResource(ConfigurationData.BOX_IMG);
			ImageIcon icon = new ImageIcon(url);
			Image image = icon.getImage();
			TrayIcon trayIcon = new TrayIcon(image);
			//add mouse action listener on the icon
			trayIcon.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e){
					if(e.BUTTON_LEFT == 1){
						//display folder
						System.out.println("left mouse button clicked.");
					}
				}
			});
			trayIcon.setToolTip("System tray");
			PopupMenu popupMenu = new PopupMenu();
			MenuItem exit =  new MenuItem();
			exit.setLabel("Logout");
			exit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//log out application
					System.out.println("log out clicked");
					//myTip.setToolTip(new ImageIcon("img/warn.png"),"log out clicked");
					System.exit(0);
				}
			});
			popupMenu.add(exit);
			MenuItem shareItem = new MenuItem();
			shareItem.setLabel("Share");
			shareItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					System.out.println("share item shared.");
					//user interface pop up here.
					Share shareGUI = new Share();
					shareGUI.setVisible(true);
				}
			});
			popupMenu.add(shareItem);
			trayIcon.setPopupMenu(popupMenu);
			SystemTray systemTray = SystemTray.getSystemTray();
			try {
				systemTray.add(trayIcon);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}	
}
