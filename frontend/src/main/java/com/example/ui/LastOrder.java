package com.example.ui;

public final class LastOrder {

    private static int lastOrderId = 1000;

    private LastOrder() {}

    public static void set(int id) {
        lastOrderId = id;
    }

    public static int get() {
        return lastOrderId;
    }

    // utilisé UNIQUEMENT en mode mock
    public static int generateMockOrderId() {
        return ++lastOrderId;
    }
}
