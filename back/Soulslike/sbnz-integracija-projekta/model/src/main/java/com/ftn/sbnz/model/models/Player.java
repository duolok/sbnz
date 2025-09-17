package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private int level;
    private PlayerClass playerClass;
    private String weaponType;
    private double hp;
    private double damage;
    private double defense;
    
    public enum PlayerClass {
        STRENGTH, DEX, MAGE, BALANCED
    }
    
    public Player() {}
    
    public Player(String name, int level, PlayerClass playerClass, String weaponType) {
        this.name = name;
        this.level = level;
        this.playerClass = playerClass;
        this.weaponType = weaponType;
        this.hp = 100 + (level * 20);
        this.damage = 50 + (level * 5);
        this.defense = 30 + (level * 3);
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    
    public PlayerClass getPlayerClass() { return playerClass; }
    public void setPlayerClass(PlayerClass playerClass) { this.playerClass = playerClass; }
    
    public String getWeaponType() { return weaponType; }
    public void setWeaponType(String weaponType) { this.weaponType = weaponType; }
    
    public double getHp() { return hp; }
    public void setHp(double hp) { this.hp = hp; }
    
    public double getDamage() { return damage; }
    public void setDamage(double damage) { this.damage = damage; }
    
    public double getDefense() { return defense; }
    public void setDefense(double defense) { this.defense = defense; }
}