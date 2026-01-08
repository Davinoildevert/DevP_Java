package com.example.model;

import java.util.List;
import java.util.Map;

public class OrderRequest {
    public String clientName;
    public List<OrderItem> items;

    public static class OrderItem {
        public int platId;
        public int quantity;
        public Map<String, String> choices; // optionnel
    }
}
