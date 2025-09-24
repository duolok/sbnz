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
    public EnemyGenerationService(KieContainer kieContainer) {
        this.kieContainer = kieContainer;
    }
    
    public Enemy generateEnemy(GameContext context) {
        log.info("Starting FORWARD CHAINING enemy generation for context: {}", context);
        
        List<Enemy> enemyCandidates = new ArrayList<>();
        Enemy selectedEnemy = null;
        
        try {
            KieSession kieSession = kieContainer.newKieSession("forwardChainingSession");
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            kieSession.setGlobal("selectedEnemy", selectedEnemy);
            kieSession.setGlobal("log", log);
            
            // Insert facts
            kieSession.insert(context);
            if (context.getPlayer() != null) {
                kieSession.insert(context.getPlayer());
            }
            
            // Faza 1: Generisanje template protivnika
            kieSession.getAgenda().getAgendaGroup("template-generation").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 1 - Templates generated: {}", enemyCandidates.size());
            
            // Faza 2: Prilagođavanje težini
            kieSession.getAgenda().getAgendaGroup("difficulty-adjustment").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 2 - Difficulty adjustment completed");
            
            // Faza 3: Prilagođavanje nivou igrača
            kieSession.getAgenda().getAgendaGroup("player-level-adjustment").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 3 - Player level adjustment completed");
            
            // Faza 4: Protivmere build-u igrača
            kieSession.getAgenda().getAgendaGroup("player-build-counter").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 4 - Player build counter completed");
            
            // Faza 5: Vremenski uslovi
            kieSession.getAgenda().getAgendaGroup("weather-effects").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 5 - Weather effects applied");
            
            // Faza 6: Vreme dana
            kieSession.getAgenda().getAgendaGroup("time-of-day-effects").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 6 - Time of day effects applied");
            
            // Faza 7: Finalna selekcija
            kieSession.getAgenda().getAgendaGroup("final-selection").setFocus();
            kieSession.fireAllRules();
            log.info("Phase 7 - Final selection completed");
            
            // Proveri da li je neprijatelj selektovan
            selectedEnemy = (Enemy) kieSession.getGlobal("selectedEnemy");
            
            kieSession.dispose();
            
            if (selectedEnemy != null) {
                log.info("Forward chaining completed. Selected enemy: {}", selectedEnemy.getName());
                return selectedEnemy;
            } else if (!enemyCandidates.isEmpty()) {
                // Fallback: odaberi najboljeg kandidata po score-u
                selectedEnemy = enemyCandidates.stream()
                    .max((e1, e2) -> Double.compare(e1.getScore(), e2.getScore()))
                    .orElse(null);
                log.info("Selected best candidate by score: {}", selectedEnemy != null ? selectedEnemy.getName() : "null");
                return selectedEnemy;
            } else {
                log.warn("No enemy generated, returning dummy enemy");
                return createDummyEnemy(context);
            }
            
        } catch (Exception e) {
            log.error("Error in forward chaining enemy generation", e);
            return createDummyEnemy(context);
        }
    }

    public Enemy findSpecificEnemy(BackwardQuery query) {
        try {
            KieSession kieSession = kieContainer.newKieSession("backwardChainingSession");
            List<Enemy> enemyCandidates = new ArrayList<>();
            
            kieSession.setGlobal("enemyCandidates", enemyCandidates);
            kieSession.setGlobal("log", log);
            
            kieSession.insert(query);
            
            // Insert context and player if available
            if (query.getContext() != null) {
                kieSession.insert(query.getContext());
                if (query.getContext().getPlayer() != null) {
                    kieSession.insert(query.getContext().getPlayer());
                }
            }
            
            // Insert available enemies for backward chaining
            insertDummyEnemiesForBackwardChaining(kieSession);
            
            kieSession.fireAllRules();
            kieSession.dispose();
            
            // Return the appropriate enemy based on query results
            if (query.isConditionsMet() && query.getTargetEnemy() != null) {
                return findEnemyByName(query.getTargetEnemy());
            } else if (query.getFallbackEnemy() != null) {
                return findEnemyByName(query.getFallbackEnemy());
            } else {
                log.warn("Backward chaining failed, returning dummy enemy");
                return createDummyEnemy(query.getContext() != null ? 
                    query.getContext() : new GameContext("castle", "medium", "clear", "day", null));
            }
            
        } catch (Exception e) {
            log.error("Error in backward chaining enemy generation", e);
            return createDummyEnemy(new GameContext("castle", "medium", "clear", "day", null));
        }
    }

    private void insertDummyEnemiesForBackwardChaining(KieSession kieSession) {
        // Boss enemies
        Enemy ironLord = new Enemy("Iron Lord", "boss");
        ironLord.setRegion("castle");
        ironLord.setHp(8000);
        ironLord.setDamage(800);
        ironLord.addAbility("melee");
        ironLord.addAbility("charge");
        kieSession.insert(ironLord);
        
        Enemy poisonHydra = new Enemy("Poison Hydra", "boss");
        poisonHydra.setRegion("swamp");
        poisonHydra.setHp(6000);
        poisonHydra.setDamage(600);
        poisonHydra.addAbility("poison");
        poisonHydra.addStatusEffect("poison");
        kieSession.insert(poisonHydra);
        
        Enemy mountainDragon = new Enemy("Mountain Dragon", "boss");
        mountainDragon.setRegion("mountain");
        mountainDragon.setHp(10000);
        mountainDragon.setDamage(900);
        mountainDragon.addAbility("fire");
        mountainDragon.addStatusEffect("fire");
        kieSession.insert(mountainDragon);
        
        // Elite enemies
        Enemy swampWitch = new Enemy("Swamp Witch", "witch");
        swampWitch.setRegion("swamp");
        swampWitch.setHp(2800);
        swampWitch.setDamage(350);
        swampWitch.addAbility("magic");
        swampWitch.addAbility("poison");
        swampWitch.addStatusEffect("poison");
        kieSession.insert(swampWitch);
        
        Enemy stoneGolem = new Enemy("Stone Golem", "golem");
        stoneGolem.setRegion("mountain");
        stoneGolem.setHp(4500);
        stoneGolem.setDamage(300);
        stoneGolem.addAbility("melee");
        stoneGolem.addResistance("physical");
        kieSession.insert(stoneGolem);
        
        // Regular enemies
        Enemy swampCreature = new Enemy("Swamp Creature", "creature");
        swampCreature.setRegion("swamp");
        swampCreature.setHp(1500);
        swampCreature.setDamage(200);
        swampCreature.addAbility("poison");
        kieSession.insert(swampCreature);
        
        Enemy castleGuard = new Enemy("Castle Guard", "knight");
        castleGuard.setRegion("castle");
        castleGuard.setHp(2000);
        castleGuard.setDamage(250);
        castleGuard.addAbility("melee");
        kieSession.insert(castleGuard);
        
        Enemy mountainHarpy = new Enemy("Mountain Harpy", "bird");
        mountainHarpy.setRegion("mountain");
        mountainHarpy.setHp(1800);
        mountainHarpy.setDamage(320);
        mountainHarpy.addAbility("flying");
        mountainHarpy.addAbility("ranged");
        kieSession.insert(mountainHarpy);
    }
    
    private Enemy findEnemyByName(String name) {
        // Simplified implementation - u produkciji bi se radilo sa bazom
        switch (name) {
            case "Iron Lord":
                Enemy enemy = new Enemy("Iron Lord", "boss");
                enemy.setRegion("castle");
                enemy.setHp(8000);
                enemy.setDamage(800);
                return enemy;
            case "Poison Hydra":
                enemy = new Enemy("Poison Hydra", "boss");
                enemy.setRegion("swamp");
                enemy.setHp(6000);
                enemy.setDamage(600);
                return enemy;
            case "Swamp Witch":
                enemy = new Enemy("Swamp Witch", "witch");
                enemy.setRegion("swamp");
                enemy.setHp(2800);
                enemy.setDamage(350);
                return enemy;
            default:
                return createDummyEnemy(new GameContext("castle", "medium", "clear", "day", null));
        }
    }
    
    private Enemy createDummyEnemy(GameContext context) {
        Enemy dummy = new Enemy("Dummy Enemy", "test");
        
        switch (context.getRegion()) {
            case "swamp":
                dummy.setName("Test Swamp Monster");
                dummy.setHp(1200);
                dummy.setDamage(150);
                dummy.addAbility("poison");
                break;
            case "castle":
                dummy.setName("Test Castle Knight");
                dummy.setHp(1800);
                dummy.setDamage(200);
                dummy.addAbility("melee");
                break;
            case "mountain":
                dummy.setName("Test Mountain Beast");
                dummy.setHp(1500);
                dummy.setDamage(180);
                dummy.addAbility("charge");
                break;
            default:
                dummy.setName("Generic Test Enemy");
                dummy.setHp(1000);
                dummy.setDamage(100);
                dummy.addAbility("basic_attack");
        }
        
        switch (context.getDifficulty()) {
            case "easy":
                dummy.setHp(dummy.getHp() * 0.7);
                dummy.setDamage(dummy.getDamage() * 0.7);
                break;
            case "hard":
                dummy.setHp(dummy.getHp() * 1.5);
                dummy.setDamage(dummy.getDamage() * 1.5);
                break;
        }
        
        if (context.getPlayer() != null) {
            int playerLevel = context.getPlayer().getLevel();
            if (playerLevel < 10) {
                dummy.setHp(dummy.getHp() * 0.8);
                dummy.setDamage(dummy.getDamage() * 0.8);
            } else if (playerLevel > 30) {
                dummy.setHp(dummy.getHp() * 1.3);
                dummy.setDamage(dummy.getDamage() * 1.2);
            }
        }
        
        dummy.setRegion(context.getRegion());
        dummy.setDefense(50);
        dummy.setBehaviour("aggressive");
        dummy.setScore(50);
        
        log.info("Created dummy enemy: " + dummy.getName());
        return dummy;
    }
}