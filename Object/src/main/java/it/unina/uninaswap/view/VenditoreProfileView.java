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

public class VenditoreProfileView extends JPanel {

    // ===== stile card annunci (come ProfileView) =====
    private static final int CARD_W = 260;
    private static final int CARD_H = 220;
    private static final int HGAP = 10;
    private static final int VGAP = 10;

    private JButton btnBack;

    private JLabel lblFotoProfilo;
    private JLabel lblNomeCompleto;
    private JLabel lblEmail;
    private JLabel lblMatricola;
    private JLabel lblPreferenze;

    private JPanel annunciPreviewPanel;
    private JButton btnVediPiuAnnunci;

    private Studente currentVenditore;

    // stato annunci
    private List<Annuncio> annunciVenditore = new ArrayList<>();
    private int shownAnnunciCount = 0;

    // listener click su annuncio
    public interface AnnuncioClickListener {
        void onAnnuncioClick(Annuncio annuncio);
    }

    private AnnuncioClickListener annuncioClickListener;

    public void setAnnuncioClickListener(AnnuncioClickListener l) {
        this.annuncioClickListener = l;
    }

    public VenditoreProfileView() {
        setLayout(new BorderLayout());

        // ===== TOP BAR =====
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(10, 12, 10, 12));

        btnBack = new JButton("← Indietro");
        top.add(btnBack, BorderLayout.WEST);

        JLabel title = new JLabel("Profilo venditore", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        top.add(title, BorderLayout.CENTER);

        top.add(Box.createHorizontalStrut(80), BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // ===== CONTENUTO SCROLLABILE =====
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);

        // FOTO
        lblFotoProfilo = new JLabel();
        lblFotoProfilo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblFotoProfilo);
        mainPanel.add(Box.createVerticalStrut(14));

        // titolo info
        JLabel lblTitoloInfo = new JLabel("Informazioni venditore");
        lblTitoloInfo.setFont(lblTitoloInfo.getFont().deriveFont(Font.BOLD, 18f));
        lblTitoloInfo.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(lblTitoloInfo);
        mainPanel.add(Box.createVerticalStrut(10));

        // info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 0, 6));
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        lblNomeCompleto = new JLabel("Nome: -");
        lblEmail        = new JLabel("Email: -");
        lblMatricola    = new JLabel("Matricola: -");
        lblPreferenze   = new JLabel("Preferenze consegna: -");

        infoPanel.add(lblNomeCompleto);
        infoPanel.add(lblEmail);
        infoPanel.add(lblMatricola);
        infoPanel.add(lblPreferenze);

        mainPanel.add(infoPanel);
        mainPanel.add(Box.createVerticalStrut(18));

        // ===== SEZIONE ANNUNCI VENDITORE =====
        JPanel annunciSection = new JPanel(new BorderLayout());
        annunciSection.setBorder(new TitledBorder("Annunci del venditore"));

        annunciPreviewPanel = new JPanel();
        annunciPreviewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));
        annunciPreviewPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        annunciSection.add(annunciPreviewPanel, BorderLayout.CENTER);

        btnVediPiuAnnunci = new JButton("Vedi più annunci");
        btnVediPiuAnnunci.setEnabled(false);
        annunciSection.add(btnVediPiuAnnunci, BorderLayout.SOUTH);

        mainPanel.add(annunciSection);

        // listener "vedi più"
        btnVediPiuAnnunci.addActionListener(e -> {
            if (annunciVenditore.isEmpty()) return;
            shownAnnunciCount = Math.min(shownAnnunciCount + 6, annunciVenditore.size());
            refreshAnnunciPreview();
        });

        // stato iniziale
        setVenditore(null);
        setAnnunciVenditore(null);
    }

    // ===== API controller =====

    public void setVenditore(Studente venditore) {
        this.currentVenditore = venditore;

        if (venditore == null) {
            lblNomeCompleto.setText("Nome: -");
            lblEmail.setText("Email: -");
            lblMatricola.setText("Matricola: -");
            lblPreferenze.setText("Preferenze consegna: -");
            setFotoProfiloDefaultBySesso(null, 120);
            return;
        }

        lblNomeCompleto.setText("Nome: " + venditore.getNome() + " " + venditore.getCognome());
        lblEmail.setText("Email: " + venditore.getEmail());
        lblMatricola.setText("Matricola: " + venditore.getMatricola());

        String pref = "";
        if (venditore.getPreferisceSpedizione()) pref += "Spedizione";
        if (venditore.getPreferisceIncontroInUni()) {
            if (!pref.isEmpty()) pref += " - ";
            pref += "Incontro in Uni";
        }
        lblPreferenze.setText("Preferenze consegna: " + (pref.isBlank() ? "-" : pref));

        setFotoProfiloDefaultBySesso(venditore, 120);
    }

    public void setAnnunciVenditore(List<Annuncio> annunci) {
        if (annunci == null) this.annunciVenditore = new ArrayList<>();
        else this.annunciVenditore = new ArrayList<>(annunci);

        this.shownAnnunciCount = Math.min(3, this.annunciVenditore.size());
        refreshAnnunciPreview();
    }

    private void refreshAnnunciPreview() {
        annunciPreviewPanel.removeAll();

        if (annunciVenditore.isEmpty()) {
            annunciPreviewPanel.setLayout(new BorderLayout());
            JLabel lbl = new JLabel("Nessun annuncio trovato.", SwingConstants.CENTER);
            lbl.setBorder(new EmptyBorder(25, 10, 10, 10));
            annunciPreviewPanel.add(lbl, BorderLayout.NORTH);
            btnVediPiuAnnunci.setEnabled(false);
        } else {
            annunciPreviewPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));

            int max = Math.min(shownAnnunciCount, annunciVenditore.size());
            for (int i = 0; i < max; i++) {
                annunciPreviewPanel.add(createAnnuncioCard(annunciVenditore.get(i)));
            }

            btnVediPiuAnnunci.setEnabled(shownAnnunciCount < annunciVenditore.size());
        }

        annunciPreviewPanel.revalidate();
        annunciPreviewPanel.repaint();
    }

    private JPanel createAnnuncioCard(Annuncio annuncio) {
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
        lblFoto.setBorder(new EmptyBorder(8, 8, 6, 8));

        ImageIcon rawIcon = getDefaultIconForCategoria(annuncio.getCategoria());
        Image scaled = rawIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        lblFoto.setIcon(new ImageIcon(scaled));
        card.add(lblFoto, BorderLayout.NORTH);

        // INFO
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setBorder(new EmptyBorder(4, 8, 8, 8));
        info.setOpaque(false);

        JLabel lblTitolo = new JLabel(annuncio.getTitolo());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD));
        info.add(lblTitolo);

        JLabel lblTipo = new JLabel("Tipo: " + annuncio.getTipologia());
        info.add(lblTipo);

        BigDecimal prezzo = annuncio.getPrezzo();
        if (prezzo != null) {
            JLabel lblPrezzo = new JLabel("Prezzo: " + prezzo.toPlainString() + " €");
            lblPrezzo.setForeground(new Color(0, 128, 0));
            info.add(lblPrezzo);
        }

        card.add(info, BorderLayout.CENTER);

        // CLICK -> dettaglio
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (annuncioClickListener != null) annuncioClickListener.onAnnuncioClick(annuncio);
            }
        });

        return card;
    }

    private ImageIcon getDefaultIconForCategoria(String categoria) {
        String path;
        if (categoria == null) categoria = "Altro";

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

    // ===== getter utili =====
    public JButton getBtnBack() { return btnBack; }
    public Studente getCurrentVenditore() { return currentVenditore; }
}
