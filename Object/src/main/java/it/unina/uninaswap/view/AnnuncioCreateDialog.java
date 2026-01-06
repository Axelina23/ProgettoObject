package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Foto;
import it.unina.uninaswap.model.entity.Studente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class AnnuncioCreateDialog extends JDialog {

    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JComboBox<String> cmbTipologia;
    private JComboBox<String> cmbCategoria;
    private JTextField txtPrezzo;
    private JTextField txtOggettoRichiesto;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;

    private JButton btnCrea;
    private JButton btnAnnulla;

    // ===== Foto annuncio =====
    private static final String ANNUNCI_IMG_DIR =
            "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/annunci/";

    private JButton btnAggiungiFoto;
    private JButton btnRimuoviFoto;
    private DefaultListModel<Foto> fotoListModel;
    private JList<Foto> lstFoto;
    private List<Foto> createdFotoList = new ArrayList<>();

    private boolean confirmed = false;
    private Annuncio createdAnnuncio;

    private final Studente venditore; // studente loggato

    public AnnuncioCreateDialog(JFrame parent, Studente venditore) {
        super(parent, "Nuovo annuncio", true);
        this.venditore = venditore;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(parent);

        buildUI();
        initListeners();
        updateFieldsByTipologia();
    }

    private void buildUI() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel(new GridBagLayout());
        content.add(form, BorderLayout.CENTER);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;

        int row = 0;

        // ===== Titolo =====
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Titolo*"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        txtTitolo = new JTextField(30);
        form.add(txtTitolo, c);
        row++;

        // ===== Tipologia =====
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Tipologia*"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        cmbTipologia = new JComboBox<>(new String[] {"Vendita", "Scambio", "Regalo"});
        form.add(cmbTipologia, c);
        row++;

        // ===== Categoria =====
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Categoria*"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        cmbCategoria = new JComboBox<>(new String[] {
                "Strumenti_musicali",
                "Libri",
                "Informatica",
                "Abbigliamento",
                "Arredo",
                "Altro"
        });
        form.add(cmbCategoria, c);
        row++;

        // ===== Prezzo ===== (solo Vendita)
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Prezzo (€)"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        txtPrezzo = new JTextField(10);
        form.add(txtPrezzo, c);
        row++;

        // ===== Oggetto richiesto ===== (solo Scambio)
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Oggetto richiesto"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        txtOggettoRichiesto = new JTextField(30);
        form.add(txtOggettoRichiesto, c);
        row++;

        // ===== Consegna =====
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.gridx = 0; c.gridy = row;
        form.add(new JLabel("Consegna*"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        JPanel consegnaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        chkSpedizione = new JCheckBox("Spedizione");
        chkInUni      = new JCheckBox("Incontro in Uni");
        consegnaPanel.add(chkSpedizione);
        consegnaPanel.add(chkInUni);
        form.add(consegnaPanel, c);
        row++;

        // ===== Descrizione =====
        c.gridx = 0; c.gridy = row;
        c.anchor = GridBagConstraints.FIRST_LINE_END;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        form.add(new JLabel("Descrizione"), c);

        c.gridx = 1; c.gridy = row;
        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 1.0;
        txtDescrizione = new JTextArea(5, 30);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescrizione);
        form.add(scrollDesc, c);

        // ===== Foto (opzionali) =====
        row++;
        c.fill = GridBagConstraints.NONE;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0; c.gridy = row;
        c.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Foto"), c);

        c.gridx = 1; c.gridy = row;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.weighty = 0.0;
        form.add(buildFotoPanel(), c);

        // ===== Bottoni =====
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAnnulla = new JButton("Annulla");
        btnCrea    = new JButton("Crea");
        buttons.add(btnAnnulla);
        buttons.add(btnCrea);
        content.add(buttons, BorderLayout.SOUTH);
    }

    private void initListeners() {
        cmbTipologia.addActionListener(e -> updateFieldsByTipologia());

        btnAggiungiFoto.addActionListener(e -> addPhotosFromChooser());

        btnRimuoviFoto.addActionListener(e -> {
            int idx = lstFoto.getSelectedIndex();
            if (idx >= 0) {
                Foto removed = fotoListModel.remove(idx);
                if (removed != null && removed.getPath() != null) {
                    try { Files.deleteIfExists(Path.of(removed.getPath())); } catch (Exception ignored) {}
                }
                refreshPrincipaleFlag();
            }
        });

        btnAnnulla.addActionListener(e -> {
            confirmed = false;
            deleteCopiedPhotosIfAny();
            dispose();
        });

        btnCrea.addActionListener(e -> {
            if (validateAndBuildAnnuncio()) {
                confirmed = true;
                dispose();
            }
        });
    }

    private JPanel buildFotoPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));

        fotoListModel = new DefaultListModel<>();
        lstFoto = new JList<>(fotoListModel);
        lstFoto.setVisibleRowCount(3);

        // mostra nome file invece di "Foto@hash"
        lstFoto.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel();
            if (value != null && value.getPath() != null) {
                lbl.setText(new File(value.getPath()).getName() + (value.isPrincipale() ? " (principale)" : ""));
            } else {
                lbl.setText("-");
            }
            lbl.setOpaque(true);
            if (isSelected) {
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            } else {
                lbl.setBackground(list.getBackground());
                lbl.setForeground(list.getForeground());
            }
            lbl.setBorder(new EmptyBorder(2, 6, 2, 6));
            return lbl;
        });

        JScrollPane scroll = new JScrollPane(lstFoto);
        scroll.setPreferredSize(new Dimension(250, 80));
        p.add(scroll, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnAggiungiFoto = new JButton("Aggiungi foto...");
        btnRimuoviFoto = new JButton("Rimuovi selezionata");

        actions.add(btnAggiungiFoto);
        actions.add(btnRimuoviFoto);

        p.add(actions, BorderLayout.SOUTH);
        return p;
    }

    private void refreshPrincipaleFlag() {
        for (int i = 0; i < fotoListModel.size(); i++) {
            Foto f = fotoListModel.get(i);
            if (f != null) f.setPrincipale(i == 0);
        }
        lstFoto.repaint();
    }

    private void deleteCopiedPhotosIfAny() {
        for (int i = 0; i < fotoListModel.size(); i++) {
            Foto f = fotoListModel.get(i);
            if (f == null || f.getPath() == null) continue;
            try {
                Files.deleteIfExists(Path.of(f.getPath()));
            } catch (Exception ignored) {}
        }
    }

    private void addPhotosFromChooser() {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setDialogTitle("Seleziona foto annuncio");

        chooser.setAcceptAllFileFilterUsed(false);
        chooser.addChoosableFileFilter(new FileNameExtensionFilter(
                "Immagini (jpg, jpeg, png)", "jpg", "jpeg", "png"
        ));

        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File[] files = chooser.getSelectedFiles();
        if (files == null || files.length == 0) return;

        try {
            Files.createDirectories(Path.of(ANNUNCI_IMG_DIR));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Impossibile creare la cartella immagini:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        for (File f : files) {
            if (f == null) continue;

            String name = f.getName();
            String safeName = System.currentTimeMillis() + "_" + name.replaceAll("\\s+", "_");
            Path dest = Path.of(ANNUNCI_IMG_DIR, safeName);

            try {
                Files.copy(f.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

                Foto foto = new Foto();
                foto.setPath(dest.toString());
                foto.setPrincipale(fotoListModel.isEmpty());

                fotoListModel.addElement(foto);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Errore copia foto:\n" + ex.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }
        }

        refreshPrincipaleFlag();
    }

    private void updateFieldsByTipologia() {
        String tipo = (String) cmbTipologia.getSelectedItem();

        if ("Vendita".equals(tipo)) {
            txtPrezzo.setEnabled(true);
            txtOggettoRichiesto.setEnabled(false);
            txtOggettoRichiesto.setText("");
        } else if ("Scambio".equals(tipo)) {
            txtPrezzo.setEnabled(false);
            txtPrezzo.setText("");
            txtOggettoRichiesto.setEnabled(true);
        } else { // Regalo
            txtPrezzo.setEnabled(false);
            txtPrezzo.setText("");
            txtOggettoRichiesto.setEnabled(false);
            txtOggettoRichiesto.setText("");
        }
    }

    private boolean validateAndBuildAnnuncio() {
        String titolo = txtTitolo.getText().trim();
        if (titolo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci un titolo.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String tipologia = (String) cmbTipologia.getSelectedItem();
        String categoria = (String) cmbCategoria.getSelectedItem();

        if (tipologia == null || categoria == null) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona tipologia e categoria.",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (!chkSpedizione.isSelected() && !chkInUni.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona almeno una modalità di consegna (spedizione o incontro in uni).",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        BigDecimal prezzo = null;
        if ("Vendita".equals(tipologia)) {
            String prezzoText = txtPrezzo.getText().trim();
            if (prezzoText.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Per gli annunci di vendita il prezzo è obbligatorio.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
            try {
                prezzoText = prezzoText.replace(",", ".");
                prezzo = new BigDecimal(prezzoText);
                if (prezzo.compareTo(BigDecimal.ZERO) < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Prezzo non valido. Usa un numero ≥ 0 (es. 10 o 10.50).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        String oggettoRich = null;
        if ("Scambio".equals(tipologia)) {
            oggettoRich = txtOggettoRichiesto.getText().trim();
            if (oggettoRich.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Per gli annunci di scambio specifica l'oggetto richiesto.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        Annuncio a = new Annuncio();
        a.setTitolo(titolo);

        String descr = txtDescrizione.getText().trim();
        a.setDescrizione(descr.isEmpty() ? null : descr);

        a.setTipologia(tipologia);
        a.setCategoria(categoria);
        a.setOggettoRichiesto(oggettoRich);
        a.setConcluso(false);
        a.setPrezzo(prezzo);

        a.setOffreSpedizione(chkSpedizione.isSelected());
        a.setOffreIncontroInUni(chkInUni.isSelected());

        if (venditore != null) {
            a.setMatricolaVenditore(venditore.getMatricola());
        }

        this.createdAnnuncio = a;

        // foto selezionate (idAnnuncio verrà messo dal controller dopo insert)
        this.createdFotoList = new ArrayList<>();
        for (int i = 0; i < fotoListModel.size(); i++) {
            Foto f = fotoListModel.get(i);
            if (f == null || f.getPath() == null) continue;

            Foto copy = new Foto();
            copy.setPath(f.getPath());
            copy.setPrincipale(i == 0);
            this.createdFotoList.add(copy);
        }

        return true;
    }

    // ===== API verso il controller =====
    public boolean isConfirmed() {
        return confirmed;
    }

    public Annuncio getCreatedAnnuncio() {
        return confirmed ? createdAnnuncio : null;
    }

    public List<Foto> getCreatedFotoList() {
        return confirmed ? createdFotoList : new ArrayList<>();
    }
}
