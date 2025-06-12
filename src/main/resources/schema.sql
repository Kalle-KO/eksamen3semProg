DROP TABLE IF EXISTS fire_event_siren;
DROP TABLE IF EXISTS fire_event;
DROP TABLE IF EXISTS sirens;

CREATE TABLE sirens (
        siren_id INT AUTO_INCREMENT PRIMARY KEY,
        latitude DOUBLE NOT NULL,
        longitude DOUBLE NOT NULL,
        status ENUM('NEUTRAL','EMERGENCY') NOT NULL DEFAULT 'NEUTRAL',
        disabled BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE fire_event (
        fire_event_id INT AUTO_INCREMENT PRIMARY KEY,
        latitude DOUBLE NOT NULL,
        longitude DOUBLE NOT NULL,
        timestamp DATETIME NOT NULL,
        closed BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE fire_event_siren (
          fire_event_id INT NOT NULL,
          siren_id INT NOT NULL,
          PRIMARY KEY (fire_event_id, siren_id),
          FOREIGN KEY (fire_event_id) REFERENCES fire_event(fire_event_id) ON DELETE CASCADE,
          FOREIGN KEY (siren_id)     REFERENCES sirens(siren_id)      ON DELETE CASCADE
);