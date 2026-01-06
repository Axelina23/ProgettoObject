package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.ConsegnaDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Consegna;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ConsegnaDAOImpl implements ConsegnaDAO {

    private Consegna mapRow(ResultSet rs) throws Exception {
        Consegna c = new Consegna();
        c.setId(rs.getInt("id"));
        c.setNote(rs.getString("note"));
        c.setSedeUniversita(rs.getString("sede_universita"));
        c.setFasciaOraria(rs.getString("fascia_oraria"));
        Date di = rs.getDate("data_incontro");
        c.setDataIncontro(di != null ? di.toLocalDate() : null);
        c.setSpedizione(rs.getBoolean("spedizione"));
        c.setIncontroInUni(rs.getBoolean("incontro_in_uni"));
        c.setIdTransazione(rs.getInt("id_transazione"));
        Object idIndirizzoObj = rs.getObject("id_indirizzo");
        c.setIdIndirizzo(idIndirizzoObj != null ? rs.getInt("id_indirizzo") : null);
        return c;
    }

    @Override
    public Consegna findByTransazione(int idTransazione) throws Exception {
        String sql = """
            SELECT id, note, sede_universita, fascia_oraria,
                   data_incontro, spedizione, incontro_in_uni,
                   id_transazione, id_indirizzo
            FROM consegna
            WHERE id_transazione = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTransazione);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public void creaConsegnaSpedizione(int idTransazione,
                                       int idIndirizzo,
                                       String note) throws Exception {
        String sql = """
            INSERT INTO consegna (
                note, sede_universita, fascia_oraria,
                data_incontro, spedizione, incontro_in_uni,
                id_transazione, id_indirizzo
            ) VALUES (?, NULL, NULL, NULL, TRUE, FALSE, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, note);
            ps.setInt(2, idTransazione);
            ps.setInt(3, idIndirizzo);

            ps.executeUpdate();
        }
    }

    @Override
    public void creaConsegnaInUni(int idTransazione,
                                  java.time.LocalDate dataIncontro,
                                  String sedeUniversita,
                                  String fasciaOraria,
                                  String note) throws Exception {
        String sql = """
            INSERT INTO consegna (
                note, sede_universita, fascia_oraria,
                data_incontro, spedizione, incontro_in_uni,
                id_transazione, id_indirizzo
            ) VALUES (?, ?, ?, ?, FALSE, TRUE, ?, NULL)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, note);
            ps.setString(2, sedeUniversita);
            ps.setString(3, fasciaOraria);
            ps.setDate(4, Date.valueOf(dataIncontro));
            ps.setInt(5, idTransazione);

            ps.executeUpdate();
        }
    }

    @Override
    public void insert(Consegna c) throws Exception {
        if (c == null) throw new IllegalArgumentException("Consegna null");
        if (c.getIdTransazione() == null) throw new IllegalArgumentException("idTransazione mancante");

        if (c.isSpedizione()) {
            if (c.getIdIndirizzo() == null) throw new IllegalArgumentException("idIndirizzo mancante per spedizione");
            creaConsegnaSpedizione(c.getIdTransazione(), c.getIdIndirizzo(), c.getNote());
            return;
        }

        if (c.isIncontroInUni()) {
            if (c.getDataIncontro() == null || c.getSedeUniversita() == null || c.getFasciaOraria() == null)
                throw new IllegalArgumentException("Dati incontro in uni incompleti");
            creaConsegnaInUni(c.getIdTransazione(), c.getDataIncontro(), c.getSedeUniversita(), c.getFasciaOraria(), c.getNote());
            return;
        }

        throw new IllegalArgumentException("Consegna: né spedizione né incontro in uni");
    }

}
