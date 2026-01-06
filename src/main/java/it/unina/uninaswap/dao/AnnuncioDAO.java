package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Annuncio;

import java.math.BigDecimal;
import java.util.List;

public interface AnnuncioDAO {

    /**
     * Ritorna gli annunci attivi (concluso = false) con filtri opzionali.
     *
     * @param search testo libero (titolo/descrizione) - null => no filtro
     * @param categoria valore enum DB tipo_categoria come String (es. "Libri") - null => no filtro
     * @param tipologia valore enum DB tipo_annuncio come String (es. "Vendita") - null => no filtro
     * @param prezzoMin null => no filtro
     * @param prezzoMax null => no filtro
     * @param offreSpedizione null => no filtro, true/false => filtra
     * @param offreInUni null => no filtro, true/false => filtra
     */
	List<Annuncio> findAttivi( String search, String categoria, String tipologia, BigDecimal prezzoMin, BigDecimal prezzoMax, Boolean offreSpedizione, Boolean offreInUni) throws Exception;
	
	List<Annuncio> findAttiviEsclusoStudente(String matricola, String search, String categoria, String tipologia, BigDecimal prezzoMin, BigDecimal prezzoMax, Boolean offreSpedizione, Boolean offreInUni)throws Exception;

	Annuncio findById(int id) throws Exception;

	// tutti gli annunci (attivi e conclusi) di uno studente
	List<Annuncio> findByStudente(String matricolaStudente) throws Exception;

	// ultimi N annunci dello studente (per preview profilo)
	List<Annuncio> findUltimiByStudente(String matricolaStudente, int limit) throws Exception;

	void update(Annuncio annuncio) throws Exception;
	
	void insert(Annuncio annuncio) throws Exception;
	
    void setConcluso(int idAnnuncio, boolean concluso) throws Exception;
    
    boolean delete(int idAnnuncio, String matricolaVenditore) throws Exception;

}
