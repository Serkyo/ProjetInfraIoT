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

CREATE TABLE IF NOT EXISTS log_machines (
    id INT PRIMARY KEY,
    nouveau_status VARCHAR,
    date_changement TIMESTAMP,
    id_utilisateur INT
);