package it.unina.uninaswap.view;

import it.unina.uninaswap.model.entity.Studente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class AnnunciMainView extends JFrame {

    private JButton hamburgerMenuButton;
    private JButton filterButton;

    private AnnunciFilterPanel filterPanel;
    private AnnunciMenuPanel menuBar;

    private JPanel topBar;

    private CardLayout centerLayout;
    private JPanel centerPanel;

    private AnnunciListView annunciListView;
    private ProfileView profileView;
    private NotificationView notificationView;
    

    private ReportView reportView;

    private AnnuncioDetailView annuncioDetailView;
    private VenditoreProfileView venditoreProfileView;

    // nomi delle card
    public static final String CARD_ANNUNCI           = "ANNUNCI";
    public static final String CARD_PROFILE           = "PROFILE";
    public static final String CARD_REPORT            = "REPORT";
    public static final String CARD_NOTIFICATION      = "CARD_NOTIFICATION";
    public static final String CARD_ANNUNCIO_DETAIL   = "ANNUNCIO_DETAIL";
    public static final String CARD_VENDITORE_PROFILE = "VENDITORE_PROFILE";

    public AnnunciMainView(Studente studenteLoggato) {
        setTitle("UniNaSwap");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(0, 0));

        // =========================
        // TOP BAR
        // =========================
        topBar = new JPanel(new BorderLayout(0, 0));
        getContentPane().add(topBar, BorderLayout.NORTH);

        // Panel East (filter)
        JPanel topBarPanelEast = new JPanel();
        topBar.add(topBarPanelEast, BorderLayout.EAST);

        filterButton = new JButton("");
        ImageIcon rawIcon = new ImageIcon(
                "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/filter.png"
        );
        Image filterIcon = rawIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        filterButton.setIcon(new ImageIcon(filterIcon));
        topBarPanelEast.add(filterButton);

        // Panel West (hamburger)
        JPanel topBarPanelWest = new JPanel();
        topBar.add(topBarPanelWest, BorderLayout.WEST);

        hamburgerMenuButton = new JButton("");
        ImageIcon rawIcon1 = new ImageIcon(
                "/Users/mario/eclipse-workspace/uninaswap/src/main/resources/images/menuIcons/hamburgerMenu.png"
        );
        Image hMenuIcon = rawIcon1.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
        hamburgerMenuButton.setIcon(new ImageIcon(hMenuIcon));
        topBarPanelWest.add(hamburgerMenuButton);

        // Panel Center (placeholder)
        JPanel topBarPanelCenter = new JPanel();
        topBar.add(topBarPanelCenter, BorderLayout.CENTER);
        // TODO: aggiungere logo o ricerca globale

        // =========================
        // MENU LATERALE
        // =========================
        menuBar = new AnnunciMenuPanel();
        getContentPane().add(menuBar, BorderLayout.WEST);

        // =========================
        // FILTER PANEL
        // =========================
        filterPanel = new AnnunciFilterPanel();
        getContentPane().add(filterPanel, BorderLayout.EAST);

        // =========================
        // CENTER (CardLayout)
        // =========================
        centerLayout = new CardLayout();
        centerPanel = new JPanel(centerLayout);
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        annunciListView = new AnnunciListView();
        profileView     = new ProfileView(studenteLoggato);
        reportView      = new ReportView();
        notificationView = new NotificationView();
        annuncioDetailView   = new AnnuncioDetailView();
        venditoreProfileView = new VenditoreProfileView();

        centerPanel.add(annunciListView,      CARD_ANNUNCI);
        centerPanel.add(profileView,          CARD_PROFILE);
        centerPanel.add(reportView,           CARD_REPORT);
        centerPanel.add(notificationView,     CARD_NOTIFICATION);
        centerPanel.add(annuncioDetailView,   CARD_ANNUNCIO_DETAIL);
        centerPanel.add(venditoreProfileView, CARD_VENDITORE_PROFILE);

        // =========================
        // BOTTOM (se ti serve)
        // =========================
        JPanel bottomPanel = new JPanel();
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);

        // stato iniziale
        showAnnunciView();
    }

    // =========================
    // Listener per controller
    // =========================
    public void addHamburgerMenuListener(ActionListener l) {
        hamburgerMenuButton.addActionListener(l);
    }

    public void addFilterButtonListener(ActionListener l) {
        filterButton.addActionListener(l);
    }

    // =========================
    // Toggle (usati dai bottoni)
    // =========================
    public void toggleFilterPanel() {
        filterPanel.setVisible(!filterPanel.isVisible());
        filterPanel.getParent().revalidate();
        filterPanel.getParent().repaint();
    }

    public void toggleMenuBar() {
        menuBar.setVisible(!menuBar.isVisible());
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
    }

    // =========================
    // Metodi "deterministici"
    // =========================
    public void hideMenuBar() {
        menuBar.setVisible(false);
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
    }

    public void showMenuBar() {
        menuBar.setVisible(true);
        menuBar.getParent().revalidate();
        menuBar.getParent().repaint();
    }

    public void hideFilterPanel() {
        filterPanel.setVisible(false);
        filterPanel.getParent().revalidate();
        filterPanel.getParent().repaint();
    }

    public void showFilterPanel() {
        filterPanel.setVisible(true);
        filterPanel.getParent().revalidate();
        filterPanel.getParent().repaint();
    }

    // =========================
    // Show cards (navigazione)
    // =========================
    public void showAnnunciView() {
        topBar.setVisible(true);
        filterButton.setVisible(true);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_ANNUNCI);
    }

    public void showProfileView() {
        topBar.setVisible(true);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_PROFILE);
    }

    public void showReportView() {
        topBar.setVisible(true);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_REPORT);
    }



    public void showNotificationView() {
        topBar.setVisible(true);
        filterButton.setVisible(false);

        hideFilterPanel();
        centerLayout.show(centerPanel, CARD_NOTIFICATION);
    }

    public void showAnnuncioDetailView() {
        // dettaglio: niente topbar e niente pannelli laterali
        topBar.setVisible(false);

        hideFilterPanel();
        hideMenuBar();

        centerLayout.show(centerPanel, CARD_ANNUNCIO_DETAIL);
    }

    public void showVenditoreProfileView() {
        // profilo venditore: niente topbar e niente pannelli laterali
        topBar.setVisible(false);

        hideFilterPanel();
        hideMenuBar();

        centerLayout.show(centerPanel, CARD_VENDITORE_PROFILE);
    }

    // =========================
    // Getter per controller
    // =========================
    public AnnunciMenuPanel getMenuBarPanel() {
        return menuBar;
    }

    public AnnunciFilterPanel getFilterPanel() {
        return filterPanel;
    }

    public ProfileView getProfileView() {
        return profileView;
    }

    public AnnunciListView getAnnunciListView() {
        return annunciListView;
    }

    public AnnuncioDetailView getAnnuncioDetailView() {
        return annuncioDetailView;
    }

    public VenditoreProfileView getVenditoreProfileView() {
        return venditoreProfileView;
    }

    public NotificationView getNotificationView() {
        return notificationView;
    }
    
    public ReportView getReportView() {
        return reportView;
    }

}
