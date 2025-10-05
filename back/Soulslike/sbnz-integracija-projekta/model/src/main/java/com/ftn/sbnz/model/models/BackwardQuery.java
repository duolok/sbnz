package com.ftn.sbnz.model.models;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class BackwardQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String targetEnemy;
    private GameContext context;
    
    private boolean processed = false;
    private boolean conditionsMet = false;
    private boolean recursionStarted = false;
    
    private String selectedEnemy;
    private String fallbackEnemy;
    
    // Nova polja za rekurzivnu pretragu
    private List<String> candidateNames = new ArrayList<>();
    private int currentIndex = 0;
    
    public BackwardQuery() {}
    
    public BackwardQuery(String targetEnemy, GameContext context) {
        this.targetEnemy = targetEnemy;
        this.context = context;
    }
    
    public boolean isRecursionStarted() { return recursionStarted; }
    public void setRecursionStarted(boolean recursionStarted) { this.recursionStarted = recursionStarted; }
    
    
    // Getters and Setters
    public String getTargetEnemy() { return targetEnemy; }
    public void setTargetEnemy(String targetEnemy) { this.targetEnemy = targetEnemy; }
    
    public GameContext getContext() { return context; }
    public void setContext(GameContext context) { this.context = context; }
    
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
    
    public boolean isConditionsMet() { return conditionsMet; }
    public void setConditionsMet(boolean conditionsMet) { this.conditionsMet = conditionsMet; }
    
    public String getSelectedEnemy() { return selectedEnemy; }
    public void setSelectedEnemy(String selectedEnemy) { this.selectedEnemy = selectedEnemy; }
    
    public String getFallbackEnemy() { return fallbackEnemy; }
    public void setFallbackEnemy(String fallbackEnemy) { this.fallbackEnemy = fallbackEnemy; }
    
    public List<String> getCandidateNames() { return candidateNames; }
    public void setCandidateNames(List<String> candidateNames) { this.candidateNames = candidateNames; }
    
    public int getCurrentIndex() { return currentIndex; }
    public void setCurrentIndex(int currentIndex) { this.currentIndex = currentIndex; }
    
    public String getCurrentCandidate() {
        if (currentIndex < candidateNames.size()) {
            return candidateNames.get(currentIndex);
        }
        return null;
    }
    
    public void moveToNextCandidate() {
        currentIndex++;
    }
}