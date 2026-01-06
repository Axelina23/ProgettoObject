package it.unina.uninaswap.dao;

import it.unina.uninaswap.model.entity.Foto;

import java.util.List;

public interface FotoDAO {

    List<Foto> findByAnnuncio(int idAnnuncio) throws Exception;

    void deleteByAnnuncio(int idAnnuncio) throws Exception;

    void insert(Foto foto) throws Exception;
}
