package com.example.model.menu;

public class Plat {
    public int id;
    public String nom;
    public String description;
    public double prix;
    public boolean disponible;
    public String image;
    public int categorieId;

    public Plat() {
        // Constructeur vide obligatoire pour Jackson
    }

    public Plat(int id, String nom, String description, double prix, boolean disponible, String image, int categorieId) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.disponible = disponible;
        this.image = image;
        this.categorieId = categorieId;
    }
}
