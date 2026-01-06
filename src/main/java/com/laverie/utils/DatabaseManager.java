package com.laverie.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class DatabaseManager {
    private static final String DB_NAME = System.getenv("POSTGRES_DB");
    private static final String USER = System.getenv("POSTGRES_USER");
    private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");
    private static final String URL = "jdbc:postgresql://db:5432/" + DB_NAME;

    public static void insererHistoriqueMachine(String idMachine, Cycles cycle, int duree, float consoElec, float consoEau, Date dateDebut, Date dateFin) {
        String sql = "INSERT INTO historique_machines (id_machine, type_cycle, duree, conso_elec, conso_eau, date_debut, date_fin) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, idMachine);
            stmt.setString(2, String.valueOf(cycle));
            stmt.setInt(3, duree);
            stmt.setFloat(4, consoElec);
            stmt.setFloat(5, consoEau);
            stmt.setTimestamp(6, new Timestamp(dateDebut.getTime()));
            stmt.setTimestamp(7, new Timestamp(dateFin.getTime()));

            stmt.executeUpdate();
            System.out.println("New row inserted into the database");
            connection.close();
        } catch (SQLException e) {
            System.err.println("An error occured while inserting into the database : " + e.getMessage());
        }
    }

    public static void getLogMachines() {
        String sql = "SELECT DISTINCT ON (id) id, nouveau_status, date_changement, id_utilisateur FROM log_machines BY id, date_changement DESC;";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            Boolean isEmpty = true;

            while (rs.next()) {
                isEmpty = false;

                int idMachine = rs.getInt("id");
                String status = rs.getString("nouveau_status");
                Timestamp dateChangement = rs.getTimestamp("date_changement");
                int idUtilisateur = rs.getInt("id_utilisateur");

                System.out.println(
                    "Machine " + idMachine +
                    " | Status: " + status +
                    " | Date: " + dateChangement +
                    " | Utilisateur: " + idUtilisateur
                );
            }

            if (isEmpty) {
                System.err.println("Aucune donnée récupérées des logs des machines...");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
