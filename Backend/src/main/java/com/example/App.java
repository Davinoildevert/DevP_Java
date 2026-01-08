package com.example;

import com.example.db.Db;
import com.example.model.Categorie;
import com.example.model.Plat;
import com.example.service.UserService;
import com.example.service.UserServiceImpl;
import io.javalin.Javalin;
import com.example.model.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {

        // ✅ Connexion SQLite + init tables
        final Connection conn = Db.connect();
        Db.initSchema(conn);
        System.out.println("✅ SQLite OK : tables prêtes");
        com.example.db.Db.seedIfEmpty(conn);


        // ✅ Service users (tu peux le garder)
        UserService userService = new UserServiceImpl();

        // ✅ Serveur Javalin
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
        }).start(7070);

        // -------------------------
        // ENDPOINTS EXISTANTS (users)
        // -------------------------
        app.get("/users", ctx -> ctx.json(userService.getUsers()));

        // ---------------------------------------
        // ✅ ENDPOINTS COMPATIBLES AVEC LE FRONT
        // ---------------------------------------

        // GET /api/categories
        app.get("/api/categories", ctx -> {
            ctx.json(fetchCategories(conn));
        });

        // GET /api/plats?categorieId=1
        app.get("/api/plats", ctx -> {
            String catIdStr = ctx.queryParam("categorieId");
            if (catIdStr == null) {
                ctx.json(fetchAllPlats(conn));
                return;
            }

            try {
                int catId = Integer.parseInt(catIdStr);
                ctx.json(fetchPlatsByCategorie(conn, catId));
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid categorieId");
            }
        });

        // (optionnel mais utile) GET /api/plats/{id}
        app.get("/api/plats/{id}", ctx -> {
            try {
                int id = Integer.parseInt(ctx.pathParam("id"));
                Plat plat = fetchPlatById(conn, id);
                if (plat == null) {
                    ctx.status(404).result("Plat not found");
                } else {
                    ctx.json(plat);
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Invalid id");
            }
        });

        System.out.println("Backend démarré sur http://localhost:7070");
        ObjectMapper mapper = new ObjectMapper();

        // POST /api/commande
        app.post("/api/commande", ctx -> {
            OrderRequest req = ctx.bodyAsClass(OrderRequest.class);

            if (req == null || req.items == null || req.items.isEmpty()) {
                ctx.status(400).result("Empty order");
                return;
            }

            int orderId = createOrder(conn, req, mapper);
            ctx.json(java.util.Map.of("orderId", orderId));
        });
    }

    // =========================
    // JDBC helpers (SANS DAO)
    // =========================

    private static List<Categorie> fetchCategories(Connection conn) throws Exception {
        List<Categorie> list = new ArrayList<>();
        String sql = "SELECT id, name FROM category ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Categorie(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        }
        return list;
    }

    private static List<Plat> fetchAllPlats(Connection conn) throws Exception {
        List<Plat> list = new ArrayList<>();

        String sql = """
        SELECT
          id,
          name        AS nom,
          description AS description,
          price       AS prix,
          available   AS disponible,
          image       AS image,
          category_id AS categorieId
        FROM plat
        ORDER BY id
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Plat(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("disponible") == 1,
                        rs.getString("image"),
                        rs.getInt("categorieId")
                ));
            }
        }
        return list;
    }



    private static List<Plat> fetchPlatsByCategorie(Connection conn, int categoryId) throws Exception {
        List<Plat> list = new ArrayList<>();

        String sql = """
        SELECT
          id,
          name        AS nom,
          description AS description,
          price       AS prix,
          available   AS disponible,
          image       AS image,
          category_id AS categorieId
        FROM plat
        WHERE category_id = ?
        ORDER BY id
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Plat(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("description"),
                            rs.getDouble("prix"),
                            rs.getInt("disponible") == 1,
                            rs.getString("image"),
                            rs.getInt("categorieId")
                    ));
                }
            }
        }
        return list;
    }



    private static Plat fetchPlatById(Connection conn, int id) throws Exception {
        String sql = """
        SELECT
          id,
          name        AS nom,
          description AS description,
          price       AS prix,
          available   AS disponible,
          image       AS image,
          category_id AS categorieId
        FROM plat
        WHERE id = ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                return new Plat(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("description"),
                        rs.getDouble("prix"),
                        rs.getInt("disponible") == 1,
                        rs.getString("image"),
                        rs.getInt("categorieId")
                );
            }
        }
    }

    private static int createOrder(Connection conn, OrderRequest req, ObjectMapper mapper) throws Exception {
        conn.setAutoCommit(false);
        try {
            double total = 0.0;

            // 1) calcul total + vérification plats
            for (OrderRequest.OrderItem it : req.items) {
                double unitPrice = fetchPlatUnitPrice(conn, it.platId);
                total += unitPrice * it.quantity;
            }

            // 2) insert order_header
            int orderId;
            String insertHeader = "INSERT INTO order_header(client_name, total, created_at) VALUES(?,?,?)";
            try (PreparedStatement ps = conn.prepareStatement(insertHeader, java.sql.Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, req.clientName);
                ps.setDouble(2, total);
                ps.setString(3, java.time.Instant.now().toString());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (!keys.next()) throw new RuntimeException("No generated key for order_header");
                    orderId = keys.getInt(1);
                }
            }

            // 3) insert order_line
            String insertLine = """
            INSERT INTO order_line(order_id, plat_id, quantity, unit_price, choices_json)
            VALUES(?,?,?,?,?)
        """;

            try (PreparedStatement ps = conn.prepareStatement(insertLine)) {
                for (OrderRequest.OrderItem it : req.items) {
                    double unitPrice = fetchPlatUnitPrice(conn, it.platId);
                    String choicesJson = (it.choices == null) ? null : mapper.writeValueAsString(it.choices);

                    ps.setInt(1, orderId);
                    ps.setInt(2, it.platId);
                    ps.setInt(3, it.quantity);
                    ps.setDouble(4, unitPrice);
                    ps.setString(5, choicesJson);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            conn.commit();
            return orderId;

        } catch (Exception e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    private static double fetchPlatUnitPrice(Connection conn, int platId) throws Exception {
        String sql = "SELECT price FROM plat WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, platId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) throw new RuntimeException("Plat not found id=" + platId);
                return rs.getDouble("price");
            }
        }
    }

}
