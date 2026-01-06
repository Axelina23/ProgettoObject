package it.unina.uninaswap.controller;

import it.unina.uninaswap.dao.*;
import it.unina.uninaswap.dao.impl.*;
import it.unina.uninaswap.model.entity.*;
import it.unina.uninaswap.model.enums.TipoAnnuncio;
import it.unina.uninaswap.model.enums.TipoCategoria;
import it.unina.uninaswap.model.enums.StatoOfferta;
import it.unina.uninaswap.view.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeController {

    private final AnnunciMainView view;

    private final AnnuncioDAO annuncioDAO       = new AnnuncioDAOImpl();
    private final StudenteDAO studenteDAO       = new StudenteDAOImpl();
    private final RecensioneDAO recensioneDAO   = new RecensioneDAOImpl();
    private final TransazioneDAO transazioneDAO = new TransazioneDAOImpl();
    private final FotoDAO fotoDAO               = new FotoDAOImpl();
    private final OffertaDAO offertaDAO         = new OffertaDAOImpl();
    private final ConsegnaDAO consegnaDAO       = new ConsegnaDAOImpl();
    private final IndirizzoDAO indirizzoDAO     = new IndirizzoDAOImpl();

    private final Studente loggedIn;

    // stato per back / profilo venditore
    private Annuncio lastOpenedAnnuncio;
    private Studente lastOpenedVenditore;
    private enum DetailOrigin { HOME, PROFILE, NOTIFICATION, VENDITORE_PROFILE }
    private DetailOrigin currentDetailOrigin = DetailOrigin.HOME;

    // serve a tornare al dettaglio "originale" quando esci dal profilo venditore
    private Integer annuncioIdBeforeVenditoreProfile = null;
    private DetailOrigin detailOriginBeforeVenditoreProfile = null;


    public HomeController(AnnunciMainView view, Studente s) {
        this.view = view;
        this.loggedIn = s;

        initDefaultState();
        initListeners();
        refreshAnnunciFromFilters();

    }

    private void initDefaultState() {
        view.showAnnunciView();
        view.getMenuBarPanel().setVisible(false);
        view.getFilterPanel().setVisible(false);
    }

    private void initListeners() {

        // ==========================
        // TOP BAR
        // ==========================
        view.addHamburgerMenuListener(e -> view.toggleMenuBar());
        view.addFilterButtonListener(e -> view.toggleFilterPanel());

        // ==========================
        // MENU LATERALE
        // ==========================
        view.getMenuBarPanel().getHomeButton().addActionListener(e -> {
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
            refreshAnnunciFromFilters();
        });

        view.getMenuBarPanel().getProfileButton().addActionListener(e -> {
            refreshProfileView();
            view.showProfileView();
        });

        view.getMenuBarPanel().getReportButton().addActionListener(e -> {
            refreshReportView();
            view.showReportView();
        });

        view.getMenuBarPanel().getNotificationButton().addActionListener(e -> {
            view.showNotificationView();
            refreshNotificationView();
        });

        view.getMenuBarPanel().getSettingsButton().addActionListener(e -> openSettingsDialog());
        view.getMenuBarPanel().getAddButton().addActionListener(e -> openCreateAnnuncioDialog());
        view.getMenuBarPanel().getLogoutButton().addActionListener(e -> handleLogout());

        // ==========================
        // FILTER PANEL: CERCA
        // ==========================
        view.getFilterPanel().getBtnCerca().addActionListener(e -> {
            refreshAnnunciFromFilters();
            view.getFilterPanel().setVisible(false);
            view.getFilterPanel().getParent().revalidate();
            view.getFilterPanel().getParent().repaint();
        });

        // ==========================
        // ANNUNCI LIST VIEW (HOME)
        // ==========================
        AnnunciListView listView = view.getAnnunciListView();
        if (listView != null) {

            listView.setAnnuncioClickListener(this::openAnnuncioDetailFromHome);

            listView.setAnnuncioOffertaListener((annuncio, action) ->
                    handleAnnuncioOffertaAction(annuncio, action.name())
            );
        }

        // ==========================
        // ANNUNCIO DETAIL VIEW
        // ==========================
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        if (detailView != null) {

            detailView.setAnnuncioOffertaListener((annuncio, action) ->
                    handleAnnuncioOffertaAction(annuncio, action.name())
            );

            detailView.getBtnVediProfiloVenditore().addActionListener(e -> {

                if (lastOpenedVenditore == null) {
                    JOptionPane.showMessageDialog(view,
                            "Venditore non disponibile.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // se venditore == loggato -> profilo
                if (loggedIn != null
                        && lastOpenedVenditore.getMatricola() != null
                        && lastOpenedVenditore.getMatricola().equals(loggedIn.getMatricola())) {
                    refreshProfileView();
                    view.showProfileView();
                    return;
                }

                // ✅ salvo origine del dettaglio e annuncio "di ingresso"
                annuncioIdBeforeVenditoreProfile = (lastOpenedAnnuncio != null) ? lastOpenedAnnuncio.getId() : null;
                detailOriginBeforeVenditoreProfile = currentDetailOrigin;

                refreshVenditoreProfileView(lastOpenedVenditore);
                view.showVenditoreProfileView();
            });
        }

        // ==========================
        // PROFILE VIEW
        // ==========================
        ProfileView profileView = view.getProfileView();
        if (profileView != null) {

            profileView.setAnnuncioClickListener(this::openAnnuncioDetailFromProfile);
            profileView.setAnnuncioEditListener(this::openAnnuncioEdit);

            try {
                List<Annuncio> mieiAnnunci = annuncioDAO.findByStudente(loggedIn.getMatricola());
                profileView.setMyAnnunci(mieiAnnunci);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(view,
                        "Errore durante il caricamento dei tuoi annunci:\n" + ex.getMessage(),
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            }

            profileView.setAnnuncioDeleteListener(annuncio -> {
                try {
                    annuncioDAO.delete(annuncio.getId(), loggedIn.getMatricola());
                    refreshProfileView();
                    refreshAnnunciFromFilters();
                    JOptionPane.showMessageDialog(view, "Annuncio eliminato.", "Info", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(view,
                            "Errore eliminazione annuncio:\n" + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            });
        }

        // ==========================
        // VENDITORE PROFILE VIEW (BACK FIX: niente ping-pong)
        // ==========================
        VenditoreProfileView vendView = view.getVenditoreProfileView();
        if (vendView != null) {

            // ✅ back intelligente (torna al dettaglio di ingresso)
            vendView.getBtnBack().addActionListener(e -> handleBackFromVenditoreProfile());

            // click sugli annunci del venditore -> apre dettaglio, back torna al profilo venditore
            vendView.setAnnuncioClickListener(this::openAnnuncioDetailFromVenditoreProfile);
        }

        // ==========================
        // NOTIFICATION VIEW
        // ==========================
        NotificationView notificationView = view.getNotificationView();
        if (notificationView != null) {

            notificationView.setOffertaRicevutaListener(new NotificationView.OffertaRicevutaListener() {
                @Override public void onAccetta(Offerta offerta) { handleAcceptOffer(offerta); }

                @Override public void onRifiuta(Offerta offerta) {
                    try {
                        offertaDAO.updateStato(offerta.getId(), "Rifiutata");
                        refreshNotificationView();
                        refreshReportView();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(view,
                                "Errore durante il rifiuto dell'offerta:\n" + ex.getMessage(),
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }

                @Override public void onApriAnnuncio(Offerta offerta) {
                    openAnnuncioFromOfferta(offerta);
                }
            });

            notificationView.setOffertaInviataListener(new NotificationView.OffertaInviataListener() {
                @Override public void onApriAnnuncio(Offerta offerta) {
                    openAnnuncioFromOfferta(offerta);
                }

                @Override public void onModifica(Offerta offerta) {
                    handleEditOfferta(offerta);
                }

                @Override public void onRitira(Offerta offerta) {
                    try {
                        // nel DB non esiste "Ritirata"
                        offertaDAO.updateStato(offerta.getId(), "Rifiutata");
                        refreshNotificationView();
                        refreshReportView();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(view,
                                "Errore durante il ritiro dell'offerta:\n" + ex.getMessage(),
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
        }
    }



    // ==========================
    // Refresh annunci con filtri
    // ==========================
    private void refreshAnnunciFromFilters() {
        try {
            String search = view.getFilterPanel().getSearchText();
            if (search != null && search.isBlank()) search = null;

            String categoria = view.getFilterPanel().getSelectedCategoria();
            String tipologia = view.getFilterPanel().getSelectedTipologia();

            BigDecimal prezzoMin = parseBigDecimalOrNull(view.getFilterPanel().getPrezzoMinText());
            BigDecimal prezzoMax = parseBigDecimalOrNull(view.getFilterPanel().getPrezzoMaxText());

            if (prezzoMin != null && prezzoMax != null && prezzoMin.compareTo(prezzoMax) > 0) {
                JOptionPane.showMessageDialog(view,
                        "Prezzo min non può essere maggiore di Prezzo max.",
                        "Errore filtri",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Boolean offreSpedizione = view.getFilterPanel().isSpedizioneSelected() ? Boolean.TRUE : null;
            Boolean offreInUni      = view.getFilterPanel().isInUniSelected()      ? Boolean.TRUE : null;

            List<Annuncio> annunci = annuncioDAO.findAttiviEsclusoStudente(
                    loggedIn.getMatricola(),
                    search,
                    categoria,
                    tipologia,
                    prezzoMin,
                    prezzoMax,
                    offreSpedizione,
                    offreInUni
            );

            view.getAnnunciListView().showAnnunci(annunci);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento annunci:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private BigDecimal parseBigDecimalOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        if (t.isEmpty()) return null;
        try {
            return new BigDecimal(t);
        } catch (Exception e) {
            return null;
        }
    }

    // ==========================
    // Dettaglio annuncio (comune)
    // ==========================
    private boolean openAnnuncioDetailCommon(Annuncio annuncioFromList) {
        if (annuncioFromList == null || annuncioFromList.getId() == null) return false;

        try {
            Annuncio full = annuncioDAO.findById(annuncioFromList.getId());
            if (full == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato (forse rimosso).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }

            Studente venditore = studenteDAO.findByMatricola(full.getMatricolaVenditore());

            // Foto -> ImageIcon (metto principale prima)
            List<Foto> fotoList = fotoDAO.findByAnnuncio(full.getId());
            if (fotoList == null) fotoList = Collections.emptyList();
            fotoList.sort(Comparator.comparing(Foto::isPrincipale).reversed());

            List<ImageIcon> icons = new ArrayList<>();
            for (Foto f : fotoList) {
                if (f == null || f.getPath() == null) continue;
                ImageIcon ic = new ImageIcon(f.getPath());
                if (ic.getIconWidth() > 0 && ic.getIconHeight() > 0) {
                    Image scaled = ic.getImage().getScaledInstance(320, -1, Image.SCALE_SMOOTH);
                    ic = new ImageIcon(scaled);
                }
                icons.add(ic);
            }

            view.getAnnuncioDetailView().setData(full, venditore, icons);
            view.getAnnuncioDetailView().updateAzioniForLoggedIn(loggedIn.getMatricola());


            this.lastOpenedAnnuncio = full;
            this.lastOpenedVenditore = venditore;

            view.showAnnuncioDetailView();
            return true;

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore apertura dettaglio annuncio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // card cliccata dalla HOME
    private void openAnnuncioDetailFromHome(Annuncio annuncioFromList) {
        if (openAnnuncioDetailCommon(annuncioFromList)) {
            currentDetailOrigin = DetailOrigin.HOME;
            configureBackToHome();
        }
    }

    private void openAnnuncioDetailFromProfile(Annuncio annuncioFromList) {
        if (openAnnuncioDetailCommon(annuncioFromList)) {
            currentDetailOrigin = DetailOrigin.PROFILE;
            configureBackToProfile();
        }
    }

    private void openAnnuncioDetailFromVenditoreProfile(Annuncio annuncioFromList) {
        if (openAnnuncioDetailCommon(annuncioFromList)) {
            currentDetailOrigin = DetailOrigin.VENDITORE_PROFILE;
            configureBackToVenditoreProfile();
        }
    }


    // ==========================
    // Modifica annuncio (dal profilo)
    // ==========================
    private void openAnnuncioEdit(Annuncio annuncioFromProfile) {
        if (annuncioFromProfile == null || annuncioFromProfile.getId() == null) return;

        try {
            Annuncio full = annuncioDAO.findById(annuncioFromProfile.getId());
            if (full == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato (forse rimosso).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            List<Foto> fotoList = fotoDAO.findByAnnuncio(full.getId());

            AnnuncioEditDialog dialog = new AnnuncioEditDialog(view, full, fotoList);
            dialog.setVisible(true);

            if (!dialog.isConfirmed()) return;

            Annuncio edited = dialog.getEditedAnnuncio();
            List<Foto> editedFotos = dialog.getEditedFotoList();

            annuncioDAO.update(edited);

            fotoDAO.deleteByAnnuncio(edited.getId());
            for (Foto f : editedFotos) {
                f.setIdAnnuncio(edited.getId());
                fotoDAO.insert(f);
            }

            refreshProfileView();
            refreshAnnunciFromFilters();

            JOptionPane.showMessageDialog(view,
                    "Annuncio aggiornato con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante la modifica annuncio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Configurazione tasto "Indietro"
    // ==========================
    private void configureBackToHome() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> {
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
        });
    }

    private void configureBackToProfile() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> {
            refreshProfileView();
            view.showProfileView();
        });
    }

    private void configureBackToNotification() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> {
            view.showNotificationView();
            refreshNotificationView();
        });
    }

    private void configureBackToVenditoreProfile() {
        AnnuncioDetailView detailView = view.getAnnuncioDetailView();
        JButton btnBack = detailView.getBtnBack();

        for (ActionListener l : btnBack.getActionListeners()) {
            btnBack.removeActionListener(l);
        }

        btnBack.addActionListener(e -> view.showVenditoreProfileView());
    }

    // ==========================
    // Refresh profilo (annunci + recensioni)
    // ==========================
    private void refreshProfileView() {
        ProfileView profileView = view.getProfileView();
        profileView.setStudente(loggedIn);

        try {
            // INDIRIZZO
            List<Indirizzo> indirizzi = indirizzoDAO.findByStudente(loggedIn.getMatricola());
            String indirizzoTxt = (indirizzi != null && !indirizzi.isEmpty())
                    ? (indirizzi.get(0).getVia() + " " + indirizzi.get(0).getCivico() + ", " +
                    indirizzi.get(0).getCap() + " " + indirizzi.get(0).getCitta())
                    : null;
            profileView.setIndirizzoInfo(indirizzoTxt);

            // ANNUNCI MIEI
            List<Annuncio> miei = annuncioDAO.findByStudente(loggedIn.getMatricola());
            profileView.setMyAnnunci(miei);

            // RECENSIONI RICEVUTE
            List<Recensione> recs = recensioneDAO.findRicevuteByStudente(loggedIn.getMatricola());
            List<ProfileView.RecensioneCardData> cardData = new ArrayList<>();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            for (Recensione r : recs) {
                Transazione trans = transazioneDAO.findById(r.getIdTransazione());
                String dataStr = (trans != null && trans.getData() != null) ? trans.getData().format(fmt) : "-";

                Studente autore = studenteDAO.findByMatricola(r.getAutore());
                String nomeAut = (autore != null) ? autore.getNome() : "N/D";
                String cognomeAut = (autore != null) ? autore.getCognome() : "";
                String matAut = (autore != null) ? autore.getMatricola() : r.getAutore();

                cardData.add(new ProfileView.RecensioneCardData(
                        r.getTitolo(), r.getCorpo(), r.getValutazione(),
                        dataStr, nomeAut, cognomeAut, matAut
                ));
            }

            profileView.setRecensioni(cardData);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento del profilo:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Refresh profilo venditore (info + annunci attivi)
    // ==========================
    private void refreshVenditoreProfileView(Studente venditore) {
        if (venditore == null || view.getVenditoreProfileView() == null) return;

        try {
            view.getVenditoreProfileView().setVenditore(venditore);

            List<Annuncio> list = annuncioDAO.findByStudente(venditore.getMatricola());
            if (list != null) {
                list.removeIf(a -> a == null || a.isConcluso()); // mostro solo attivi
            }

            view.getVenditoreProfileView().setAnnunciVenditore(list);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore caricamento profilo venditore:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Notifiche
    // ==========================
    private void openAnnuncioFromOfferta(Offerta offerta) {
        if (offerta == null) return;

        try {
            Annuncio annuncio = annuncioDAO.findById(offerta.getIdAnnuncio());
            if (annuncio == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato (forse rimosso).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (openAnnuncioDetailCommon(annuncio)) {
            	currentDetailOrigin = DetailOrigin.NOTIFICATION;
                configureBackToNotification();
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore apertura annuncio dall'offerta:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshNotificationView() {
        NotificationView notificationView = view.getNotificationView();
        if (notificationView == null || loggedIn == null) return;

        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            // Offerte ricevute (io sono venditore)
            List<Offerta> ricevute = offertaDAO.findRicevuteInAttesaPerVenditore(loggedIn.getMatricola());
            ArrayList<NotificationView.OffertaNotificationData> ricevuteData = new ArrayList<>();

            for (Offerta o : ricevute) {
                Annuncio a = annuncioDAO.findById(o.getIdAnnuncio());
                Studente offerente = studenteDAO.findByMatricola(o.getMatricolaOfferente());

                String titolo = (a != null) ? a.getTitolo() : "Annuncio #" + o.getIdAnnuncio();
                String controparte = (offerente != null)
                        ? offerente.getNome() + " " + offerente.getCognome() + " (" + offerente.getMatricola() + ")"
                        : o.getMatricolaOfferente();

                String dataStr = (o.getData() != null) ? o.getData().format(fmt) : "";
                String importo = (o.getImportoOfferto() != null)
                        ? o.getImportoOfferto().toPlainString() + " €"
                        : "-";

                String tipoAnn = (a != null) ? a.getTipologia() : "-";

                ricevuteData.add(new NotificationView.OffertaNotificationData(
                        o, titolo, tipoAnn, controparte, dataStr, importo
                ));
            }
            notificationView.setOfferteRicevute(ricevuteData);

            // Offerte inviate (io sono offerente)
            List<Offerta> inviate = offertaDAO.findInviateInAttesaPerOfferente(loggedIn.getMatricola());
            ArrayList<NotificationView.OffertaNotificationData> inviateData = new ArrayList<>();

            for (Offerta o : inviate) {
                Annuncio a = annuncioDAO.findById(o.getIdAnnuncio());
                Studente venditore = (a != null) ? studenteDAO.findByMatricola(a.getMatricolaVenditore()) : null;

                String titolo = (a != null) ? a.getTitolo() : "Annuncio #" + o.getIdAnnuncio();
                String controparte = (venditore != null)
                        ? venditore.getNome() + " " + venditore.getCognome() + " (" + venditore.getMatricola() + ")"
                        : (a != null ? a.getMatricolaVenditore() : "-");

                String dataStr = (o.getData() != null) ? o.getData().format(fmt) : "";
                String importo = (o.getImportoOfferto() != null)
                        ? o.getImportoOfferto().toPlainString() + " €"
                        : "-";

                String tipoAnn = (a != null) ? a.getTipologia() : "-";

                inviateData.add(new NotificationView.OffertaNotificationData(
                        o, titolo, tipoAnn, controparte, dataStr, importo
                ));
            }
            notificationView.setOfferteInviate(inviateData);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento notifiche:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Modifica offerta inviata (In_Attesa)
    // ==========================
    private void handleEditOfferta(Offerta offertaBase) {
        if (offertaBase == null) return;

        try {
            Offerta offerta = offertaDAO.findById(offertaBase.getId());
            if (offerta == null) {
                JOptionPane.showMessageDialog(view,
                        "Offerta non trovata.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!"In_Attesa".equals(offerta.getStato().name())) {
                JOptionPane.showMessageDialog(view,
                        "Puoi modificare solo offerte in stato 'In_Attesa'.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
                refreshNotificationView();
                return;
            }

            Annuncio annuncio = annuncioDAO.findById(offerta.getIdAnnuncio());
            if (annuncio == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            OffertaEditDialog dialog = new OffertaEditDialog(view, offerta, annuncio);
            dialog.setVisible(true);

            if (!dialog.isConfirmed()) return;

            Offerta edited = dialog.getEditedOfferta();
            if (edited == null) return;

            offertaDAO.updateContenuto(edited);

            JOptionPane.showMessageDialog(view,
                    "Offerta modificata con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshNotificationView();
            refreshReportView();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante la modifica dell'offerta:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Gestione ACCETTAZIONE offerta (con consegna)
    // ==========================
    private void handleAcceptOffer(Offerta offertaBase) {
        if (offertaBase == null) return;

        try {
            Offerta offerta = offertaDAO.findById(offertaBase.getId());
            if (offerta == null) {
                JOptionPane.showMessageDialog(view,
                        "Offerta non trovata.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Annuncio annuncio = annuncioDAO.findById(offerta.getIdAnnuncio());
            if (annuncio == null) {
                JOptionPane.showMessageDialog(view,
                        "Annuncio non trovato.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Studente acquirente = studenteDAO.findByMatricola(offerta.getMatricolaOfferente());
            if (acquirente == null) {
                JOptionPane.showMessageDialog(view,
                        "Acquirente non trovato.",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // indirizzi acquirente (per spedizione)
            List<Indirizzo> indirizziAcquirente = indirizzoDAO.findByStudente(acquirente.getMatricola());

            AccettaOffertaDialog dialog = new AccettaOffertaDialog(view, annuncio, acquirente, indirizziAcquirente);
            dialog.setVisible(true);

            AccettaOffertaDialog.Result scelta = dialog.getResult();
            if (scelta == null) return;

            // 1) aggiorno stato offerta
            offertaDAO.updateStato(offerta.getId(), "Accettata");

            // 2) trigger crea transazione -> la recupero
            Transazione trans = transazioneDAO.findByOffertaAccettata(offerta.getId());
            if (trans == null) {
                JOptionPane.showMessageDialog(view,
                        "Transazione non trovata dopo l'accettazione (controlla trigger).",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
                refreshNotificationView();
                refreshReportView();
                return;
            }

            // 3) creo consegna
            String note = null;
            if (scelta.isSpedizione()) {
                Integer idInd = scelta.getIdIndirizzo();
                if (idInd == null) {
                    JOptionPane.showMessageDialog(view,
                            "Seleziona un indirizzo per la spedizione.",
                            "Errore",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                consegnaDAO.creaConsegnaSpedizione(trans.getId(), idInd, note);

            } else {
                LocalDate dataIncontro = scelta.getDataIncontro();
                consegnaDAO.creaConsegnaInUni(
                        trans.getId(),
                        dataIncontro,
                        scelta.getSedeUniversita(),
                        scelta.getFasciaOraria(),
                        note
                );
            }

            JOptionPane.showMessageDialog(view,
                    "Offerta accettata e consegna registrata.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

            refreshNotificationView();
            refreshProfileView();
            refreshReportView();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante l'accettazione dell'offerta:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Report
    // ==========================
    
    private void refreshReportView() {
        ReportView reportView = view.getReportView();
        if (reportView == null) return;

        try {
            ReportView.ReportData data = new ReportView.ReportData();

            // ==========================
            // VENDITE (io VENDITORE)
            // ==========================

            // Per categoria
            for (TipoCategoria cat : TipoCategoria.values()) {
                int arrivate = offertaDAO.countOfferteArrivatePerCategoria(loggedIn.getMatricola(), cat);
                int accettate = offertaDAO.countVenditeAccettatePerCategoria(loggedIn.getMatricola(), cat);

                data.venditePerCategoria.putArrivate(cat, arrivate);
                data.venditePerCategoria.putAccettate(cat, accettate);
            }
            data.venditePerCategoria.computeTotals();

            // Per tipologia
            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                int arrivate = offertaDAO.countOfferteArrivatePerTipologia(loggedIn.getMatricola(), tipo);
                int accettate = offertaDAO.countVenditeAccettatePerTipologia(loggedIn.getMatricola(), tipo);

                data.venditePerTipologia.putArrivate(tipo, arrivate);
                data.venditePerTipologia.putAccettate(tipo, accettate);
            }
            data.venditePerTipologia.computeTotals();

            // ==========================
            // ACQUISTI (io OFFERENTE)
            // ==========================

            // Per categoria
            for (TipoCategoria cat : TipoCategoria.values()) {
                int inviate = offertaDAO.countOfferteInviatePerCategoria(loggedIn.getMatricola(), cat);
                int accettate = offertaDAO.countAcquistiAccettatiPerCategoria(loggedIn.getMatricola(), cat);

                data.acquistiPerCategoria.putArrivate(cat, inviate);      // "arrivate" = "inviate" in questa sezione
                data.acquistiPerCategoria.putAccettate(cat, accettate);
            }
            data.acquistiPerCategoria.computeTotals();

            // Per tipologia
            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                int inviate = offertaDAO.countOfferteInviatePerTipologia(loggedIn.getMatricola(), tipo);
                int accettate = offertaDAO.countAcquistiAccettatiPerTipologia(loggedIn.getMatricola(), tipo);

                data.acquistiPerTipologia.putArrivate(tipo, inviate);
                data.acquistiPerTipologia.putAccettate(tipo, accettate);
            }
            data.acquistiPerTipologia.computeTotals();

            // ==========================
            // STATISTICHE IMPORTO (solo tipologia Vendita)
            // ==========================
            data.mediaVendite = transazioneDAO.getMediaImportoVenditaAccettataPerVenditore(loggedIn.getMatricola());
            data.minVendite   = transazioneDAO.getMinImportoVenditaAccettataPerVenditore(loggedIn.getMatricola());
            data.maxVendite   = transazioneDAO.getMaxImportoVenditaAccettataPerVenditore(loggedIn.getMatricola());

            data.mediaAcquisti = transazioneDAO.getMediaImportoVenditaAccettataPerAcquirente(loggedIn.getMatricola());
            data.minAcquisti   = transazioneDAO.getMinImportoVenditaAccettataPerAcquirente(loggedIn.getMatricola());
            data.maxAcquisti   = transazioneDAO.getMaxImportoVenditaAccettataPerAcquirente(loggedIn.getMatricola());

            // ==========================
            // GRAFICI (4)
            // ==========================
            DefaultCategoryDataset dsVenditeCat = new DefaultCategoryDataset();
            for (TipoCategoria cat : TipoCategoria.values()) {
                dsVenditeCat.addValue(data.venditePerCategoria.getArrivate(cat), "Offerte arrivate", cat.toString());
                dsVenditeCat.addValue(data.venditePerCategoria.getAccettate(cat), "Accettate", cat.toString());
            }
            JFreeChart chartVenditeCat = ChartFactory.createBarChart(
                    "Vendite - per categoria", "Categoria", "Numero",
                    dsVenditeCat, PlotOrientation.VERTICAL, true, true, false
            );

            DefaultCategoryDataset dsVenditeTipo = new DefaultCategoryDataset();
            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                dsVenditeTipo.addValue(data.venditePerTipologia.getArrivate(tipo), "Offerte arrivate", tipo.toString());
                dsVenditeTipo.addValue(data.venditePerTipologia.getAccettate(tipo), "Accettate", tipo.toString());
            }
            JFreeChart chartVenditeTipo = ChartFactory.createBarChart(
                    "Vendite - per tipologia", "Tipologia", "Numero",
                    dsVenditeTipo, PlotOrientation.VERTICAL, true, true, false
            );

            DefaultCategoryDataset dsAcquistiCat = new DefaultCategoryDataset();
            for (TipoCategoria cat : TipoCategoria.values()) {
                dsAcquistiCat.addValue(data.acquistiPerCategoria.getArrivate(cat), "Offerte inviate", cat.toString());
                dsAcquistiCat.addValue(data.acquistiPerCategoria.getAccettate(cat), "Accettate", cat.toString());
            }
            JFreeChart chartAcquistiCat = ChartFactory.createBarChart(
                    "Acquisti - per categoria", "Categoria", "Numero",
                    dsAcquistiCat, PlotOrientation.VERTICAL, true, true, false
            );

            DefaultCategoryDataset dsAcquistiTipo = new DefaultCategoryDataset();
            for (TipoAnnuncio tipo : TipoAnnuncio.values()) {
                dsAcquistiTipo.addValue(data.acquistiPerTipologia.getArrivate(tipo), "Offerte inviate", tipo.toString());
                dsAcquistiTipo.addValue(data.acquistiPerTipologia.getAccettate(tipo), "Accettate", tipo.toString());
            }
            JFreeChart chartAcquistiTipo = ChartFactory.createBarChart(
                    "Acquisti - per tipologia", "Tipologia", "Numero",
                    dsAcquistiTipo, PlotOrientation.VERTICAL, true, true, false
            );

            // Push su view
            reportView.setData(data);
            reportView.setVenditeChartCategoria(chartVenditeCat);
            reportView.setVenditeChartTipologia(chartVenditeTipo);
            reportView.setAcquistiChartCategoria(chartAcquistiCat);
            reportView.setAcquistiChartTipologia(chartAcquistiTipo);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante il caricamento report:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }



    // ==========================
    // Crea annuncio
    // ==========================
    private void openCreateAnnuncioDialog() {
        try {
            AnnuncioCreateDialog dialog = new AnnuncioCreateDialog(view, loggedIn);
            dialog.setVisible(true);

            if (!dialog.isConfirmed()) return;

            Annuncio created = dialog.getCreatedAnnuncio();
            if (created == null) return;

            annuncioDAO.insert(created); // created ha già l'id valorizzato

            // SALVATAGGIO FOTO
            List<Foto> fotos = dialog.getCreatedFotoList();
            if (fotos != null && !fotos.isEmpty()) {
                for (Foto f : fotos) {
                    f.setIdAnnuncio(created.getId());
                    fotoDAO.insert(f);
                }
            }

            refreshProfileView();
            refreshAnnunciFromFilters();

            JOptionPane.showMessageDialog(view,
                    "Annuncio creato con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante la creazione annuncio:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==========================
    // Settings profilo
    // ==========================
    private void openSettingsDialog() {
        StudenteSettingsDialog dialog = new StudenteSettingsDialog(view, loggedIn);
        dialog.setVisible(true);

        if (!dialog.isConfirmed()) return;

        Studente updated = dialog.getEditedStudente();
        if (updated == null) return;

        try {
            studenteDAO.update(updated);

            loggedIn.setNome(updated.getNome());
            loggedIn.setCognome(updated.getCognome());
            loggedIn.setEmail(updated.getEmail());
            loggedIn.setPassword(updated.getPassword());
            loggedIn.setSesso(updated.getSesso());
            loggedIn.setPreferisceSpedizione(updated.getPreferisceSpedizione());
            loggedIn.setPreferisceIncontroInUni(updated.getPreferisceIncontroInUni());

            refreshProfileView();

            JOptionPane.showMessageDialog(view,
                    "Profilo aggiornato con successo.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view,
                    "Errore durante l'aggiornamento del profilo:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleAnnuncioOffertaAction(Annuncio annuncio, String actionName) {
        if (annuncio == null || annuncio.getId() == null) return;

        // blocco soft (comunque c’è anche il trigger DB)
        if (annuncio.getMatricolaVenditore() != null &&
                annuncio.getMatricolaVenditore().equals(loggedIn.getMatricola())) {
            JOptionPane.showMessageDialog(view,
                    "Non puoi fare offerte ai tuoi annunci.",
                    "Operazione non consentita",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            switch (actionName) {
                case "ACQUISTA" -> {
                    if (annuncio.getPrezzo() == null) {
                        JOptionPane.showMessageDialog(view,
                                "Prezzo non disponibile per questo annuncio.",
                                "Errore",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    int res = JOptionPane.showConfirmDialog(
                            view,
                            "Confermi l'acquisto a prezzo pieno (" + annuncio.getPrezzo() + " €)?",
                            "Conferma acquisto",
                            JOptionPane.YES_NO_OPTION
                    );
                    if (res != JOptionPane.YES_OPTION) return;

                    Offerta o = buildNewOffertaForAnnuncio(annuncio);
                    o.setImportoOfferto(annuncio.getPrezzo());
                    offertaDAO.insert(o);

                    JOptionPane.showMessageDialog(view, "Acquisto inviato come offerta!", "OK", JOptionPane.INFORMATION_MESSAGE);
                }

                case "FAI_OFFERTA", "RICHIEDI_REGALO", "PROPONI_SCAMBIO" -> {
                    Offerta bozza = buildNewOffertaForAnnuncio(annuncio);

                    OffertaEditDialog dialog = new OffertaEditDialog(view, bozza, annuncio);
                    dialog.setVisible(true);
                    if (!dialog.isConfirmed()) return;

                    Offerta edited = dialog.getEditedOfferta();
                    if (edited == null) return;

                    offertaDAO.insert(edited);

                    JOptionPane.showMessageDialog(view, "Offerta inviata!", "OK", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            // aggiorna UI
            refreshNotificationView();
            refreshReportView();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view,
                    "Operazione non riuscita:\n" + ex.getMessage(),
                    "Errore",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private Offerta buildNewOffertaForAnnuncio(Annuncio annuncio) {
        Offerta o = new Offerta();
        o.setId(0);
        o.setData(LocalDate.now());
        o.setStato(StatoOfferta.In_Attesa);
        o.setIdAnnuncio(annuncio.getId());
        o.setMatricolaOfferente(loggedIn.getMatricola());
        return o;
    }

    private void handleBackFromVenditoreProfile() {
        // se non ho "origine", torno alla home
        if (annuncioIdBeforeVenditoreProfile == null || detailOriginBeforeVenditoreProfile == null) {
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
            return;
        }

        try {
            Annuncio a = annuncioDAO.findById(annuncioIdBeforeVenditoreProfile);
            if (a == null) {
                view.showAnnunciView();
                view.getFilterPanel().setVisible(false);
                return;
            }

            // riapro IL DETTAGLIO ORIGINALE (quello da cui ero entrato nel profilo venditore)
            openAnnuncioDetailCommon(a);

            // ripristino il back corretto del dettaglio
            switch (detailOriginBeforeVenditoreProfile) {
                case HOME -> configureBackToHome();
                case PROFILE -> configureBackToProfile();
                case NOTIFICATION -> configureBackToNotification();
                case VENDITORE_PROFILE -> configureBackToVenditoreProfile();
            }
            currentDetailOrigin = detailOriginBeforeVenditoreProfile;

            // pulisco (evito stati vecchi)
            annuncioIdBeforeVenditoreProfile = null;
            detailOriginBeforeVenditoreProfile = null;

        } catch (Exception ex) {
            ex.printStackTrace();
            view.showAnnunciView();
            view.getFilterPanel().setVisible(false);
        }
    }

    
    // ==========================
    // Logout
    // ==========================
    private void handleLogout() {
        view.dispose();

        LoginView loginView = new LoginView();
        new LoginController(loginView);
        loginView.setVisible(true);
    }
}

