package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Annuncio;
import it.unina.uninaswap.util.WrapLayout;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

public class AnnunciListView extends JPanel {

    private static final int CARD_W = 260;
    private static final int CARD_H = 220;
    private static final int HGAP = 10;
    private static final int VGAP = 10;

    // pannello che contiene tutte le card
    private JPanel cardsPanel;
    private JScrollPane scrollPane;

    // listener click su annuncio
    public interface AnnuncioClickListener {
        void onAnnuncioClick(Annuncio annuncio);
    }

    private AnnuncioClickListener annuncioClickListener;

    public enum OffertaAction { ACQUISTA, FAI_OFFERTA, RICHIEDI_REGALO, PROPONI_SCAMBIO }

    public interface AnnuncioOffertaListener {
        void onOffertaAction(Annuncio annuncio, OffertaAction action);
    }

    private AnnuncioOffertaListener annuncioOffertaListener;

    public void setAnnuncioOffertaListener(AnnuncioOffertaListener listener) {
        this.annuncioOffertaListener = listener;
    }
    
    public AnnunciListView() {
        setLayout(new BorderLayout());

        cardsPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));
        cardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // scroll più fluido

        add(scrollPane, BorderLayout.CENTER);

        showAnnunci(java.util.Collections.emptyList());
    }

    public void setAnnuncioClickListener(AnnuncioClickListener listener) {
        this.annuncioClickListener = listener;
    }

    public void showAnnunci(List<Annuncio> annunci) {
        cardsPanel.removeAll();

        if (annunci == null || annunci.isEmpty()) {
            JLabel lblVuoto = new JLabel("Nessun annuncio trovato", SwingConstants.CENTER);
            lblVuoto.setBorder(new EmptyBorder(30, 10, 10, 10));
            cardsPanel.setLayout(new BorderLayout());
            cardsPanel.add(lblVuoto, BorderLayout.NORTH);
        } else {
            // ripristino wrap layout (nel caso prima fosse diventato BorderLayout)
            cardsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, HGAP, VGAP));

            for (Annuncio a : annunci) {
                cardsPanel.add(createAnnuncioCard(a));
            }
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
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
        lblFoto.setBorder(new EmptyBorder(8, 8, 8, 8));

        ImageIcon rawIcon = getDefaultIconForCategoria(annuncio.getCategoria());
        Image scaled = rawIcon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        lblFoto.setIcon(new ImageIcon(scaled));
        card.add(lblFoto, BorderLayout.NORTH);

        // INFO
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(5, 8, 2, 8));
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

        // AZIONI
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actions.setBorder(new EmptyBorder(0, 8, 8, 8));
        actions.setOpaque(false);

        String tip = annuncio.getTipologia();

        if ("Vendita".equals(tip)) {
            JButton btnAcquista = new JButton("Acquista");
            btnAcquista.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.ACQUISTA);
                }
            });

            JButton btnOfferta = new JButton("Fai offerta");
            btnOfferta.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.FAI_OFFERTA);
                }
            });

            actions.add(btnAcquista);
            actions.add(btnOfferta);

        } else if ("Regalo".equals(tip)) {
            JButton btnRichiedi = new JButton("Richiedi");
            btnRichiedi.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.RICHIEDI_REGALO);
                }
            });
            actions.add(btnRichiedi);

        } else { // Scambio
            JButton btnScambio = new JButton("Proponi scambio");
            btnScambio.addActionListener(e -> {
                if (annuncioOffertaListener != null) {
                    annuncioOffertaListener.onOffertaAction(annuncio, OffertaAction.PROPONI_SCAMBIO);
                }
            });
            actions.add(btnScambio);
        }

        card.add(actions, BorderLayout.SOUTH);

        // CLICK sulla card (ma non quando clicchi i bottoni)
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Component deep = SwingUtilities.getDeepestComponentAt(card, e.getX(), e.getY());
                if (deep instanceof JButton) return;

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
}
