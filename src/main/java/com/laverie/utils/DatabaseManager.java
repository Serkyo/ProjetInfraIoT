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
            System.out.println("New row inserted into the database at the table historique_machines");
            connection.close();
        } catch (SQLException e) {
            System.err.println("An error occured while inserting into the database : " + e.getMessage());
        }
    }

    public static void insererLogMachine(String nouveauStatus, String idUtilisateur, String idMachine) {
        String sql = "INSERT INTO log_machines (nouveau_status, date_changement, id_utilisateur, id_machine) VALUES (?, ?, ?, ?)";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, nouveauStatus);
            stmt.setTimestamp(2, new Timestamp(new Date().getTime()));
            stmt.setString(3, idUtilisateur);
            stmt.setString(4, idMachine);

            stmt.executeUpdate();
            System.out.println("New row inserted into the database at the table log_machines");
            connection.close();
        } catch (SQLException e) {
            System.err.println("An error occured while inserting into the database : " + e.getMessage());
        }
    }

    public static void getLogMachines() {
        String sql =
            "SELECT DISTINCT ON (id_machine) id, id_machine, nouveau_status, date_changement, id_utilisateur " +
            "FROM log_machines " +
            "ORDER BY id_machine, date_changement DESC";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            boolean isEmpty = true;

            while (rs.next()) {
                isEmpty = false;

                int idLog = rs.getInt("id");
                String idMachine = rs.getString("id_machine");
                String status = rs.getString("nouveau_status");
                Timestamp dateChangement = rs.getTimestamp("date_changement");
                String idUtilisateur = rs.getString("id_utilisateur");

                System.out.println(
                    "Log #" + idLog +
                    " | Machine: " + idMachine +
                    " | Status: " + status +
                    " | Date: " + dateChangement +
                    " | Utilisateur: " + idUtilisateur
                );
            }

            if (isEmpty) {
                System.err.println("Aucune donnée récupérée des logs des machines...");
            }

            rs.close();
            stmt.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
