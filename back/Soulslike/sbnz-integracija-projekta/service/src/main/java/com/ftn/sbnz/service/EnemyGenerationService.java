package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.BackwardQuery;
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
import java.util.List;

@Service
public class EnemyGenerationService {
    
    private static Logger log = LoggerFactory.getLogger(EnemyGenerationService.class);
    private final KieContainer kieContainer;
    
    @Autowired
    private EnemyRepository enemyRepository;
    
    @Autowired
    public EnemyGenerationService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }
    
    public Enemy generateEnemy(GameContext context) {
        log.info("Starting FORWARD CHAINING enemy generation for context: {}", context);
        
        KieSession kieSession = null;
        List<Enemy> enemyCandidates = new ArrayList<>();
        
        try {
            // Create KieSession
            kieSession = kieContainer.newKieSession("forwardChainingSession");
            
            // Set globals - ONLY use the ones declared in your DRL files
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            
            // Initialize selectedEnemy as null - this global is declared in your DRL files
            Enemy selectedEnemy = null;
            kieSession.setGlobal("selectedEnemy", selectedEnemy);
            
            // Insert facts
            kieSession.insert(context);
            if (context.getPlayer() != null) {
                kieSession.insert(context.getPlayer());
            }
            
            // Load existing enemies from database
            loadExistingEnemies(kieSession, context.getRegion(), enemyCandidates);
            
            // Execute rules in phases
            executeRulesInPhases(kieSession);
            
            // Get result from the global that your rules actually use
            selectedEnemy = (Enemy) kieSession.getGlobal("selectedEnemy");
            
            return handleResult(selectedEnemy, enemyCandidates, context);
            
        } catch (Exception e) {
            log.error("Error in forward chaining enemy generation", e);
            return createFallbackEnemy(context);
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    private void loadExistingEnemies(KieSession kieSession, String region, List<Enemy> enemyCandidates) {
        try {
            List<Enemy> existingEnemies = enemyRepository.findByRegion(region);
            log.info("Loaded {} existing enemies for region: {}", existingEnemies.size(), region);
            
            for (Enemy enemy : existingEnemies) {
                kieSession.insert(enemy);
                enemyCandidates.add(enemy);
            }
        } catch (Exception e) {
            log.warn("Could not load enemies from database", e);
        }
    }

    private void executeRulesInPhases(KieSession kieSession) {
        String[] phases = {
            "template-generation",
            "difficulty-adjustment", 
            "player-level-adjustment",
            "player-build-counter",
            "weather-effects",
            "time-of-day-effects",
            "final-selection"
        };
        
        for (String phase : phases) {
            try {
                kieSession.getAgenda().getAgendaGroup(phase).setFocus();
                int fired = kieSession.fireAllRules();
                log.info("Phase {}: {} rules fired", phase, fired);
            } catch (Exception e) {
                log.warn("Error in phase {}: {}", phase, e.getMessage());
            }
        }
    }

    private Enemy handleResult(Enemy selectedEnemy, List<Enemy> enemyCandidates, GameContext context) {
        if (selectedEnemy != null) {
            log.info("Selected enemy: {}", selectedEnemy.getName());
            return saveEnemy(selectedEnemy);
        } else if (!enemyCandidates.isEmpty()) {
            Enemy bestCandidate = enemyCandidates.stream()
                .max((e1, e2) -> Double.compare(e1.getScore(), e2.getScore()))
                .orElse(null);
            log.info("Selected best candidate: {}", bestCandidate != null ? bestCandidate.getName() : "null");
            return bestCandidate != null ? saveEnemy(bestCandidate) : createFallbackEnemy(context);
        } else {
            log.warn("No enemies generated, creating fallback");
            return createFallbackEnemy(context);
        }
    }

    private Enemy saveEnemy(Enemy enemy) {
        try {
            return enemyRepository.save(enemy);
        } catch (Exception e) {
            log.warn("Could not save enemy to database", e);
            return enemy;
        }
    }

    private Enemy createFallbackEnemy(GameContext context) {
        Enemy fallback = new Enemy("Fallback " + context.getRegion() + " Creature", "creature");
        fallback.setRegion(context.getRegion());
        fallback.setHp(1000);
        fallback.setDamage(100);
        fallback.setDefense(50);
        fallback.setScore(50);
        
        // Apply basic scaling
        switch (context.getDifficulty()) {
            case "easy":
                fallback.setHp(700);
                fallback.setDamage(70);
                break;
            case "hard":
                fallback.setHp(1500);
                fallback.setDamage(150);
                break;
        }
        
        return fallback;
    }

    // Backward chaining method
    public Enemy findSpecificEnemy(BackwardQuery query) {
        KieSession kieSession = null;
        try {
            kieSession = kieContainer.newKieSession("backwardChainingSession");
            List<Enemy> enemyCandidates = new ArrayList<>();
            
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            kieSession.setGlobal("log", log);
            
            kieSession.insert(query);
            if (query.getContext() != null) {
                kieSession.insert(query.getContext());
                if (query.getContext().getPlayer() != null) {
                    kieSession.insert(query.getContext().getPlayer());
                }
            }
            
            // Load all enemies for backward chaining
            loadAllEnemiesForBackward(kieSession, enemyCandidates);
            
            kieSession.fireAllRules();
            
            return handleBackwardResult(query, enemyCandidates);
            
        } catch (Exception e) {
            log.error("Error in backward chaining", e);
            return generateEnemy(query.getContext() != null ? query.getContext() : 
                new GameContext("castle", "medium", "clear", "day", null));
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }

    private void loadAllEnemiesForBackward(KieSession kieSession, List<Enemy> enemyCandidates) {
        try {
            List<Enemy> allEnemies = enemyRepository.findAll();
            for (Enemy enemy : allEnemies) {
                kieSession.insert(enemy);
                enemyCandidates.add(enemy);
            }
        } catch (Exception e) {
            log.warn("Could not load enemies for backward chaining", e);
        }
    }

    private Enemy handleBackwardResult(BackwardQuery query, List<Enemy> enemyCandidates) {
        if (query.isConditionsMet() && query.getTargetEnemy() != null) {
            return findEnemyByName(query.getTargetEnemy(), query.getContext());
        } else if (query.getFallbackEnemy() != null) {
            return findEnemyByName(query.getFallbackEnemy(), query.getContext());
        } else if (!enemyCandidates.isEmpty()) {
            return enemyCandidates.get(0);
        } else {
            return createFallbackEnemy(query.getContext());
        }
    }

    private Enemy findEnemyByName(String name, GameContext context) {
        try {
            List<Enemy> enemies = enemyRepository.findByName(name);
            if (!enemies.isEmpty()) {
                return enemies.get(0);
            }
        } catch (Exception e) {
            log.warn("Could not find enemy by name: {}", name, e);
        }
        return createFallbackEnemy(context);
    }
}