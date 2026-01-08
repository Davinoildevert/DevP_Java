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

            // catégories
            st.executeUpdate("""
            INSERT INTO category (id, name, icon) VALUES
            (1, 'Entrées', null),
            (2, 'Plats', null),
            (3, 'Desserts', null),
            (4, 'Boissons', null)
        """);

            // plats
            st.executeUpdate("""
INSERT INTO plat (id, name, description, price, available, image, category_id) VALUES
(1, 'Nems poulet', 'Nems croustillants au poulet', 6.90, 1, 'nems.jpg', 1),
(2, 'Soupe miso', 'Soupe miso traditionnelle', 4.50, 1, 'soupe_miso.jpg', 1),
(3, 'Gyozas', 'Raviolis japonais grillés', 7.90, 1, 'gyozas.jpg', 1),

(4, 'Ramen miso', 'Ramen au bouillon miso', 12.90, 1, 'ramen.jpg', 2),
(5, 'Pad Thai', 'Nouilles sautées façon thaï', 13.50, 1, 'padthay.jpg', 2),
(6, 'Curry rouge', 'Curry rouge lait de coco', 14.50, 1, 'curry.jpg', 2),

(7, 'Mochi', 'Mochi japonais', 4.90, 1, 'mochi.jpg', 3),
(8, 'Dorayaki', 'Pancake japonais fourré', 4.80, 1, 'dorayaki.jpg', 3),

(9, 'Boba thé', 'Thé au lait et perles', 4.50, 1, 'boba.jpg', 4),
(10,'Thé vert', 'Thé vert chaud', 2.90, 1, 'the_vert.jpg', 4)
""");


            System.out.println("✅ Seed terminé");

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du seed DB", e);
        }
    }
}