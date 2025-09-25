package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.BackwardQuery;
import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
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
            kieSession = kieContainer.newKieSession("forwardChainingSession");
            
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            Enemy selectedEnemy = null;
            kieSession.setGlobal("selectedEnemy", selectedEnemy);
            
            kieSession.insert(context);
            if (context.getPlayer() != null) {
                kieSession.insert(context.getPlayer());
            }
            
            loadExistingEnemiesAsCopies(kieSession, context.getRegion(), enemyCandidates);
            executeRulesInPhases(kieSession);

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

    private void loadExistingEnemiesAsCopies(KieSession kieSession, String region, List<Enemy> enemyCandidates) {
        try {
            List<Enemy> existingEnemies = enemyRepository.findByRegion(region);
            log.info("Loaded {} existing enemies for region: {}", existingEnemies.size(), region);
            
            for (Enemy originalEnemy : existingEnemies) {
                Enemy enemyCopy = createEnemyCopy(originalEnemy);
                kieSession.insert(enemyCopy);
                enemyCandidates.add(enemyCopy);
                log.info("Added existing enemy to session: {}", enemyCopy.getName());
            }
        } catch (Exception e) {
            log.warn("Could not load enemies from database, relying on templates", e);
        }
    }

    private Enemy createEnemyCopy(Enemy original) {
        Enemy copy = new Enemy(original.getName(), original.getType());
        copy.setRegion(original.getRegion());
        copy.setHp(original.getHp());
        copy.setDamage(original.getDamage());
        copy.setDefense(original.getDefense());
        copy.setBehaviour(original.getBehaviour());
        copy.setScore(original.getScore());
        
        copy.getAbilities().addAll(original.getAbilities());
        copy.getResistances().addAll(original.getResistances());
        copy.getStatusEffects().addAll(original.getStatusEffects());
        copy.getWeaknesses().addAll(original.getWeaknesses());
        
        return copy;
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
                log.info("=== Starting phase: {} ===", phase);
                kieSession.getAgenda().getAgendaGroup(phase).setFocus();
                int fired = kieSession.fireAllRules();
                log.info("Phase {}: {} rules fired", phase, fired);
                
                // Debug: log current candidate count
                List<Enemy> candidates = (List<Enemy>) kieSession.getGlobal("enemyCandidates");
                log.info("After phase {}: {} total candidates", phase, candidates.size());
                
                // Log candidate names for debugging
                for (Enemy candidate : candidates) {
                    log.debug("Candidate: {} (Score: {})", candidate.getName(), candidate.getScore());
                }
                
            } catch (Exception e) {
                log.error("Error in phase {}: {}", phase, e.getMessage(), e);
            }
        }
    }

    private Enemy handleResult(Enemy selectedEnemy, List<Enemy> enemyCandidates, GameContext context) {
        log.info("=== FINAL SELECTION ===");
        log.info("Total candidates: {}", enemyCandidates.size());
        log.info("Selected enemy from rules: {}", selectedEnemy != null ? selectedEnemy.getName() : "null");
        
        if (selectedEnemy != null) {
            log.info("Selected enemy via rules: {} (Score: {})", selectedEnemy.getName(), selectedEnemy.getScore());
            return selectedEnemy;
        } else if (!enemyCandidates.isEmpty()) {
            // Find enemy with highest score
            Enemy bestCandidate = enemyCandidates.stream()
                .max((e1, e2) -> Double.compare(e1.getScore(), e2.getScore()))
                .orElse(null);
                
            if (bestCandidate != null) {
                log.info("Selected best candidate by score: {} (Score: {})", 
                    bestCandidate.getName(), bestCandidate.getScore());
                return bestCandidate;
            }
        }
        
        log.warn("No enemies generated, creating fallback");
        return createFallbackEnemy(context);
    }

    private Enemy createFallbackEnemy(GameContext context) {
        Enemy fallback = new Enemy("Fallback " + context.getRegion() + " Creature", "creature");
        fallback.setRegion(context.getRegion());
        fallback.setHp(1000);
        fallback.setDamage(100);
        fallback.setDefense(50);
        fallback.setScore(50);
        
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

    // Backward chaining method remains the same
    public Enemy findSpecificEnemy(BackwardQuery query) {
        KieSession kieSession = null;
        try {
            kieSession = kieContainer.newKieSession("backwardChainingSession");
            List<Enemy> enemyCandidates = new ArrayList<>();
            
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            
            kieSession.insert(query);
            if (query.getContext() != null) {
                kieSession.insert(query.getContext());
                if (query.getContext().getPlayer() != null) {
                    kieSession.insert(query.getContext().getPlayer());
                }
            }
            
            loadAllEnemiesForBackwardAsCopies(kieSession, enemyCandidates);
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

    private void loadAllEnemiesForBackwardAsCopies(KieSession kieSession, List<Enemy> enemyCandidates) {
        try {
            List<Enemy> allEnemies = enemyRepository.findAll();
            for (Enemy originalEnemy : allEnemies) {
                Enemy enemyCopy = createEnemyCopy(originalEnemy);
                kieSession.insert(enemyCopy);
                enemyCandidates.add(enemyCopy);
            }
        } catch (Exception e) {
            log.warn("Could not load enemies for backward chaining", e);
        }
    }

    private Enemy handleBackwardResult(BackwardQuery query, List<Enemy> enemyCandidates) {
        if (query.isConditionsMet() && query.getTargetEnemy() != null) {
            return findEnemyByNameInCandidates(query.getTargetEnemy(), enemyCandidates);
        } else if (query.getFallbackEnemy() != null) {
            return findEnemyByNameInCandidates(query.getFallbackEnemy(), enemyCandidates);
        } else if (!enemyCandidates.isEmpty()) {
            return enemyCandidates.get(0);
        } else {
            return createFallbackEnemy(query.getContext());
        }
    }

    private Enemy findEnemyByNameInCandidates(String name, List<Enemy> enemyCandidates) {
        return enemyCandidates.stream()
            .filter(enemy -> enemy.getName().equals(name))
            .findFirst()
            .orElse(createFallbackEnemy(new GameContext("castle", "medium", "clear", "day", null)));
    }
}