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
import java.util.Collection;
import java.util.List;

@Service
public class EnemyGenerationService {
    
    private static Logger log = LoggerFactory.getLogger(EnemyGenerationService.class);
    
    private final KieContainer kieContainer;
    
    @Autowired
    public EnemyGenerationService(KieContainer kieContainer) {
        log.info("Initialising Simplified Enemy Service");
        this.kieContainer = kieContainer;
    }
    
    /**
     * Generiše protivnika koristeći Drools pravila ili vraća dummy protivnika
     */
    public Enemy generateEnemy(GameContext context) {
        log.info("Starting enemy generation for context: " + context);
        
        try {
            // Pokušaj da koristiš Drools
            KieSession kieSession = kieContainer.newKieSession("simpleSession");
            
            // Insert the game context
            kieSession.insert(context);
            
            // Fire all rules
            int rulesFired = kieSession.fireAllRules();
            log.info("Number of rules fired: " + rulesFired);
            
            // Get all Enemy objects from session
            Collection<?> enemies = kieSession.getObjects(object -> object instanceof Enemy);
            
            kieSession.dispose();
            
            if (!enemies.isEmpty()) {
                Enemy enemy = (Enemy) enemies.iterator().next();
                log.info("Generated enemy from rules: " + enemy);
                return enemy;
            } else {
                log.info("No enemies generated from rules, returning dummy enemy");
                return createDummyEnemy(context);
            }
            
        } catch (Exception e) {
            log.error("Error with Drools, returning dummy enemy", e);
            return createDummyEnemy(context);
        }
    }
    
    /**
     * Kreira dummy protivnika za testiranje
     */
    private Enemy createDummyEnemy(GameContext context) {
        Enemy dummy = new Enemy("Dummy Enemy", "test");
        
        // Osnovno podešavanje na osnovu regiona
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
        
        // Podešavanje na osnovu težine
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
        
        // Podešavanje na osnovu nivoa igrača
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
        
        log.info("Created dummy enemy: " + dummy);
        return dummy;
    }
}