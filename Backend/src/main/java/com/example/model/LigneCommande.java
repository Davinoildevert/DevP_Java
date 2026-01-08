package com.example.model;

public class LigneCommande {
    private Plat plat;
    private int quantite;

    public LigneCommande() {}

    public LigneCommande(Plat plat, int quantite) {
        this.plat = plat;
        this.quantite = quantite;
    }

    public Plat getPlat() { return plat; }
    public void setPlat(Plat plat) { this.plat = plat; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }
}
