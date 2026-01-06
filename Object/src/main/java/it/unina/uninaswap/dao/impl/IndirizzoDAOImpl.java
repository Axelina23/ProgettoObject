package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.IndirizzoDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Indirizzo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IndirizzoDAOImpl implements IndirizzoDAO {

    @Override
    public List<Indirizzo> findByStudente(String matricolaStudente) throws Exception {
        String sql = """
            SELECT id, via, citta, cap, civico, stato, matricola_studente
            FROM indirizzo
            WHERE matricola_studente = ?
            ORDER BY id
            """;

        List<Indirizzo> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaStudente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Indirizzo i = new Indirizzo();
                    i.setId(rs.getInt("id"));
                    i.setVia(rs.getString("via"));
                    i.setCitta(rs.getString("citta"));
                    i.setCap(rs.getInt("cap"));
                    i.setCivico(rs.getInt("civico"));
                    i.setStato(rs.getString("stato"));
                    i.setMatricolaStudente(rs.getString("matricola_studente"));
                    list.add(i);
                }
            }
        }
        return list;
    }
}
