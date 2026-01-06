package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Transazione;

import java.math.BigDecimal;

public interface TransazioneDAO {

    Transazione findById(int id) throws Exception;

    Transazione findByOffertaAccettata(int idOfferta) throws Exception;

    // ======================
    // REPORT (solo tipologia 'Vendita')
    // ======================

    BigDecimal getMediaImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception;
    BigDecimal getMinImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception;
    BigDecimal getMaxImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception;

    BigDecimal getMediaImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception;
    BigDecimal getMinImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception;
    BigDecimal getMaxImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception;
}
