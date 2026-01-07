package com.example.service;

import com.example.model.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class UserApiService {

    private static final UserApiService INSTANCE = new UserApiService();
    public static UserApiService getInstance() { return INSTANCE; }

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String baseUrl = "http://localhost:7070";

    public List<User> getUsers() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .GET()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() != 200) {
            throw new RuntimeException("HTTP " + res.statusCode() + " : " + res.body());
        }
        return mapper.readValue(res.body(), new TypeReference<List<User>>() {});
    }

    public User createUser(String name, String email) throws Exception {
        String json = mapper.writeValueAsString(Map.of("name", name, "email", email));

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/users"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (res.statusCode() != 201) {
            throw new RuntimeException("HTTP " + res.statusCode() + " : " + res.body());
        }
        return mapper.readValue(res.body(), User.class);
    }
}
