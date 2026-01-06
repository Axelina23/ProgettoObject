package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.TransazioneDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Transazione;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<Transazione> findAcquistiNonRecensiti(String matricolaAcquirente) throws Exception {
        // Seleziona le transazioni dove l'utente è acquirente E non esiste una recensione scritta da lui per quella transazione
        String sql = """
                SELECT t.id, t.data, t.importo_finale, t.annuncio_concluso,
                       t.id_offerta_accettata, t.matricola_venditore, t.matricola_acquirente
                FROM transazione t
                LEFT JOIN recensione r ON r.id_transazione = t.id AND r.autore = ?
                WHERE t.matricola_acquirente = ?
                  AND r.id IS NULL
                ORDER BY t.data DESC
                """;

        List<Transazione> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaAcquirente);
            ps.setString(2, matricolaAcquirente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToTransazione(rs));
                }
            }
        }
        return result;
    }

    // ... (Mantieni i metodi REPORT e Helpers esistenti uguali a prima) ...
    // Per brevità qui metto solo i placeholder, assicurati di non cancellare il resto del file che avevi già!
    
    @Override
    public BigDecimal getMediaImportoVenditaAccettataPerAcquirente(String m) throws Exception { return singleDecimal(queryRep("AVG", "acquirente"), m); }
    @Override
    public BigDecimal getMinImportoVenditaAccettataPerAcquirente(String m) throws Exception { return singleDecimal(queryRep("MIN", "acquirente"), m); }
    @Override
    public BigDecimal getMaxImportoVenditaAccettataPerAcquirente(String m) throws Exception { return singleDecimal(queryRep("MAX", "acquirente"), m); }
    @Override
    public BigDecimal getMediaImportoVenditaAccettataPerVenditore(String m) throws Exception { return singleDecimal(queryRep("AVG", "venditore"), m); }
    @Override
    public BigDecimal getMinImportoVenditaAccettataPerVenditore(String m) throws Exception { return singleDecimal(queryRep("MIN", "venditore"), m); }
    @Override
    public BigDecimal getMaxImportoVenditaAccettataPerVenditore(String m) throws Exception { return singleDecimal(queryRep("MAX", "venditore"), m); }

    private String queryRep(String func, String role) {
        return "SELECT " + func + "(t.importo_finale) FROM transazione t JOIN annuncio a ON t.annuncio_concluso = a.id WHERE t.matricola_" + role + " = ? AND a.tipologia = 'Vendita'";
    }

    private BigDecimal singleDecimal(String sql, String matricola) throws Exception {
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricola);
            try (ResultSet rs = ps.executeQuery()) { if (rs.next()) return rs.getBigDecimal(1); }
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
        if (rs.wasNull()) t.setIdOffertaAccettata(null); else t.setIdOffertaAccettata(idOff);
        t.setMatricolaVenditore(rs.getString("matricola_venditore"));
        t.setMatricolaAcquirente(rs.getString("matricola_acquirente"));
        return t;
    }
}