package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.model.entity.Studente;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AnnuncioDetailView extends JPanel {

    private static final String PROFILE_M_PATH =
            "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileM.jpg";
    private static final String PROFILE_F_PATH =
            "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/profileF.jpg";

    private JButton btnBack;

    private JLabel lblMainPhoto;
    private JPanel thumbnailsPanel;

    private JLabel lblTitolo;
    private JLabel lblPrezzo;
    private JLabel lblDataPubblicazione;
    private JLabel lblTipologia;
    private JLabel lblCategoria;
    private JLabel lblOggettoRichiesto;
    private JLabel lblConsegna;

    private JTextArea txtDescrizione;

    private JLabel lblVenditoreFoto;
    private JLabel lblVenditoreNome;
    private JLabel lblVenditoreEmail;
    private JLabel lblVenditoreMatricola;
    private JButton btnVediProfiloVenditore;
    
    public enum OffertaAction { ACQUISTA, FAI_OFFERTA, RICHIEDI_REGALO, PROPONI_SCAMBIO }

    public interface AnnuncioOffertaListener {
        void onOffertaAction(Annuncio annuncio, OffertaAction action);
    }

    private AnnuncioOffertaListener annuncioOffertaListener;
    public void setAnnuncioOffertaListener(AnnuncioOffertaListener l) { this.annuncioOffertaListener = l; }

    private JPanel azioniPanel;
    private JButton btnAcquista;
    private JButton btnFaiOfferta;
    private JButton btnRichiediRegalo;
    private JButton btnProponiScambio;


    private Annuncio currentAnnuncio;
    private Studente currentVenditore;

    public AnnuncioDetailView() {
        setLayout(new BorderLayout());

        // TOP BAR
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(10, 10, 10, 10));

        btnBack = new JButton("← Indietro");
        topBar.add(btnBack, BorderLayout.WEST);

        JLabel titoloPagina = new JLabel("Dettaglio annuncio");
        titoloPagina.setFont(titoloPagina.getFont().deriveFont(Font.BOLD, 16f));
        topBar.add(titoloPagina, BorderLayout.CENTER);

        add(topBar, BorderLayout.NORTH);

        // AREA SCROLLABILE
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(10, 18, 18, 18));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        // ======================
        // SEZIONE FOTO
        // ======================
        JPanel fotoSection = new JPanel(new BorderLayout());
        fotoSection.setBorder(new TitledBorder("Foto"));

        lblMainPhoto = new JLabel();
        lblMainPhoto.setHorizontalAlignment(SwingConstants.CENTER);
        lblMainPhoto.setBorder(new EmptyBorder(10, 10, 10, 10));

        ImageIcon def = new ImageIcon("/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/annunci/default_annuncio.png");
        Image scaled = def.getImage().getScaledInstance(320, 240, Image.SCALE_SMOOTH);
        lblMainPhoto.setIcon(new ImageIcon(scaled));

        fotoSection.add(lblMainPhoto, BorderLayout.CENTER);

        thumbnailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        fotoSection.add(thumbnailsPanel, BorderLayout.SOUTH);

        content.add(fotoSection);
        content.add(Box.createVerticalStrut(12));

        // ======================
        // SEZIONE DETTAGLI + DESCRIZIONE
        // ======================
        JPanel infoSection = new JPanel(new BorderLayout());
        infoSection.setBorder(new TitledBorder("Dettagli annuncio"));

        JPanel infoLeft = new JPanel(new GridLayout(0, 1, 0, 6));
        infoLeft.setBorder(new EmptyBorder(8, 8, 8, 8));

        lblTitolo = new JLabel("Titolo: -");
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 14f));

        lblPrezzo = new JLabel("Prezzo: -");
        lblDataPubblicazione = new JLabel("Data pubblicazione: -");
        lblTipologia = new JLabel("Tipologia: -");
        lblCategoria = new JLabel("Categoria: -");
        lblOggettoRichiesto = new JLabel("Oggetto richiesto: -");
        lblConsegna = new JLabel("Consegna: -");

        infoLeft.add(lblTitolo);
        infoLeft.add(lblPrezzo);
        infoLeft.add(lblDataPubblicazione);
        infoLeft.add(lblTipologia);
        infoLeft.add(lblCategoria);
        infoLeft.add(lblOggettoRichiesto);
        infoLeft.add(lblConsegna);

        infoSection.add(infoLeft, BorderLayout.WEST);

        // riquadro descrizione
        JPanel descrPanel = new JPanel(new BorderLayout());
        descrPanel.setBorder(new TitledBorder("Descrizione"));

        txtDescrizione = new JTextArea(6, 25);
        txtDescrizione.setLineWrap(true);
        txtDescrizione.setWrapStyleWord(true);
        txtDescrizione.setEditable(false);

        JScrollPane scrDesc = new JScrollPane(txtDescrizione);
        scrDesc.setBorder(null);
        scrDesc.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        descrPanel.add(scrDesc, BorderLayout.CENTER);

        infoSection.add(descrPanel, BorderLayout.CENTER);

        content.add(infoSection);
        content.add(Box.createVerticalStrut(12));

        // ======================
        // SEZIONE VENDITORE
        // ======================
        JPanel venditoreSection = new JPanel(new BorderLayout());
        venditoreSection.setBorder(new TitledBorder("Venditore"));

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(new EmptyBorder(10, 10, 10, 10));

        lblVenditoreFoto = new JLabel();
        lblVenditoreFoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        setVenditoreFotoDefault(null); // default iniziale

        left.add(lblVenditoreFoto);
        left.add(Box.createVerticalStrut(8));

        lblVenditoreNome = new JLabel("Nome: -");
        lblVenditoreEmail = new JLabel("Email: -");
        lblVenditoreMatricola = new JLabel("Matricola: -");

        left.add(lblVenditoreNome);
        left.add(lblVenditoreEmail);
        left.add(lblVenditoreMatricola);

        venditoreSection.add(left, BorderLayout.CENTER);

        btnVediProfiloVenditore = new JButton("Vedi profilo venditore");
        venditoreSection.add(btnVediProfiloVenditore, BorderLayout.SOUTH);

        content.add(venditoreSection);
        
     // ======================
     // SEZIONE AZIONI (offerte)
     // ======================
     azioniPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
     azioniPanel.setBorder(new TitledBorder("Azioni"));

     btnAcquista = new JButton("Acquista");
     btnFaiOfferta = new JButton("Fai offerta");
     btnRichiediRegalo = new JButton("Richiedi");
     btnProponiScambio = new JButton("Proponi scambio");

     btnAcquista.addActionListener(e -> {
         if (annuncioOffertaListener != null && currentAnnuncio != null)
             annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.ACQUISTA);
     });
     btnFaiOfferta.addActionListener(e -> {
         if (annuncioOffertaListener != null && currentAnnuncio != null)
             annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.FAI_OFFERTA);
     });
     btnRichiediRegalo.addActionListener(e -> {
         if (annuncioOffertaListener != null && currentAnnuncio != null)
             annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.RICHIEDI_REGALO);
     });
     btnProponiScambio.addActionListener(e -> {
         if (annuncioOffertaListener != null && currentAnnuncio != null)
             annuncioOffertaListener.onOffertaAction(currentAnnuncio, OffertaAction.PROPONI_SCAMBIO);
     });

     azioniPanel.add(btnAcquista);
     azioniPanel.add(btnFaiOfferta);
     azioniPanel.add(btnRichiediRegalo);
     azioniPanel.add(btnProponiScambio);

     content.add(azioniPanel);

    }

    public void setData(Annuncio annuncio, Studente venditore, List<ImageIcon> foto) {
        this.currentAnnuncio = annuncio;
        this.currentVenditore = venditore;

        // FOTO ANNUNCIO
        thumbnailsPanel.removeAll();
        if (foto != null && !foto.isEmpty()) {
            setMainPhoto(foto.get(0));
            for (ImageIcon icon : foto) {
                JLabel thumb = new JLabel();
                Image t = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                thumb.setIcon(new ImageIcon(t));
                thumb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                thumb.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        setMainPhoto(icon);
                    }
                });
                thumbnailsPanel.add(thumb);
            }
        } else {
            ImageIcon rawIcon = getDefaultIconForCategoria(annuncio.getCategoria());
            Image scaled = rawIcon.getImage().getScaledInstance(320, 240, Image.SCALE_SMOOTH);
            lblMainPhoto.setIcon(new ImageIcon(scaled));
        }
        thumbnailsPanel.revalidate();
        thumbnailsPanel.repaint();

        // INFO ANNUNCIO
        lblTitolo.setText("Titolo: " + safe(annuncio.getTitolo()));
        lblTipologia.setText("Tipologia: " + safe(annuncio.getTipologia()));
        lblCategoria.setText("Categoria: " + safe(annuncio.getCategoria()));

        LocalDate data = annuncio.getDataPubblicazione();
        lblDataPubblicazione.setText("Data pubblicazione: " + (data != null ? data.toString() : "-"));

        BigDecimal prezzo = annuncio.getPrezzo();
        lblPrezzo.setText(prezzo != null ? "Prezzo: " + prezzo.toPlainString() + " €" : "Prezzo: -");

        String ogg = annuncio.getOggettoRichiesto();
        lblOggettoRichiesto.setText("Oggetto richiesto: " + (ogg == null || ogg.isBlank() ? "-" : ogg));

        lblConsegna.setText("Consegna: " +
                buildConsegnaText(annuncio.isOffreSpedizione(), annuncio.isOffreIncontroInUni()));

        // DESCRIZIONE
        String descr = annuncio.getDescrizione();
        if (descr == null || descr.isBlank()) {
            txtDescrizione.setText("Nessuna descrizione fornita.");
        } else {
            txtDescrizione.setText(descr);
        }
        txtDescrizione.setCaretPosition(0);

        // INFO VENDITORE + FOTO default
        if (venditore != null) {
            lblVenditoreNome.setText("Nome: " + venditore.getNome() + " " + venditore.getCognome());
            lblVenditoreEmail.setText("Email: " + venditore.getEmail());
            lblVenditoreMatricola.setText("Matricola: " + venditore.getMatricola());
            setVenditoreFotoDefault(venditore);
        } else {
            lblVenditoreNome.setText("Nome: -");
            lblVenditoreEmail.setText("Email: -");
            lblVenditoreMatricola.setText("Matricola: -");
            setVenditoreFotoDefault(null);
        }
    }

    private void setMainPhoto(ImageIcon icon) {
        Image scaled = icon.getImage().getScaledInstance(320, 240, Image.SCALE_SMOOTH);
        lblMainPhoto.setIcon(new ImageIcon(scaled));
    }

    private String buildConsegnaText(boolean sped, boolean uni) {
        if (sped && uni) return "Spedizione + Incontro in Uni";
        if (sped) return "Spedizione";
        if (uni) return "Incontro in Uni";
        return "-";
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }

    private void setVenditoreFotoDefault(Studente s) {
        String path;
        if (s != null && s.getSesso() != null) {
            switch (s.getSesso()) {
                case F:
                    path = PROFILE_F_PATH;
                    break;
                case M:
                case Altro:
                default:
                    path = PROFILE_M_PATH;
                    break;
            }
        } else {
            path = PROFILE_M_PATH;
        }

        ImageIcon def = new ImageIcon(path);
        Image scaled = def.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        lblVenditoreFoto.setIcon(new ImageIcon(scaled));
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

    public void updateAzioniForLoggedIn(String matricolaLoggedIn) {
        if (currentAnnuncio == null || azioniPanel == null) return;

        boolean isMine = (matricolaLoggedIn != null
                && currentAnnuncio.getMatricolaVenditore() != null
                && matricolaLoggedIn.equals(currentAnnuncio.getMatricolaVenditore()));

        azioniPanel.setVisible(!isMine);
        if (isMine) return;

        // nascondo tutto e poi mostro solo quello giusto
        btnAcquista.setVisible(false);
        btnFaiOfferta.setVisible(false);
        btnRichiediRegalo.setVisible(false);
        btnProponiScambio.setVisible(false);

        String tip = currentAnnuncio.getTipologia();

        if ("Vendita".equals(tip)) {
            // se prezzo nullo non ha senso "Acquista"
            btnAcquista.setVisible(currentAnnuncio.getPrezzo() != null);
            btnFaiOfferta.setVisible(true);
        } else if ("Regalo".equals(tip)) {
            btnRichiediRegalo.setVisible(true);
        } else { // Scambio
            btnProponiScambio.setVisible(true);
        }

        azioniPanel.revalidate();
        azioniPanel.repaint();
    }

    
    // Getter per controller
    public JButton getBtnBack() { return btnBack; }
    public JButton getBtnVediProfiloVenditore() { return btnVediProfiloVenditore; }
    public Annuncio getCurrentAnnuncio() { return currentAnnuncio; }
    public Studente getCurrentVenditore() { return currentVenditore; }
}
