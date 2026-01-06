package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.model.enums.SessoStudente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog impostazioni profilo.
 * Estensione: aggiunta sezione "Indirizzo" modificabile (via/civico/cap/città/stato).
 *
 * Nota: qui NON facciamo query al DB: il Controller passa eventualmente i valori iniziali
 * e, dopo il salvataggio, legge i getter per aggiornare/creare l'indirizzo tramite IndirizzoDAO.
 */
public class StudenteSettingsDialog extends JDialog {

    private JTextField txtMatricola;
    private JTextField txtEmail;
    private JTextField txtNome;
    private JTextField txtCognome;
    private JPasswordField txtPassword;
    private JComboBox<SessoStudente> cmbSesso;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;

    // ===== Indirizzo =====
    private JTextField txtVia;
    private JTextField txtCivico;
    private JTextField txtCap;
    private JTextField txtCitta;
    private JTextField txtStato;

    private String origVia;
    private String origCitta;
    private Integer origCap;
    private Integer origCivico;
    private String origStato;

    private boolean confirmed = false;
    private Studente editedStudente;

    /**
     * Costruttore base (retrocompatibile): nessun indirizzo precompilato.
     */
    public StudenteSettingsDialog(JFrame parent, Studente originale) {
        this(parent, originale, null, null, null, null, null);
    }

    /**
     * Costruttore con indirizzo precompilato (se lo studente ha già un indirizzo salvato).
     */
    public StudenteSettingsDialog(JFrame parent,
                                  Studente originale,
                                  String via,
                                  String citta,
                                  Integer cap,
                                  Integer civico,
                                  String stato) {
        super(parent, "Impostazioni profilo", true);
        setSize(460, 520);
        setLocationRelativeTo(parent);

        this.origVia = via;
        this.origCitta = citta;
        this.origCap = cap;
        this.origCivico = civico;
        this.origStato = stato;

        JPanel content = new JPanel();
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        setContentPane(content);

        // MATRICOLA (read-only)
        txtMatricola = new JTextField();
        txtMatricola.setEditable(false);
        content.add(labeledRow("Matricola:", txtMatricola));
        content.add(Box.createVerticalStrut(8));

        // NOME
        txtNome = new JTextField();
        content.add(labeledRow("Nome:", txtNome));
        content.add(Box.createVerticalStrut(8));

        // COGNOME
        txtCognome = new JTextField();
        content.add(labeledRow("Cognome:", txtCognome));
        content.add(Box.createVerticalStrut(8));

        // EMAIL
        txtEmail = new JTextField();
        content.add(labeledRow("Email:", txtEmail));
        content.add(Box.createVerticalStrut(8));

        // PASSWORD
        txtPassword = new JPasswordField();
        content.add(labeledRow("Password:", txtPassword));
        content.add(Box.createVerticalStrut(8));

        // SESSO
        cmbSesso = new JComboBox<>(SessoStudente.values());
        content.add(labeledRow("Sesso:", cmbSesso));
        content.add(Box.createVerticalStrut(8));

        // PREFERENZE CONSEGNA
        JPanel prefPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        prefPanel.setBorder(BorderFactory.createTitledBorder("Preferenze consegna"));
        chkSpedizione = new JCheckBox("Spedizione");
        chkInUni = new JCheckBox("Incontro in uni");
        prefPanel.add(chkSpedizione);
        prefPanel.add(chkInUni);
        content.add(prefPanel);
        content.add(Box.createVerticalStrut(10));

        // ===== INDIRIZZO =====
        JPanel indPanel = new JPanel();
        indPanel.setLayout(new BoxLayout(indPanel, BoxLayout.Y_AXIS));
        indPanel.setBorder(BorderFactory.createTitledBorder("Indirizzo (per spedizione)"));

        txtVia = new JTextField();
        txtCivico = new JTextField();
        txtCap = new JTextField();
        txtCitta = new JTextField();
        txtStato = new JTextField();

        indPanel.add(labeledRow("Via:", txtVia));
        indPanel.add(Box.createVerticalStrut(6));
        indPanel.add(labeledRow("Civico:", txtCivico));
        indPanel.add(Box.createVerticalStrut(6));
        indPanel.add(labeledRow("CAP:", txtCap));
        indPanel.add(Box.createVerticalStrut(6));
        indPanel.add(labeledRow("Città:", txtCitta));
        indPanel.add(Box.createVerticalStrut(6));
        indPanel.add(labeledRow("Stato:", txtStato));

        content.add(indPanel);
        content.add(Box.createVerticalStrut(10));

        // BOTTONI
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAnnulla = new JButton("Annulla");
        JButton btnSalva = new JButton("Salva");
        buttons.add(btnAnnulla);
        buttons.add(btnSalva);
        content.add(buttons);

        btnAnnulla.addActionListener(e -> dispose());

        // Pre-popola campi (Studente)
        if (originale != null) {
            txtMatricola.setText(originale.getMatricola());
            txtNome.setText(originale.getNome());
            txtCognome.setText(originale.getCognome());
            txtEmail.setText(originale.getEmail());
            txtPassword.setText(originale.getPassword());

            if (originale.getSesso() != null) {
                cmbSesso.setSelectedItem(originale.getSesso());
            }
            chkSpedizione.setSelected(originale.getPreferisceSpedizione());
            chkInUni.setSelected(originale.getPreferisceIncontroInUni());
        }

        // Pre-popola indirizzo (se presente)
        if (via != null) txtVia.setText(via);
        if (civico != null) txtCivico.setText(String.valueOf(civico));
        if (cap != null) txtCap.setText(String.valueOf(cap));
        if (citta != null) txtCitta.setText(citta);
        if (stato != null) txtStato.setText(stato);

        btnSalva.addActionListener(e -> onSave(originale));
    }

    private JPanel labeledRow(String label, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JLabel(label), BorderLayout.WEST);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void onSave(Studente originale) {
        String nome = txtNome.getText().trim();
        String cognome = txtCognome.getText().trim();
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Nome, cognome, email e password sono obbligatori.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!chkSpedizione.isSelected() && !chkInUni.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona almeno una preferenza di consegna (spedizione o incontro in uni).",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ===== Validazione indirizzo =====
        String via = txtVia.getText().trim();
        String civicoStr = txtCivico.getText().trim();
        String capStr = txtCap.getText().trim();
        String citta = txtCitta.getText().trim();
        String stato = txtStato.getText().trim();

        boolean anyAddrFilled = !via.isEmpty() || !civicoStr.isEmpty() || !capStr.isEmpty() || !citta.isEmpty() || !stato.isEmpty();

        Integer civico = null;
        Integer cap = null;

        if (anyAddrFilled) {
            if (via.isEmpty() || civicoStr.isEmpty() || capStr.isEmpty() || citta.isEmpty() || stato.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Per salvare l'indirizzo devi compilare TUTTI i campi (via, civico, CAP, città, stato).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                civico = Integer.parseInt(civicoStr);
                cap = Integer.parseInt(capStr);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Civico e CAP devono essere numerici.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (civico <= 0 || cap <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Civico e CAP devono essere maggiori di 0.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // build Studente
        Studente s = new Studente();
        if (originale != null) {
            s.setMatricola(originale.getMatricola());
        } else {
            s.setMatricola(txtMatricola.getText().trim());
        }
        s.setNome(nome);
        s.setCognome(cognome);
        s.setEmail(email);
        s.setPassword(password);
        s.setSesso((SessoStudente) cmbSesso.getSelectedItem());
        s.setPreferisceSpedizione(chkSpedizione.isSelected());
        s.setPreferisceIncontroInUni(chkInUni.isSelected());

        // salva risultato
        this.editedStudente = s;
        this.confirmed = true;

        // aggiorna originali indirizzo (per controller: confronto)
        this.origVia = (origVia == null) ? null : origVia.trim();
        this.origCitta = (origCitta == null) ? null : origCitta.trim();
        this.origStato = (origStato == null) ? null : origStato.trim();
        this.origCap = origCap;
        this.origCivico = origCivico;

        dispose();
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Studente getEditedStudente() {
        return editedStudente;
    }

    // ===== Getter Indirizzo compilato (null se non compilato) =====
    public boolean hasIndirizzoCompilato() {
        String via = txtVia.getText().trim();
        String civicoStr = txtCivico.getText().trim();
        String capStr = txtCap.getText().trim();
        String citta = txtCitta.getText().trim();
        String stato = txtStato.getText().trim();
        return !(via.isEmpty() || civicoStr.isEmpty() || capStr.isEmpty() || citta.isEmpty() || stato.isEmpty());
    }

    public String getIndirizzoVia() { return txtVia.getText().trim(); }
    public String getIndirizzoCitta() { return txtCitta.getText().trim(); }
    public String getIndirizzoStato() { return txtStato.getText().trim(); }

    public Integer getIndirizzoCivico() {
        String s = txtCivico.getText().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
    }

    public Integer getIndirizzoCap() {
        String s = txtCap.getText().trim();
        if (s.isEmpty()) return null;
        try { return Integer.parseInt(s); } catch (NumberFormatException ex) { return null; }
    }
}
