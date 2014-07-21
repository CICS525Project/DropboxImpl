package UserControl;

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

import java.awt.PopupMenu;

public class minimizeApp {
	ToolTip myTip;
	
	public minimizeApp() throws MalformedURLException {
		// TODO Auto-generated constructor stub
		myTip = new ToolTip();
		if(SystemTray.isSupported()){
			//the image should be put in the bin folder of the application
			URL url = this.getClass().getResource("/b.png");
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
					myTip.setToolTip(new ImageIcon("/Users/haonanxu/Documents/workspace/UserControl/bin/warn.png"),"log out clicked");
				}
			});
			popupMenu.add(exit);
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
	
	public static void main(String[] args) throws MalformedURLException {
		// TODO Auto-generated method stub
		minimizeApp mapp = new minimizeApp();
	}
}