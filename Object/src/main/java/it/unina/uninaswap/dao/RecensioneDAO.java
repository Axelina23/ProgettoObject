package it.unina.uninaswap.dao;

import java.util.List;

import it.unina.uninaswap.model.entity.Recensione;

public interface RecensioneDAO {

    /**
     * Restituisce tutte le recensioni ricevute da uno studente
     * (cioè dove la colonna 'recensito' = matricola dello studente).
     * Ordinate dalla più recente (id più alto) alla più vecchia.
     */
    List<Recensione> findRicevuteByStudente(String matricolaRecensito) throws Exception;
}
