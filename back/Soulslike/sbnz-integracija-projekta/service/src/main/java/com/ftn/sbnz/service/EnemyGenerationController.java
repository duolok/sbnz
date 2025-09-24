package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.BackwardQuery;
import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.model.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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