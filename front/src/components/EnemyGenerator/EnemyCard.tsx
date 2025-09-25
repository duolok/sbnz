// src/components/EnemyGenerator/EnemyCard.tsx
import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Enemy } from '@/types/enemy';
import {
    Heart,
    Sword,
    Shield,
    Zap,
    Target,
    Flame,
    Skull,
    Eye,
    Activity
} from 'lucide-react';

interface EnemyCardProps {
    enemy: Enemy | null;
    loading?: boolean;
}

const EnemyCard: React.FC<EnemyCardProps> = ({ enemy, loading = false }) => {
    if (loading) {
        return (
            <Card className="w-full h-full souls-border enemy-card-glow bg-gradient-to-br from-zinc-900 via-black to-zinc-900">
                <CardHeader>
                    <div className="h-8 bg-zinc-800 rounded animate-pulse" />
                    <div className="h-4 bg-zinc-800 rounded w-2/3 animate-pulse mt-2" />
                </CardHeader>
                <CardContent>
                    <div className="space-y-4">
                        {[1, 2, 3].map((i) => (
                            <div key={i} className="h-20 bg-zinc-800 rounded animate-pulse" />
                        ))}
                    </div>
                </CardContent>
            </Card>
        );
    }

    if (!enemy) {
        return (
            <Card className="w-full h-full souls-border bg-gradient-to-br from-zinc-900 via-black to-zinc-900 flex items-center justify-center min-h-[500px]">
                <div className="text-center p-8">
                    <Skull className="w-16 h-16 mx-auto text-zinc-600 mb-4" />
                    <p className="text-zinc-400 text-lg">Enemy is still not generated</p>
                    <p className="text-zinc-500 text-sm mt-2">Configure context and click "Generate"</p>
                </div>
            </Card>
        );
    }

    const getStatPercentage = (value: number, max: number) => (value / max) * 100;
    const maxHp = 10000;
    const maxDamage = 1000;
    const maxDefense = 600;

    const getTypeIcon = (type: string) => {
        switch (type.toLowerCase()) {
            case 'boss': return <Flame className="w-5 h-5 text-red-500" />;
            case 'witch': return <Eye className="w-5 h-5 text-purple-500" />;
            case 'knight': return <Shield className="w-5 h-5 text-gray-400" />;
            case 'goblin': return <Target className="w-5 h-5 text-green-500" />;
            default: return <Skull className="w-5 h-5 text-orange-500" />;
        }
    };

    return (
        <Card className="w-full souls-border enemy-card-glow bg-gradient-to-br from-zinc-900 via-black to-zinc-900 overflow-hidden">
            <div className="absolute top-0 right-0 w-32 h-32 bg-orange-500 opacity-5 rounded-full blur-3xl" />
            <CardHeader className="relative">
                <div className="flex items-center justify-between">
                    <CardTitle className="text-3xl souls-glow text-orange-400 flex items-center gap-3">
                        {getTypeIcon(enemy.type)}
                        {enemy.name}
                    </CardTitle>
                    {enemy.score > 150 && (
                        <Badge variant="outline" className="border-yellow-600 text-yellow-500 animate-pulse">
                            Elite
                        </Badge>
                    )}
                </div>
                <CardDescription className="text-zinc-400 mt-2">
                    <span className="capitalize">{enemy.type}</span> •
                    <span className="capitalize ml-2">{enemy.region}</span> •
                    <span className="capitalize ml-2 text-orange-300">{enemy.behaviour}</span>
                </CardDescription>
            </CardHeader>

            <CardContent className="space-y-6">
                {/* Stats */}
                <div className="space-y-4">
                    <div className="space-y-2">
                        <div className="flex justify-between items-center">
                            <span className="flex items-center gap-2 text-zinc-300">
                                <Heart className="w-4 h-4 text-red-500" />
                                HP
                            </span>
                            <span className="text-red-400 font-bold">{enemy.hp}</span>
                        </div>
                        <div className="w-full bg-zinc-800 rounded-full h-2 overflow-hidden">
                            <div
                                className="h-full hp-bar rounded-full transition-all duration-500"
                                style={{ width: `${getStatPercentage(enemy.hp, maxHp)}%` }}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <div className="flex justify-between items-center">
                            <span className="flex items-center gap-2 text-zinc-300">
                                <Sword className="w-4 h-4 text-orange-500" />
                                Damage
                            </span>
                            <span className="text-orange-400 font-bold">{enemy.damage}</span>
                        </div>
                        <div className="w-full bg-zinc-800 rounded-full h-2 overflow-hidden">
                            <div
                                className="h-full dmg-bar rounded-full transition-all duration-500"
                                style={{ width: `${getStatPercentage(enemy.damage, maxDamage)}%` }}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <div className="flex justify-between items-center">
                            <span className="flex items-center gap-2 text-zinc-300">
                                <Shield className="w-4 h-4 text-gray-500" />
                                Defense
                            </span>
                            <span className="text-gray-400 font-bold">{enemy.defense}</span>
                        </div>
                        <div className="w-full bg-zinc-800 rounded-full h-2 overflow-hidden">
                            <div
                                className="h-full def-bar rounded-full transition-all duration-500"
                                style={{ width: `${getStatPercentage(enemy.defense, maxDefense)}%` }}
                            />
                        </div>
                    </div>
                </div>

                {/* Abilities */}
                {enemy.abilities && enemy.abilities.length > 0 && (
                    <div>
                        <h4 className="text-sm font-semibold text-zinc-400 mb-2 flex items-center gap-2">
                            <Zap className="w-4 h-4 text-yellow-500" />
                            Abilities
                        </h4>
                        <div className="flex flex-wrap gap-2">
                            {enemy.abilities.map((ability) => (
                                <Badge
                                    key={ability}
                                    variant="secondary"
                                    className="bg-zinc-800 hover:bg-zinc-700 border-yellow-900 text-yellow-200"
                                >
                                    {ability}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Status Effects */}
                {enemy.statusEffects && enemy.statusEffects.length > 0 && (
                    <div>
                        <h4 className="text-sm font-semibold text-zinc-400 mb-2 flex items-center gap-2">
                            <Activity className="w-4 h-4 text-purple-500" />
                            Status effect
                        </h4>
                        <div className="flex flex-wrap gap-2">
                            {enemy.statusEffects.map((effect) => (
                                <Badge
                                    key={effect}
                                    variant="outline"
                                    className="border-purple-900 text-purple-300"
                                >
                                    {effect}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Resistances */}
                {enemy.resistances && enemy.resistances.length > 0 && (
                    <div>
                        <h4 className="text-sm font-semibold text-zinc-400 mb-2 flex items-center gap-2">
                            <Shield className="w-4 h-4 text-blue-500" />
                            Resistances:
                        </h4>
                        <div className="flex flex-wrap gap-2">
                            {enemy.resistances.map((resistance) => (
                                <Badge
                                    key={resistance}
                                    variant="outline"
                                    className="border-blue-900 text-blue-300"
                                >
                                    {resistance}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Score */}
                <div className="pt-4 border-t border-zinc-800">
                    <div className="flex justify-between items-center">
                        <span className="text-zinc-400">Score</span>
                        <span className="text-2xl font-bold text-orange-400">{enemy.score}</span>
                    </div>
                </div>
            </CardContent>
        </Card>
    );
};

export default EnemyCard;
