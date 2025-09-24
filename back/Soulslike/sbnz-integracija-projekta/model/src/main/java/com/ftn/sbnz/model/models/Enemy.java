package com.ftn.sbnz.model.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "enemies")
public class Enemy implements Serializable {

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String type;
    private double hp;
    private double damage;
    private double defense;
    private String behaviour;
    private String region;
    private double score;
    
    @ElementCollection
    private List<String> abilities = new ArrayList<>();

    @ElementCollection
    private List<String> resistances = new ArrayList<>();

    @ElementCollection
    private List<String> statusEffects = new ArrayList<>();

    @ElementCollection
    private Set<String> weaknesses = new HashSet<>();
    
    private int experienceReward = 0;
    private double criticalChance = 0.1;
    private double dodgeChance = 0.1;

    public Enemy() {

    }
    
    
    public Enemy(String name, String type) {
        this.name = name;
        this.type = type;
        this.hp = 1000;
        this.damage = 100;
        this.defense = 50;
        this.behaviour = "aggressive";
        this.score = 0;
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
    
    public void addWeakness(String weakness) {
        weaknesses.add(weakness);
    }
    
    public boolean isBoss() {
        return "boss".equals(this.type);
    }
    
    public boolean isElite() {
        return "elite".equals(this.type) || this.hp > 3000 || this.damage > 400;
    }

    // All getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
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
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    
    public List<String> getAbilities() { return abilities; }
    public void setAbilities(List<String> abilities) { this.abilities = abilities; }
    
    public List<String> getResistances() { return resistances; }
    public void setResistances(List<String> resistances) { this.resistances = resistances; }
    
    public List<String> getStatusEffects() { return statusEffects; }
    public void setStatusEffects(List<String> statusEffects) { this.statusEffects = statusEffects; }
    
    public Set<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(Set<String> weaknesses) { this.weaknesses = weaknesses; }
}