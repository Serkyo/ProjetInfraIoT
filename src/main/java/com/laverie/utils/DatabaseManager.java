package com.laverie.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

public class DatabaseManager {
    private static final String DB_NAME = System.getenv("POSTGRES_NAME");
    private static final String USER = System.getenv("POSTGRES_USER");
    private static final String PASSWORD = System.getenv("POSTGRES_PASSWORD");
    private static final String URL = "jdbc:postgresql://db:5432/" + DB_NAME;

    public static void insererHistoriqueMachine(String idMachine, Cycles cycle, int duree, float consoElec, float consoEau, Date dateDebut, Date dateFin) {
        String sql = "INSERT INTO historique_machines (id_machine, type_cycle, duree, conso_elec, conso_eau, date_debut, date_fin) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, idMachine);
            stmt.setString(2, cycle.name());
            stmt.setInt(3, duree);
            stmt.setFloat(4, consoElec);
            stmt.setFloat(5, consoEau);
            stmt.setDate(6, (java.sql.Date) dateDebut);
            stmt.setDate(7, (java.sql.Date) dateFin);

            stmt.executeUpdate();
            System.out.println("New row inserted into the database");
        } catch (SQLException e) {
            System.err.println("An error occured while inserting into the database : " + e.getMessage());
        }
    }
}
