package it.unina.uninaswap.view;

import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AnnunciMenuPanel extends JPanel{

	private JButton homeButton;
    private JButton profileButton;
    private JButton reportButton;
    private JButton notificationButton;
    private JButton addButton;
    private JButton settingsButton;
    private JButton logoutButton;
    
    public AnnunciMenuPanel() {
    	setPreferredSize(new Dimension(150, 0));
    	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    	
        JLabel menuLabel = new JLabel("Menu");
        add(menuLabel);
        
        setVisible(false);
        
        // Bottone Home
 		homeButton = new JButton();
 		ImageIcon rawIcon = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/home-button.jpg");
 		Image homeIcon = rawIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledHomeIcon = new ImageIcon(homeIcon);
 		homeButton.setIcon(scaledHomeIcon);
 		add(homeButton);
        
        // Bottone Profilo
 		profileButton = new JButton();
 		ImageIcon rawIcon1 = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileM.jpg");
 		Image profileIcon = rawIcon1.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledProfileIcon = new ImageIcon(profileIcon);
 		profileButton.setIcon(scaledProfileIcon);
 		add(profileButton);
 		
 		// Bottone Report
 		reportButton = new JButton();
 		ImageIcon rawIcon2 = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/report.jpg");
 		Image reportIcon = rawIcon2.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledReportIcon = new ImageIcon(reportIcon);
 		reportButton.setIcon(scaledReportIcon);
 		add(reportButton);

 		// Bottone Notifiche
 		notificationButton = new JButton();
 		ImageIcon rawIcon3 = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/notification.jpg");
 		Image notificationIcon = rawIcon3.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledNotificationIcon = new ImageIcon(notificationIcon);
 		notificationButton.setIcon(scaledNotificationIcon);		
 		// TODO: ImageIcon new notifica
 		add(notificationButton);
 		
 		// Bottone Pubblica annuncio
 		addButton = new JButton();
 		ImageIcon rawIcon4 = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/add.jpg");
 		Image addIcon = rawIcon4.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledAddIcon = new ImageIcon(addIcon);
 		addButton.setIcon(scaledAddIcon);
 		add(addButton);
 		
 		// Bottone Impostazioni
 		settingsButton = new JButton();
 		ImageIcon rawIcon5 = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileSettings.jpg");
 		Image settingsIcon = rawIcon5.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledSettingsIcon = new ImageIcon(settingsIcon);
 		settingsButton.setIcon(scaledSettingsIcon);
 		add(settingsButton);
 		
 		// Bottone Logout
 		logoutButton = new JButton();
 		ImageIcon rawIcon6 = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/logout.jpg");
 		Image logoutIcon = rawIcon6.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
 		ImageIcon scaledLogoutIcon = new ImageIcon(logoutIcon);
 		logoutButton.setIcon(scaledLogoutIcon);
 		add(logoutButton);
    	
 		
    }
    
    // Getter per il controller
    public JButton getHomeButton() {
        return homeButton;
    }
    
    public JButton getProfileButton() {
        return profileButton;
    }

    public JButton getReportButton() {
        return reportButton;
    }

    public JButton getNotificationButton() {
        return notificationButton;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getSettingsButton() {
        return settingsButton;
    }

    public JButton getLogoutButton() {
        return logoutButton;
    }
    
    
    
	
}
