-- =========================
-- STUDENTI (3)
-- NB: almeno una preferenza deve essere TRUE (vincolo chk_preferenze_studente)
-- =========================
INSERT INTO studente (
  matricola, email, nome, cognome, password, sesso,
  preferisce_spedizione, preferisce_incontro_in_uni
) VALUES
('000000001', 's1@example.com', 'Mario', 'Rossi', 'pass1', 'M', TRUE, TRUE),
('000000002', 's2@example.com', 'Luisa', 'Bianchi', 'pass2', 'F', TRUE, TRUE),
('000000003', 's3@example.com', 'Giorgio', 'Verdi', 'pass3', 'Altro', TRUE, TRUE);

-- =========================
-- INDIRIZZI (1 ciascuno)
-- =========================
INSERT INTO indirizzo (via, citta, cap, civico, stato, matricola_studente) VALUES
('Via Roma',      'Napoli', 80100, 10, 'Italia', '000000001'),
('Via Toledo',    'Napoli', 80134, 25, 'Italia', '000000002'),
('Via Partenope', 'Napoli', 80121,  5, 'Italia', '000000003');

-- =========================
-- ANNUNCI (12) - nessuna foto/offerta
-- NB: chk_spedizione_o_ritiro => almeno uno tra offre_spedizione/offre_incontro_in_uni deve essere TRUE
-- NB: Vendita => prezzo NOT NULL, Scambio/Regalo => prezzo NULL
-- =========================

-- Studente 1 vende/scambia/regala
INSERT INTO annuncio (
  titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
  offre_spedizione, offre_incontro_in_uni, matricola_venditore
) VALUES
('Chitarra classica', 'Buone condizioni', 'Vendita', 'Strumenti_musicali', NULL, FALSE, 120.00, TRUE, TRUE, '000000001'),
('Monitor 24"',       'Full HD',          'Vendita', 'Informatica',        NULL, FALSE, 90.00,  TRUE, TRUE, '000000001'),
('Libri Programmazione', 'Scambio libri',  'Scambio', 'Libri', 'Libro di basi di dati', FALSE, NULL, TRUE, TRUE, '000000001'),
('Felpa',               'Taglia M',       'Regalo',  'Abbigliamento', NULL, FALSE, NULL, TRUE, TRUE, '000000001');

-- Studente 2 vende/scambia/regala
INSERT INTO annuncio (
  titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
  offre_spedizione, offre_incontro_in_uni, matricola_venditore
) VALUES
('Cuffie Bluetooth', 'Ottime',        'Vendita', 'Informatica', NULL, FALSE, 80.00, TRUE, TRUE, '000000002'),
('Libro Inglese B2', 'Usato poco',    'Vendita', 'Libri',       NULL, FALSE, 25.00, TRUE, TRUE, '000000002'),
('Scrivania',        'Piccola',       'Scambio', 'Arredo',      'Sedia da studio', FALSE, NULL, TRUE, TRUE, '000000002'),
('Calcolatrice',     'Funzionante',   'Regalo',  'Altro',       NULL, FALSE, NULL, TRUE, TRUE, '000000002');

-- Studente 3 vende/scambia/regala
INSERT INTO annuncio (
  titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
  offre_spedizione, offre_incontro_in_uni, matricola_venditore
) VALUES
('Giacca',          'Taglia L',      'Vendita', 'Abbigliamento', NULL, FALSE, 45.00, TRUE, TRUE, '000000003'),
('Lampada scrivania','LED',          'Vendita', 'Arredo',        NULL, FALSE, 15.00, TRUE, TRUE, '000000003'),
('Tastiera meccanica','Scambio',     'Scambio', 'Informatica',   'Mouse gaming', FALSE, NULL, TRUE, TRUE, '000000003'),
('Appunti Analisi',  'PDF stampato', 'Regalo',  'Libri',         NULL, FALSE, NULL, TRUE, TRUE, '000000003');
