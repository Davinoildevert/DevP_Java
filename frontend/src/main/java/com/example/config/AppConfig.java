package com.example.config;

import java.io.InputStream;
import java.util.Properties;

public final class AppConfig {

    private static final Properties props = new Properties();
    private static boolean loaded = false;

    private AppConfig() {}

    private static void loadIfNeeded() {
        if (loaded) return;

        try (InputStream is = AppConfig.class
                .getClassLoader()
                .getResourceAsStream("app.properties")) {

            if (is == null) {
                throw new IllegalStateException("app.properties introuvable dans resources");
            }

            props.load(is);
            loaded = true;

        } catch (Exception e) {
            throw new RuntimeException("Erreur chargement app.properties", e);
        }
    }

    public static boolean isApiMode() {
        loadIfNeeded();
        return "api".equalsIgnoreCase(props.getProperty("data.source", "mock"));
    }

    public static String apiBaseUrl() {
        loadIfNeeded();
        return props.getProperty("api.baseUrl", "http://localhost:7070");
    }
}
