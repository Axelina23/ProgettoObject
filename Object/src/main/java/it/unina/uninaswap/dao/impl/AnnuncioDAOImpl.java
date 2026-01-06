package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.AnnuncioDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Annuncio;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AnnuncioDAOImpl implements AnnuncioDAO {

    @Override
    public List<Annuncio> findAttivi(String search,
                                    String categoria,
                                    String tipologia,
                                    BigDecimal prezzoMin,
                                    BigDecimal prezzoMax,
                                    Boolean offreSpedizione,
                                    Boolean offreInUni) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, titolo, descrizione, data_pubblicazione, tipologia, categoria, ")
           .append("       oggetto_richiesto, concluso, prezzo, offre_spedizione, offre_incontro_in_uni, ")
           .append("       matricola_venditore ")
           .append("FROM annuncio ")
           .append("WHERE concluso = FALSE ");

        List<Object> params = new ArrayList<>();

        // search su titolo/descrizione
        if (search != null && !search.isBlank()) {
            sql.append("AND (titolo ILIKE ? OR descrizione ILIKE ?) ");
            String like = "%" + search.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (categoria != null && !categoria.isBlank()) {
            sql.append("AND categoria = ?::tipo_categoria ");
            params.add(categoria);
        }

        if (tipologia != null && !tipologia.isBlank()) {
            sql.append("AND tipologia = ?::tipo_annuncio ");
            params.add(tipologia);
        }

        // Filtri prezzo: di fatto per Vendita (prezzo NOT NULL)
        if (prezzoMin != null) {
            sql.append("AND prezzo IS NOT NULL AND prezzo >= ? ");
            params.add(prezzoMin);
        }
        if (prezzoMax != null) {
            sql.append("AND prezzo IS NOT NULL AND prezzo <= ? ");
            params.add(prezzoMax);
        }

        if (offreSpedizione != null) {
            sql.append("AND offre_spedizione = ? ");
            params.add(offreSpedizione);
        }

        if (offreInUni != null) {
            sql.append("AND offre_incontro_in_uni = ? ");
            params.add(offreInUni);
        }

        sql.append("ORDER BY data_pubblicazione DESC, id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;
            for (Object p : params) {
                if (p instanceof BigDecimal bd) {
                    ps.setBigDecimal(i++, bd);
                } else if (p instanceof Boolean b) {
                    ps.setBoolean(i++, b);
                } else {
                    ps.setString(i++, p.toString());
                }
            }

            List<Annuncio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToAnnuncio(rs));
                }
            }
            return result;
        }
    }
    
    
    @Override
    public List<Annuncio> findAttiviEsclusoStudente(
            String matricola,
            String search,
            String categoria,
            String tipologia,
            BigDecimal prezzoMin,
            BigDecimal prezzoMax,
            Boolean offreSpedizione,
            Boolean offreInUni
    ) throws Exception {

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT id, titolo, descrizione, data_pubblicazione, tipologia, categoria, ")
           .append("       oggetto_richiesto, concluso, prezzo, offre_spedizione, offre_incontro_in_uni, ")
           .append("       matricola_venditore ")
           .append("FROM annuncio a ")
           .append("WHERE a.concluso = FALSE ")
           .append("  AND a.matricola_venditore <> ? ");

        List<Object> params = new ArrayList<>();

        // 👉 PRIMO parametro: matricola da escludere
        params.add(matricola);

        // search su titolo/descrizione
        if (search != null && !search.isBlank()) {
            sql.append("AND (a.titolo ILIKE ? OR a.descrizione ILIKE ?) ");
            String like = "%" + search.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (categoria != null && !categoria.isBlank()) {
            sql.append("AND a.categoria = ?::tipo_categoria ");
            params.add(categoria);
        }

        if (tipologia != null && !tipologia.isBlank()) {
            sql.append("AND a.tipologia = ?::tipo_annuncio ");
            params.add(tipologia);
        }

        if (prezzoMin != null) {
            sql.append("AND a.prezzo IS NOT NULL AND a.prezzo >= ? ");
            params.add(prezzoMin);
        }
        if (prezzoMax != null) {
            sql.append("AND a.prezzo IS NOT NULL AND a.prezzo <= ? ");
            params.add(prezzoMax);
        }

        if (offreSpedizione != null) {
            sql.append("AND a.offre_spedizione = ? ");
            params.add(offreSpedizione);
        }

        if (offreInUni != null) {
            sql.append("AND a.offre_incontro_in_uni = ? ");
            params.add(offreInUni);
        }

        sql.append("ORDER BY a.data_pubblicazione DESC, a.id DESC");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int i = 1;
            for (Object p : params) {
                if (p instanceof BigDecimal bd) {
                    ps.setBigDecimal(i++, bd);
                } else if (p instanceof Boolean b) {
                    ps.setBoolean(i++, b);
                } else {
                    ps.setString(i++, p.toString());
                }
            }

            List<Annuncio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToAnnuncio(rs));
                }
            }
            return result;
        }
    }


	
    @Override
    public Annuncio findById(int id) throws Exception {
        String sql = "SELECT id, titolo, descrizione, data_pubblicazione, tipologia, categoria, " +
                     "       oggetto_richiesto, concluso, prezzo, offre_spedizione, offre_incontro_in_uni, " +
                     "       matricola_venditore " +
                     "FROM annuncio WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAnnuncio(rs);
                }
            }
        }
        return null;
    }

    
    private Annuncio mapRowToAnnuncio(ResultSet rs) throws Exception {
        Annuncio a = new Annuncio();

        a.setId(rs.getInt("id")); // id non null in tabella
        a.setTitolo(rs.getString("titolo"));
        a.setDescrizione(rs.getString("descrizione"));

        var d = rs.getDate("data_pubblicazione");
        a.setDataPubblicazione(d != null ? d.toLocalDate() : null);

        a.setTipologia(rs.getString("tipologia"));
        a.setCategoria(rs.getString("categoria"));

        a.setOggettoRichiesto(rs.getString("oggetto_richiesto"));
        a.setConcluso(rs.getBoolean("concluso"));
        a.setPrezzo(rs.getBigDecimal("prezzo"));

        a.setOffreSpedizione(rs.getBoolean("offre_spedizione"));
        a.setOffreIncontroInUni(rs.getBoolean("offre_incontro_in_uni"));

        a.setMatricolaVenditore(rs.getString("matricola_venditore"));

        return a;
    }

    @Override
    public List<Annuncio> findByStudente(String matricolaStudente) throws Exception {
        if (matricolaStudente == null || matricolaStudente.isBlank()) {
            return new ArrayList<>();
        }

        String sql =
            "SELECT id, titolo, descrizione, data_pubblicazione, tipologia, categoria, " +
            "       oggetto_richiesto, concluso, prezzo, offre_spedizione, offre_incontro_in_uni, " +
            "       matricola_venditore " +
            "FROM annuncio " +
            "WHERE matricola_venditore = ? " +
            "  AND concluso = FALSE " +              // 👈 solo annunci NON conclusi
            "ORDER BY data_pubblicazione DESC, id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaStudente);

            List<Annuncio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToAnnuncio(rs));
                }
            }
            return result;
        }
    }

    @Override
    public List<Annuncio> findUltimiByStudente(String matricolaStudente, int limit) throws Exception {
        if (matricolaStudente == null || matricolaStudente.isBlank() || limit <= 0) {
            return new ArrayList<>();
        }

        String sql =
            "SELECT id, titolo, descrizione, data_pubblicazione, tipologia, categoria, " +
            "       oggetto_richiesto, concluso, prezzo, offre_spedizione, offre_incontro_in_uni, " +
            "       matricola_venditore " +
            "FROM annuncio " +
            "WHERE matricola_venditore = ? " +
            "  AND concluso = FALSE " +              // 👈 anche qui solo NON conclusi
            "ORDER BY data_pubblicazione DESC, id DESC " +
            "LIMIT ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaStudente);
            ps.setInt(2, limit);

            List<Annuncio> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRowToAnnuncio(rs));
                }
            }
            return result;
        }
    }

    @Override
    public void update(Annuncio a) throws Exception {
        String sql =
            "UPDATE annuncio " +
            "SET titolo = ?, " +
            "    descrizione = ?, " +
            "    tipologia = ?::tipo_annuncio, " +
            "    categoria = ?::tipo_categoria, " +
            "    oggetto_richiesto = ?, " +
            "    prezzo = ?, " +
            "    offre_spedizione = ?, " +
            "    offre_incontro_in_uni = ? " +
            "WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getTitolo());
            ps.setString(2, a.getDescrizione());
            ps.setString(3, a.getTipologia());
            ps.setString(4, a.getCategoria());
            ps.setString(5, a.getOggettoRichiesto());

            if (a.getPrezzo() != null) {
                ps.setBigDecimal(6, a.getPrezzo());
            } else {
                ps.setNull(6, java.sql.Types.NUMERIC);
            }

            // 👇 niente più null-check, usiamo il boolean direttamente
            ps.setBoolean(7, a.isOffreSpedizione());
            ps.setBoolean(8, a.isOffreIncontroInUni());

            ps.setInt(9, a.getId());

            ps.executeUpdate();
        }
    }

    
    @Override
    public void insert(Annuncio a) throws Exception {
        String sql = """
                INSERT INTO annuncio
                    (titolo,
                     descrizione,
                     tipologia,
                     categoria,
                     oggetto_richiesto,
                     concluso,
                     prezzo,
                     offre_spedizione,
                     offre_incontro_in_uni,
                     matricola_venditore)
                VALUES
                    (?, ?, ?::tipo_annuncio, ?::tipo_categoria, ?,
                     FALSE, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;

            // titolo (NOT NULL)
            ps.setString(i++, a.getTitolo());

            // descrizione (può essere null)
            if (a.getDescrizione() != null && !a.getDescrizione().isBlank()) {
                ps.setString(i++, a.getDescrizione());
            } else {
                ps.setNull(i++, java.sql.Types.VARCHAR);
            }

            // tipologia: deve combaciare con enum Postgres ('Vendita','Scambio','Regalo')
            ps.setString(i++, a.getTipologia());

            // categoria: enum Postgres ('Libri','Informatica',...)
            ps.setString(i++, a.getCategoria());

            // oggetto_richiesto (solo Scambio; per Vendita/Regalo normalmente lo lasci vuoto)
            if (a.getOggettoRichiesto() != null && !a.getOggettoRichiesto().isBlank()) {
                ps.setString(i++, a.getOggettoRichiesto());
            } else {
                ps.setNull(i++, java.sql.Types.VARCHAR);
            }

            // concluso = FALSE (già messo nel VALUES)

            // prezzo (solo Vendita; per gli altri deve essere NULL -> pensaci nel dialog)
            if (a.getPrezzo() != null) {
                ps.setBigDecimal(i++, a.getPrezzo());
            } else {
                ps.setNull(i++, java.sql.Types.NUMERIC);
            }

            // offre_spedizione / offre_incontro_in_uni: boolean primitivi
            ps.setBoolean(i++, a.isOffreSpedizione());
            ps.setBoolean(i++, a.isOffreIncontroInUni());

            // matricola_venditore (NOT NULL)
            ps.setString(i++, a.getMatricolaVenditore());

            // leggo l'id generato
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    a.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void setConcluso(int idAnnuncio, boolean concluso) throws Exception {
        String sql = "UPDATE annuncio SET concluso = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, concluso);
            ps.setInt(2, idAnnuncio);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new Exception("Annuncio non trovato (id=" + idAnnuncio + ")");
            }
        }
    }

    @Override
    public boolean delete(int idAnnuncio, String matricolaVenditore) throws Exception {
        String sql = "DELETE FROM annuncio " +
                     "WHERE id = ? AND matricola_venditore = ? AND concluso = FALSE";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idAnnuncio);
            ps.setString(2, matricolaVenditore);

            int rows = ps.executeUpdate();
            return rows == 1;
        }
    }

}
