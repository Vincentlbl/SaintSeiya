package com.example.saintseiya.model;

public class SeiyaAttack {
    private final String name;
    private final int damage;
    private final int cost;

    public SeiyaAttack(String name, int damage, int cost) {
        this.name = name;
        this.damage = damage;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public int getDamage() {
        return damage;
    }

    public int getCost() {
        return cost;
    }
}
