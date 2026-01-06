package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Consegna;

public interface ConsegnaDAO {

    void insert(Consegna consegna) throws Exception;

    Consegna findByTransazione(int idTransazione) throws Exception;

    void creaConsegnaSpedizione(int idTransazione,
                                int idIndirizzo,
                                String note) throws Exception;

    void creaConsegnaInUni(int idTransazione,
                           java.time.LocalDate dataIncontro,
                           String sedeUniversita,
                           String fasciaOraria,
                           String note) throws Exception;

}
