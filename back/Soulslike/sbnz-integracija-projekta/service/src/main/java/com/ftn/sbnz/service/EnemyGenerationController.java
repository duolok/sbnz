package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.model.models.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/enemy")
public class EnemyGenerationController {
    
    private static Logger log = LoggerFactory.getLogger(EnemyGenerationController.class);
    
    private final EnemyGenerationService enemyService;
    
    @Autowired
    public EnemyGenerationController(EnemyGenerationService enemyService) {
        this.enemyService = enemyService;
    }
    
    /**
     * Generate an enemy based on provided context
     * Example URL: http://localhost:8080/api/enemy/generate?region=swamp&difficulty=medium&weather=fog&timeOfDay=night&playerLevel=28&playerClass=DEX&weapon=katana
     */
    @GetMapping("/generate")
    public Enemy generateEnemy(
            @RequestParam(required = true) String region,
            @RequestParam(required = true) String difficulty,
            @RequestParam(defaultValue = "clear") String weather,
            @RequestParam(defaultValue = "day") String timeOfDay,
            @RequestParam(defaultValue = "20") int playerLevel,
            @RequestParam(defaultValue = "STRENGTH") String playerClass,
            @RequestParam(defaultValue = "sword") String weapon) {
        
        log.info("Enemy generation request received");
        log.info("Region: {}, Difficulty: {}, Weather: {}, Time: {}", 
                region, difficulty, weather, timeOfDay);
        log.info("Player - Level: {}, Class: {}, Weapon: {}", 
                playerLevel, playerClass, weapon);
        
        // Create player
        Player player = new Player("TestPlayer", playerLevel, 
                Player.PlayerClass.valueOf(playerClass.toUpperCase()), weapon);
        
        // Create game context
        GameContext context = new GameContext(region.toLowerCase(), 
                difficulty.toLowerCase(), weather.toLowerCase(), 
                timeOfDay.toLowerCase(), player);
        
        // Generate enemy
        Enemy enemy = enemyService.generateEnemy(context);
        
        log.info("Generated enemy: {}", enemy);
        return enemy;
    }
    
    /**
     * Generate enemy with full context as JSON body
     */
    @PostMapping("/generate")
    public Enemy generateEnemyWithContext(@RequestBody GameContext context) {
        log.info("Enemy generation request with full context: {}", context);
        
        Enemy enemy = enemyService.generateEnemy(context);
        
        log.info("Generated enemy: {}", enemy);
        return enemy;
    }
    
    /**
     * Try to spawn a specific boss
     */
    @GetMapping("/boss/{bossName}")
    public Enemy trySpawnBoss(
            @PathVariable String bossName,
            @RequestParam(required = true) String region,
            @RequestParam(defaultValue = "hard") String difficulty,
            @RequestParam(defaultValue = "50") int playerLevel) {
        
        log.info("Boss spawn request for: {}", bossName);
        
        Player player = new Player("BossHunter", playerLevel, 
                Player.PlayerClass.STRENGTH, "greatsword");
        
        GameContext context = new GameContext(region.toLowerCase(), 
                difficulty.toLowerCase(), "clear", "day", player);
        
        Enemy boss = enemyService.trySpawnBoss(bossName, context);
        
        if (boss == null) {
            log.warn("Could not spawn boss: {}", bossName);
            // Return a message enemy indicating failure
            Enemy message = new Enemy("Boss Not Available", "message");
            message.setRegion("Conditions not met for spawning " + bossName);
            return message;
        }
        
        return boss;
    }
    
    /**
     * Test endpoint to verify service is working
     */
    @GetMapping("/test")
    public String test() {
        return "Enemy Generation Service is running!";
    }
    
    /**
     * Quick test scenarios
     */
    @GetMapping("/scenarios")
    public String getTestScenarios() {
        StringBuilder scenarios = new StringBuilder();
        scenarios.append("Test Scenarios:\n\n");
        
        scenarios.append("1. Low level player in easy swamp:\n");
        scenarios.append("   /api/enemy/generate?region=swamp&difficulty=easy&playerLevel=5&playerClass=DEX\n\n");
        
        scenarios.append("2. Mid level player in medium castle with rain:\n");
        scenarios.append("   /api/enemy/generate?region=castle&difficulty=medium&weather=rain&playerLevel=25&playerClass=MAGE\n\n");
        
        scenarios.append("3. High level player in hard mountain at night:\n");
        scenarios.append("   /api/enemy/generate?region=mountain&difficulty=hard&timeOfDay=night&playerLevel=45&playerClass=STRENGTH\n\n");
        
        scenarios.append("4. Swamp with fog (stealth bonus):\n");
        scenarios.append("   /api/enemy/generate?region=swamp&difficulty=medium-hard&weather=fog&playerLevel=30&playerClass=DEX&weapon=katana\n\n");
        
        scenarios.append("5. Try to spawn Iron Lord boss:\n");
        scenarios.append("   /api/enemy/boss/Iron%20Lord?region=castle&difficulty=hard&playerLevel=50\n\n");
        
        return scenarios.toString();
    }
}