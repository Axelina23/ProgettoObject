package it.unina.uninaswap.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class AnnunciFilterPanel extends JPanel {

    // componenti filtri
    private JTextField txtSearchFilter;
    private JComboBox<String> cmbCategoria;
    private JRadioButton rbTipoTutti;
    private JRadioButton rbVendita;
    private JRadioButton rbScambio;
    private JRadioButton rbRegalo;
    private JTextField txtPrezzoMin;
    private JTextField txtPrezzoMax;
    private JCheckBox chkSpedizione;
    private JCheckBox chkInUni;
    private JButton btnCerca;


    public AnnunciFilterPanel() {
        setPreferredSize(new Dimension(220, 0));
        setLayout(new GridBagLayout());
        setVisible(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 10, 5, 10);
        c.anchor = GridBagConstraints.LINE_START;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;

        // ----- Ricerca -----
        c.gridx = 0;
        c.gridy = row++;
        add(new JLabel("Cerca"), c);

        txtSearchFilter = new JTextField();
        c.gridx = 0;
        c.gridy = row++;
        add(txtSearchFilter, c);

        // separatore
        c.gridx = 0;
        c.gridy = row++;
        add(new JSeparator(), c);

        // ----- Categoria -----
        c.gridx = 0;
        c.gridy = row++;
        add(new JLabel("Categoria"), c);

        cmbCategoria = new JComboBox<>();
        // prima voce: nessun filtro
        cmbCategoria.addItem("Tutte");
        cmbCategoria.addItem("Strumenti_musicali");
        cmbCategoria.addItem("Libri");
        cmbCategoria.addItem("Informatica");
        cmbCategoria.addItem("Abbigliamento");
        cmbCategoria.addItem("Arredo");
        cmbCategoria.addItem("Altro");

        c.gridx = 0;
        c.gridy = row++;
        add(cmbCategoria, c);

        // separatore
        c.gridx = 0;
        c.gridy = row++;
        add(new JSeparator(), c);

        // ----- Tipologia -----
        c.gridx = 0;
        c.gridy = row++;
        add(new JLabel("Tipologia"), c);

        JPanel tipoPanel = new JPanel();
        tipoPanel.setLayout(new BoxLayout(tipoPanel, BoxLayout.Y_AXIS));

        rbTipoTutti = new JRadioButton("Tutte", true);
        rbVendita   = new JRadioButton("Vendita");
        rbScambio   = new JRadioButton("Scambio");
        rbRegalo    = new JRadioButton("Regalo");

        ButtonGroup gruppoTipo = new ButtonGroup();
        gruppoTipo.add(rbTipoTutti);
        gruppoTipo.add(rbVendita);
        gruppoTipo.add(rbScambio);
        gruppoTipo.add(rbRegalo);

        tipoPanel.add(rbTipoTutti);
        tipoPanel.add(rbVendita);
        tipoPanel.add(rbScambio);
        tipoPanel.add(rbRegalo);

        c.gridx = 0;
        c.gridy = row++;
        add(tipoPanel, c);


        // separatore
        c.gridx = 0;
        c.gridy = row++;
        add(new JSeparator(), c);

        // ----- Prezzo minimo -----
        c.gridx = 0;
        c.gridy = row++;
        add(new JLabel("Prezzo min"), c);

        txtPrezzoMin = new JTextField();
        c.gridx = 0;
        c.gridy = row++;
        add(txtPrezzoMin, c);

        // ----- Prezzo massimo -----
        c.gridx = 0;
        c.gridy = row++;
        add(new JLabel("Prezzo max"), c);

        txtPrezzoMax = new JTextField();
        c.gridx = 0;
        c.gridy = row++;
        add(txtPrezzoMax, c);

        // separatore
        c.gridx = 0;
        c.gridy = row++;
        add(new JSeparator(), c);

        // ----- Consegna -----
        c.gridx = 0;
        c.gridy = row++;
        add(new JLabel("Consegna"), c);

        chkSpedizione = new JCheckBox("Offre spedizione");
        chkInUni      = new JCheckBox("Incontro in uni");

        JPanel consegnaPanel = new JPanel();
        consegnaPanel.setLayout(new BoxLayout(consegnaPanel, BoxLayout.Y_AXIS));
        consegnaPanel.add(chkSpedizione);
        consegnaPanel.add(chkInUni);

        c.gridx = 0;
        c.gridy = row++;
        add(consegnaPanel, c);
        
        // separatore
        c.gridx = 0;
        c.gridy = row++;
        add(new JSeparator(), c);

        // ----- Bottone Cerca -----
        btnCerca = new JButton("Cerca");
        c.gridx = 0;
        c.gridy = row++;
        add(btnCerca, c);

    }
    
    // Getter per il controller

    public String getSearchText() {
        return txtSearchFilter.getText().trim();
    }
    
    
    public String getSelectedCategoria() {
        String value = (String) cmbCategoria.getSelectedItem();
        if ("Tutte".equals(value)) {
            return null;
        }
        return value;
    }
    
    
    public String getSelectedTipologia() {
        if (rbVendita.isSelected()) {
            return "Vendita";
        } else if (rbScambio.isSelected()) {
            return "Scambio";
        } else if (rbRegalo.isSelected()) {
            return "Regalo";
        } else {
            return null;
        }
    }
    
    
    public String getPrezzoMinText() {
        String text = txtPrezzoMin.getText().trim();
        return text.isEmpty() ? null : text;
    }
    
    public String getPrezzoMaxText() {
        String text = txtPrezzoMax.getText().trim();
        return text.isEmpty() ? null : text;
    }
    
    
    public boolean isSpedizioneSelected() {
        return chkSpedizione.isSelected();
    }

    public boolean isInUniSelected() {
        return chkInUni.isSelected();
    }

    
    public JButton getBtnCerca() {
        return btnCerca;
    }

}