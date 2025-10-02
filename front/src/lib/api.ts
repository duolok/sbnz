// src/lib/api.ts
import axios from 'axios';
import { GameContext, Enemy, BackwardQuery } from '@/types/enemy';

const getApiBaseUrl = () => {
    if (typeof window === 'undefined') {
        return process.env.BACKEND_URL || 'http://localhost:8080/api';
    }
    return 'http://localhost:8080/api';
};

const API_BASE_URL = getApiBaseUrl();

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// ==================== GENERATION ====================

export const generateEnemyForward = async (context: GameContext): Promise<Enemy> => {
    try {
        const response = await api.post<Enemy>('/enemy/generate/forward', context);
        return response.data;
    } catch (error) {
        console.error('Error generating enemy (forward):', error);
        throw error;
    }
};

export const generateEnemyBackward = async (query: BackwardQuery): Promise<Enemy> => {
    try {
        const response = await api.post<Enemy>('/enemy/generate/backward', query);
        return response.data;
    } catch (error) {
        console.error('Error generating enemy (backward):', error);
        throw error;
    }
};

// ==================== CRUD OPERATIONS ====================

export const createEnemy = async (enemy: Partial<Enemy>): Promise<{ success: boolean; enemy?: Enemy; error?: string }> => {
    try {
        const response = await api.post('/enemy/create', enemy);
        return response.data;
    } catch (error: any) {
        if (error.response?.data?.error) {
            throw new Error(error.response.data.error);
        }
        throw new Error('Failed to create enemy');
    }
};

export const listAllEnemies = async (): Promise<Enemy[]> => {
    try {
        const response = await api.get<Enemy[]>('/enemy/list');
        return response.data;
    } catch (error) {
        console.error('Error listing enemies:', error);
        throw error;
    }
};

export const listEnemiesByRegion = async (region: string): Promise<Enemy[]> => {
    try {
        const response = await api.get<Enemy[]>(`/enemy/list/${region}`);
        return response.data;
    } catch (error) {
        console.error('Error listing enemies by region:', error);
        throw error;
    }
};

export const deleteEnemy = async (id: number): Promise<void> => {
    try {
        await api.delete(`/enemy/${id}`);
    } catch (error) {
        console.error('Error deleting enemy:', error);
        throw error;
    }
};

// ==================== TEST ENDPOINTS ====================

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

// ==================== MOCK DATA ====================

export const mockGenerateEnemy = async (): Promise<Enemy> => {
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
        weaknesses: [],
        score: 185
    };
};
