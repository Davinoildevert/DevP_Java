package com.example.service.menu;

import java.io.InputStream;
import java.util.Properties;

public final class MenuServiceFactory {

    private static MenuService instance;

    private MenuServiceFactory() { }

    public static MenuService getInstance() {
        if (instance != null) return instance;

        Properties props = new Properties();
        try (InputStream is = MenuServiceFactory.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (is == null) {
                throw new IllegalStateException("Fichier app.properties introuvable dans resources");
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lecture app.properties", e);
        }

        String source = props.getProperty("data.source", "mock").trim();

        // Pour l’instant, on supporte seulement "mock"
        if (source.equalsIgnoreCase("mock")) {
            instance = new MockMenuService();
            return instance;
        }

        if (source.equalsIgnoreCase("api")) {
            String baseUrl = props.getProperty("api.baseUrl", "http://localhost:7070");
            instance = new ApiMenuService(baseUrl);
            return instance;
        }

        throw new IllegalStateException("data.source=" + source + " inconnu");

    }
}
