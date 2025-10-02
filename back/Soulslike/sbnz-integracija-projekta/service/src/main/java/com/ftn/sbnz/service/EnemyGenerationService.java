package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.BackwardQuery;
import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.SelectionResult;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.service.EnemyRepository;

import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;
import org.kie.api.builder.Results;
import org.kie.api.builder.Message;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

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

    // ==================== FORWARD CHAINING ====================
    
    public Enemy generateEnemy(GameContext context) {
        log.info("Starting FORWARD CHAINING enemy generation for context: {}", context);
        
        KieSession kieSession = null;
        List<Enemy> enemyCandidates = new ArrayList<>();
        
        try {
            InputStream template = getClass().getResourceAsStream("/templates/difficulty-adjustment.drt");
            InputStream data = getClass().getResourceAsStream("/templates/template-data.xls");
            
            if (template != null && data != null) {
                ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
                String generatedDRL = converter.compile(data, template, 3, 2);
                log.info("=== GENERATED DRL FROM TEMPLATE ===\n{}\n=== END ===", generatedDRL);
                
                kieSession = createKieSessionFromDRL(generatedDRL);
            } else {
                log.warn("Template or data file not found, using base session");
                kieSession = kieContainer.newKieSession("forwardChainingSession");
            }
            
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            
            kieSession.insert(context);
            if (context.getPlayer() != null) {
                kieSession.insert(context.getPlayer());
            }
            
            loadExistingEnemiesAsCopies(kieSession, context.getRegion(), enemyCandidates);
            executeRulesInPhases(kieSession);
            
            Enemy selectedEnemy = getSelectedEnemyFromSession(kieSession);
            
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
    
    private KieSession createKieSessionFromDRL(String generatedDRL) {
        KieHelper kieHelper = new KieHelper();
        
        kieHelper.addContent(generatedDRL, ResourceType.DRL);
        
        String[] ruleFiles = {
            "/rules/enemy/region.drl",
            "/rules/enemy/player-level.drl",
            "/rules/enemy/build.drl",
            "/rules/enemy/weather.drl",
            "/rules/enemy/daytime.drl",
            "/rules/enemy/final.drl"
        };
        
        for (String ruleFile : ruleFiles) {
            try {
                InputStream ruleStream = getClass().getResourceAsStream(ruleFile);
                if (ruleStream != null) {
                    String ruleContent = readInputStream(ruleStream);
                    kieHelper.addContent(ruleContent, ResourceType.DRL);
                    log.info("Loaded rule file: {}", ruleFile);
                } else {
                    log.warn("Rule file not found: {}", ruleFile);
                }
            } catch (Exception e) {
                log.error("Error loading rule file {}: {}", ruleFile, e.getMessage());
            }
        }
        
        Results results = kieHelper.verify();
        if (results.hasMessages(Message.Level.WARNING, Message.Level.ERROR)) {
            List<Message> messages = results.getMessages(Message.Level.WARNING, Message.Level.ERROR);
            for (Message message : messages) {
                log.error("DRL Compilation Error: {}", message.getText());
            }
            throw new IllegalStateException("DRL compilation errors found. Check logs.");
        }
        
        log.info("Successfully compiled all DRL rules");
        return kieHelper.build().newKieSession();
    }
    
    private String readInputStream(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
            .lines()
            .collect(Collectors.joining("\n"));
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
            log.warn("Could not load enemies from database", e);
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

    private Enemy getSelectedEnemyFromSession(KieSession kieSession) {
        try {
            Collection<?> results = kieSession.getObjects(new org.kie.api.runtime.ObjectFilter() {
                @Override
                public boolean accept(Object object) {
                    return object instanceof SelectionResult;
                }
            });
            
            if (!results.isEmpty()) {
                SelectionResult result = (SelectionResult) results.iterator().next();
                return result.getSelectedEnemy();
            }
        } catch (Exception e) {
            log.error("Error retrieving SelectionResult", e);
        }
        return null;
    }

    private void executeRulesInPhases(KieSession kieSession) {
        String[] phases = {
            "region-filter",
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
                log.info("PHASE {}: {} rules fired", phase, fired);
                
                List<Enemy> candidates = (List<Enemy>) kieSession.getGlobal("enemyCandidates");
                log.info("AFTER {}: {} total candidates", phase, candidates.size());
                
                for (Enemy candidate : candidates) {
                    log.debug("ENEMY: {} (Score: {}) HP: {} DMG: {}", 
                        candidate.getName(), candidate.getScore(), 
                        candidate.getHp(), candidate.getDamage());
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

    // ==================== BACKWARD CHAINING WITH TREE STRUCTURE ====================
    
    public Enemy findSpecificEnemy(BackwardQuery query) {
        log.info("╔════════════════════════════════════════════════════════════════╗");
        log.info("║          STARTING BACKWARD CHAINING TREE TRAVERSAL            ║");
        log.info("╚════════════════════════════════════════════════════════════════╝");
        log.info("Target Enemy: {}", query.getTargetEnemy());
        
        KieSession kieSession = null;
        try {
            kieSession = kieContainer.newKieSession("backwardChainingSession");
            List<Enemy> enemyCandidates = new ArrayList<>();
            
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            
            kieSession.insert(query);
            if (query.getContext() != null) {
                kieSession.insert(query.getContext());
                log.info("Context: Region={}, Difficulty={}, Weather={}, Time={}", 
                    query.getContext().getRegion(),
                    query.getContext().getDifficulty(),
                    query.getContext().getWeather(),
                    query.getContext().getTimeOfDay());
                
                if (query.getContext().getPlayer() != null) {
                    kieSession.insert(query.getContext().getPlayer());
                    log.info("Player: Name={}, Level={}, Class={}, Weapon={}", 
                        query.getContext().getPlayer().getName(),
                        query.getContext().getPlayer().getLevel(),
                        query.getContext().getPlayer().getPlayerClass(),
                        query.getContext().getPlayer().getWeaponType());
                }
            }
            
            loadAllEnemiesForBackwardAsCopies(kieSession, enemyCandidates);
            
            // Execute backward chaining with tree validation
            log.info("\n┌─────────────────────────────────────────────────────────┐");
            log.info("│  PHASE 1: BACKWARD CHAIN - TREE VALIDATION             │");
            log.info("└─────────────────────────────────────────────────────────┘");
            
            kieSession.getAgenda().getAgendaGroup("backward-chain").setFocus();
            int backwardRulesFired = kieSession.fireAllRules();
            log.info("Backward chain rules fired: {}", backwardRulesFired);
            
            // Log tree traversal results
            logTreeTraversalResults(kieSession, query);
            
            // If enemy was selected, apply post-selection modifications
            if (query.isConditionsMet() && query.getSelectedEnemy() != null) {
                log.info("\n┌─────────────────────────────────────────────────────────┐");
                log.info("│  PHASE 2: POST-SELECTION MODIFICATIONS                 │");
                log.info("└─────────────────────────────────────────────────────────┘");
                
                kieSession.getAgenda().getAgendaGroup("post-selection").setFocus();
                int postSelectionRules = kieSession.fireAllRules();
                log.info("Post-selection rules fired: {}", postSelectionRules);
            }
            
            Enemy result = handleBackwardResult(query, enemyCandidates);
            
            log.info("\n╔════════════════════════════════════════════════════════════════╗");
            log.info("║            BACKWARD CHAINING COMPLETE                          ║");
            log.info("╠════════════════════════════════════════════════════════════════╣");
            log.info("║ Final Result: {}", String.format("%-43s", result.getName()) + "║");
            log.info("║ Type: {}", String.format("%-51s", result.getType()) + "║");
            log.info("║ HP: {}", String.format("%-53d", result.getHp()) + "║");
            log.info("║ Damage: {}", String.format("%-49d", result.getDamage()) + "║");
            log.info("╚════════════════════════════════════════════════════════════════╝");
            
            return result;
            
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

    private void logTreeTraversalResults(KieSession kieSession, BackwardQuery query) {
        if (query.getContext() == null || query.getContext().getPlayer() == null) {
            return;
        }
        
        try {
            log.info("\n──────────────────────────────────────────────────────────");
            log.info("TREE TRAVERSAL VALIDATION:");
            log.info("──────────────────────────────────────────────────────────");
            
            // Check Level 1: Region Match
            QueryResults level1Results = kieSession.getQueryResults(
                "level1_regionMatch", 
                query.getTargetEnemy(), 
                query.getContext()
            );
            
            if (level1Results.size() > 0) {
                log.info("✓ LEVEL 1 (OR): Region Match - PASSED");
                log.info("  └─ Region: {} matches enemy region", query.getContext().getRegion());
            } else {
                log.info("✗ LEVEL 1 (OR): Region Match - FAILED");
                log.info("  └─ Region: {} does not match enemy", query.getContext().getRegion());
            }
            
            // Check Level 2: Difficulty and Level
            String enemyType = determineEnemyType(query.getTargetEnemy(), kieSession);
            QueryResults level2Results = kieSession.getQueryResults(
                "level2_difficultyAndLevel",
                query.getTargetEnemy(),
                query.getContext(),
                query.getContext().getPlayer(),
                enemyType
            );
            
            if (level2Results.size() > 0) {
                log.info("✓ LEVEL 2 (AND): Difficulty={} AND Level={} - PASSED", 
                    query.getContext().getDifficulty(),
                    query.getContext().getPlayer().getLevel());
            } else {
                log.info("✗ LEVEL 2 (AND): Difficulty OR Level check - FAILED");
                log.info("  └─ Difficulty: {}, Player Level: {}", 
                    query.getContext().getDifficulty(),
                    query.getContext().getPlayer().getLevel());
            }
            
            // Check Level 3: Build and Equipment
            QueryResults level3Results = kieSession.getQueryResults(
                "level3_buildAndEquipment",
                query.getTargetEnemy(),
                query.getContext(),
                query.getContext().getPlayer(),
                enemyType
            );
            
            if (level3Results.size() > 0) {
                log.info("✓ LEVEL 3 (AND): Build={} AND Weapon={} - PASSED",
                    query.getContext().getPlayer().getPlayerClass(),
                    query.getContext().getPlayer().getWeaponType());
            } else {
                log.info("✗ LEVEL 3 (AND): Build OR Weapon check - FAILED");
                log.info("  └─ Class: {}, Weapon: {}",
                    query.getContext().getPlayer().getPlayerClass(),
                    query.getContext().getPlayer().getWeaponType());
            }
            
            log.info("──────────────────────────────────────────────────────────");
            
        } catch (Exception e) {
            log.warn("Could not validate tree levels: {}", e.getMessage());
        }
    }
    
    private String determineEnemyType(String enemyName, KieSession kieSession) {
        Collection<?> enemies = kieSession.getObjects(new org.kie.api.runtime.ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object instanceof Enemy && ((Enemy) object).getName().equals(enemyName);
            }
        });
        
        if (!enemies.isEmpty()) {
            Enemy enemy = (Enemy) enemies.iterator().next();
            return enemy.getType();
        }
        return "regular";
    }

    private void loadAllEnemiesForBackwardAsCopies(KieSession kieSession, List<Enemy> enemyCandidates) {
        try {
            List<Enemy> allEnemies = enemyRepository.findAll();
            log.info("Loading {} total enemies for backward chaining", allEnemies.size());
            
            for (Enemy originalEnemy : allEnemies) {
                Enemy enemyCopy = createEnemyCopy(originalEnemy);
                kieSession.insert(enemyCopy);
                enemyCandidates.add(enemyCopy);
            }
            
            log.info("Loaded {} enemy candidates", enemyCandidates.size());
        } catch (Exception e) {
            log.warn("Could not load enemies for backward chaining", e);
        }
    }

    private Enemy handleBackwardResult(BackwardQuery query, List<Enemy> enemyCandidates) {
        log.info("\n=== BACKWARD RESULT HANDLER ===");
        log.info("Query Status:");
        log.info("  - Processed: {}", query.isProcessed());
        log.info("  - Conditions Met: {}", query.isConditionsMet());
        log.info("  - Selected Enemy: {}", query.getSelectedEnemy());
        log.info("  - Fallback Enemy: {}", query.getFallbackEnemy());
        
        // Priority 1: Exact match - all tree conditions met
        if (query.isConditionsMet() && query.getSelectedEnemy() != null) {
            log.info("→ PRIORITY 1: Exact match (all tree levels passed)");
            Enemy selected = findEnemyByNameInCandidates(query.getSelectedEnemy(), enemyCandidates);
            if (selected != null) {
                log.info("  ✓ Returning: {}", selected.getName());
                return selected;
            }
        }
        
        // Priority 2: Conditions met with fallback (e.g., AUTO_COUNTER)
        if (query.isConditionsMet() && query.getFallbackEnemy() != null) {
            log.info("→ PRIORITY 2: Counter match");
            Enemy fallback = findEnemyByNameInCandidates(query.getFallbackEnemy(), enemyCandidates);
            if (fallback != null) {
                log.info("  ✓ Returning: {}", fallback.getName());
                return fallback;
            }
        }
        
        // Priority 3: Regional fallback (OR alternative)
        if (query.getFallbackEnemy() != null) {
            log.info("→ PRIORITY 3: Regional fallback (OR alternative)");
            Enemy fallback = findEnemyByNameInCandidates(query.getFallbackEnemy(), enemyCandidates);
            if (fallback != null) {
                log.info("  ✓ Returning: {}", fallback.getName());
                return fallback;
            }
        }
        
        // Priority 4: Best candidate by score
        if (!enemyCandidates.isEmpty()) {
            log.info("→ PRIORITY 4: Best candidate by score");
            
            List<Enemy> filtered = enemyCandidates;
            if (query.getContext() != null && query.getContext().getRegion() != null) {
                filtered = enemyCandidates.stream()
                    .filter(e -> e.getRegion().equals(query.getContext().getRegion()))
                    .collect(Collectors.toList());
                log.info("  - Filtered to {} enemies in region {}", 
                    filtered.size(), query.getContext().getRegion());
            }
            
            if (!filtered.isEmpty()) {
                Enemy best = filtered.stream()
                    .max((e1, e2) -> Double.compare(e1.getScore(), e2.getScore()))
                    .orElse(null);
                
                if (best != null) {
                    log.info("  ✓ Returning: {} (Score: {})", best.getName(), best.getScore());
                    return best;
                }
            }
            
            log.info("  - Using first from original candidates");
            return enemyCandidates.get(0);
        }
        
        // Priority 5: Create fallback
        log.warn("→ PRIORITY 5: Creating fallback enemy");
        GameContext ctx = query.getContext() != null ? 
            query.getContext() : 
            new GameContext("castle", "medium", "clear", "day", null);
        return createFallbackEnemy(ctx);
    }

    private Enemy findEnemyByNameInCandidates(String name, List<Enemy> enemyCandidates) {
        return enemyCandidates.stream()
            .filter(enemy -> enemy.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
    
    // ==================== UTILITY METHODS FOR TESTING ====================
    
    public void testRecursiveQueries(String enemyName, GameContext context) {
        KieSession kieSession = null;
        try {
            kieSession = kieContainer.newKieSession("backwardChainingSession");
            
            kieSession.insert(context);
            if (context.getPlayer() != null) {
                kieSession.insert(context.getPlayer());
            }
            
            Enemy testEnemy = new Enemy(enemyName, "boss");
            testEnemy.setRegion(context.getRegion());
            kieSession.insert(testEnemy);
            
            log.info("Testing recursive queries for: {}", enemyName);
            
            // Test level 1
            QueryResults level1 = kieSession.getQueryResults("level1_regionMatch", enemyName, context);
            log.info("Level 1 (Region): {} results", level1.size());
            
            // Test level 2
            QueryResults level2 = kieSession.getQueryResults("level2_difficultyAndLevel", 
                enemyName, context, context.getPlayer(), "boss");
            log.info("Level 2 (Difficulty + Level): {} results", level2.size());
            
            // Test level 3
            QueryResults level3 = kieSession.getQueryResults("level3_buildAndEquipment",
                enemyName, context, context.getPlayer(), "boss");
            log.info("Level 3 (Build + Equipment): {} results", level3.size());
            
        } catch (Exception e) {
            log.error("Error testing recursive queries", e);
        } finally {
            if (kieSession != null) {
                kieSession.dispose();
            }
        }
    }
}