package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Foto;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AnnuncioEditDialog extends JDialog {

    private final Annuncio original;
    private Annuncio edited;

    // lista foto solo in memoria (il DB si aggiorna solo su Salva)
    private final List<Foto> currentFotos = new ArrayList<>();

    private JTextField txtTitolo;
    private JTextArea txtDescrizione;
    private JComboBox<String> cmbTipologia;
    private JComboBox<String> cmbCategoria;
    private JTextField txtOggettoRichiesto;
    private JTextField txtPrezzo;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;

    private JPanel fotoListPanel; // contiene righe (path + bottone elimina)

    private boolean confirmed = false;

    public AnnuncioEditDialog(Window parent, Annuncio annuncio, List<Foto> fotoList) {
        super(parent, "Modifica annuncio", ModalityType.APPLICATION_MODAL);
        this.original = annuncio;
        this.edited = cloneAnnuncio(annuncio);

        if (fotoList != null) {
            this.currentFotos.addAll(fotoList);
        }

        initUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private Annuncio cloneAnnuncio(Annuncio a) {
        Annuncio c = new Annuncio();
        c.setId(a.getId());
        c.setTitolo(a.getTitolo());
        c.setDescrizione(a.getDescrizione());
        c.setDataPubblicazione(a.getDataPubblicazione());
        c.setTipologia(a.getTipologia());
        c.setCategoria(a.getCategoria());
        c.setOggettoRichiesto(a.getOggettoRichiesto());
        c.setConcluso(a.isConcluso());
        c.setPrezzo(a.getPrezzo());
        c.setOffreSpedizione(a.isOffreSpedizione());
        c.setOffreIncontroInUni(a.isOffreIncontroInUni());
        c.setMatricolaVenditore(a.getMatricolaVenditore());
        return c;
    }

    private void initUI() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        JScrollPane formScroll = new JScrollPane(form);
        formScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        content.add(formScroll, BorderLayout.CENTER);

        // Titolo
        txtTitolo = new JTextField(edited.getTitolo(), 30);
        form.add(labeled("Titolo", txtTitolo));

        // Descrizione
        txtDescrizione = new JTextArea(
                edited.getDescrizione() != null ? edited.getDescrizione() : "",
                3, 30
        );
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        form.add(labeled("Descrizione", new JScrollPane(txtDescrizione)));

        // Tipologia
        cmbTipologia = new JComboBox<>(new String[]{"Vendita", "Scambio", "Regalo"});
        cmbTipologia.setSelectedItem(edited.getTipologia());
        form.add(labeled("Tipologia", cmbTipologia));

        // Categoria
        cmbCategoria = new JComboBox<>(new String[]{
                "Strumenti_musicali", "Libri", "Informatica",
                "Abbigliamento", "Arredo", "Altro"
        });
        cmbCategoria.setSelectedItem(edited.getCategoria());
        form.add(labeled("Categoria", cmbCategoria));

        // Oggetto richiesto
        txtOggettoRichiesto = new JTextField(
                edited.getOggettoRichiesto() != null ? edited.getOggettoRichiesto() : "",
                30
        );
        form.add(labeled("Oggetto richiesto (per Scambio)", txtOggettoRichiesto));

        // Prezzo
        txtPrezzo = new JTextField();
        if (edited.getPrezzo() != null) {
            txtPrezzo.setText(edited.getPrezzo().toPlainString());
        }
        form.add(labeled("Prezzo (solo Vendita)", txtPrezzo));

        // Opzioni consegna
        JPanel consegnaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        chkSpedizione = new JCheckBox("Offre spedizione", edited.isOffreSpedizione());
        chkInUni = new JCheckBox("Incontro in Uni", edited.isOffreIncontroInUni());
        consegnaPanel.add(chkSpedizione);
        consegnaPanel.add(chkInUni);
        form.add(labeled("Consegna", consegnaPanel));

        // FOTO
        JPanel fotoPanel = new JPanel(new BorderLayout(5, 5));
        fotoPanel.setBorder(BorderFactory.createTitledBorder("Foto annuncio"));

        fotoListPanel = new JPanel();
        fotoListPanel.setLayout(new BoxLayout(fotoListPanel, BoxLayout.Y_AXIS));

        JScrollPane fotoScroll = new JScrollPane(fotoListPanel);
        fotoScroll.setPreferredSize(new Dimension(400, 150));
        fotoScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        fotoPanel.add(fotoScroll, BorderLayout.CENTER);

        JPanel fotoButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAddFoto = new JButton("Aggiungi foto...");
        fotoButtons.add(btnAddFoto);
        fotoPanel.add(fotoButtons, BorderLayout.SOUTH);

        form.add(Box.createVerticalStrut(10));
        form.add(fotoPanel);

        // Pulsanti OK / Annulla
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOk = new JButton("Salva");
        JButton btnCancel = new JButton("Annulla");
        buttons.add(btnCancel);
        buttons.add(btnOk);
        content.add(buttons, BorderLayout.SOUTH);

        // Listener combobox tipologia -> abilita/disabilita campi
        cmbTipologia.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                updateFieldsByTipologia();
            }
        });
        // stato iniziale coerente
        updateFieldsByTipologia();

        // Listener foto
        btnAddFoto.addActionListener(e -> onAddFoto());

        // Listener pulsanti
        btnCancel.addActionListener(e -> {
            confirmed = false;
            dispose();
        });
        btnOk.addActionListener(e -> onSave());

        // inizializza lista foto
        refreshFotoListPanel();
    }

    private JPanel labeled(String label, JComponent comp) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        JLabel lbl = new JLabel(label);
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(comp, BorderLayout.CENTER);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));
        return panel;
    }

    // ======================
    // Tipologia -> abilita/disabilita campi
    // ======================
    private void updateFieldsByTipologia() {
        String tipo = (String) cmbTipologia.getSelectedItem();

        if ("Vendita".equals(tipo)) {
            // solo prezzo attivo
            txtPrezzo.setEnabled(true);

            txtOggettoRichiesto.setText("");
            txtOggettoRichiesto.setEnabled(false);

        } else if ("Scambio".equals(tipo)) {
            // solo oggetto_richiesto attivo
            txtOggettoRichiesto.setEnabled(true);

            txtPrezzo.setText("");
            txtPrezzo.setEnabled(false);

        } else if ("Regalo".equals(tipo)) {
            // nessuno dei due: niente prezzo, niente oggetto richiesto
            txtPrezzo.setText("");
            txtPrezzo.setEnabled(false);

            txtOggettoRichiesto.setText("");
            txtOggettoRichiesto.setEnabled(false);
        }
    }

    // ======================
    // FOTO: gestione UI
    // ======================
    private void refreshFotoListPanel() {
        fotoListPanel.removeAll();

        if (currentFotos.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna foto. Verrà usata l'immagine di default per categoria.");
            lbl.setBorder(new EmptyBorder(5, 5, 5, 5));
            fotoListPanel.add(lbl);
        } else {
            for (int i = 0; i < currentFotos.size(); i++) {
                final int index = i;
                Foto f = currentFotos.get(i);

                JPanel row = new JPanel(new BorderLayout(5, 5));
                row.setBorder(new EmptyBorder(5, 5, 5, 5));

                JLabel lblPath = new JLabel(f.getPath());
                row.add(lblPath, BorderLayout.CENTER);

                JButton btnDel = new JButton("Elimina");
                btnDel.addActionListener(e -> {
                    // RIMUOVE SOLO IN MEMORIA, il DB si aggiorna su Salva
                    currentFotos.remove(index);
                    refreshFotoListPanel();
                });
                row.add(btnDel, BorderLayout.EAST);

                fotoListPanel.add(row);
            }
        }

        fotoListPanel.revalidate();
        fotoListPanel.repaint();
    }

    private void onAddFoto() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            String path = fc.getSelectedFile().getAbsolutePath();
            Foto f = new Foto();
            f.setIdAnnuncio(edited.getId());
            f.setPath(path);
            f.setPrincipale(false); // la principale la decido al salvataggio
            currentFotos.add(f);
            refreshFotoListPanel();
        }
    }

    // ======================
    // Salvataggio
    // ======================
    private void onSave() {
        try {
            edited.setTitolo(txtTitolo.getText().trim());
            edited.setDescrizione(txtDescrizione.getText().trim());

            String tipologia = (String) cmbTipologia.getSelectedItem();
            String categoria = (String) cmbCategoria.getSelectedItem();
            edited.setTipologia(tipologia);
            edited.setCategoria(categoria);

            // --- Oggetto richiesto ---
            if ("Scambio".equals(tipologia)) {
                String objReq = txtOggettoRichiesto.getText().trim();
                if (objReq.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Per gli annunci di Scambio devi indicare l'oggetto richiesto.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                edited.setOggettoRichiesto(objReq);
            } else {
                // Vendita / Regalo -> forziamo oggetto_richiesto a NULL
                edited.setOggettoRichiesto(null);
            }

            // --- Prezzo ---
            String prezzoText = txtPrezzo.getText().trim();
            if ("Vendita".equals(tipologia)) {
                if (prezzoText.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Per gli annunci di Vendita il prezzo è obbligatorio.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                prezzoText = prezzoText.replace(",", ".");
                BigDecimal prezzo = new BigDecimal(prezzoText);
                if (prezzo.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Il prezzo non può essere negativo.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                edited.setPrezzo(prezzo);
            } else {
                // Scambio / Regalo -> prezzo deve essere NULL
                edited.setPrezzo(null);
            }

            // --- Consegna ---
            edited.setOffreSpedizione(chkSpedizione.isSelected());
            edited.setOffreIncontroInUni(chkInUni.isSelected());

            if (!chkSpedizione.isSelected() && !chkInUni.isSelected()) {
                JOptionPane.showMessageDialog(this,
                        "Devi selezionare almeno una modalità di consegna.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // --- Foto: marca la principale se c'è almeno una foto ---
            for (int i = 0; i < currentFotos.size(); i++) {
                currentFotos.get(i).setPrincipale(i == 0);
            }

            confirmed = true;
            dispose();

        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this,
                    "Prezzo non valido. Usa un numero (es. 10 oppure 10.50).",
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Annuncio getEditedAnnuncio() {
        return edited;
    }

    public List<Foto> getEditedFotoList() {
        return new ArrayList<>(currentFotos);
    }
}
