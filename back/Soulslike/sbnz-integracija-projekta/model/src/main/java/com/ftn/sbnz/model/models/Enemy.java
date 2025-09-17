package com.ftn.sbnz.model.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Enemy implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String type;
    private double hp;
    private double damage;
    private double defense;
    private String behaviour;
    private List<String> abilities = new ArrayList<>();
    private List<String> resistances = new ArrayList<>();
    private List<String> statusEffects = new ArrayList<>();
    private String region;
    private double score; // Za evaluaciju najboljeg kandidata
    
    public Enemy() {}
    
    public Enemy(String name, String type) {
        this.name = name;
        this.type = type;
        this.hp = 1000;
        this.damage = 100;
        this.defense = 50;
        this.behaviour = "aggressive";
    }
    
    // Helper metode za Drools pravila
    public void applyDifficultyModifier(double hpModifier, double dmgModifier) {
        this.hp *= hpModifier;
        this.damage *= dmgModifier;
    }
    
    public void adjustForPlayerLevel(int playerLevel) {
        if (playerLevel <= 10) {
            this.hp *= 0.7;
            this.damage *= 0.8;
        } else if (playerLevel >= 50) {
            this.hp *= 1.5;
            this.damage *= 1.3;
        }
    }
    
    public void addAbility(String ability) {
        if (!abilities.contains(ability)) {
            abilities.add(ability);
        }
    }
    
    public void addResistance(String resistance) {
        if (!resistances.contains(resistance)) {
            resistances.add(resistance);
        }
    }
    
    public void addStatusEffect(String effect) {
        if (!statusEffects.contains(effect)) {
            statusEffects.add(effect);
        }
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public double getHp() { return hp; }
    public void setHp(double hp) { this.hp = hp; }
    
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
    
    public double getDefense() { return defense; }
    public void setDefense(double defense) { this.defense = defense; }
    
    public String getBehaviour() { return behaviour; }
    public void setBehaviour(String behaviour) { this.behaviour = behaviour; }
    
    public List<String> getAbilities() { return abilities; }
    public void setAbilities(List<String> abilities) { this.abilities = abilities; }
    
    public List<String> getResistances() { return resistances; }
    public void setResistances(List<String> resistances) { this.resistances = resistances; }
    
    public List<String> getStatusEffects() { return statusEffects; }
    public void setStatusEffects(List<String> statusEffects) { this.statusEffects = statusEffects; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    @Override
    public String toString() {
        return "Enemy{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", hp=" + hp +
                ", damage=" + damage +
                ", defense=" + defense +
                ", behaviour='" + behaviour + '\'' +
                ", abilities=" + abilities +
                ", resistances=" + resistances +
                ", statusEffects=" + statusEffects +
                ", region='" + region + '\'' +
                '}';
    }
}