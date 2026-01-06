package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.OffertaDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Offerta;
import it.unina.uninaswap.model.enums.StatoOfferta;
import it.unina.uninaswap.model.enums.TipoCategoria;
import it.unina.uninaswap.model.enums.TipoAnnuncio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class OffertaDAOImpl implements OffertaDAO {

    @Override
    public Offerta findById(int id) throws Exception {
        String sql = """
                SELECT id, data, stato, importo_offerto, messaggio, oggetto_offerto,
                       id_annuncio, matricola_offerente
                FROM offerta
                WHERE id = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRowToOfferta(rs);
            }
        }
        return null;
    }

    @Override
    public List<Offerta> findRicevuteInAttesaPerVenditore(String matricolaVenditore) throws Exception {
        String sql = """
                SELECT o.id, o.data, o.stato, o.importo_offerto, o.messaggio, o.oggetto_offerto,
                       o.id_annuncio, o.matricola_offerente
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE a.matricola_venditore = ?
                  AND o.stato = 'In_Attesa'
                ORDER BY o.data DESC, o.id DESC
                """;

        List<Offerta> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaVenditore);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRowToOfferta(rs));
            }
        }
        return result;
    }

    @Override
    public List<Offerta> findInviateInAttesaPerOfferente(String matricolaOfferente) throws Exception {
        String sql = """
                SELECT o.id, o.data, o.stato, o.importo_offerto, o.messaggio, o.oggetto_offerto,
                       o.id_annuncio, o.matricola_offerente
                FROM offerta o
                WHERE o.matricola_offerente = ?
                  AND o.stato = 'In_Attesa'
                ORDER BY o.data DESC, o.id DESC
                """;

        List<Offerta> result = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaOfferente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) result.add(mapRowToOfferta(rs));
            }
        }
        return result;
    }

    @Override
    public void updateStato(int idOfferta, String nuovoStato) throws Exception {
        String sql = "UPDATE offerta SET stato = ?::stato_offerta WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoStato);   // "In_Attesa" / "Accettata" / "Rifiutata"
            ps.setInt(2, idOfferta);

            ps.executeUpdate();
        }
    }

    @Override
    public void rifiutaAltreOfferteStessoAnnuncio(int idAnnuncio, int idOffertaDaTenere) throws Exception {
        // usato per: quando una è accettata -> le altre devono diventare rifiutate
        String sql = """
                UPDATE offerta
                   SET stato = 'Rifiutata'::stato_offerta
                 WHERE id_annuncio = ?
                   AND id <> ?
                   AND stato = 'In_Attesa'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idAnnuncio);
            ps.setInt(2, idOffertaDaTenere);
            ps.executeUpdate();
        }
    }

    @Override
    public void updateContenuto(Offerta o) throws Exception {
        String sql = """
                UPDATE offerta
                   SET importo_offerto = ?,
                       messaggio = ?,
                       oggetto_offerto = ?
                 WHERE id = ?
                   AND stato = 'In_Attesa'
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (o.getImportoOfferto() != null) ps.setBigDecimal(1, o.getImportoOfferto());
            else ps.setNull(1, java.sql.Types.NUMERIC);

            if (o.getMessaggio() != null && !o.getMessaggio().isBlank()) ps.setString(2, o.getMessaggio());
            else ps.setNull(2, java.sql.Types.VARCHAR);

            if (o.getOggettoOfferto() != null && !o.getOggettoOfferto().isBlank()) ps.setString(3, o.getOggettoOfferto());
            else ps.setNull(3, java.sql.Types.VARCHAR);

            ps.setInt(4, o.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new Exception("Offerta non trovata o non in stato 'In_Attesa' (id=" + o.getId() + ").");
            }
        }
    }

    @Override
    public void insert(Offerta o) throws Exception {
        String sql = """
                INSERT INTO offerta
                    (data, stato, importo_offerto, messaggio, oggetto_offerto, id_annuncio, matricola_offerente)
                VALUES
                    (CURRENT_DATE, 'In_Attesa'::stato_offerta, ?, ?, ?, ?, ?)
                RETURNING id
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            int i = 1;

            if (o.getImportoOfferto() != null) ps.setBigDecimal(i++, o.getImportoOfferto());
            else ps.setNull(i++, java.sql.Types.NUMERIC);

            if (o.getMessaggio() != null && !o.getMessaggio().isBlank()) ps.setString(i++, o.getMessaggio());
            else ps.setNull(i++, java.sql.Types.VARCHAR);

            if (o.getOggettoOfferto() != null && !o.getOggettoOfferto().isBlank()) ps.setString(i++, o.getOggettoOfferto());
            else ps.setNull(i++, java.sql.Types.VARCHAR);

            ps.setInt(i++, o.getIdAnnuncio());
            ps.setString(i++, o.getMatricolaOfferente());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) o.setId(rs.getInt(1));
            }
        }
    }

    // ======================
    // REPORT - VENDITE
    // ======================
    @Override
    public int countOfferteArrivatePerCategoria(String matricolaVenditore, TipoCategoria categoria) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE a.matricola_venditore = ?
                  AND a.categoria = ?::tipo_categoria
                """;

        return singleCount(sql, matricolaVenditore, categoria.name());
    }

    @Override
    public int countVenditeAccettatePerCategoria(String matricolaVenditore, TipoCategoria categoria) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE a.matricola_venditore = ?
                  AND a.categoria = ?::tipo_categoria
                  AND o.stato = 'Accettata'
                """;

        return singleCount(sql, matricolaVenditore, categoria.name());
    }

    // ======================
    // REPORT - ACQUISTI
    // ======================
    @Override
    public int countOfferteInviatePerCategoria(String matricolaOfferente, TipoCategoria categoria) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE o.matricola_offerente = ?
                  AND a.categoria = ?::tipo_categoria
                """;

        return singleCount(sql, matricolaOfferente, categoria.name());
    }

    @Override
    public int countAcquistiAccettatiPerCategoria(String matricolaOfferente, TipoCategoria categoria) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE o.matricola_offerente = ?
                  AND a.categoria = ?::tipo_categoria
                  AND o.stato = 'Accettata'
                """;

        return singleCount(sql, matricolaOfferente, categoria.name());
    }

    // ======================
    // Helpers
    // ======================
    private int singleCount(String sql, String matricola, String categoriaName) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);
            ps.setString(2, categoriaName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        }
        return 0;
    }

    private Offerta mapRowToOfferta(ResultSet rs) throws Exception {
        Offerta o = new Offerta();

        o.setId(rs.getInt("id"));

        var d = rs.getDate("data");
        o.setData(d != null ? d.toLocalDate() : null);

        String statoStr = rs.getString("stato");
        o.setStato(statoStr != null ? StatoOfferta.valueOf(statoStr) : null);

        o.setOggettoOfferto(rs.getString("oggetto_offerto"));
        o.setImportoOfferto(rs.getBigDecimal("importo_offerto"));
        o.setMessaggio(rs.getString("messaggio"));
        o.setIdAnnuncio(rs.getInt("id_annuncio"));
        o.setMatricolaOfferente(rs.getString("matricola_offerente"));

        return o;
    }
    
    @Override
    public int countOfferteArrivatePerTipologia(String matricolaVenditore, TipoAnnuncio tipologia) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE a.matricola_venditore = ?
                  AND a.tipologia = ?::tipo_annuncio
                """;
        return singleCountTipologia(sql, matricolaVenditore, tipologia.name());
    }

    @Override
    public int countVenditeAccettatePerTipologia(String matricolaVenditore, TipoAnnuncio tipologia) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE a.matricola_venditore = ?
                  AND a.tipologia = ?::tipo_annuncio
                  AND o.stato = 'Accettata'
                """;
        return singleCountTipologia(sql, matricolaVenditore, tipologia.name());
    }

    @Override
    public int countOfferteInviatePerTipologia(String matricolaOfferente, TipoAnnuncio tipologia) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE o.matricola_offerente = ?
                  AND a.tipologia = ?::tipo_annuncio
                """;
        return singleCountTipologia(sql, matricolaOfferente, tipologia.name());
    }

    @Override
    public int countAcquistiAccettatiPerTipologia(String matricolaOfferente, TipoAnnuncio tipologia) throws Exception {
        String sql = """
                SELECT COUNT(*) AS cnt
                FROM offerta o
                JOIN annuncio a ON o.id_annuncio = a.id
                WHERE o.matricola_offerente = ?
                  AND a.tipologia = ?::tipo_annuncio
                  AND o.stato = 'Accettata'
                """;
        return singleCountTipologia(sql, matricolaOfferente, tipologia.name());
    }

    // helper dedicato (evita conflitti con l'altro helper categoria)
    private int singleCountTipologia(String sql, String matricola, String tipologiaName) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);
            ps.setString(2, tipologiaName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("cnt");
            }
        }
        return 0;
    }

}
