package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProfileView extends JPanel {

    // dimensioni card annunci
    private static final int CARD_W = 260;
    private static final int CARD_H = 220;
    private static final int HGAP = 10;
    private static final int VGAP = 10;

    // dimensioni card recensioni
    private static final int REC_CARD_W = 260;
    private static final int REC_CARD_H = 220;

    // path stellina
    private static final String STAR_PATH =
            "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/altro/star.jpg";

    private JLabel lblFotoProfilo;
    private JLabel lblNomeCompleto;
    private JLabel lblEmail;
    private JLabel lblMatricola;
    private JLabel lblIndirizzo;
    private JLabel lblPreferenze;

    private JPanel annunciPreviewPanel;
    private JButton btnVediTuttiAnnunci;

    private JPanel recensioniPreviewPanel;
    private JButton btnVediTutteRecensioni;

    private Studente currentStudente;

    // stato annunci
    private List<Annuncio> mieiAnnunci = new ArrayList<>();
    private int shownAnnunciCount = 0;

    // stato recensioni
    private List<RecensioneCardData> mieRecensioni = new ArrayList<>();
    private int shownRecensioniCount = 0;
    private ImageIcon starIcon; // cache stellina

    // listener click/modifica/elimina su annuncio
    public interface AnnuncioClickListener {
        void onAnnuncioClick(Annuncio annuncio);
    }

    public interface AnnuncioEditListener {
        void onModificaAnnuncio(Annuncio annuncio);
    }

    public interface AnnuncioDeleteListener {
        void onEliminaAnnuncio(Annuncio annuncio);
    }

    private AnnuncioClickListener annuncioClickListener;
    private AnnuncioEditListener annuncioEditListener;
    private AnnuncioDeleteListener annuncioDeleteListener;

    public ProfileView(Studente studenteLoggato) {
        setLayout(new BorderLayout());

        // pannello principale con BoxLayout (verticale)
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // scrollpane solo verticale
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // FOTO PROFILO (sempre default in base al sesso)
        lblFotoProfilo = new JLabel();
        lblFotoProfilo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblFotoProfilo);
        mainPanel.add(Box.createVerticalStrut(15));

        // titolo
        JLabel lblTitoloInfo = new JLabel("Informazioni studente");
        lblTitoloInfo.setFont(lblTitoloInfo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitoloInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitoloInfo);
        mainPanel.add(Box.createVerticalStrut(10));

        // info
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 5));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNomeCompleto = new JLabel("Nome: -");
        lblEmail = new JLabel("Email: -");
        lblMatricola = new JLabel("Matricola: -");
        lblIndirizzo = new JLabel("Indirizzo: -");
        lblPreferenze = new JLabel("Preferenze consegna: -");

        infoPanel.add(lblNomeCompleto);
        infoPanel.add(lblEmail);
        infoPanel.add(lblMatricola);
        infoPanel.add(lblIndirizzo);
        infoPanel.add(lblPreferenze);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // ======================
        // I MIEI ANNUNCI
        // ======================
        JPanel annunciSection = new JPanel(new BorderLayout());
        annunciSection.setBorder(new TitledBorder("I miei annunci"));

        annunciPreviewPanel = new JPanel();
        annunciPreviewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));
        annunciPreviewPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        annunciSection.add(annunciPreviewPanel, BorderLayout.CENTER);

        btnVediTuttiAnnunci = new JButton("Vedi più annunci");
        btnVediTuttiAnnunci.setEnabled(false);
        annunciSection.add(btnVediTuttiAnnunci, BorderLayout.SOUTH);

        mainPanel.add(annunciSection);
        mainPanel.add(Box.createVerticalStrut(15));

        // ======================
        // LE MIE RECENSIONI
        // ======================
        JPanel recensioniSection = new JPanel(new BorderLayout());
        recensioniSection.setBorder(new TitledBorder("Le mie recensioni"));

        recensioniPreviewPanel = new JPanel();
        recensioniPreviewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));
        recensioniPreviewPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        recensioniSection.add(recensioniPreviewPanel, BorderLayout.CENTER);

        btnVediTutteRecensioni = new JButton("Vedi tutte le mie recensioni");
        btnVediTutteRecensioni.setEnabled(false);
        recensioniSection.add(btnVediTutteRecensioni, BorderLayout.SOUTH);

        mainPanel.add(recensioniSection);

        // listener interno per "Vedi più annunci" (+6 alla volta)
        btnVediTuttiAnnunci.addActionListener(e -> {
            if (mieiAnnunci.isEmpty()) return;
            shownAnnunciCount = Math.min(shownAnnunciCount + 6, mieiAnnunci.size());
            refreshAnnunciPreview();
        });

        // listener interno per "Vedi tutte le mie recensioni" (+6 alla volta)
        btnVediTutteRecensioni.addActionListener(e -> loadMoreRecensioni(6));

        // set iniziale
        setStudente(studenteLoggato);
        refreshAnnunciPreview();
        refreshRecensioniPreview();
    }

    // ====== Listener setters ======

    public void setAnnuncioClickListener(AnnuncioClickListener listener) {
        this.annuncioClickListener = listener;
    }

    public void setAnnuncioEditListener(AnnuncioEditListener listener) {
        this.annuncioEditListener = listener;
    }

    public void setAnnuncioDeleteListener(AnnuncioDeleteListener listener) {
        this.annuncioDeleteListener = listener;
    }

    // ====== Studente ======
    public void setStudente(Studente studente) {
        this.currentStudente = studente;

        if (studente == null) {
            lblNomeCompleto.setText("Nome: -");
            lblEmail.setText("Email: -");
            lblMatricola.setText("Matricola: -");
            lblIndirizzo.setText("Indirizzo: -");
            lblPreferenze.setText("Preferenze consegna: -");
            setFotoProfiloDefaultBySesso(null, 120);
            return;
        }

        lblNomeCompleto.setText("Nome: " + studente.getNome() + " " + studente.getCognome());
        lblEmail.setText("Email: " + studente.getEmail());
        lblMatricola.setText("Matricola: " + studente.getMatricola());

        // se il controller non setta un indirizzo, resta '-'
        if (lblIndirizzo.getText() == null || lblIndirizzo.getText().startsWith("Indirizzo: -")) {
            lblIndirizzo.setText("Indirizzo: -");
        }

        String pref = "";
        if (studente.getPreferisceSpedizione()) pref += "Spedizione ";
        if (studente.getPreferisceIncontroInUni()) {
            if (!pref.isEmpty()) pref += "- ";
            pref += "Incontro in Uni";
        }
        lblPreferenze.setText("Preferenze consegna: " + (pref.isBlank() ? "-" : pref));

        setFotoProfiloDefaultBySesso(studente, 120);
    }

    /**
     * Imposta l'indirizzo (o una descrizione) da mostrare nel riquadro info del profilo.
     * La logica di scelta (es. indirizzo principale / primo indirizzo salvato) la decide il Controller.
     */
    public void setIndirizzoInfo(String indirizzo) {
        if (indirizzo == null || indirizzo.trim().isEmpty()) {
            lblIndirizzo.setText("Indirizzo: -");
        } else {
            lblIndirizzo.setText("Indirizzo: " + indirizzo);
        }
    }

    /**
     * Usa SEMPRE la foto di default in base al sesso
     */
    private void setFotoProfiloDefaultBySesso(Studente s, int sizePx) {
        String defaultPath;
        if (s != null && s.getSesso() != null) {
            switch (s.getSesso()) {
                case F:
                    defaultPath = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileF.jpg";
                    break;
                case M:
                case Altro:
                default:
                    defaultPath = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileM.jpg";
                    break;
            }
        } else {
            defaultPath = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileM.jpg";
        }

        ImageIcon def = new ImageIcon(defaultPath);
        Image scaled = def.getImage().getScaledInstance(sizePx, sizePx, Image.SCALE_SMOOTH);
        lblFotoProfilo.setIcon(new ImageIcon(scaled));
    }

    // ====== Annunci: gestione lista + preview ======

    public void setMyAnnunci(List<Annuncio> annunci) {
        if (annunci == null) {
            this.mieiAnnunci = new ArrayList<>();
        } else {
            this.mieiAnnunci = new ArrayList<>(annunci);
        }
        this.shownAnnunciCount = Math.min(3, this.mieiAnnunci.size());
        refreshAnnunciPreview();
    }

    public void showMyAnnunciPreview(List<Annuncio> annunci) {
        setMyAnnunci(annunci);
    }

    private void refreshAnnunciPreview() {
        annunciPreviewPanel.removeAll();

        if (mieiAnnunci.isEmpty()) {
            annunciPreviewPanel.setLayout(new BorderLayout());
            JLabel lblVuoto = new JLabel("Nessun annuncio trovato", SwingConstants.CENTER);
            lblVuoto.setBorder(new EmptyBorder(30, 10, 10, 10));
            annunciPreviewPanel.add(lblVuoto, BorderLayout.NORTH);
            btnVediTuttiAnnunci.setEnabled(false);
        } else {
            annunciPreviewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));

            int max = Math.min(shownAnnunciCount, mieiAnnunci.size());
            for (int i = 0; i < max; i++) {
                Annuncio a = mieiAnnunci.get(i);
                annunciPreviewPanel.add(createMyAnnuncioCard(a));
            }

            btnVediTuttiAnnunci.setEnabled(shownAnnunciCount < mieiAnnunci.size());
        }

        annunciPreviewPanel.revalidate();
        annunciPreviewPanel.repaint();
    }

    private JPanel createMyAnnuncioCard(Annuncio annuncio) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // FOTO
        JLabel lblFoto = new JLabel();
        lblFoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblFoto.setBorder(new EmptyBorder(4, 8, 4, 8));

        ImageIcon rawIcon = getDefaultIconForCategoria(annuncio.getCategoria());
        Image scaled = rawIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        lblFoto.setIcon(new ImageIcon(scaled));

        // opzionale: altezza fissa dell’area foto (così non “mangia” spazio)
        lblFoto.setPreferredSize(new Dimension(CARD_W, 112));
        
        card.add(lblFoto, BorderLayout.NORTH);

        // INFO
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(2, 8, 2, 8));
        infoPanel.setOpaque(false);

        JLabel lblTitolo = new JLabel(annuncio.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD));
        infoPanel.add(lblTitolo);

        JLabel lblTipo = new JLabel("Tipo: " + annuncio.getTipologia());
        infoPanel.add(lblTipo);

        BigDecimal prezzo = annuncio.getPrezzo();
        if (prezzo != null) {
            JLabel lblPrezzo = new JLabel("Prezzo: " + prezzo.toPlainString() + " €");
            lblPrezzo.setForeground(new Color(0, 128, 0));
            infoPanel.add(lblPrezzo);
        }

        card.add(infoPanel, BorderLayout.CENTER);

        // PULSANTI AZIONE (MODIFICA / ELIMINA)
        JPanel actions = new JPanel(new GridLayout(1, 2, 8, 0));
        actions.setBorder(new EmptyBorder(2, 8, 6, 8));
        actions.setOpaque(false);
        
        // altezza fissa per non rubare spazio al centro
        actions.setPreferredSize(new Dimension(CARD_W, 34));

        JButton btnModifica = new JButton("Modifica");
        btnModifica.addActionListener(e -> {
            if (annuncioEditListener != null) {
                annuncioEditListener.onModificaAnnuncio(annuncio);
            }
        });

        JButton btnElimina = new JButton("Elimina");
        btnElimina.addActionListener(e -> {
            if (annuncioDeleteListener == null) return;

            int res = JOptionPane.showConfirmDialog(
                    ProfileView.this,
                    "Vuoi eliminare questo annuncio?\nL'operazione non è reversibile.",
                    "Conferma eliminazione",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (res == JOptionPane.YES_OPTION) {
                annuncioDeleteListener.onEliminaAnnuncio(annuncio);
            }
        });

        actions.add(btnModifica);
        actions.add(btnElimina);

        card.add(actions, BorderLayout.SOUTH);

        // CLICK SULLA CARD -> DETTAGLIO
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (annuncioClickListener != null) {
                    annuncioClickListener.onAnnuncioClick(annuncio);
                }
            }
        });

        return card;
    }

    private ImageIcon getDefaultIconForCategoria(String categoria) {
        String path;
        switch (categoria) {
            case "Strumenti_musicali":
                path = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/categories/strumenti.png";
                break;
            case "Libri":
                path = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/categories/libri.jpg";
                break;
            case "Informatica":
                path = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/categories/informatica.jpg";
                break;
            case "Abbigliamento":
                path = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/categories/abbigliamento.jpg";
                break;
            case "Arredo":
                path = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/categories/arredo.jpg";
                break;
            default:
                path = "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/categories/altro.jpg";
                break;
        }
        return new ImageIcon(path);
    }

    // ====== RECENSIONI ======

    public static class RecensioneCardData {
        private final String titolo;
        private final String corpo;
        private final int valutazione;
        private final String dataTransazione;
        private final String nomeAutore;
        private final String cognomeAutore;
        private final String matricolaAutore;

        public RecensioneCardData(String titolo,
                                  String corpo,
                                  int valutazione,
                                  String dataTransazione,
                                  String nomeAutore,
                                  String cognomeAutore,
                                  String matricolaAutore) {
            this.titolo = titolo;
            this.corpo = corpo;
            this.valutazione = valutazione;
            this.dataTransazione = dataTransazione;
            this.nomeAutore = nomeAutore;
            this.cognomeAutore = cognomeAutore;
            this.matricolaAutore = matricolaAutore;
        }

        public String getTitolo() { return titolo; }
        public String getCorpo() { return corpo; }
        public int getValutazione() { return valutazione; }
        public String getDataTransazione() { return dataTransazione; }
        public String getNomeAutore() { return nomeAutore; }
        public String getCognomeAutore() { return cognomeAutore; }
        public String getMatricolaAutore() { return matricolaAutore; }
    }

    public void setRecensioni(List<RecensioneCardData> recensioni) {
        if (recensioni == null) {
            this.mieRecensioni = new ArrayList<>();
        } else {
            this.mieRecensioni = new ArrayList<>(recensioni);
        }
        this.shownRecensioniCount = Math.min(3, this.mieRecensioni.size());
        refreshRecensioniPreview();
    }

    private void loadMoreRecensioni(int n) {
        if (mieRecensioni.isEmpty()) return;
        this.shownRecensioniCount = Math.min(shownRecensioniCount + n, mieRecensioni.size());
        refreshRecensioniPreview();
    }

    private void refreshRecensioniPreview() {
        recensioniPreviewPanel.removeAll();

        if (mieRecensioni.isEmpty()) {
            recensioniPreviewPanel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel("Nessuna recensione trovata.", SwingConstants.CENTER);
            lbl.setBorder(new EmptyBorder(30, 10, 10, 10));
            recensioniPreviewPanel.add(lbl, BorderLayout.NORTH);
            btnVediTutteRecensioni.setEnabled(false);
        } else {
            recensioniPreviewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));

            int max = Math.min(shownRecensioniCount, mieRecensioni.size());
            for (int i = 0; i < max; i++) {
                RecensioneCardData r = mieRecensioni.get(i);
                recensioniPreviewPanel.add(createRecensioneCard(r));
            }

            btnVediTutteRecensioni.setEnabled(shownRecensioniCount < mieRecensioni.size());
        }

        recensioniPreviewPanel.revalidate();
        recensioniPreviewPanel.repaint();
    }

    private JPanel createRecensioneCard(RecensioneCardData r) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setBackground(Color.WHITE);

        Dimension fixed = new Dimension(REC_CARD_W, REC_CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP: titolo + data
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(5, 8, 5, 8));

        JLabel lblTitolo = new JLabel(r.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD));
        top.add(lblTitolo);

        JLabel lblData = new JLabel(r.getDataTransazione());
        lblData.setFont(lblData.getFont().deriveFont(Font.ITALIC, 11f));
        top.add(lblData);

        card.add(top, BorderLayout.NORTH);

        // CENTER: autore + corpo (accorciato)
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(5, 8, 5, 8));
        center.setOpaque(false);

        String autore = r.getNomeAutore() + " " + r.getCognomeAutore()
                + " (" + r.getMatricolaAutore() + ")";
        JLabel lblAutore = new JLabel(autore);
        center.add(lblAutore);

        String corpo = r.getCorpo();
        String shortBody = corpo;
        if (corpo != null && corpo.length() > 180) {
            shortBody = corpo.substring(0, 180) + "...";
        }

        JTextArea txtCorpoShort = new JTextArea(shortBody != null ? shortBody : "-");
        txtCorpoShort.setLineWrap(true);
        txtCorpoShort.setWrapStyleWord(true);
        txtCorpoShort.setEditable(false);
        txtCorpoShort.setOpaque(false);
        txtCorpoShort.setBorder(null);
        center.add(txtCorpoShort);

        card.add(center, BorderLayout.CENTER);

        // BOTTOM: valutazione con stelline
        JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        ratingPanel.add(new JLabel("Valutazione: "));

        int v = Math.max(1, Math.min(5, r.getValutazione()));
        ImageIcon star = getStarIcon();
        for (int i = 0; i < v; i++) {
            ratingPanel.add(new JLabel(star));
        }

        card.add(ratingPanel, BorderLayout.SOUTH);

        // CLICK: dialog con recensione completa
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showRecensioneDialog(r);
            }
        });

        return card;
    }

    private ImageIcon getStarIcon() {
        if (starIcon == null) {
            ImageIcon raw = new ImageIcon(STAR_PATH);
            if (raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
                Image scaled = raw.getImage().getScaledInstance(16, 16, Image.SCALE_SMOOTH);
                starIcon = new ImageIcon(scaled);
            } else {
                starIcon = raw;
            }
        }
        return starIcon;
    }

    private void showRecensioneDialog(RecensioneCardData r) {
        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this),
                "Dettaglio recensione",
                Dialog.ModalityType.APPLICATION_MODAL
        );

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(10, 10, 10, 10));
        dialog.setContentPane(content);

        JLabel lblTitle = new JLabel(r.getTitolo());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        content.add(lblTitle, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(5, 0, 5, 0));

        center.add(new JLabel("Data transazione: " + r.getDataTransazione()));
        center.add(new JLabel("Autore: " + r.getNomeAutore() + " " + r.getCognomeAutore()
                + " (" + r.getMatricolaAutore() + ")"));
        center.add(Box.createVerticalStrut(8));

        JTextArea txtFull = new JTextArea(r.getCorpo() != null ? r.getCorpo() : "-");
        txtFull.setLineWrap(true);
        txtFull.setWrapStyleWord(true);
        txtFull.setEditable(false);
        JScrollPane scr = new JScrollPane(txtFull);
        scr.setPreferredSize(new Dimension(400, 200));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        center.add(scr);

        content.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout());

        JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        starsPanel.add(new JLabel("Valutazione: "));
        int v = Math.max(1, Math.min(5, r.getValutazione()));
        ImageIcon star = getStarIcon();
        for (int i = 0; i < v; i++) {
            starsPanel.add(new JLabel(star));
        }
        south.add(starsPanel, BorderLayout.WEST);

        JButton btnClose = new JButton("Chiudi");
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.add(btnClose);
        south.add(btnPanel, BorderLayout.EAST);

        content.add(south, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Getter
    public JButton getBtnVediTuttiAnnunci() { return btnVediTuttiAnnunci; }
    public JButton getBtnVediTutteRecensioni() { return btnVediTutteRecensioni; }
    public JPanel getAnnunciPreviewPanel() { return annunciPreviewPanel; }
    public JPanel getRecensioniPreviewPanel() { return recensioniPreviewPanel; }
    public Studente getCurrentStudente() { return currentStudente; }
}
