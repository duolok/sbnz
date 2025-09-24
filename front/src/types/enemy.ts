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
    { value: 'swamp', label: 'Močvara', icon: '🌿' },
    { value: 'castle', label: 'Zamak', icon: '🏰' },
    { value: 'mountain', label: 'Planina', icon: '⛰️' },
    { value: 'volcano', label: 'Vulkan', icon: '🌋' },
    { value: 'desert', label: 'Pustinja', icon: '🏜️' },
] as const;

export const DIFFICULTIES = [
    { value: 'easy', label: 'Lako', color: 'text-green-400' },
    { value: 'medium', label: 'Srednje', color: 'text-yellow-400' },
    { value: 'medium-hard', label: 'Srednje-teško', color: 'text-orange-400' },
    { value: 'hard', label: 'Teško', color: 'text-red-400' },
] as const;

export const WEATHER_CONDITIONS = [
    { value: 'clear', label: 'Vedro', icon: '☀️' },
    { value: 'rain', label: 'Kiša', icon: '🌧️' },
    { value: 'fog', label: 'Magla', icon: '🌫️' },
    { value: 'wind', label: 'Vetar', icon: '💨' },
    { value: 'snow', label: 'Sneg', icon: '❄️' },
] as const;

export const TIME_OF_DAY = [
    { value: 'day', label: 'Dan', icon: '🌞' },
    { value: 'night', label: 'Noć', icon: '🌙' },
    { value: 'dawn', label: 'Zora', icon: '🌅' },
    { value: 'dusk', label: 'Sumrak', icon: '🌆' },
] as const;

export const PLAYER_CLASSES = [
    { value: 'DEX', label: 'Dexterity Build', icon: '🗡️', description: 'Brz i agilan, koristi katane i lukove' },
    { value: 'STRENGTH', label: 'Strength Build', icon: '⚔️', description: 'Snažan i izdržljiv, koristi teško oružje' },
    { value: 'MAGE', label: 'Mage Build', icon: '🔮', description: 'Magični napadač, koristi čarolije' },
] as const;

export const WEAPON_TYPES = {
    DEX: ['katana', 'bow', 'daggers', 'rapier'],
    STRENGTH: ['greatsword', 'hammer', 'axe', 'mace'],
    MAGE: ['staff', 'wand', 'catalyst', 'talisman'],
} as const;
