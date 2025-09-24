// src/lib/api.ts
import axios from 'axios';
import { GameContext, Enemy, BackwardQuery } from '@/types/enemy';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Forward Chaining - generisanje neprijatelja na osnovu konteksta
export const generateEnemyForward = async (context: GameContext): Promise<Enemy> => {
    try {
        const response = await api.post<Enemy>('/enemy/generate/forward', context);
        return response.data;
    } catch (error) {
        console.error('Error generating enemy (forward):', error);
        throw error;
    }
};

// Backward Chaining - traženje specifičnog neprijatelja
export const generateEnemyBackward = async (query: BackwardQuery): Promise<Enemy> => {
    try {
        const response = await api.post<Enemy>('/enemy/generate/backward', query);
        return response.data;
    } catch (error) {
        console.error('Error generating enemy (backward):', error);
        throw error;
    }
};

// Test endpoints
export const testBackwardChaining = async (): Promise<Enemy> => {
    try {
        const response = await api.get<Enemy>('/enemy/test/backward');
        return response.data;
    } catch (error) {
        console.error('Error testing backward chaining:', error);
        throw error;
    }
};

export const testBackwardChainingElite = async (): Promise<Enemy> => {
    try {
        const response = await api.get<Enemy>('/enemy/test/backward/elite');
        return response.data;
    } catch (error) {
        console.error('Error testing backward chaining elite:', error);
        throw error;
    }
};

export const testBackwardChainingCounter = async (): Promise<Enemy> => {
    try {
        const response = await api.get<Enemy>('/enemy/test/backward/counter');
        return response.data;
    } catch (error) {
        console.error('Error testing backward chaining counter:', error);
        throw error;
    }
};

// Custom test sa parametrima
export const testCustomEnemy = async (params: {
    region?: string;
    difficulty?: string;
    playerLevel?: number;
    playerClass?: string;
    weather?: string;
    timeOfDay?: string;
}): Promise<Enemy> => {
    try {
        const response = await api.get<Enemy>('/enemy/test/custom', { params });
        return response.data;
    } catch (error) {
        console.error('Error testing custom enemy:', error);
        throw error;
    }
};

// Utility funkcija za simulaciju generisanja (za development)
export const mockGenerateEnemy = async (): Promise<Enemy> => {
    // Simulacija network delay-a
    await new Promise(resolve => setTimeout(resolve, 1000));

    return {
        name: "Swamp Witch",
        type: "witch",
        region: "swamp",
        hp: 3500,
        damage: 450,
        defense: 200,
        behaviour: "defensive",
        abilities: ["magic", "poison", "teleport"],
        statusEffects: ["poison", "slow"],
        resistances: ["magic", "poison"],
        score: 185
    };
};
