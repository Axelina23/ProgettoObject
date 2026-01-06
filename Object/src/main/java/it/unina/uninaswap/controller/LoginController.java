package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.dao.impl.StudenteDAOImpl;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.view.AnnunciMainView;
import it.unina.uninaswap.view.LoginView;

import java.sql.SQLException;

import javax.swing.*;

public class LoginController {

    private final LoginView view;
    private final StudenteDAO studenteDAO;

    public LoginController(LoginView view) {
        this.view = view;
        this.studenteDAO = new StudenteDAOImpl();

        // collega il bottone Accedi a questo controller
        this.view.addLoginListener(e -> handleLogin());
    }

    private void handleLogin() {
        String email = view.getEmail();
        String password = view.getPassword();

        if (email.isEmpty() || password.isEmpty()) {
            view.showError("Inserisci email e password.");
            return;
        }

        try {
            Studente s = studenteDAO.findByEmailAndPassword(email, password);
            if (s == null) {
                view.showError("Credenziali non valide.");
            } else {
                // login ok → apri home e chiudi login
                AnnunciMainView home = new AnnunciMainView(s);
                new HomeController(home, s);
                home.setVisible(true);
                view.dispose();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            view.showError("Errore di connessione al database.");
        } catch (Exception e) {
        	e.printStackTrace();
        	view.showError("Errore interno dell'applicazione");
        }
    }
}
