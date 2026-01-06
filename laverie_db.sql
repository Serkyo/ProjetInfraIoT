CREATE TABLE IF NOT EXISTS historique_machines (
    id SERIAL,
    id_machine VARCHAR,
    type_cycle VARCHAR,
    duree INT,
    conso_elec FLOAT,
    conso_eau FLOAT,
    date_debut TIMESTAMP,
    date_fin TIMESTAMP,
    PRIMARY KEY (id, id_machine)
);

INSERT INTO historique_machines(id_machine, type_cycle, duree, conso_elec, conso_eau, date_debut, date_fin)
VALUES ('0', 'LOL', 20, 55.0, 55.0, '2026-01-06 10:00:00', '2026-01-06 10:20:00');

CREATE TABLE IF NOT EXISTS log_machines (
    id INT PRIMARY KEY,
    nouveau_status VARCHAR,
    date_changement TIMESTAMP,
    id_utilisateur INT
);