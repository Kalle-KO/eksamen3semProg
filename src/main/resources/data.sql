INSERT INTO sirens (latitude, longitude, status, disabled) VALUES
   (10.0000, 10.0000, 'NEUTRAL', FALSE),
   (20.0000, 20.0000, 'NEUTRAL', FALSE),
   (30.0000, 30.0000, 'NEUTRAL', FALSE);

INSERT INTO fire_event (latitude, longitude, timestamp, closed) VALUES
    (15.0000, 15.0000, NOW(), FALSE);

-- Optionally associate existing event to nearest siren
INSERT INTO fire_event_siren (fire_event_id, siren_id) VALUES
    (1, 1);