package com.ftn.sbnz.model.models;

import java.io.Serializable;

public class SelectionResult implements Serializable {
    private static final long serialVersionID = 1L;
    private Enemy selectedEnemy;
    
    public SelectionResult() {}

    public SelectionResult(Enemy selectedEnemy)  {
        this.selectedEnemy = selectedEnemy;
    }

    public Enemy getSelectedEnemy() {
        return selectedEnemy;
    }
    
    public void setSelectedEnemy(Enemy selectedEnemy) {
        this.selectedEnemy = selectedEnemy;
    }
}
