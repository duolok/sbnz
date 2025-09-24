DROP TABLE IF EXISTS enemies;

CREATE TABLE enemies (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type VARCHAR(50) NOT NULL,
    region VARCHAR(50) NOT NULL,
    hp DOUBLE PRECISION NOT NULL,
    damage DOUBLE PRECISION NOT NULL,
    defense DOUBLE PRECISION NOT NULL,
    behaviour VARCHAR(50),
    abilities TEXT[] DEFAULT '{}',
    status_effects TEXT[] DEFAULT '{}',
    resistances TEXT[] DEFAULT '{}',
    weaknesses TEXT[] DEFAULT '{}',
    score DOUBLE PRECISION DEFAULT 0,
    experience_reward INTEGER DEFAULT 0,
    critical_chance DOUBLE PRECISION DEFAULT 0.1,
    dodge_chance DOUBLE PRECISION DEFAULT 0.1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO enemies (
    name, type, region, hp, damage, defense, behaviour,
    abilities, status_effects, resistances, weaknesses, score
) VALUES
-- Swamp enemies
('Swamp Witch', 'witch', 'swamp', 2800, 350, 150, 'defensive',
 ARRAY['magic','poison'], ARRAY['poison'], ARRAY[]::TEXT[], ARRAY[]::TEXT[], 100),
('Swamp Goblin', 'goblin', 'swamp', 1500, 250, 100, 'aggressive',
 ARRAY['stealth','melee'], ARRAY['poison'], ARRAY[]::TEXT[], ARRAY[]::TEXT[], 100),
('Bog Monster', 'creature', 'swamp', 3200, 400, 200, 'aggressive',
 ARRAY['melee','poison-spit'], ARRAY['poison'], ARRAY['poison'], ARRAY[]::TEXT[], 120),

-- Castle enemies
('Iron Knight', 'knight', 'castle', 3500, 400, 350, 'defensive',
 ARRAY['melee','shield-bash'], ARRAY[]::TEXT[], ARRAY['physical'], ARRAY[]::TEXT[], 100),
('Court Wizard', 'mage', 'castle', 2200, 500, 120, 'ranged',
 ARRAY['magic','ranged'], ARRAY['fire'], ARRAY['magic'], ARRAY[]::TEXT[], 100),
('Castle Archer', 'archer', 'castle', 1800, 300, 100, 'ranged',
 ARRAY['ranged','stealth'], ARRAY[]::TEXT[], ARRAY[]::TEXT[], ARRAY[]::TEXT[], 90),

-- Mountain enemies
('Mountain Harpy', 'bird', 'mountain', 1800, 320, 80, 'aggressive',
 ARRAY['flying','dive-attack'], ARRAY['wind'], ARRAY[]::TEXT[], ARRAY[]::TEXT[], 100),
('Stone Golem', 'golem', 'mountain', 5000, 450, 500, 'defensive',
 ARRAY['melee','earth-slam'], ARRAY[]::TEXT[], ARRAY['physical','magic'], ARRAY[]::TEXT[], 150),
('Mountain Troll', 'troll', 'mountain', 3000, 380, 200, 'aggressive',
 ARRAY['melee','regeneration'], ARRAY[]::TEXT[], ARRAY[]::TEXT[], ARRAY[]::TEXT[], 110),

-- Desert enemies
('Sand Wraith', 'wraith', 'desert', 2000, 280, 80, 'stealth',
 ARRAY['stealth','sand-blast'], ARRAY['sand-blind'], ARRAY['physical'], ARRAY[]::TEXT[], 100),
('Desert Scorpion', 'creature', 'desert', 2500, 320, 150, 'aggressive',
 ARRAY['melee','poison-sting'], ARRAY['poison'], ARRAY['poison'], ARRAY[]::TEXT[], 100),
('Sun Priest', 'mage', 'desert', 2400, 420, 100, 'ranged',
 ARRAY['magic','fire'], ARRAY['fire'], ARRAY['fire'], ARRAY[]::TEXT[], 120),

-- Forest enemies
('Forest Spirit', 'spirit', 'forest', 1600, 260, 60, 'stealth',
 ARRAY['stealth','nature-magic'], ARRAY['entangle'], ARRAY['magic'], ARRAY[]::TEXT[], 100),
('Wolf Pack', 'beast', 'forest', 1200, 200, 50, 'aggressive',
 ARRAY['melee','pack-tactics'], ARRAY[]::TEXT[], ARRAY[]::TEXT[], ARRAY[]::TEXT[], 80),
('Ancient Treant', 'treant', 'forest', 4000, 350, 300, 'defensive',
 ARRAY['melee','root-grab'], ARRAY['entangle'], ARRAY['physical'], ARRAY[]::TEXT[], 140),

-- Boss enemies
('Iron Lord', 'boss', 'castle', 8000, 800, 500, 'aggressive',
 ARRAY['melee','charge','ground-slam'], ARRAY['intimidation'], ARRAY['physical'], ARRAY[]::TEXT[], 200),
('Poison Hydra', 'boss', 'swamp', 10000, 600, 400, 'aggressive',
 ARRAY['poison-spit','regeneration','multi-attack'], ARRAY['poison'], ARRAY['poison'], ARRAY[]::TEXT[], 220),
('Mountain Dragon', 'boss', 'mountain', 12000, 900, 600, 'aggressive',
 ARRAY['fire-breath','flying','tail-swipe'], ARRAY['fire'], ARRAY['fire','physical'], ARRAY[]::TEXT[], 250);