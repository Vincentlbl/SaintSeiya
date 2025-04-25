package com.example.saintseiya.model;

import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;


public class Inventory {
    private static Inventory instance;
    private Set<String> items = new HashSet<>();

    private Inventory() {}

    public static Inventory getInstance() {
        if (instance == null) instance = new Inventory();
        return instance;
    }

    public void addItem(String item) {
        items.add(item);
    }

    public boolean hasItem(String item) {
        return items.contains(item);
    }

    public void useItem(String item) {
        items.remove(item);
    }

    public void clearInventory() {
        items.clear();
    }

    // 👇 Ajoute cette méthode ici
    public List<String> getItems() {
        return new ArrayList<>(items);
    }
}
