CREATE TABLE IF NOT EXISTS users (
    login TEXT PRIMARY KEY,
    password_hash TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS studios (
    id BIGSERIAL PRIMARY KEY,
    name TEXT NOT NULL
);


CREATE TABLE IF NOT EXISTS music_bands (
    id BIGSERIAL PRIMARY KEY,
    band_key TEXT NOT NULL UNIQUE,
    name TEXT NOT NULL,
    coord_x DOUBLE PRECISION NOT NULL CHECK (coord_x > -431),
    coord_y INTEGER NOT NULL,
    creation_date TIMESTAMPTZ NOT NULL DEFAULT now(),
    number_of_participants BIGINT NOT NULL CHECK (number_of_participants > 0),
    singles_count INTEGER CHECK (singles_count > 0),
    establishment_date DATE,
    genre TEXT,
    studio_id BIGINT REFERENCES studios(id),
    owner_login TEXT NOT NULL REFERENCES users(login)
);

CREATE INDEX IF NOT EXISTS ix_music_bands_owner ON music_bands (owner_login);
CREATE INDEX IF NOT EXISTS ix_music_bands_name ON music_bands (name);
CREATE INDEX IF NOT EXISTS ix_music_bands_participants ON music_bands (number_of_participants);

