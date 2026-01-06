package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.model.enums.TipoAnnuncio;
import it.unina.uninaswap.model.enums.TipoCategoria;

import java.util.List;

public interface OffertaDAO {

    Offerta findById(int id) throws Exception;

    List<Offerta> findRicevuteInAttesaPerVenditore(String matricolaVenditore) throws Exception;
    List<Offerta> findInviateInAttesaPerOfferente(String matricolaOfferente) throws Exception;

    void updateStato(int idOfferta, String nuovoStato) throws Exception;
    void rifiutaAltreOfferteStessoAnnuncio(int idAnnuncio, int idOffertaDaTenere) throws Exception;

    void updateContenuto(Offerta offerta) throws Exception;
    void insert(Offerta offerta) throws Exception;

    // ======================
    // REPORT - PER CATEGORIA
    // ======================
    int countOfferteArrivatePerCategoria(String matricolaVenditore, TipoCategoria categoria) throws Exception;
    int countVenditeAccettatePerCategoria(String matricolaVenditore, TipoCategoria categoria) throws Exception;

    int countOfferteInviatePerCategoria(String matricolaOfferente, TipoCategoria categoria) throws Exception;
    int countAcquistiAccettatiPerCategoria(String matricolaOfferente, TipoCategoria categoria) throws Exception;

    // ======================
    // REPORT - PER TIPOLOGIA (traccia)
    // ======================
    int countOfferteArrivatePerTipologia(String matricolaVenditore, TipoAnnuncio tipologia) throws Exception;
    int countVenditeAccettatePerTipologia(String matricolaVenditore, TipoAnnuncio tipologia) throws Exception;

    int countOfferteInviatePerTipologia(String matricolaOfferente, TipoAnnuncio tipologia) throws Exception;
    int countAcquistiAccettatiPerTipologia(String matricolaOfferente, TipoAnnuncio tipologia) throws Exception;
}
