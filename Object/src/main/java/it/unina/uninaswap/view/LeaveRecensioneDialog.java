package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Recensione;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LeaveRecensioneDialog extends JDialog {

    private boolean confirmed = false;
    private int rating = 5; 
    private JTextField txtTitolo;
    private JTextArea txtCorpo;
    private Recensione resultRecensione;

    public LeaveRecensioneDialog(Window owner, String nomeVenditore) {
        super(owner, "Lascia una recensione a " + nomeVenditore, ModalityType.APPLICATION_MODAL);
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        setContentPane(content);

        // Form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        // Voto
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(new JLabel("Voto (1-5):"), gbc);

        JPanel starPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup grp = new ButtonGroup();
        for (int i = 1; i <= 5; i++) {
            JRadioButton r = new JRadioButton(String.valueOf(i));
            if (i == 5) r.setSelected(true);
            final int val = i;
            r.addActionListener(e -> rating = val);
            grp.add(r);
            starPanel.add(r);
        }
        gbc.gridx = 1;
        form.add(starPanel, gbc);

        // Titolo
        gbc.gridx = 0; gbc.gridy = 1;
        form.add(new JLabel("Titolo:"), gbc);
        
        txtTitolo = new JTextField(20);
        gbc.gridx = 1;
        form.add(txtTitolo, gbc);

        // Testo
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Recensione:"), gbc);

        txtCorpo = new JTextArea(5, 20);
        txtCorpo.setLineWrap(true);
        txtCorpo.setWrapStyleWord(true);
        gbc.gridx = 1;
        form.add(new JScrollPane(txtCorpo), gbc);

        content.add(form, BorderLayout.CENTER);

        // Bottoni
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalva = new JButton("Pubblica Recensione");
        btnSalva.addActionListener(e -> onSalva());
        
        JButton btnAnnulla = new JButton("Annulla");
        btnAnnulla.addActionListener(e -> dispose());

        btnPanel.add(btnAnnulla);
        btnPanel.add(btnSalva);
        content.add(btnPanel, BorderLayout.SOUTH);
    }

    private void onSalva() {
        if (txtTitolo.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Inserisci un titolo.", "Errore", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        resultRecensione = new Recensione();
        resultRecensione.setTitolo(txtTitolo.getText().trim());
        resultRecensione.setCorpo(txtCorpo.getText().trim());
        resultRecensione.setValutazione(rating);
        
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }
    public Recensione getRecensione() { return resultRecensione; }
}