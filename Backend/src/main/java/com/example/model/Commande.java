package com.example.model;

import java.util.List;

public class Commande {
    private int id;
    private List<LigneCommande> lignes;
    private double total;

    public Commande() {}

    public Commande(int id, List<LigneCommande> lignes, double total) {
        this.id = id;
        this.lignes = lignes;
        this.total = total;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public List<LigneCommande> getLignes() { return lignes; }
    public void setLignes(List<LigneCommande> lignes) { this.lignes = lignes; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}
