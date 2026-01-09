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

// Javalin OpenAPI / Swagger imports
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.http.Context;
import io.javalin.openapi.HttpMethod;
import io.javalin.openapi.OpenApi;

public class App {

    /**
     * Connection used throughout the lifecycle of the application. It is stored
     * in a static field so that annotated handlers can access the same
     * connection instance without passing it around explicitly.
     */
    private static Connection conn;
    private static final ObjectMapper mapper = new ObjectMapper();


    public static void main(String[] args) {

        // ✅ Connexion SQLite + init tables
        // Stocke la connexion dans le champ static afin que les méthodes
        // annotées puissent y accéder. Ne pas déclarer comme final ici car
        // elle est réutilisée ailleurs.
        conn = Db.connect();
        Db.initSchema(conn);
        System.out.println("✅ SQLite OK : tables prêtes");
        com.example.db.Db.seedIfEmpty(conn);


        // ✅ Service users (tu peux le garder)
        UserService userService = new UserServiceImpl();

        // ✅ Serveur Javalin
        Javalin app = Javalin.create(config -> {
            config.http.defaultContentType = "application/json";
            // Enregistre le plugin OpenAPI. Il génère automatiquement la
            // documentation JSON au chemin /openapi. La configuration fixe
            // simplement le titre mais pourrait être étendue au besoin.
            config.registerPlugin(new OpenApiPlugin(pluginConfig -> {
                pluginConfig.withDocumentationPath("/openapi");
                pluginConfig.withDefinitionConfiguration((version, definition) -> {
                    definition.withOpenApiInfo(info -> info.setTitle("API Restaurant"));
                });
            }));
            // Enregistre le plugin Swagger afin d'exposer une interface
            // interactive de test et consultation de l'API. Elle sera
            // disponible à l'URL /swagger et utilisera la documentation
            // générée sur /openapi. Ces routes n'impactent pas les autres
            // endpoints et n'interagissent pas avec la base de données.
            config.registerPlugin(new SwaggerPlugin(swagger -> {
                swagger.setUiPath("/swagger");
                swagger.setDocumentationPath("/openapi");
            }));
        }).start(7070);

        // -------------------------
        // ENDPOINTS EXISTANTS (users)
        // -------------------------
        app.get("/users", ctx -> ctx.json(userService.getUsers()));

        // ---------------------------------------
        // ✅ ENDPOINTS COMPATIBLES AVEC LE FRONT
        // ---------------------------------------

        // GET /api/categories (via méthode référencée)
        // Utilisation d'une méthode référencée permet à l'annotation @OpenApi
        // d'être appliquée sur la méthode afin de générer la documentation.
        app.get("/api/categories", App::getCategories);

        // GET /api/plats?categorieId=1
        app.get("/api/plats", App::getPlats);


        // (optionnel mais utile) GET /api/plats/{id}
        app.get("/api/plats/{id}", App::getPlatById);


        System.out.println("Backend démarré sur http://localhost:7070");
        ObjectMapper mapper = new ObjectMapper();

        // POST /api/commande
        app.post("/api/commande", App::postCommande);


        // GET /api/commande/{id} - retourne la commande et ses lignes
        app.get("/api/commande/{id}", App::getCommandeById);


        // POST /api/paiement (simulation CB / NFC)
        app.post("/api/paiement", App::postPaiement);

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

    // =========================
    // Lecture d'une commande par id
    // =========================

    /**
     * Enregistre un objet de transfert pour renvoyer une commande complète.
     * Il agrège l'entête de la commande et ses lignes.
     */
    public record OrderDto(int id, String clientName, double total, String createdAt, List<OrderLineDto> items) {}

    /**
     * Objet de transfert pour une ligne de commande.
     */
    public record OrderLineDto(int platId, String name, int quantity, double unitPrice, String image) {}

    private static OrderDto fetchOrderById(Connection conn, int orderId) throws Exception {
        String sql = "SELECT id, client_name, total, created_at FROM order_header WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                int id = rs.getInt("id");
                String clientName = rs.getString("client_name");
                double total = rs.getDouble("total");
                String createdAt = rs.getString("created_at");
                List<OrderLineDto> items = fetchOrderLines(conn, id);
                return new OrderDto(id, clientName, total, createdAt, items);
            }
        }
    }

    private static List<OrderLineDto> fetchOrderLines(Connection conn, int orderId) throws Exception {
        List<OrderLineDto> items = new ArrayList<>();
        String sql = """
        SELECT
          ol.plat_id     AS platId,
          p.name         AS name,
          ol.quantity    AS quantity,
          ol.unit_price  AS unitPrice,
          p.image        AS image
        FROM order_line ol
        JOIN plat p ON p.id = ol.plat_id
        WHERE ol.order_id = ?
        ORDER BY ol.id
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new OrderLineDto(
                            rs.getInt("platId"),
                            rs.getString("name"),
                            rs.getInt("quantity"),
                            rs.getDouble("unitPrice"),
                            rs.getString("image")
                    ));
                }
            }
        }
        return items;
    }

    // =========================
    // Paiement (simulation) helpers
    // =========================

    /**
     * Requête de paiement simulée. Contient simplement l'identifiant de la
     * commande et la méthode de paiement ("CARD" ou "NFC").
     */
    public static class PaymentRequest {
        public int orderId;
        public String method;
    }

    private static boolean orderExists(Connection conn, int orderId) throws Exception {
        String sql = "SELECT 1 FROM order_header WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    // =========================
    // Documentation OpenAPI - méthodes annotées
    // =========================

    /**
     * Handler pour l'endpoint GET /api/categories. Cette méthode est
     * annotée afin d'apparaître dans la documentation OpenAPI générée par
     * le plugin. Elle utilise la connexion statique pour interroger la
     * base de données et renvoyer les catégories en JSON.
     *
     * @param ctx Contexte HTTP fourni par Javalin
     * @throws Exception si une erreur SQL survient
     */
    @OpenApi(
            path = "/api/categories",
            methods = {HttpMethod.GET},
            summary = "Lister les catégories"
    )
    public static void getCategories(Context ctx) throws Exception {
        ctx.json(fetchCategories(conn));
    }
    @OpenApi(
            path = "/api/plats",
            methods = {HttpMethod.GET},
            summary = "Lister les plats (optionnel: filtrer par categorieId)"
    )
    public static void getPlats(Context ctx) throws Exception {
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
    }

    @OpenApi(
            path = "/api/plats/{id}",
            methods = {HttpMethod.GET},
            summary = "Récupérer un plat par id"
    )
    public static void getPlatById(Context ctx) throws Exception {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Plat plat = fetchPlatById(conn, id);
            if (plat == null) ctx.status(404).result("Plat not found");
            else ctx.json(plat);
        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid id");
        }
    }

    @OpenApi(
            path = "/api/commande",
            methods = {HttpMethod.POST},
            summary = "Créer une commande"
    )
    public static void postCommande(Context ctx) throws Exception {
        OrderRequest req = ctx.bodyAsClass(OrderRequest.class);

        if (req == null || req.items == null || req.items.isEmpty()) {
            ctx.status(400).result("Empty order");
            return;
        }

        int orderId = createOrder(conn, req, mapper);
        ctx.json(java.util.Map.of("orderId", orderId));
    }

    @OpenApi(
            path = "/api/commande/{id}",
            methods = {HttpMethod.GET},
            summary = "Consulter une commande par id"
    )
    public static void getCommandeById(Context ctx) throws Exception {
        try {
            int orderId = Integer.parseInt(ctx.pathParam("id"));

            var order = fetchOrderById(conn, orderId);
            if (order == null) {
                ctx.status(404).result("Order not found");
                return;
            }

            ctx.json(order);

        } catch (NumberFormatException e) {
            ctx.status(400).result("Invalid id");
        } catch (Exception e) {
            e.printStackTrace();
            ctx.status(500).result("Server error");
        }
    }

    @OpenApi(
            path = "/api/paiement",
            methods = {HttpMethod.POST},
            summary = "Paiement simulé (CARD ou NFC)"
    )
    public static void postPaiement(Context ctx) throws Exception {
        PaymentRequest req = ctx.bodyAsClass(PaymentRequest.class);

        if (req == null || req.orderId <= 0) {
            ctx.status(400).result("Invalid orderId");
            return;
        }
        if (req.method == null || req.method.isBlank()) {
            ctx.status(400).result("Missing method (CARD or NFC)");
            return;
        }

        // Vérifier que la commande existe
        if (!orderExists(conn, req.orderId)) {
            ctx.status(404).result("Order not found");
            return;
        }

        String method = req.method.trim().toUpperCase();
        if (!method.equals("CARD") && !method.equals("NFC")) {
            ctx.status(400).result("Invalid method (CARD or NFC)");
            return;
        }

        ctx.json(java.util.Map.of(
                "orderId", req.orderId,
                "status", "PAID",
                "method", method,
                "transactionId", "TX-" + System.currentTimeMillis()
        ));
    }

}
