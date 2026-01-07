package com.example.service.menu;

import com.example.model.menu.Categorie;
import com.example.model.menu.Plat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class MockMenuService implements MenuService {


    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Categorie> getCategories() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/data/categories.json")) {
            if (is == null) {
                throw new IllegalStateException("Fichier introuvable: /data/categories.json");
            }
            return mapper.readValue(is, new TypeReference<List<Categorie>>() {});
        }
    }

    @Override
    public List<Plat> getAllPlats() throws Exception {
        try (InputStream is = getClass().getResourceAsStream("/data/plats.json")) {
            if (is == null) {
                throw new IllegalStateException("Fichier introuvable: /data/plats.json");
            }
            return mapper.readValue(is, new TypeReference<List<Plat>>() {});
        }
    }

    @Override
    public List<Plat> getPlatsByCategorie(int categorieId) throws Exception {
        return getAllPlats().stream()
                .filter(p -> p.categorieId == categorieId)
                .collect(Collectors.toList());
    }
}
