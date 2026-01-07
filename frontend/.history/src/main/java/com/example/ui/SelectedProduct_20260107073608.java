package com.example.ui;

public final class SelectedProduct {

    private static CatalogueController.Product current;

    private SelectedProduct() {}

    public static void set(CatalogueController.Product p) {
        current = p;
    }

    public static CatalogueController.Product get() {
        return current;
    }

    public static void clear() {
        current = null;
    }
}
