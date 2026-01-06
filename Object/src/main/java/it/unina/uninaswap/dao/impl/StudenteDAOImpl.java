package it.unina.uninaswap.dao.impl;

import it.unina.uninaswap.dao.StudenteDAO;
import it.unina.uninaswap.dao.util.DBConnection;
import it.unina.uninaswap.model.entity.Studente;
import it.unina.uninaswap.model.enums.SessoStudente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class StudenteDAOImpl implements StudenteDAO {

	private static final String FIND_BY_EMAIL_PW =
		    "SELECT matricola, email, nome, cognome, password, " +
		    "       sesso, " +
		    "       preferisce_spedizione, preferisce_incontro_in_uni " +
		    "FROM studente WHERE email = ? AND password = ?";


    @Override
    public Studente findByEmailAndPassword(String email, String password) throws Exception {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(FIND_BY_EMAIL_PW)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudente(rs, true);
                }
            }
        }
        return null;
    }

    @Override
    public Studente findByMatricola(String matricola) throws Exception {
        String sql = """
            SELECT matricola, email, nome, cognome, password,
                   sesso,
                   preferisce_spedizione, preferisce_incontro_in_uni
            FROM studente
            WHERE matricola = ?
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudente(rs, true);
                }
            }
        }
        return null;
    }

    private Studente mapRowToStudente(ResultSet rs, boolean includePassword) throws Exception {
        Studente s = new Studente();

        s.setMatricola(rs.getString("matricola"));
        s.setEmail(rs.getString("email"));
        s.setNome(rs.getString("nome"));
        s.setCognome(rs.getString("cognome"));

        if (includePassword) {
            s.setPassword(rs.getString("password"));
        }

        String sessoDb = rs.getString("sesso");
        if (sessoDb != null) {
            s.setSesso(SessoStudente.valueOf(sessoDb));
        } else {
            s.setSesso(null);
        }

        // niente più fotoProfiloPath: usi sempre default in base al sesso
        s.setPreferisceSpedizione(rs.getBoolean("preferisce_spedizione"));
        s.setPreferisceIncontroInUni(rs.getBoolean("preferisce_incontro_in_uni"));

        return s;
    }

    @Override
    public void update(Studente s) throws Exception {
        String sql = """
                UPDATE studente
                   SET email = ?,
                       nome = ?,
                       cognome = ?,
                       password = ?,
                       sesso = ?::sesso_studente,
                       preferisce_spedizione = ?,
                       preferisce_incontro_in_uni = ?
                 WHERE matricola = ?
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, s.getEmail());
            ps.setString(2, s.getNome());
            ps.setString(3, s.getCognome());
            ps.setString(4, s.getPassword());
            ps.setString(5, s.getSesso() != null ? s.getSesso().name() : "Altro");
            ps.setBoolean(6, s.getPreferisceSpedizione());
            ps.setBoolean(7, s.getPreferisceIncontroInUni());
            ps.setString(8, s.getMatricola());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new Exception(
                        "Nessuno studente aggiornato: matricola non trovata (" + s.getMatricola() + ")"
                );
            }
        }
    }
}
