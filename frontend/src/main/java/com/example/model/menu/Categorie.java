package com.example.model.menu;

public class Categorie {
    public int id;
    public String nom;
    public String icone;

    public Categorie() {
        // Constructeur vide obligatoire pour Jackson
    }

    public Categorie(int id, String nom, String icone) {
        this.id = id;
        this.nom = nom;
        this.icone = icone;
    }
}
