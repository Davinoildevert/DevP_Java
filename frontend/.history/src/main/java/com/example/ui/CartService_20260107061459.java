package com.example.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CartService {

    private static final List<CartItem> items = new ArrayList<>();
    private static int orderNumber = 1234;

    private CartService() {}

    public static void add(String name, double price) {
        // si existe déjà → qty++
        for (int i = 0; i < items.size(); i++) {
            CartItem it = items.get(i);
            if (it.name().equals(name)) {
                items.set(i, new CartItem(it.name(), it.price(), it.qty() + 1));
                return;
            }
        }
        items.add(new CartItem(name, price, 1));
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

    public static int nextOrderNumber() {
        return orderNumber++;
    }

    public static void clear() {
        items.clear();
    }

    public record CartItem(String name, double price, int qty) {}
}
