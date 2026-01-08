package com.example.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CartService {

    private static final List<CartItem> items = new ArrayList<>();

    private CartService() {}

    // ✅ On garde l'id du plat (obligatoire pour le back)
    public static void add(int platId, String name, double price) {
        for (int i = 0; i < items.size(); i++) {
            CartItem it = items.get(i);
            if (it.platId() == platId) {
                items.set(i, new CartItem(it.platId(), it.name(), it.price(), it.qty() + 1));
                return;
            }
        }
        items.add(new CartItem(platId, name, price, 1));
        System.out.println("🧺 ADD -> id=" + platId + " name=" + name + " price=" + price);

    }

    public static boolean isEmpty() {
        return items.isEmpty();
    }

    public static List<CartItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public static double total() {
        double t = 0;
        for (CartItem it : items) t += it.price() * it.qty();
        return t;
    }

    public static void clear() {
        items.clear();
    }
    public static void debugPrint() {
        System.out.println("=== PANIER ===");
        for (CartItem it : items) {
            System.out.println(it);
        }
    }

    public record CartItem(int platId, String name, double price, int qty) {}
}
