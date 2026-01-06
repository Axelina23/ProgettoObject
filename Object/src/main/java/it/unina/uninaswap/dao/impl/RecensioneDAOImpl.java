package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.RecensioneDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Recensione;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RecensioneDAOImpl implements RecensioneDAO {

    @Override
    public List<Recensione> findRicevuteByStudente(String matricolaRecensito) throws Exception {
        String sql = "SELECT id, titolo, corpo, valutazione, " +
                     "       id_transazione, autore, recensito " +
                     "FROM recensione " +
                     "WHERE recensito = ? " +
                     "ORDER BY id DESC";

        List<Recensione> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaRecensito);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recensione r = new Recensione();
                    r.setId(rs.getInt("id"));
                    r.setTitolo(rs.getString("titolo"));
                    r.setCorpo(rs.getString("corpo"));
                    r.setValutazione(rs.getInt("valutazione"));
                    r.setIdTransazione(rs.getInt("id_transazione"));
                    r.setAutore(rs.getString("autore"));
                    r.setRecensito(rs.getString("recensito"));
                    result.add(r);
                }
            }
        }

        return result;
    }

    @Override
    public void insert(Recensione r) throws Exception {
        String sql = "INSERT INTO recensione (titolo, corpo, valutazione, id_transazione, autore, recensito) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, r.getTitolo());
            ps.setString(2, r.getCorpo());
            ps.setInt(3, r.getValutazione());
            ps.setInt(4, r.getIdTransazione());
            ps.setString(5, r.getAutore());
            ps.setString(6, r.getRecensito());

            ps.executeUpdate();
        }
    }
}