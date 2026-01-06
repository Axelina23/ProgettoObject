package it.unina.uninaswap.app;

import javax.swing.SwingUtilities;
import it.unina.uninaswap.view.LoginView;
import it.unina.uninaswap.controller.LoginController;
import it.unina.uninaswap.dao.impl.StudenteDAOImpl;

import com.formdev.flatlaf.FlatLightLaf;


public class App {
    public static void main(String[] args) {
    	FlatLightLaf.setup();
    	
        SwingUtilities.invokeLater(() -> {  // uso invokeLater per garantire la creazione dell'interfaccia grafica sull'Event Dispatch Thread per assicurare la corretta gestione concorrente degli eveni e la sicurezza dell'interfaccia
            LoginView view = new LoginView();
            new LoginController(view); // collega controller alla view
            view.setVisible(true);
        });
    }
}
