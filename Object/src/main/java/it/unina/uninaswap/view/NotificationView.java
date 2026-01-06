package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Offerta;
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

/**
 * Schermata Notifiche: mostra offerte ricevute e inviate in stato "In_Attesa".
 * - Card compatte con anteprima (prima riga messaggio)
 * - Click sulla card -> dialog dettagli con messaggio completo + azioni
 */
public class NotificationView extends JPanel {

    // Card compatte (fisse)
    private static final int CARD_W = 420;
    private static final int CARD_H = 125;

    private JPanel ricevutePanel;
    private JPanel inviatePanel;

    private List<OffertaNotificationData> offerteRicevute = new ArrayList<>();
    private List<OffertaNotificationData> offerteInviate = new ArrayList<>();

    // Listener verso Controller
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

    private OffertaRicevutaListener ricevutaListener;
    private OffertaInviataListener inviataListener;

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

        // ======================
        // OFFERTE RICEVUTE
        // ======================
        JPanel ricevuteSection = new JPanel(new BorderLayout());
        ricevuteSection.setBorder(new TitledBorder("Offerte ricevute (in attesa)"));

        ricevutePanel = new JPanel();
        ricevutePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
        ricevutePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        ricevuteSection.add(ricevutePanel, BorderLayout.CENTER);
        mainPanel.add(ricevuteSection);
        mainPanel.add(Box.createVerticalStrut(16));

        // ======================
        // OFFERTE INVIATE
        // ======================
        JPanel inviateSection = new JPanel(new BorderLayout());
        inviateSection.setBorder(new TitledBorder("Offerte inviate (in attesa)"));

        inviatePanel = new JPanel();
        inviatePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
        inviatePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        inviateSection.add(inviatePanel, BorderLayout.CENTER);
        mainPanel.add(inviateSection);
    }

    // ===== Listener setters =====
    public void setOffertaRicevutaListener(OffertaRicevutaListener l) { this.ricevutaListener = l; }
    public void setOffertaInviataListener(OffertaInviataListener l) { this.inviataListener = l; }

    // ===== Data setters =====
    public void setOfferteRicevute(List<OffertaNotificationData> list) {
        this.offerteRicevute = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshRicevute();
    }

    public void setOfferteInviate(List<OffertaNotificationData> list) {
        this.offerteInviate = (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        refreshInviate();
    }

    private void refreshRicevute() {
        ricevutePanel.removeAll();

        if (offerteRicevute.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna offerta ricevuta in attesa.", SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(5, 5, 5, 5));
            ricevutePanel.setLayout(new BorderLayout());
            ricevutePanel.add(lbl, BorderLayout.NORTH);
        } else {
            ricevutePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
            for (OffertaNotificationData data : offerteRicevute) {
                ricevutePanel.add(createCompactCard(data, true));
            }
        }

        ricevutePanel.revalidate();
        ricevutePanel.repaint();
    }

    private void refreshInviate() {
        inviatePanel.removeAll();

        if (offerteInviate.isEmpty()) {
            JLabel lbl = new JLabel("Nessuna offerta inviata in attesa.", SwingConstants.LEFT);
            lbl.setBorder(new EmptyBorder(5, 5, 5, 5));
            inviatePanel.setLayout(new BorderLayout());
            inviatePanel.add(lbl, BorderLayout.NORTH);
        } else {
            inviatePanel.setLayout(new WrapLayout(FlowLayout.LEFT, 12, 12));
            for (OffertaNotificationData data : offerteInviate) {
                inviatePanel.add(createCompactCard(data, false));
            }
        }

        inviatePanel.revalidate();
        inviatePanel.repaint();
    }

    // ==========================
    // CARD COMPATTA (ANTEPRIMA)
    // ==========================
    private JPanel createCompactCard(OffertaNotificationData data, boolean ricevuta) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));

        Dimension fixed = new Dimension(CARD_W, CARD_H);
        card.setPreferredSize(fixed);
        card.setMinimumSize(fixed);
        card.setMaximumSize(fixed);

        // TOP: titolo + meta (tipo + data)
        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBorder(new EmptyBorder(8, 10, 0, 10));

        JLabel lblTitolo = new JLabel(data.getTitoloAnnuncio());
        lblTitolo.setFont(lblTitolo.getFont().deriveFont(Font.BOLD, 13f));
        top.add(lblTitolo);

        String tipo = (data.getTipologiaAnnuncio() == null || data.getTipologiaAnnuncio().isBlank())
                ? "-"
                : data.getTipologiaAnnuncio();

        String ruolo = ricevuta ? "Ricevuta" : "Inviata";
        JLabel lblMeta = new JLabel("Tipo: " + tipo + "  •  " + ruolo + " - " + safe(data.getDataOfferta()));
        lblMeta.setFont(lblMeta.getFont().deriveFont(Font.ITALIC, 11f));
        top.add(lblMeta);

        card.add(top, BorderLayout.NORTH);

        // CENTER: righe compatte
        JPanel center = new JPanel();
        center.setOpaque(false);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(new EmptyBorder(4, 10, 6, 10));

        String prefix = ricevuta ? "Da: " : "A: ";
        center.add(new JLabel(prefix + safe(data.getControparteDisplay())));

        // Importo (solo se presente e non "-")
        if (data.getImportoDisplay() != null) {
            String imp = data.getImportoDisplay().trim();
            if (!imp.isEmpty() && !imp.equals("-") && !imp.equals("- €")) {
                center.add(new JLabel("Importo: " + imp));
            }
        }

        // Oggetto offerto (solo se presente)
        Offerta off = data.getOfferta();
        if (off != null && off.getOggettoOfferto() != null && !off.getOggettoOfferto().trim().isEmpty()) {
            center.add(new JLabel("Oggetto offerto: " + off.getOggettoOfferto().trim()));
        }

        // Prima riga del messaggio
        String preview = buildMessagePreview(off != null ? off.getMessaggio() : null);
        if (!preview.isBlank()) {
            JLabel lblMsg = new JLabel("Msg: " + preview);
            lblMsg.setFont(lblMsg.getFont().deriveFont(11f));
            center.add(lblMsg);
        }

        card.add(center, BorderLayout.CENTER);

        // Click card -> dialog dettagli
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showOffertaDialog(data, ricevuta);
            }
        });

        return card;
    }

    private String safe(String s) {
        return (s == null) ? "-" : s;
    }

    private String buildMessagePreview(String msg) {
        if (msg == null) return "";
        String t = msg.trim();
        if (t.isEmpty()) return "";
        // prima riga
        int idx = t.indexOf('\n');
        if (idx >= 0) t = t.substring(0, idx).trim();
        // limite caratteri
        if (t.length() > 60) t = t.substring(0, 60).trim() + "...";
        return t;
    }

    // ==========================
    // DIALOG DETTAGLI
    // ==========================
    private void showOffertaDialog(OffertaNotificationData data, boolean ricevuta) {
        Offerta offerta = data.getOfferta();
        if (offerta == null) return;

        JDialog dialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                "Dettaglio offerta", Dialog.ModalityType.APPLICATION_MODAL);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        dialog.setContentPane(content);

        // titolo
        JLabel title = new JLabel(data.getTitoloAnnuncio());
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        content.add(title, BorderLayout.NORTH);

        // info
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        String tipo = (data.getTipologiaAnnuncio() == null || data.getTipologiaAnnuncio().isBlank())
                ? "-"
                : data.getTipologiaAnnuncio();

        info.add(new JLabel("Tipo annuncio: " + tipo));
        info.add(new JLabel("Data offerta: " + safe(data.getDataOfferta())));
        info.add(new JLabel((ricevuta ? "Offerente: " : "Venditore: ") + safe(data.getControparteDisplay())));

        if (data.getImportoDisplay() != null) {
            String imp = data.getImportoDisplay().trim();
            if (!imp.isEmpty() && !imp.equals("-") && !imp.equals("- €")) {
                info.add(new JLabel("Importo: " + imp));
            }
        }

        if (offerta.getOggettoOfferto() != null && !offerta.getOggettoOfferto().trim().isEmpty()) {
            info.add(new JLabel("Oggetto offerto: " + offerta.getOggettoOfferto().trim()));
        }

        info.add(Box.createVerticalStrut(8));

        JTextArea txtMsg = new JTextArea(offerta.getMessaggio() != null ? offerta.getMessaggio() : "-");
        txtMsg.setLineWrap(true);
        txtMsg.setWrapStyleWord(true);
        txtMsg.setEditable(false);
        JScrollPane scr = new JScrollPane(txtMsg);
        scr.setPreferredSize(new Dimension(520, 220));
        scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel center = new JPanel(new BorderLayout(8, 8));
        center.add(info, BorderLayout.NORTH);

        JPanel msgBox = new JPanel(new BorderLayout());
        msgBox.setBorder(BorderFactory.createTitledBorder("Messaggio"));
        msgBox.add(scr, BorderLayout.CENTER);

        center.add(msgBox, BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);

        // bottoni azione
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnVedi = new JButton("Vedi annuncio");
        btnVedi.addActionListener(e -> {
            dialog.dispose();
            if (ricevuta && ricevutaListener != null) {
                ricevutaListener.onApriAnnuncio(offerta);
            } else if (!ricevuta && inviataListener != null) {
                inviataListener.onApriAnnuncio(offerta);
            }
        });
        actions.add(btnVedi);

        if (ricevuta) {
            JButton btnAccetta = new JButton("Accetta");
            btnAccetta.addActionListener(e -> {
                dialog.dispose();
                if (ricevutaListener != null) {
                    ricevutaListener.onAccetta(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Accetta' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnAccetta);

            JButton btnRifiuta = new JButton("Rifiuta");
            btnRifiuta.addActionListener(e -> {
                dialog.dispose();
                if (ricevutaListener != null) {
                    ricevutaListener.onRifiuta(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Rifiuta' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnRifiuta);
        } else {
            JButton btnModifica = new JButton("Modifica");
            btnModifica.addActionListener(e -> {
                dialog.dispose();
                if (inviataListener != null) {
                    inviataListener.onModifica(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Modifica' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnModifica);

            JButton btnRitira = new JButton("Ritira");
            btnRitira.addActionListener(e -> {
                dialog.dispose();
                if (inviataListener != null) {
                    inviataListener.onRitira(offerta);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Azione 'Ritira' non collegata al controller.",
                            "Info",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            actions.add(btnRitira);
        }

        JButton btnChiudi = new JButton("Chiudi");
        btnChiudi.addActionListener(e -> dialog.dispose());
        actions.add(btnChiudi);

        content.add(actions, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // ==========================
    // DTO per UI
    // ==========================
    public static class OffertaNotificationData {
        private final Offerta offerta;
        private final String titoloAnnuncio;
        private final String tipologiaAnnuncio;     // <-- nuovo
        private final String controparteDisplay;
        private final String dataOfferta;
        private final String importoDisplay;

        /**
         * Costruttore nuovo (con tipologia annuncio).
         */
        public OffertaNotificationData(Offerta offerta,
                                       String titoloAnnuncio,
                                       String tipologiaAnnuncio,
                                       String controparteDisplay,
                                       String dataOfferta,
                                       String importoDisplay) {
            this.offerta = offerta;
            this.titoloAnnuncio = titoloAnnuncio;
            this.tipologiaAnnuncio = tipologiaAnnuncio;
            this.controparteDisplay = controparteDisplay;
            this.dataOfferta = dataOfferta;
            this.importoDisplay = importoDisplay;
        }

        /**
         * Costruttore compatibilità (vecchio): tipologia annuncio non fornita.
         */
        public OffertaNotificationData(Offerta offerta,
                                       String titoloAnnuncio,
                                       String controparteDisplay,
                                       String dataOfferta,
                                       String importoDisplay) {
            this(offerta, titoloAnnuncio, null, controparteDisplay, dataOfferta, importoDisplay);
        }

        public Offerta getOfferta() { return offerta; }
        public String getTitoloAnnuncio() { return titoloAnnuncio; }
        public String getTipologiaAnnuncio() { return tipologiaAnnuncio; }
        public String getControparteDisplay() { return controparteDisplay; }
        public String getDataOfferta() { return dataOfferta; }
        public String getImportoDisplay() { return importoDisplay; }
    }
}
