package com.ftn.sbnz.model.models;

import org.kie.api.definition.type.Position;
import java.io.Serializable;

/**
 * Model za rekurzivnu pretragu kandidata.
 * Modeluje sekvencu: "Ako currentCandidate nije validan, probaj nextCandidate"
 * Analogno Location(item, container) iz Location primera.
 */
public class CandidateSequence implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Position(0)
    private String currentCandidate;
    
    @Position(1)
    private String nextCandidate;
    
    // Kontekst za validaciju
    private String region;
    private String difficulty;
    private int playerLevel;
    private String playerClass;
    
    public CandidateSequence() {}
    
    public CandidateSequence(String currentCandidate, String nextCandidate) {
        this.currentCandidate = currentCandidate;
        this.nextCandidate = nextCandidate;
    }
    
    public CandidateSequence(String currentCandidate, String nextCandidate, 
                            String region, String difficulty, int playerLevel, String playerClass) {
        this.currentCandidate = currentCandidate;
        this.nextCandidate = nextCandidate;
        this.region = region;
        this.difficulty = difficulty;
        this.playerLevel = playerLevel;
        this.playerClass = playerClass;
    }
    
    // Getters and Setters
    public String getCurrentCandidate() { return currentCandidate; }
    public void setCurrentCandidate(String currentCandidate) { this.currentCandidate = currentCandidate; }
    
    public String getNextCandidate() { return nextCandidate; }
    public void setNextCandidate(String nextCandidate) { this.nextCandidate = nextCandidate; }
    
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public int getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }
    
    public String getPlayerClass() { return playerClass; }
    public void setPlayerClass(String playerClass) { this.playerClass = playerClass; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        CandidateSequence that = (CandidateSequence) o;
        
        if (currentCandidate != null ? !currentCandidate.equals(that.currentCandidate) : that.currentCandidate != null)
            return false;
        return nextCandidate != null ? nextCandidate.equals(that.nextCandidate) : that.nextCandidate == null;
    }
    
    @Override
    public int hashCode() {
        int result = currentCandidate != null ? currentCandidate.hashCode() : 0;
        result = 31 * result + (nextCandidate != null ? nextCandidate.hashCode() : 0);
        return result;
    }
    
    @Override
    public String toString() {
        return "CandidateSequence{" +
                "current='" + currentCandidate + '\'' +
                ", next='" + nextCandidate + '\'' +
                '}';
    }
}