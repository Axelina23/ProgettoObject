package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Studente;

public interface StudenteDAO {
	Studente findByEmailAndPassword(String emali, String password) throws Exception;
    Studente findByMatricola(String matricola) throws Exception;
    void update(Studente studente) throws Exception;
}
