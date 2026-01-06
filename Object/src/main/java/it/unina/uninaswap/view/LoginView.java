package it.unina.uninaswap.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import com.formdev.flatlaf.*;

public class LoginView extends JFrame {
	
    // componenti esposti al controller
    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin;

    public LoginView() {
        setTitle("UniNaSwap - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 260);
        setLocationRelativeTo(null); // per centrare la finestra sullo schermo

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(18,18,18,18));
        setContentPane(content);

        
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0,0,18,0);
        JLabel lblTitolo = new JLabel("Login");
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 18f));
        content.add(lblTitolo, c);

        // label Email
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 1;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0,0,10,12);
        content.add(new JLabel("Email"), c);

        // campo Email
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 1;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        txtEmail = new JTextField();
        txtEmail.setColumns(24);
        content.add(txtEmail, c);

        // label Password
        c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 2;
        c.anchor = GridBagConstraints.LINE_END;
        c.insets = new Insets(0,0,10,12);
        content.add(new JLabel("Password"), c);

        // campo Password
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 2;
        c.weightx = 1.0;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,10,0);
        txtPassword = new JPasswordField();
        txtPassword.setColumns(24);
        content.add(txtPassword, c);

        // bottone Accedi
        c = new GridBagConstraints();
        c.gridx = 1; c.gridy = 3;
        c.anchor = GridBagConstraints.LINE_END;
        btnLogin = new JButton("Accedi");
        content.add(btnLogin, c);
        
        //PlaceHolders
        txtEmail.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "email unina");
        txtPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "password");
        

  
    }
    
    //Metodi per il controller
    public String getEmail() {
        return txtEmail.getText().trim();
    }

    public String getPassword() {
        return new String(txtPassword.getPassword());
    }

    public void addLoginListener(ActionListener listener) {
        btnLogin.addActionListener(listener);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Errore", JOptionPane.ERROR_MESSAGE);
    }

}
