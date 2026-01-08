package com.example.service.menu;

import com.example.model.menu.Categorie;
import com.example.model.menu.Plat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiMenuService implements MenuService {

    private final String apiBase; // ex: http://localhost:7070/api
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public ApiMenuService(String baseUrl) {
        // baseUrl vient de app.properties (http://localhost:7070)
        // on normalise pour obtenir http://localhost:7070/api
        String clean = baseUrl;
        if (clean.endsWith("/")) clean = clean.substring(0, clean.length() - 1);
        this.apiBase = clean + "/api";
    }

    @Override
    public List<Categorie> getCategories() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiBase + "/categories"))
                .GET()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(res.body(), new TypeReference<List<Categorie>>() {});
    }

    @Override
    public List<Plat> getAllPlats() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiBase + "/plats"))
                .GET()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(res.body(), new TypeReference<List<Plat>>() {});
    }

    @Override
    public List<Plat> getPlatsByCategorie(int categorieId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(apiBase + "/plats?categorieId=" + categorieId))
                .GET()
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(res.body(), new TypeReference<List<Plat>>() {});
    }
}
