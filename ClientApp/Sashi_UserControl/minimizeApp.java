package UserControl;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
			URL url = this.getClass().getResource("b.png");
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
			popupMenu.add(exit);
			exit.setLabel("Logout");
			MenuItem share =  new MenuItem("Share");
			//exit =  new MenuItem("Share");
			popupMenu.add(share);
			
			exit.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					//log out application
					
					System.out.println("log out clicked");
					myTip.setToolTip(new ImageIcon("E:\\525 Project\\DropBox WorkSpace\\525 login\\bin\\warn.png"),"log out clicked");
				}
			});
			
			share.addActionListener(new ActionListener() {
				
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					try {

						Parent root = FXMLLoader.load(getClass().getResource("FileShare.fxml"));
						Scene scene = new Scene(root);
						GUICtl.mainStage.setScene(scene);
						GUICtl.mainStage.show();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			//popupMenu.add(exit);
			//popupMenu.add(share);
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
