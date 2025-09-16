package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.model.models.Player;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class EnemyGenerationService {
    
    private static Logger log = LoggerFactory.getLogger(EnemyGenerationService.class);
    
    private final KieContainer kieContainer;
    
    @Autowired
    public EnemyGenerationService(KieContainer kieContainer) {
        log.info("Initialising Enemy Generation Service");
        this.kieContainer = kieContainer;
    }
    
    public Enemy generateEnemy(GameContext context) {
        log.info("Starting enemy generation for context: " + context);
        
        KieSession kieSession = kieContainer.newKieSession();
        List<Enemy> enemyCandidates = new ArrayList<>();
        
        try {
            // Set global variable for collecting enemy candidates
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            
            // Insert the game context
            kieSession.insert(context);
            
            // Fire all rules
            int rulesFired = kieSession.fireAllRules();
            log.info("Number of rules fired: " + rulesFired);
            
            // Select the best enemy based on score
            Enemy selectedEnemy = selectBestEnemy(enemyCandidates);
            
            if (selectedEnemy != null) {
                log.info("Selected enemy: " + selectedEnemy);
            } else {
                log.warn("No enemy could be generated for the given context");
                // Return a default enemy if no rules matched
                selectedEnemy = createDefaultEnemy(context);
            }
            
            return selectedEnemy;
            
        } finally {
            kieSession.dispose();
        }
    }
    
    private Enemy selectBestEnemy(List<Enemy> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        
        // Sort by score (highest first) and return the best one
        return candidates.stream()
                .max(Comparator.comparing(Enemy::getScore))
                .orElse(candidates.get(0));
    }
    
    private Enemy createDefaultEnemy(GameContext context) {
        Enemy defaultEnemy = new Enemy("Default Minion", "minion");
        defaultEnemy.setRegion(context.getRegion());
        defaultEnemy.setHp(1000);
        defaultEnemy.setDamage(100);
        defaultEnemy.setDefense(50);
        defaultEnemy.setBehaviour("neutral");
        defaultEnemy.addAbility("melee");
        
        // Basic adjustments based on player level
        if (context.getPlayer() != null) {
            defaultEnemy.adjustForPlayerLevel(context.getPlayer().getLevel());
        }
        
        log.info("Created default enemy: " + defaultEnemy);
        return defaultEnemy;
    }
    
    // Method for testing backward chaining (trying to spawn a specific boss)
    public Enemy trySpawnBoss(String bossName, GameContext context) {
        log.info("Attempting to spawn boss: " + bossName);
        
        KieSession kieSession = kieContainer.newKieSession();
        List<Enemy> enemyCandidates = new ArrayList<>();
        
        try {
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            kieSession.insert(context);
            
            // Insert a query for specific boss
            kieSession.insert(bossName);
            
            int rulesFired = kieSession.fireAllRules();
            log.info("Number of rules fired for boss spawn: " + rulesFired);
            
            // Look for the boss in candidates
            Enemy boss = enemyCandidates.stream()
                    .filter(e -> e.getName().equalsIgnoreCase(bossName))
                    .findFirst()
                    .orElse(null);
            
            if (boss != null) {
                log.info("Successfully spawned boss: " + boss);
            } else {
                log.info("Could not spawn requested boss, conditions not met");
            }
            
            return boss;
            
        } finally {
            kieSession.dispose();
        }
    }
}