package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class BackwardQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String targetEnemy;
    private boolean processed;
    private boolean conditionsMet;
    private String fallbackEnemy;
    private GameContext context;
    private String selectedEnemy;
    
    public BackwardQuery() {
        this.conditionsMet = false;
        this.processed = false;
    }
    
    public BackwardQuery(String targetEnemy) {
        this.targetEnemy = targetEnemy;
    }
    
    public BackwardQuery(String targetEnemy, GameContext context) {
        this.targetEnemy = targetEnemy;
        this.context = context;
        this.conditionsMet = false;
        this.processed = false;
    }
    
    public String getSelectedEnemy() {
        return selectedEnemy;
    }
    
    public void setSelectedEnemy(String selectedEnemy) {
        this.selectedEnemy = selectedEnemy;
    }
    
    public String getTargetEnemy() { return targetEnemy; }
    public void setTargetEnemy(String targetEnemy) { this.targetEnemy = targetEnemy; }
    
    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
    
    public boolean isConditionsMet() { return conditionsMet; }
    public void setConditionsMet(boolean conditionsMet) { this.conditionsMet = conditionsMet; }
    
    public String getFallbackEnemy() { return fallbackEnemy; }
    public void setFallbackEnemy(String fallbackEnemy) { this.fallbackEnemy = fallbackEnemy; }
    
    public GameContext getContext() { return context; }
    public void setContext(GameContext context) { this.context = context; }
}