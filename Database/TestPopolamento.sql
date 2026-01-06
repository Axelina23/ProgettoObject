BEGIN;

-- =========================
-- STUDENTI
-- =========================
INSERT INTO studente (
  matricola, email, nome, cognome, password,
  sesso,
  preferisce_spedizione, preferisce_incontro_in_uni
) VALUES
('000000001', 'test1@exemple.com', 'test', 'uno', 'test1', 'M', TRUE, TRUE),
('000000002', 'test2@exemple.com', 'test', 'due', 'test2', 'F', TRUE, TRUE);

-- =========================
-- INDIRIZZI (1 ciascuno)
-- =========================
INSERT INTO indirizzo(via, citta, cap, civico, stato, matricola_studente) VALUES
('Via Roma',   'Napoli', 80100, 10, 'Italia', '000000001'),
('Via Toledo', 'Napoli', 80134, 25, 'Italia', '000000002');

-- =========================
-- ANNUNCI ATTIVI (12) - NO FOTO
-- =========================
-- Studente 1 (1..6)
INSERT INTO annuncio (id, titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
                      offre_spedizione, offre_incontro_in_uni, matricola_venditore)
VALUES
(1, 'Chitarra classica', 'Buone condizioni', 'Vendita', 'Strumenti_musicali', NULL, FALSE, 120.00, TRUE, TRUE, '000000001'),
(2, 'Monitor 24 pollici', 'Full HD', 'Vendita', 'Informatica', NULL, FALSE, 90.00, TRUE, TRUE, '000000001'),
(3, 'Scambio libri Programmazione', 'Scambio con libri simili', 'Scambio', 'Libri', 'Libro di basi di dati', FALSE, NULL, TRUE, TRUE, '000000001'),
(4, 'Scambio tastiera meccanica', 'Layout ITA', 'Scambio', 'Informatica', 'Mouse gaming', FALSE, NULL, TRUE, TRUE, '000000001'),
(5, 'Regalo appunti Analisi', 'PDF stampato', 'Regalo', 'Libri', NULL, FALSE, NULL, TRUE, TRUE, '000000001'),
(6, 'Regalo felpa', 'Taglia M', 'Regalo', 'Abbigliamento', NULL, FALSE, NULL, TRUE, TRUE, '000000001');

-- Studente 2 (7..12)
INSERT INTO annuncio (id, titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
                      offre_spedizione, offre_incontro_in_uni, matricola_venditore)
VALUES
(7, 'Cuffie Bluetooth', 'Ottime', 'Vendita', 'Informatica', NULL, FALSE, 80.00, TRUE, TRUE, '000000002'),
(8, 'Libro Inglese B2', 'Usato poco', 'Vendita', 'Libri', NULL, FALSE, 25.00, TRUE, TRUE, '000000002'),
(9, 'Scambio scrivania', 'Piccola', 'Scambio', 'Arredo', 'Sedia da studio', FALSE, NULL, TRUE, TRUE, '000000002'),
(10,'Scambio giacca', 'Taglia L', 'Scambio', 'Abbigliamento', 'Felpa', FALSE, NULL, TRUE, TRUE, '000000002'),
(11,'Regalo calcolatrice', 'Funzionante', 'Regalo', 'Altro', NULL, FALSE, NULL, TRUE, TRUE, '000000002'),
(12,'Regalo lampada', 'Da scrivania', 'Regalo', 'Arredo', NULL, FALSE, NULL, TRUE, TRUE, '000000002');

-- =========================
-- OFFERTE IN ATTESA su attivi
-- =========================
INSERT INTO offerta (id, stato, importo_offerto, messaggio, oggetto_offerto, id_annuncio, matricola_offerente)
VALUES
(1, 'In_Attesa', 95.00, 'Ciao, posso offrirti 95€?', NULL, 1, '000000002'),
(2, 'In_Attesa', NULL, 'Ti interessa questo scambio?', 'Libro "Clean Code" (usato)', 3, '000000002'),
(3, 'In_Attesa', NULL, 'Mi servirebbero per studiare, posso prenderli in uni?', NULL, 5, '000000002'),
(4, 'In_Attesa', 60.00, 'Ciao, ti va bene 60€?', NULL, 7, '000000001'),
(5, 'In_Attesa', NULL, 'Propongo questo oggetto in scambio', 'Mouse Logitech G203 (ottimo stato)', 9, '000000001'),
(6, 'In_Attesa', NULL, 'Posso passare in uni a prenderla?', NULL, 11, '000000001');

-- =========================
-- ANNUNCI DA CONCLUDERE (12)
-- =========================
-- Studente1 (13..18)
INSERT INTO annuncio (id, titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
                      offre_spedizione, offre_incontro_in_uni, matricola_venditore)
VALUES
(13,'Laptop usato', 'Funzionante', 'Vendita', 'Informatica', NULL, FALSE, 300.00, TRUE, TRUE, '000000001'),
(14,'Libro Matematica', 'Buono', 'Vendita', 'Libri', NULL, FALSE, 20.00, TRUE, TRUE, '000000001'),
(15,'Scambio cuffie', 'Scambio', 'Scambio', 'Informatica', 'Powerbank', FALSE, NULL, TRUE, TRUE, '000000001'),
(16,'Scambio sedia', 'Scambio', 'Scambio', 'Arredo', 'Scrivania', FALSE, NULL, TRUE, TRUE, '000000001'),
(17,'Regalo quaderno', 'Nuovo', 'Regalo', 'Libri', NULL, FALSE, NULL, TRUE, TRUE, '000000001'),
(18,'Regalo zaino', 'Usato', 'Regalo', 'Abbigliamento', NULL, FALSE, NULL, TRUE, TRUE, '000000001');

-- Studente2 (19..24)
INSERT INTO annuncio (id, titolo, descrizione, tipologia, categoria, oggetto_richiesto, concluso, prezzo,
                      offre_spedizione, offre_incontro_in_uni, matricola_venditore)
VALUES
(19,'Tablet', 'Ottimo', 'Vendita', 'Informatica', NULL, FALSE, 150.00, TRUE, TRUE, '000000002'),
(20,'Giubbotto', 'Taglia M', 'Vendita', 'Abbigliamento', NULL, FALSE, 40.00, TRUE, TRUE, '000000002'),
(21,'Scambio libro Java', 'Scambio', 'Scambio', 'Libri', 'Libro SQL', FALSE, NULL, TRUE, TRUE, '000000002'),
(22,'Scambio lampada', 'Scambio', 'Scambio', 'Arredo', 'Piantana', FALSE, NULL, TRUE, TRUE, '000000002'),
(23,'Regalo mousepad', 'Nuovo', 'Regalo', 'Informatica', NULL, FALSE, NULL, TRUE, TRUE, '000000002'),
(24,'Regalo penne', 'Set', 'Regalo', 'Altro', NULL, FALSE, NULL, TRUE, TRUE, '000000002');

-- =========================
-- OFFERTE (12) che verranno ACCETTATE
-- =========================
INSERT INTO offerta (id, stato, importo_offerto, messaggio, oggetto_offerto, id_annuncio, matricola_offerente) VALUES
(101, 'In_Attesa', 270.00, 'Ti va bene 270€?', NULL, 13, '000000002'),
(102, 'In_Attesa', 18.00,  'Posso offrire 18€?', NULL, 14, '000000002'),
(103, 'In_Attesa', NULL,   'Propongo scambio', 'Powerbank Anker 10.000mAh', 15, '000000002'),
(104, 'In_Attesa', NULL,   'Propongo scambio', 'Scrivania IKEA (piccola)', 16, '000000002'),
(105, 'In_Attesa', NULL,   'Posso prenderlo in uni?', NULL, 17, '000000002'),
(106, 'In_Attesa', NULL,   'Posso passare oggi?', NULL, 18, '000000002'),

(107, 'In_Attesa', 140.00, 'Ok per 140€?', NULL, 19, '000000001'),
(108, 'In_Attesa', 35.00,  'Ti va bene 35€?', NULL, 20, '000000001'),
(109, 'In_Attesa', NULL,   'Propongo scambio', 'Libro SQL + esercizi', 21, '000000001'),
(110, 'In_Attesa', NULL,   'Propongo scambio', 'Piantana da salotto', 22, '000000001'),
(111, 'In_Attesa', NULL,   'Posso prenderlo?', NULL, 23, '000000001'),
(112, 'In_Attesa', NULL,   'Posso passare in dipartimento?', NULL, 24, '000000001');

-- Accetto (scatta trigger: crea transazione + conclude annuncio)
UPDATE offerta
SET stato = 'Accettata'
WHERE id IN (101,102,103,104,105,106,107,108,109,110,111,112);

-- =========================
-- CONSEGNE per tutte le transazioni create
-- Vendita -> spedizione (usa indirizzo acquirente, nessuna data)
-- Scambio/Regalo -> incontro in uni (data_incontro + fascia_oraria)
-- =========================
INSERT INTO consegna (
  note,
  sede_universita, data_incontro, fascia_oraria,
  spedizione, incontro_in_uni,
  id_transazione, id_indirizzo
)
SELECT
  CASE WHEN a.tipologia = 'Vendita'
       THEN 'Spedizione all''indirizzo dell''acquirente'
       ELSE 'Incontro in università'
  END AS note,
  CASE WHEN a.tipologia <> 'Vendita'
       THEN 'Università Federico II - Monte Sant''Angelo'
       ELSE NULL
  END AS sede_universita,
  CASE WHEN a.tipologia <> 'Vendita'
       THEN CURRENT_DATE
       ELSE NULL
  END AS data_incontro,
  CASE WHEN a.tipologia <> 'Vendita'
       THEN '10:00 - 12:00'
       ELSE NULL
  END AS fascia_oraria,
  CASE WHEN a.tipologia = 'Vendita' THEN TRUE ELSE FALSE END AS spedizione,
  CASE WHEN a.tipologia <> 'Vendita' THEN TRUE ELSE FALSE END AS incontro_in_uni,
  t.id,
  CASE WHEN a.tipologia = 'Vendita' THEN (
      SELECT i.id
      FROM indirizzo i
      WHERE i.matricola_studente = t.matricola_acquirente
      ORDER BY i.id
      LIMIT 1
  ) ELSE NULL END AS id_indirizzo
FROM transazione t
JOIN annuncio a ON a.id = t.annuncio_concluso
WHERE a.id IN (13,14,15,16,17,18,19,20,21,22,23,24);

-- =========================
-- RECENSIONI
-- =========================
INSERT INTO recensione (titolo, corpo, valutazione, id_transazione, autore, recensito)
SELECT
  'Ottima esperienza',
  'Scambio/consegna rapida, tutto ok.',
  5,
  t.id,
  t.matricola_acquirente,
  t.matricola_venditore
FROM transazione t
WHERE t.annuncio_concluso IN (13,14,15,16,17,18,19,20,21,22,23,24);

-- =========================
-- ALLINEAMENTO SEQUENZE
-- =========================
SELECT setval(pg_get_serial_sequence('annuncio','id'),   (SELECT COALESCE(MAX(id),1) FROM annuncio), true);
SELECT setval(pg_get_serial_sequence('offerta','id'),    (SELECT COALESCE(MAX(id),1) FROM offerta), true);
SELECT setval(pg_get_serial_sequence('foto','id'),       (SELECT COALESCE(MAX(id),1) FROM foto), true);
SELECT setval(pg_get_serial_sequence('indirizzo','id'),  (SELECT COALESCE(MAX(id),1) FROM indirizzo), true);
SELECT setval(pg_get_serial_sequence('consegna','id'),   (SELECT COALESCE(MAX(id),1) FROM consegna), true);
SELECT setval(pg_get_serial_sequence('recensione','id'), (SELECT COALESCE(MAX(id),1) FROM recensione), true);

COMMIT;
