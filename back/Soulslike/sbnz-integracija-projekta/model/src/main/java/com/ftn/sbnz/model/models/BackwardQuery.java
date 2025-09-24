package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class BackwardQuery implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String targetEnemy;
    private boolean processed = false;
    private boolean conditionsMet = false;
    private String fallbackEnemy;
    private GameContext context;
    
    public BackwardQuery() {}
    
    public BackwardQuery(String targetEnemy) {
        this.targetEnemy = targetEnemy;
    }
    
    public BackwardQuery(String targetEnemy, GameContext context) {
        this.targetEnemy = targetEnemy;
        this.context = context;
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