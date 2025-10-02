package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.BackwardQuery;
import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.model.models.Player;

import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enemy")
public class EnemyGenerationController {
    
    @Autowired
    private EnemyGenerationService enemyService;
    
    @PostMapping("/generate/forward")
    public Enemy generateEnemyForward(@RequestBody GameContext context) {
        return enemyService.generateEnemy(context);
    }
    
    @PostMapping("/generate/backward")
    public Enemy generateEnemyBackward(@RequestBody BackwardQuery query) {
        return enemyService.findSpecificEnemy(query);
    }

    @GetMapping("/test/backward/boss/{bossName}")
    public Map<String, Object> testBossSpawn(
        @PathVariable String bossName,
        @RequestParam(defaultValue = "40") int playerLevel,
        @RequestParam(defaultValue = "castle") String region,
        @RequestParam(defaultValue = "hard") String difficulty
    ) {
        Player player = new Player("BossHunter", playerLevel, 
            Player.PlayerClass.STRENGTH, "greatsword");
        GameContext context = new GameContext(region, difficulty, "clear", "day", player);
        BackwardQuery query = new BackwardQuery(bossName, context);
        
        Enemy result = enemyService.findSpecificEnemy(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestedBoss", bossName);
        response.put("conditionsMet", query.isConditionsMet());
        response.put("resultEnemy", result.getName());
        response.put("resultType", result.getType());
        response.put("meetsRequest", result.getName().equals(bossName));
        
        return response;
    }
    
    /**
     * Test AUTO_COUNTER recursive query
     */
    @GetMapping("/test/backward/auto-counter")
    public Map<String, Object> testAutoCounter(
        @RequestParam(defaultValue = "MAGE") String playerClass,
        @RequestParam(defaultValue = "30") int playerLevel,
        @RequestParam(defaultValue = "castle") String region
    ) {
        Player.PlayerClass pClass = Player.PlayerClass.valueOf(playerClass);
        Player player = new Player("TestPlayer", playerLevel, pClass, "staff");
        GameContext context = new GameContext(region, "hard", "clear", "day", player);
        
        BackwardQuery query = new BackwardQuery("AUTO_COUNTER", context);
        Enemy result = enemyService.findSpecificEnemy(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("playerClass", playerClass);
        response.put("selectedEnemy", result.getName());
        response.put("enemyAbilities", result.getAbilities());
        response.put("enemyResistances", result.getResistances());
        response.put("isGoodCounter", evaluateCounter(pClass, result));
        
        return response;
    }
    
    @GetMapping("/test/backward/region-hierarchy")
    public Map<String, Object> testRegionHierarchy() {
        Player player = new Player("Explorer", 25, Player.PlayerClass.DEX, "bow");
        GameContext context = new GameContext("swamp", 
            "medium", "clear", "day", player);
        
        BackwardQuery query = new BackwardQuery("Iron Knight", context);
        Enemy result = enemyService.findSpecificEnemy(query);
        
        Map<String, Object> response = new HashMap<>();
        response.put("requestedRegion", "castle-inner-sanctum");
        response.put("resultEnemy", result.getName());
        response.put("enemyRegion", result.getRegion());
        
        return response;
    }
    
    private boolean evaluateCounter(Player.PlayerClass playerClass, Enemy enemy) {
        switch (playerClass) {
            case DEX:
                return enemy.getAbilities().contains("stealth") || 
                       enemy.getStatusEffects().contains("poison");
            case STRENGTH:
                return enemy.getAbilities().contains("ranged") || 
                       enemy.getAbilities().contains("magic");
            case MAGE:
                return enemy.getResistances().contains("magic");
            default:
                return false;
        }
    }
    
    @GetMapping("/test/backward")
    public Enemy testBackwardChaining() {
        Player testPlayer = new Player("BossHunter", 45, Player.PlayerClass.STRENGTH, "greatsword");
        GameContext testContext = new GameContext("castle", "hard", "clear", "day", testPlayer);
        BackwardQuery query = new BackwardQuery("Iron Lord", testContext);
        return enemyService.findSpecificEnemy(query);
    }

    @GetMapping("/test/backward/elite")
    public Enemy testBackwardChainingElite() {
        Player testPlayer = new Player("EliteHunter", 25, Player.PlayerClass.DEX, "bow");
        GameContext testContext = new GameContext("swamp", "medium-hard", "fog", "night", testPlayer);
        
        BackwardQuery query = new BackwardQuery("Swamp Witch", testContext);
        return enemyService.findSpecificEnemy(query);
    }

    @GetMapping("/test/backward/counter")
    public Enemy testBackwardChainingCounter() {
        Player testPlayer = new Player("MagePlayer", 30, Player.PlayerClass.MAGE, "staff");
        GameContext testContext = new GameContext("castle", "hard", "clear", "day", testPlayer);
        
        BackwardQuery query = new BackwardQuery("AUTO_COUNTER", testContext);
        return enemyService.findSpecificEnemy(query);
    }

    
    @GetMapping("/test/custom")
    public Enemy testCustomEnemy(
            @RequestParam(defaultValue = "swamp") String region,
            @RequestParam(defaultValue = "medium") String difficulty,
            @RequestParam(defaultValue = "25") int playerLevel,
            @RequestParam(defaultValue = "STRENGTH") String playerClass,
            @RequestParam(defaultValue = "clear") String weather,
            @RequestParam(defaultValue = "day") String timeOfDay) {
        
        Player.PlayerClass pClass = Player.PlayerClass.valueOf(playerClass);
        Player player = new Player("TestPlayer", playerLevel, pClass, "sword");
        GameContext context = new GameContext(region, difficulty, weather, timeOfDay, player);
        
        return enemyService.generateEnemy(context);
    }
    
    @GetMapping("/info")
    public String getInfo() {
        return "Enemy Generation API is working! Available endpoints: /test/forward, /test/backward, /test/custom";
    }
}