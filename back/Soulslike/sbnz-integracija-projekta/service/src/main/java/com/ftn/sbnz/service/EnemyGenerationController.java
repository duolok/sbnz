package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.BackwardQuery;
import com.ftn.sbnz.model.models.Enemy;
import com.ftn.sbnz.model.models.GameContext;
import com.ftn.sbnz.model.models.Player;

import org.apache.tools.ant.taskdefs.condition.Http;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    private EnemyRepository enemyRepository;
    
    @PostMapping("/generate/forward")
    public Enemy generateEnemyForward(@RequestBody GameContext context) {
        return enemyService.generateEnemy(context);
    }
    
    @PostMapping("/generate/backward")
    public Enemy generateEnemyBackward(@RequestBody BackwardQuery query) {
        return enemyService.findSpecificEnemy(query);
    }

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createEnemy(@RequestBody Enemy enemy) {
        try {
            // Validacija
            if (enemy.getName() == null || enemy.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Enemy name is required"));
            }
            
            if (enemy.getRegion() == null || enemy.getRegion().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Region is required"));
            }
            
            // Provera da li već postoji neprijatelj sa istim imenom u istom regionu
            List<Enemy> existing = enemyRepository.findByName(enemy.getName());
            boolean duplicateInRegion = existing.stream()
                .anyMatch(e -> e.getRegion().equals(enemy.getRegion()));
            
            if (duplicateInRegion) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of(
                        "error", "Enemy with this name already exists in this region",
                        "suggestion", "Try a different name or region"
                    ));
            }
            
            // Postavi default vrednosti ako nisu postavljene
            if (enemy.getHp() <= 0) enemy.setHp(1000);
            if (enemy.getDamage() <= 0) enemy.setDamage(100);
            if (enemy.getDefense() < 0) enemy.setDefense(50);
            if (enemy.getScore() < 0) enemy.setScore(100);
            if (enemy.getBehaviour() == null) enemy.setBehaviour("aggressive");
            if (enemy.getType() == null) enemy.setType("regular");
            
            // Sačuvaj u bazu
            Enemy savedEnemy = enemyRepository.save(enemy);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Enemy created successfully");
            response.put("enemy", savedEnemy);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to create enemy: " + e.getMessage()));
        }
    }
    
    @GetMapping("/list")
    public ResponseEntity<List<Enemy>> listAllEnemies() {
        try {
            List<Enemy> enemies = enemyRepository.findAll();
            return ResponseEntity.ok(enemies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/list/{region}")
    public ResponseEntity<List<Enemy>> listEnemiesByRegion(@PathVariable String region) {
        try {
            List<Enemy> enemies = enemyRepository.findByRegion(region);
            return ResponseEntity.ok(enemies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEnemy(@PathVariable Long id) {
        try {
            if (!enemyRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            
            enemyRepository.deleteById(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Enemy deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete enemy"));
        }
    }
}