// src/types/enemy.ts
export interface Player {
    name: string;
    level: number;
    playerClass: 'DEX' | 'STRENGTH' | 'MAGE';
    weaponType: string;
}

export interface GameContext {
    region: string;
    difficulty: string;
    weather: string;
    timeOfDay: string;
    player: Player;
}

export interface Enemy {
    name: string;
    type: string;
    region: string;
    hp: number;
    damage: number;
    defense: number;
    behaviour: string;
    abilities: string[];
    statusEffects: string[];
    resistances: string[];
    score: number;
}

export interface BackwardQuery {
    targetEnemy: string;
    context: GameContext;
}

export const REGIONS = [
    { value: 'swamp', label: 'Swamp', icon: '🌿' },
    { value: 'castle', label: 'Castle', icon: '🏰' },
    { value: 'mountain', label: 'Mountain', icon: '⛰️' },
    { value: 'volcano', label: 'Volcano', icon: '🌋' },
    { value: 'desert', label: 'Desert', icon: '🏜️' },
] as const;

export const DIFFICULTIES = [
    { value: 'easy', label: 'Easy', color: 'text-green-400' },
    { value: 'medium', label: 'Medium', color: 'text-yellow-400' },
    { value: 'medium-hard', label: 'Medium-Hard', color: 'text-orange-400' },
    { value: 'hard', label: 'Hard', color: 'text-red-400' },
] as const;

export const WEATHER_CONDITIONS = [
    { value: 'clear', label: 'Clear', icon: '☀️' },
    { value: 'rain', label: 'Rain', icon: '🌧️' },
    { value: 'fog', label: 'Fog', icon: '🌫️' },
    { value: 'wind', label: 'Wind', icon: '💨' },
    { value: 'snow', label: 'Snow', icon: '❄️' },
] as const;

export const TIME_OF_DAY = [
    { value: 'day', label: 'Day', icon: '🌞' },
    { value: 'night', label: 'Night', icon: '🌙' },
    { value: 'dawn', label: 'Dawn', icon: '🌅' },
    { value: 'dusk', label: 'Dusk', icon: '🌆' },
] as const;

export const PLAYER_CLASSES = [
    { value: 'STRENGTH', label: 'Strength Build', icon: '⚔️', description: 'Strong and durable, uses heavy weapons' },
    { value: 'DEX', label: 'Dexterity Build', icon: '🗡️', description: 'Fast and agile, uses katanas and bows' },
    { value: 'MAGE', label: 'Mage Build', icon: '🔮', description: 'Magical attacker, uses spells' },
] as const;

export const WEAPON_TYPES = {
    STRENGTH: ['greatsword', 'hammer', 'axe', 'mace'],
    DEX: ['katana', 'bow', 'daggers', 'rapier'],
    MAGE: ['staff', 'wand', 'catalyst', 'talisman'],
} as const;

