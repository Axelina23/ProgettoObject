package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Recensione;
import java.util.List;

public interface RecensioneDAO {

    List<Recensione> findRicevuteByStudente(String matricolaRecensito) throws Exception;

    // NUOVO
    void insert(Recensione r) throws Exception;
}