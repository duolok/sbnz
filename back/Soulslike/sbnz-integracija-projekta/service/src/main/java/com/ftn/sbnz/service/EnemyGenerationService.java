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
                
                // Create session with generated DRL + all existing rules
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
        
        // Add the generated difficulty rules
        kieHelper.addContent(generatedDRL, ResourceType.DRL);
        
        // Load all existing rule files via InputStreams
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