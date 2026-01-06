-- ============================================================
-- ENUM
-- ============================================================
CREATE TYPE tipo_annuncio  AS ENUM ('Vendita', 'Scambio', 'Regalo');
CREATE TYPE stato_offerta  AS ENUM ('In_Attesa', 'Accettata', 'Rifiutata');
CREATE TYPE tipo_categoria AS ENUM ('Strumenti_musicali','Libri','Informatica','Abbigliamento','Arredo','Altro');
CREATE TYPE sesso_studente AS ENUM ('M', 'F', 'Altro');

-- ============================================================
-- TABELLE
-- ============================================================

-- studente
CREATE TABLE studente (
    matricola VARCHAR(9) PRIMARY KEY,
    email VARCHAR(50) NOT NULL UNIQUE,
    nome VARCHAR(50) NOT NULL,
    cognome VARCHAR(50) NOT NULL,
    password TEXT NOT NULL,
    sesso sesso_studente NOT NULL DEFAULT 'Altro',
    preferisce_spedizione BOOLEAN NOT NULL DEFAULT FALSE,
    preferisce_incontro_in_uni BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT chk_preferenze_studente
        CHECK (preferisce_spedizione IS TRUE OR preferisce_incontro_in_uni IS TRUE)
);

-- indirizzo
CREATE TABLE indirizzo (
    id SERIAL PRIMARY KEY,
    via VARCHAR(100) NOT NULL,
    citta VARCHAR(50) NOT NULL,
    cap INT NOT NULL,
    civico INT NOT NULL,
    stato VARCHAR(50) NOT NULL,

    matricola_studente VARCHAR(9) NOT NULL,
    CONSTRAINT fk_indirizzo_studente
        FOREIGN KEY (matricola_studente) REFERENCES studente(matricola)
        ON DELETE CASCADE,

    CONSTRAINT uk_indirizzo_per_studente
        UNIQUE (matricola_studente, via, citta, cap, civico, stato)
);

-- annuncio
CREATE TABLE annuncio (
    id SERIAL PRIMARY KEY,
    titolo VARCHAR(150) NOT NULL,
    descrizione TEXT,
    data_pubblicazione DATE NOT NULL DEFAULT CURRENT_DATE,
    tipologia tipo_annuncio NOT NULL,
    categoria  tipo_categoria NOT NULL,
    oggetto_richiesto TEXT,
    concluso BOOLEAN NOT NULL DEFAULT FALSE,
    prezzo NUMERIC(9,2),
    offre_spedizione BOOLEAN NOT NULL DEFAULT FALSE,
    offre_incontro_in_uni BOOLEAN NOT NULL DEFAULT FALSE,
    matricola_venditore VARCHAR(9) NOT NULL,

    CONSTRAINT fk_annuncio_venditore
        FOREIGN KEY (matricola_venditore) REFERENCES studente(matricola)
        ON DELETE CASCADE,

    CONSTRAINT chk_spedizione_o_ritiro
        CHECK (offre_spedizione IS TRUE OR offre_incontro_in_uni IS TRUE),

    CONSTRAINT chk_prezzo_per_tipologia
        CHECK (
          (tipologia = 'Vendita'  AND prezzo IS NOT NULL AND prezzo >= 0)
          OR
          (tipologia <> 'Vendita' AND prezzo IS NULL)
        )
);

-- foto
CREATE TABLE foto (
    id SERIAL PRIMARY KEY,
    path TEXT NOT NULL,
    is_principale BOOLEAN NOT NULL DEFAULT FALSE,
    id_annuncio INT NOT NULL,

    CONSTRAINT fk_foto_annuncio
        FOREIGN KEY (id_annuncio) REFERENCES annuncio(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_foto_path_not_empty
        CHECK (btrim(path) <> '')
);

-- offerta
CREATE TABLE offerta (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    stato stato_offerta NOT NULL DEFAULT 'In_Attesa',

    importo_offerto NUMERIC(9,2),
    messaggio TEXT,
    oggetto_offerto TEXT,

    id_annuncio INT NOT NULL,
    matricola_offerente VARCHAR(9) NOT NULL,

    CONSTRAINT fk_offerta_annuncio
        FOREIGN KEY (id_annuncio) REFERENCES annuncio(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_offerta_studente
        FOREIGN KEY (matricola_offerente) REFERENCES studente(matricola)
        ON DELETE CASCADE,

    CONSTRAINT chk_importo_nonnegativo
        CHECK (importo_offerto IS NULL OR importo_offerto >= 0)
);

-- transazione
CREATE TABLE transazione (
    id SERIAL PRIMARY KEY,
    data DATE NOT NULL DEFAULT CURRENT_DATE,
    importo_finale NUMERIC(9,2) CHECK (importo_finale IS NULL OR importo_finale >= 0),
    annuncio_concluso INT NOT NULL,
    id_offerta_accettata INT,
    matricola_venditore VARCHAR(9) NOT NULL,
    matricola_acquirente VARCHAR(9) NOT NULL,

    CONSTRAINT fk_trans_annuncio
        FOREIGN KEY (annuncio_concluso) REFERENCES annuncio(id),

    CONSTRAINT fk_trans_offerta
        FOREIGN KEY (id_offerta_accettata) REFERENCES offerta(id)
        ON DELETE SET NULL,

    CONSTRAINT fk_trans_venditore
        FOREIGN KEY (matricola_venditore) REFERENCES studente(matricola),

    CONSTRAINT fk_trans_acquirente
        FOREIGN KEY (matricola_acquirente) REFERENCES studente(matricola)
);

-- consegna (SEMPLIFICATA)
CREATE TABLE consegna (
    id SERIAL PRIMARY KEY,

    note TEXT,

    -- solo per INCONTRO IN UNI
    sede_universita VARCHAR(150),
    data_incontro DATE,
    fascia_oraria VARCHAR(50),

    -- modalità
    spedizione BOOLEAN NOT NULL DEFAULT FALSE,
    incontro_in_uni BOOLEAN NOT NULL DEFAULT FALSE,

    id_transazione INT NOT NULL UNIQUE,
    id_indirizzo INT,

    CONSTRAINT fk_consegna_transazione
        FOREIGN KEY (id_transazione) REFERENCES transazione(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_consegna_indirizzo
        FOREIGN KEY (id_indirizzo) REFERENCES indirizzo(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_consegna_modalita
    CHECK (
        (spedizione = TRUE AND incontro_in_uni = FALSE
         AND id_indirizzo IS NOT NULL
         AND sede_universita IS NULL AND data_incontro IS NULL AND fascia_oraria IS NULL)
        OR
        (incontro_in_uni = TRUE AND spedizione = FALSE
         AND sede_universita IS NOT NULL AND data_incontro IS NOT NULL AND fascia_oraria IS NOT NULL
         AND id_indirizzo IS NULL)
    )
);

-- recensione
CREATE TABLE recensione (
    id SERIAL PRIMARY KEY,
    titolo VARCHAR(150) NOT NULL,
    corpo TEXT NOT NULL,
    valutazione INT CHECK (valutazione BETWEEN 1 AND 5),
    id_transazione INT NOT NULL,
    autore VARCHAR(9) NOT NULL,
    recensito VARCHAR(9) NOT NULL,

    CONSTRAINT fk_rec_trans
        FOREIGN KEY (id_transazione) REFERENCES transazione(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_rec_autore
        FOREIGN KEY (autore) REFERENCES studente(matricola)
        ON DELETE CASCADE,

    CONSTRAINT fk_rec_recensito
        FOREIGN KEY (recensito) REFERENCES studente(matricola)
        ON DELETE CASCADE,

    CONSTRAINT uc_recensione UNIQUE (id_transazione, autore)
);

-- ============================================================
-- INDICI
-- ============================================================
CREATE INDEX idx_annuncio_tipologia     ON annuncio(tipologia);
CREATE INDEX idx_annuncio_categoria     ON annuncio(categoria);
CREATE INDEX idx_annuncio_concluso      ON annuncio(concluso) WHERE concluso = FALSE;
CREATE INDEX idx_annuncio_data          ON annuncio(data_pubblicazione DESC);

CREATE INDEX idx_offerta_stato          ON offerta(stato);
CREATE INDEX idx_offerta_annuncio       ON offerta(id_annuncio);
CREATE INDEX idx_offerta_offerente      ON offerta(matricola_offerente);

CREATE INDEX idx_foto_annuncio          ON foto(id_annuncio);

CREATE INDEX idx_trans_annuncio         ON transazione(annuncio_concluso);
CREATE INDEX idx_consegna_transazione   ON consegna(id_transazione);

CREATE INDEX idx_indirizzo_studente     ON indirizzo(matricola_studente);

-- max 1 foto principale per annuncio
CREATE UNIQUE INDEX ux_foto_principale_per_annuncio
ON foto(id_annuncio)
WHERE is_principale = TRUE;

-- ============================================================
-- NUOVA AGGIUNTA: max 1 offerta "attiva" per (annuncio, offerente)
-- Attiva = In_Attesa o Accettata
-- ============================================================
CREATE UNIQUE INDEX ux_offerta_attiva_per_annuncio_offerente
ON offerta(id_annuncio, matricola_offerente)
WHERE stato IN ('In_Attesa', 'Accettata');

-- ============================================================
-- VIEW
-- ============================================================
CREATE OR REPLACE VIEW transazioni_con_totale AS
SELECT
  t.id AS id_transazione,
  t.data,
  t.annuncio_concluso,
  a.tipologia,
  t.id_offerta_accettata,
  o.importo_offerto,
  a.prezzo AS prezzo_annuncio,
  c.spedizione,
  COALESCE(o.importo_offerto, a.prezzo, 0) AS prezzo_base,
  COALESCE(o.importo_offerto, a.prezzo, 0) AS totale_pagato
FROM transazione t
JOIN annuncio a ON t.annuncio_concluso = a.id
LEFT JOIN offerta  o ON t.id_offerta_accettata = o.id
LEFT JOIN consegna c ON c.id_transazione = t.id;

-- ============================================================
-- FUNZIONI & TRIGGER
-- ============================================================

-- Foto: se non esiste una principale, la prima inserita diventa principale
CREATE OR REPLACE FUNCTION foto_set_principale_se_manca()
RETURNS TRIGGER AS $$
BEGIN
  IF NOT EXISTS (
    SELECT 1 FROM foto
    WHERE id_annuncio = NEW.id_annuncio AND is_principale = TRUE
  ) THEN
    NEW.is_principale := TRUE;
  END IF;
  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_foto_principale_default
BEFORE INSERT ON foto
FOR EACH ROW EXECUTE FUNCTION foto_set_principale_se_manca();


-- Unica transazione per annuncio
CREATE OR REPLACE FUNCTION check_annuncio_unica_transazione()
RETURNS TRIGGER AS $$
DECLARE v_count INT;
BEGIN
  SELECT COUNT(*) INTO v_count
  FROM transazione
  WHERE annuncio_concluso = NEW.annuncio_concluso;

  IF v_count > 0 THEN
    RAISE EXCEPTION 'Annuncio già concluso con una transazione.';
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_annuncio_unica_transazione
BEFORE INSERT ON transazione
FOR EACH ROW EXECUTE FUNCTION check_annuncio_unica_transazione();


-- Offerta al proprio annuncio: vietato
CREATE OR REPLACE FUNCTION chk_no_self_offer()
RETURNS TRIGGER AS $$
DECLARE vend VARCHAR(9);
BEGIN
  SELECT matricola_venditore INTO vend FROM annuncio WHERE id = NEW.id_annuncio;

  IF vend = NEW.matricola_offerente THEN
    RAISE EXCEPTION 'Non puoi fare un''offerta al tuo stesso annuncio.';
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_self_offer
BEFORE INSERT ON offerta
FOR EACH ROW EXECUTE FUNCTION chk_no_self_offer();


-- Regola importo: obbligatorio per Vendita, NULL per gli altri
CREATE OR REPLACE FUNCTION check_offerta_importo()
RETURNS TRIGGER AS $$
DECLARE v_tipo tipo_annuncio;
BEGIN
  SELECT tipologia INTO v_tipo FROM annuncio WHERE id = NEW.id_annuncio;
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Annuncio % non trovato.', NEW.id_annuncio;
  END IF;

  IF (v_tipo = 'Vendita' AND NEW.importo_offerto IS NULL) THEN
    RAISE EXCEPTION 'L''importo_offerto è obbligatorio per offerte su annunci di Vendita.';
  ELSIF (v_tipo <> 'Vendita' AND NEW.importo_offerto IS NOT NULL) THEN
    RAISE EXCEPTION 'L''importo_offerto deve essere NULL se l''annuncio non è di Vendita.';
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_offerta_importo
BEFORE INSERT OR UPDATE ON offerta
FOR EACH ROW EXECUTE FUNCTION check_offerta_importo();


-- Scambio / Regalo
CREATE OR REPLACE FUNCTION check_offerta_scambio_regalo()
RETURNS TRIGGER AS $$
DECLARE v_tipo tipo_annuncio;
BEGIN
  SELECT tipologia INTO v_tipo FROM annuncio WHERE id = NEW.id_annuncio;
  IF NOT FOUND THEN
    RAISE EXCEPTION 'Annuncio % non trovato.', NEW.id_annuncio;
  END IF;

  IF v_tipo = 'Scambio' THEN
    IF NEW.oggetto_offerto IS NULL OR btrim(NEW.oggetto_offerto) = '' THEN
      RAISE EXCEPTION 'Per un annuncio di Scambio, oggetto_offerto è obbligatorio.';
    END IF;
  END IF;

  IF v_tipo = 'Regalo' THEN
    IF NEW.messaggio IS NULL OR btrim(NEW.messaggio) = '' THEN
      RAISE EXCEPTION 'Per un annuncio di Regalo, il messaggio è obbligatorio.';
    END IF;
    IF NEW.importo_offerto IS NOT NULL THEN
      RAISE EXCEPTION 'Per un annuncio di Regalo, importo_offerto deve essere NULL.';
    END IF;
    IF NEW.oggetto_offerto IS NOT NULL THEN
      RAISE EXCEPTION 'Per un annuncio di Regalo, oggetto_offerto deve essere NULL.';
    END IF;
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_offerta_scambio_regalo
BEFORE INSERT OR UPDATE ON offerta
FOR EACH ROW EXECUTE FUNCTION check_offerta_scambio_regalo();


-- ============================================================
-- NUOVA AGGIUNTA: blocco multi-offerta "attiva" (messaggio chiaro)
-- (l'indice unique sopra è la protezione "hard")
-- ============================================================
CREATE OR REPLACE FUNCTION check_unica_offerta_attiva_per_studente_annuncio()
RETURNS TRIGGER AS $$
DECLARE v_count INT;
BEGIN
  -- consideriamo "attiva" solo In_Attesa o Accettata
  IF NEW.stato IN ('In_Attesa', 'Accettata') THEN
    SELECT COUNT(*) INTO v_count
    FROM offerta
    WHERE id_annuncio = NEW.id_annuncio
      AND matricola_offerente = NEW.matricola_offerente
      AND stato IN ('In_Attesa', 'Accettata')
      AND id <> COALESCE(NEW.id, -1);

    IF v_count > 0 THEN
      RAISE EXCEPTION
        'Hai già un''offerta attiva su questo annuncio. Prima devi ritirarla o farla rifiutare.';
    END IF;
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_unica_offerta_attiva
BEFORE INSERT OR UPDATE OF stato, id_annuncio, matricola_offerente ON offerta
FOR EACH ROW EXECUTE FUNCTION check_unica_offerta_attiva_per_studente_annuncio();


-- Unica offerta 'Accettata' per annuncio
CREATE OR REPLACE FUNCTION check_unica_offerta_accettata()
RETURNS TRIGGER AS $$
DECLARE v_count INT;
BEGIN
  IF NEW.stato = 'Accettata' THEN
    SELECT COUNT(*) INTO v_count
    FROM offerta
    WHERE id_annuncio = NEW.id_annuncio
      AND stato = 'Accettata'
      AND id <> NEW.id;

    IF v_count > 0 THEN
      RAISE EXCEPTION 'Esiste già un''offerta accettata per questo annuncio.';
    END IF;
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_unica_offerta_accettata
BEFORE INSERT OR UPDATE ON offerta
FOR EACH ROW EXECUTE FUNCTION check_unica_offerta_accettata();


-- Non accettare offerte su annuncio già concluso
CREATE OR REPLACE FUNCTION no_accettata_se_concluso()
RETURNS TRIGGER AS $$
DECLARE concl BOOLEAN;
BEGIN
  IF NEW.stato = 'Accettata' AND (OLD.stato IS DISTINCT FROM 'Accettata') THEN
    SELECT concluso INTO concl FROM annuncio WHERE id = NEW.id_annuncio;
    IF concl THEN
      RAISE EXCEPTION 'Annuncio già concluso.';
    END IF;
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_no_accettata_se_concluso
BEFORE UPDATE OF stato ON offerta
FOR EACH ROW EXECUTE FUNCTION no_accettata_se_concluso();


-- Congela offerta accettata
CREATE OR REPLACE FUNCTION congela_offerta_accettata()
RETURNS TRIGGER AS $$
BEGIN
  IF OLD.stato = 'Accettata' AND NEW.stato <> 'Accettata' THEN
    RAISE EXCEPTION 'Non è possibile modificare un''offerta già accettata.';
  END IF;
  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_congela_offerta_accettata
BEFORE UPDATE ON offerta
FOR EACH ROW EXECUTE FUNCTION congela_offerta_accettata();


-- Alla prima 'Accettata' crea Transazione e chiudi Annuncio
CREATE OR REPLACE FUNCTION crea_transazione_e_concludi_annuncio()
RETURNS TRIGGER AS $$
DECLARE
  v_ann   INT;
  v_vend  VARCHAR(20);
  v_acq   VARCHAR(20);
  v_imp   NUMERIC(9,2);
BEGIN
  IF NEW.stato = 'Accettata' AND OLD.stato <> 'Accettata' THEN
    SELECT id_annuncio, matricola_offerente, importo_offerto
      INTO v_ann, v_acq, v_imp
    FROM offerta
    WHERE id = NEW.id;

    SELECT matricola_venditore INTO v_vend
    FROM annuncio
    WHERE id = v_ann;

    IF NOT EXISTS (SELECT 1 FROM transazione WHERE id_offerta_accettata = NEW.id) THEN
      INSERT INTO transazione (data, importo_finale, annuncio_concluso, id_offerta_accettata,
                               matricola_venditore, matricola_acquirente)
      VALUES (CURRENT_DATE, v_imp, v_ann, NEW.id, v_vend, v_acq);
    END IF;

    UPDATE annuncio SET concluso = TRUE WHERE id = v_ann;
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_crea_transazione_e_concludi_annuncio
AFTER UPDATE OF stato ON offerta
FOR EACH ROW EXECUTE FUNCTION crea_transazione_e_concludi_annuncio();


-- Alla creazione della Transazione, imposta l'Annuncio come concluso
CREATE OR REPLACE FUNCTION aggiorna_annuncio_concluso()
RETURNS TRIGGER AS $$
BEGIN
  UPDATE annuncio SET concluso = TRUE WHERE id = NEW.annuncio_concluso;
  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_aggiorna_annuncio_concluso
AFTER INSERT ON transazione
FOR EACH ROW EXECUTE FUNCTION aggiorna_annuncio_concluso();


-- autore=acquirente, recensito=venditore
CREATE OR REPLACE FUNCTION check_recensione_ruoli()
RETURNS TRIGGER AS $$
DECLARE v_vend VARCHAR(9); v_acq VARCHAR(9);
BEGIN
  SELECT matricola_venditore, matricola_acquirente
    INTO v_vend, v_acq
  FROM transazione
  WHERE id = NEW.id_transazione;

  IF NOT FOUND THEN
    RAISE EXCEPTION 'Transazione % non trovata.', NEW.id_transazione;
  END IF;

  IF NEW.recensito <> v_vend THEN
    RAISE EXCEPTION 'Il recensito deve essere il venditore della transazione.';
  END IF;

  IF NEW.autore <> v_acq THEN
    RAISE EXCEPTION 'Solo l''acquirente può lasciare la recensione.';
  END IF;

  RETURN NEW;
END; $$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_recensione_ruoli
BEFORE INSERT OR UPDATE ON recensione
FOR EACH ROW EXECUTE FUNCTION check_recensione_ruoli();
