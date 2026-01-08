package com.example.ui;

import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiOrderService {

    private static final String API_BASE = "http://localhost:7070/api";
    private static final Gson gson = new Gson();
    private static final HttpClient http = HttpClient.newHttpClient();

    // Retourne l'orderId créé côté backend
    public static int sendOrder(String clientName,
                                List<CartService.CartItem> cartItems,
                                double taxRate) {

        Map<String, Object> body = new HashMap<>();
        body.put("clientName", clientName == null ? "" : clientName);

        List<Map<String, Object>> items = new ArrayList<>();
        for (CartService.CartItem it : cartItems) {
            Map<String, Object> line = new HashMap<>();
            line.put("platId", it.platId());
            line.put("quantity", it.qty());
            line.put("choices", new HashMap<String, String>()); // simple pour l’instant
            items.add(line);
        }
        body.put("items", items);

        String json = gson.toJson(body);

        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(API_BASE + "/commande"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));

            if (resp.statusCode() >= 400) {
                throw new RuntimeException("Erreur API " + resp.statusCode() + " : " + resp.body());
            }

            // Réponse attendue: {"orderId": 1}
            Map<?, ?> map = gson.fromJson(resp.body(), Map.class);
            return ((Number) map.get("orderId")).intValue();

        } catch (Exception e) {
            throw new RuntimeException("Impossible d'envoyer la commande au backend", e);
        }
    }
}
