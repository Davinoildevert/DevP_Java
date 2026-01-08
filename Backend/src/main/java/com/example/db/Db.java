package com.example.db;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.stream.Collectors;

public class Db {

    private static final String DB_URL = "jdbc:sqlite:restaurant.db";

    public static Connection connect() {
        try {
            // charge le driver
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            throw new RuntimeException("Impossible de se connecter à SQLite", e);
        }
    }

    public static void initSchema(Connection conn) {
        try {
            String sql = readResourceFile("/db/init.sql");
            try (Statement st = conn.createStatement()) {
                // exécute tout le script (plusieurs CREATE TABLE)
                st.executeUpdate(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossible d'initialiser le schéma SQL", e);
        }
    }

    private static String readResourceFile(String path) {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(Db.class.getResourceAsStream(path))
        )) {
            if (br == null) throw new IllegalStateException("Ressource introuvable: " + path);
            return br.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Impossible de lire la ressource: " + path, e);
        }
    }
    public static void seedIfEmpty(Connection conn) {
        try (var st = conn.createStatement()) {

            var rs = st.executeQuery("SELECT COUNT(*) FROM category");
            rs.next();
            int count = rs.getInt(1);

            if (count > 0) {
                System.out.println("ℹ️ Données déjà présentes, seed ignoré");
                return;
            }

            System.out.println("🌱 Insertion des données initiales...");

            String sql = readResourceFile("/db/seed.sql");
            st.executeUpdate(sql);

            System.out.println("✅ Seed terminé");

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du seed DB", e);
        }
    }

}