package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.model.models.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
public class EnemyGenerationController {
    
    @Autowired
    private EnemyGenerationService enemyService;
    
    @GetMapping("/dummy")
    public Enemy getDummyEnemy() {
        // Kreiraj test kontekst
        Player player = new Player("Test", 20, Player.PlayerClass.STRENGTH, "sword");
        GameContext context = new GameContext("swamp", "medium", "clear", "day", player);
        
        return enemyService.generateEnemy(context);
    }
    
    @GetMapping("/swamp")
    public Enemy getSwampEnemy() {
        Player player = new Player("Test", 15, Player.PlayerClass.DEX, "dagger");
        GameContext context = new GameContext("swamp", "easy", "fog", "night", player);
        
        return enemyService.generateEnemy(context);
    }
    
    @GetMapping("/castle")
    public Enemy getCastleEnemy() {
        Player player = new Player("Test", 30, Player.PlayerClass.MAGE, "staff");
        GameContext context = new GameContext("castle", "hard", "rain", "day", player);
        
        return enemyService.generateEnemy(context);
    }
    
    @GetMapping("/custom")
    public Enemy getCustomEnemy(
            @RequestParam(defaultValue = "swamp") String region,
            @RequestParam(defaultValue = "medium") String difficulty,
            @RequestParam(defaultValue = "20") int playerLevel) {
        
        Player player = new Player("Test", playerLevel, Player.PlayerClass.STRENGTH, "sword");
        GameContext context = new GameContext(region, difficulty, "clear", "day", player);
        
        return enemyService.generateEnemy(context);
    }
    
    @GetMapping("/info")
    public String getInfo() {
        return "Simple Test API is working! Try: /api/test/dummy, /api/test/swamp, /api/test/castle";
    }
}