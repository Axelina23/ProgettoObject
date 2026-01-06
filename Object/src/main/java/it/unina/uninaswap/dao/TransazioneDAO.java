package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Transazione;
import java.math.BigDecimal;
import java.util.List;

public interface TransazioneDAO {

    Transazione findById(int id) throws Exception;

    Transazione findByOffertaAccettata(int idOfferta) throws Exception;

    // NUOVO: Trova acquisti dell'utente che NON hanno ancora una recensione associata
    List<Transazione> findAcquistiNonRecensiti(String matricolaAcquirente) throws Exception;

    // ======================
    // REPORT
    // ======================
    BigDecimal getMediaImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception;
    BigDecimal getMinImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception;
    BigDecimal getMaxImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception;

    BigDecimal getMediaImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception;
    BigDecimal getMinImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception;
    BigDecimal getMaxImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception;
}