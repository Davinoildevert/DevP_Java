package com.example.ui;

import com.example.model.menu.Plat;

public class SelectedProduct {

    private static Plat selected;

    public static void set(Plat plat) {
        selected = plat;
    }

    public static Plat get() {
        return selected;
    }
}
