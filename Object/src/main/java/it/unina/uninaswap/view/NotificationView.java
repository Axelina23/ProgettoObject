package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.model.entity.Transazione;
import it.unina.uninaswap.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class NotificationView extends JPanel {

    private static final int CARD_W = 420;
    private static final int CARD_H = 125;

    private JPanel ricevutePanel;
    private JPanel inviatePanel;
    private JPanel acquistiPanel; // NUOVO

    private List<OffertaNotificationData> offerteRicevute = new ArrayList<>();
    private List<OffertaNotificationData> offerteInviate = new ArrayList<>();
    private List<TransazioneToReviewData> acquistiDaRecensire = new ArrayList<>(); // NUOVO

    public interface OffertaRicevutaListener {
        void onAccetta(Offerta offerta);
        void onRifiuta(Offerta offerta);
        void onApriAnnuncio(Offerta offerta);
    }

    public interface OffertaInviataListener {
        void onModifica(Offerta offerta);
        void onRitira(Offerta offerta);
        void onApriAnnuncio(Offerta offerta);
    }
    
    // NUOVO LISTENER
    public interface RecensioneListener {
        void onLasciaRecensione(Transazione transazione, TransazioneToReviewData cardData);
    }

    private OffertaRicevutaListener ricevutaListener;
    private OffertaInviataListener inviataListener;
    private RecensioneListener recensioneListener;

    public NotificationView() {
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // 1. OFFERTE RICEVUTE
        ricevutePanel = createSection(mainPanel, "Offerte ricevute (in attesa)");
        mainPanel.add(Box.createVerticalStrut(16));

        // 2. OFFERTE INVIATE
        inviatePanel = createSection(mainPanel, "Offerte inviate (in attesa)");
        mainPanel.add(Box.createVerticalStrut(16));
        
        // 3. ACQUISTI DA RECENSIRE (NUOVO)
        acquistiPanel = createSection(mainPanel, "Acquisti conclusi (Da recensire)");
    }

    private JPanel createSection(JPanel parent, String title) {
        JPanel section = new JPanel(new BorderLayout());
        section.setBorder(new TitledBorder(title));

        JPanel inner = new JPanel();
        inner.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
        inner.setBorder(new EmptyBorder(10, 10, 10, 10));

        section.add(inner, BorderLayout.CENTER);
        parent.add(section);
        return inner;
    }

    // ===== Setters =====
    public void setOffertaRicevutaListener(OffertaRicevutaListener l) { this.ricevutaListener = l; }
    public void setOffertaInviataListener(OffertaInviataListener l) { this.inviataListener = l; }
    public void setRecensioneListener(RecensioneListener l) { this.recensioneListener = l; }

    public void setOfferteRicevute(List<OffertaNotificationData> list) {
        this.offerteRicevute = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshOffertePanel(ricevutePanel, offerteRicevute, true);
    }

    public void setOfferteInviate(List<OffertaNotificationData> list) {
        this.offerteInviate = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshOffertePanel(inviatePanel, offerteInviate, false);
    }
    
    public void setAcquistiDaRecensire(List<TransazioneToReviewData> list) {
        this.acquistiDaRecensire = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshAcquistiPanel();
    }

    // ===== Refresh Methods =====
    private void refreshOffertePanel(JPanel panel, List<OffertaNotificationData> list, boolean ricevuta) {
        panel.removeAll();
        if (list.isEmpty()) {
            panel.add(new JLabel("Nessuna offerta in questa sezione."));
        } else {
            for (OffertaNotificationData data : list) {
                panel.add(createCompactCard(data, ricevuta));
            }
        }
        panel.revalidate();
        panel.repaint();
    }
    
    private void refreshAcquistiPanel() {
        acquistiPanel.removeAll();
        if (acquistiDaRecensire.isEmpty()) {
            acquistiPanel.add(new JLabel("Nessun acquisto da recensire."));
        } else {
            for (TransazioneToReviewData data : acquistiDaRecensire) {
                acquistiPanel.add(createAcquistoCard(data));
            }
        }
        acquistiPanel.revalidate();
        acquistiPanel.repaint();
    }

    // ==========================
    // CARD ACQUISTO (NUOVO)
    // ==========================
    private JPanel createAcquistoCard(TransazioneToReviewData data) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(new Color(245, 255, 250)); // Colore diverso per distinguere
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setPreferredSize(new Dimension(CARD_W, 140)); // Un po' più alta per i bottoni

        // Info
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(10, 10, 5, 10));

        JLabel lblTitolo = new JLabel(data.titoloAnnuncio);
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 13f));
        center.add(lblTitolo);
        
        center.add(new JLabel("Venditore: " + data.nomeVenditore));
        center.add(new JLabel("Data: " + data.dataTransazione + "  •  Importo: " + data.importo));
        
        card.add(center, BorderLayout.CENTER);

        // Azioni
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        
        if (!data.recensito) {
            JButton btn = new JButton("Lascia Recensione");
            btn.setBackground(new Color(100, 149, 237));
            btn.setForeground(Color.WHITE);
            btn.addActionListener(e -> {
                if (recensioneListener != null) recensioneListener.onLasciaRecensione(data.transazione, data);
            });
            bottom.add(btn);
        } else {
            JLabel lbl = new JLabel("✓ Recensito!");
            lbl.setForeground(new Color(34, 139, 34));
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            bottom.add(lbl);
            
            JButton btnDel = new JButton("Cancella");
            btnDel.addActionListener(e -> {
                acquistiDaRecensire.remove(data);
                refreshAcquistiPanel();
            });
            bottom.add(btnDel);
        }

        card.add(bottom, BorderLayout.SOUTH);
        return card;
    }

    // ==========================
    // CARD OFFERTA (Tuo codice precedente)
    // ==========================
    private JPanel createCompactCard(OffertaNotificationData data, boolean ricevuta) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
        card.setPreferredSize(new Dimension(CARD_W, CARD_H));
        
        // TOP
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(8, 10, 0, 10));

        JLabel lblTitolo = new JLabel(data.getTitoloAnnuncio());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 13f));
        top.add(lblTitolo);
        
        String tipo = (data.getTipologiaAnnuncio() == null) ? "-" : data.getTipologiaAnnuncio();
        String ruolo = ricevuta ? "Ricevuta" : "Inviata";
        JLabel lblMeta = new JLabel("Tipo: " + tipo + " • " + ruolo + " - " + safe(data.getDataOfferta()));
        lblMeta.setFont(lblMeta.getFont().deriveFont(Font.ITALIC, 11f));
        top.add(lblMeta);
        card.add(top, BorderLayout.NORTH);

        // CENTER
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(4, 10, 6, 10));
        
        String prefix = ricevuta ? "Da: " : "A: ";
        center.add(new JLabel(prefix + safe(data.getControparteDisplay())));
        
        if (data.getImportoDisplay() != null && !data.getImportoDisplay().equals("-")) {
            center.add(new JLabel("Importo: " + data.getImportoDisplay()));
        }
        
        Offerta off = data.getOfferta();
        if (off != null && off.getOggettoOfferto() != null) {
            center.add(new JLabel("Oggetto: " + off.getOggettoOfferto()));
        }
        
        card.add(center, BorderLayout.CENTER);

        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { showOffertaDialog(data, ricevuta); }
        });

        return card;
    }

    private String safe(String s) { return (s == null) ? "-" : s; }

    // ... (Inserisci qui il tuo metodo showOffertaDialog originale completo che avevi già) ...
    private void showOffertaDialog(OffertaNotificationData data, boolean ricevuta) {
       // ... codice del dialog dettaglio offerta (copialo dal file originale per brevità) ...
       // È quello con i bottoni Accetta/Rifiuta/Modifica/Ritira
       // Assumo che tu lo abbia mantenuto.
        Offerta offerta = data.getOfferta();
        if (offerta == null) return;
        
        // Esempio molto ridotto per far compilare se copi-incolli tutto, ma usa il tuo completo!
        JOptionPane.showMessageDialog(this, "Dettaglio offerta: " + offerta.getMessaggio());
    }

    // ===== DTO =====
    public static class OffertaNotificationData {
        private final Offerta offerta;
        private final String titoloAnnuncio;
        private final String tipologiaAnnuncio;
        private final String controparteDisplay;
        private final String dataOfferta;
        private final String importoDisplay;

        public OffertaNotificationData(Offerta o, String t, String type, String c, String d, String i) {
            this.offerta = o; this.titoloAnnuncio = t; this.tipologiaAnnuncio = type;
            this.controparteDisplay = c; this.dataOfferta = d; this.importoDisplay = i;
        }
        public Offerta getOfferta() { return offerta; }
        public String getTitoloAnnuncio() { return titoloAnnuncio; }
        public String getTipologiaAnnuncio() { return tipologiaAnnuncio; }
        public String getControparteDisplay() { return controparteDisplay; }
        public String getDataOfferta() { return dataOfferta; }
        public String getImportoDisplay() { return importoDisplay; }
    }
    
    // NUOVO DTO PER RECENSIONI
    public static class TransazioneToReviewData {
        public Transazione transazione;
        public String titoloAnnuncio;
        public String nomeVenditore;
        public String dataTransazione;
        public String importo;
        public boolean recensito = false; 

        public TransazioneToReviewData(Transazione t, String tit, String vend, String data, String imp) {
            this.transazione = t; this.titoloAnnuncio = tit; this.nomeVenditore = vend;
            this.dataTransazione = data; this.importo = imp;
        }
    }
}