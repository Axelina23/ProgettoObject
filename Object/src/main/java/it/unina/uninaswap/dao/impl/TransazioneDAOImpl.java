package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.TransazioneDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Transazione;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TransazioneDAOImpl implements TransazioneDAO {

    @Override
    public Transazione findById(int id) throws Exception {
        String sql = """
                SELECT id, data, importo_finale, annuncio_concluso,
                       id_offerta_accettata, matricola_venditore, matricola_acquirente
                FROM transazione
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToTransazione(rs);
            }
        }
        return null;
    }

    @Override
    public Transazione findByOffertaAccettata(int idOfferta) throws Exception {
        String sql = """
                SELECT id, data, importo_finale, annuncio_concluso,
                       id_offerta_accettata, matricola_venditore, matricola_acquirente
                FROM transazione
                WHERE id_offerta_accettata = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idOfferta);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToTransazione(rs);
            }
        }
        return null;
    }

    // ======================
    // REPORT - ACQUIRENTE (solo Vendita)
    // ======================
    @Override
    public BigDecimal getMediaImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception {
        String sql = """
                SELECT AVG(t.importo_finale)
                FROM transazione t
                JOIN annuncio a ON t.annuncio_concluso = a.id
                WHERE t.matricola_acquirente = ?
                  AND a.tipologia = 'Vendita'
                """;
        return singleDecimal(sql, matricolaAcquirente);
    }

    @Override
    public BigDecimal getMinImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception {
        String sql = """
                SELECT MIN(t.importo_finale)
                FROM transazione t
                JOIN annuncio a ON t.annuncio_concluso = a.id
                WHERE t.matricola_acquirente = ?
                  AND a.tipologia = 'Vendita'
                """;
        return singleDecimal(sql, matricolaAcquirente);
    }

    @Override
    public BigDecimal getMaxImportoVenditaAccettataPerAcquirente(String matricolaAcquirente) throws Exception {
        String sql = """
                SELECT MAX(t.importo_finale)
                FROM transazione t
                JOIN annuncio a ON t.annuncio_concluso = a.id
                WHERE t.matricola_acquirente = ?
                  AND a.tipologia = 'Vendita'
                """;
        return singleDecimal(sql, matricolaAcquirente);
    }

    // ======================
    // REPORT - VENDITORE (solo Vendita)
    // ======================
    @Override
    public BigDecimal getMediaImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception {
        String sql = """
                SELECT AVG(t.importo_finale)
                FROM transazione t
                JOIN annuncio a ON t.annuncio_concluso = a.id
                WHERE t.matricola_venditore = ?
                  AND a.tipologia = 'Vendita'
                """;
        return singleDecimal(sql, matricolaVenditore);
    }

    @Override
    public BigDecimal getMinImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception {
        String sql = """
                SELECT MIN(t.importo_finale)
                FROM transazione t
                JOIN annuncio a ON t.annuncio_concluso = a.id
                WHERE t.matricola_venditore = ?
                  AND a.tipologia = 'Vendita'
                """;
        return singleDecimal(sql, matricolaVenditore);
    }

    @Override
    public BigDecimal getMaxImportoVenditaAccettataPerVenditore(String matricolaVenditore) throws Exception {
        String sql = """
                SELECT MAX(t.importo_finale)
                FROM transazione t
                JOIN annuncio a ON t.annuncio_concluso = a.id
                WHERE t.matricola_venditore = ?
                  AND a.tipologia = 'Vendita'
                """;
        return singleDecimal(sql, matricolaVenditore);
    }

    // ======================
    // Helpers
    // ======================
    private BigDecimal singleDecimal(String sql, String matricola) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getBigDecimal(1);
            }
        }
        return null;
    }

    private Transazione mapRowToTransazione(ResultSet rs) throws Exception {
        Transazione t = new Transazione();
        t.setId(rs.getInt("id"));

        var d = rs.getDate("data");
        t.setData(d != null ? d.toLocalDate() : null);

        t.setImportoFinale(rs.getBigDecimal("importo_finale"));
        t.setAnnuncioConcluso(rs.getInt("annuncio_concluso"));

        int idOff = rs.getInt("id_offerta_accettata");
        if (rs.wasNull()) t.setIdOffertaAccettata(null);
        else t.setIdOffertaAccettata(idOff);

        t.setMatricolaVenditore(rs.getString("matricola_venditore"));
        t.setMatricolaAcquirente(rs.getString("matricola_acquirente"));

        return t;
    }
}
