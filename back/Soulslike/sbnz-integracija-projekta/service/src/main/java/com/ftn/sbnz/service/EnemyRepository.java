package com.ftn.sbnz.service;

import com.ftn.sbnz.model.models.Enemy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnemyRepository extends JpaRepository<Enemy, Long> {
    
    List<Enemy> findByRegion(String region);
    
    @Query("SELECT e FROM Enemy e WHERE e.region = :region AND e.score >= :minScore ORDER BY e.score DESC")
    List<Enemy> findByRegionAndMinScore(@Param("region") String region, @Param("minScore") double minScore);
    
    List<Enemy> findByType(String type);
    
    @Query("SELECT e FROM Enemy e WHERE e.region = :region AND e.type != 'boss'")
    List<Enemy> findRegularEnemiesByRegion(@Param("region") String region);
    
    @Query("SELECT e FROM Enemy e WHERE e.region = :region AND e.type = 'boss'")
    List<Enemy> findBossEnemiesByRegion(@Param("region") String region);
}