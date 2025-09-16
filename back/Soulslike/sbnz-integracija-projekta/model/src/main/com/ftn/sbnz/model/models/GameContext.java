package com.ftn.sbnz.model.game;

import java.io.Serializable;

public class GameContext implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String region; // "swamp", "castle", "mountain", "desert", "volcano"
    private String difficulty; // "easy", "medium", "medium-hard", "hard"
    private String weather; // "clear", "rain", "fog", "snow", "sandstorm", "wind"
    private String timeOfDay; // "day", "night"
    private Player player;
    
    public GameContext() {}
    
    public GameContext(String region, String difficulty, String weather, String timeOfDay, Player player) {
        this.region = region;
        this.difficulty = difficulty;
        this.weather = weather;
        this.timeOfDay = timeOfDay;
        this.player = player;
    }
    
    // Getters and Setters
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }
    
    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }
    
    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }
    
    @Override
    public String toString() {
        return "GameContext{" +
                "region='" + region + '\'' +
                ", difficulty='" + difficulty + '\'' +
                ", weather='" + weather + '\'' +
                ", timeOfDay='" + timeOfDay + '\'' +
                ", player=" + player +
                '}';
    }
}