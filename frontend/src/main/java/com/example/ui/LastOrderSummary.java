package com.example.ui;

import java.util.ArrayList;
import java.util.List;

public final class LastOrderSummary {

    private static List<CartService.CartItem> items = new ArrayList<>();
    private static double total = 0.0;

    private LastOrderSummary() {}

    public static void capture() {
        items = new ArrayList<>(CartService.getItems());
        total = CartService.total();
    }

    public static List<CartService.CartItem> getItems() {
        return items;
    }

    public static double getTotal() {
        return total;
    }

    public static void clear() {
        items.clear();
        total = 0.0;
    }
}
