package com.sage.rest.models;

public class Goat {

    private static int latestGoatId = 0;
    private int goatId;
    private int age;
    private int aggression;
    private double weight;

    public Goat() {
        // set and increment goatId
        goatId = latestGoatId++;
    }

    public int getGoatId() {
        return goatId;
    }

    public void setGoatId(int goatId) {
        this.goatId = goatId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAggression() {
        return aggression;
    }

    public void setAggression(int aggression) {
        this.aggression = aggression;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
