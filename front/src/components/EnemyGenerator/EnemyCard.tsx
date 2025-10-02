import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import {
    Skull,
    Heart,
    Swords,
    Shield,
    Sparkles,
    Zap,
    Flame,
    Loader2,
    Crown,
    Star
} from 'lucide-react';
import { Enemy } from '@/types/enemy';

interface EnemyCardProps {
    enemy: Enemy | null;
    loading: boolean;
}

export default function EnemyCard({ enemy, loading }: EnemyCardProps) {
    if (loading) {
        return (
            <Card className="bg-gradient-to-br from-zinc-900/90 to-zinc-950/90 border border-zinc-800/50 backdrop-blur-sm shadow-2xl overflow-hidden">
                <CardContent className="flex flex-col items-center justify-center py-20">
                    <div className="relative">
                        <div className="absolute inset-0 bg-gradient-to-r from-orange-600 to-red-600 rounded-full blur-xl opacity-50 animate-pulse" />
                        <Loader2 className="w-16 h-16 text-orange-400 animate-spin relative z-10" />
                    </div>
                    <p className="text-zinc-400 mt-6 text-lg animate-pulse">Summoning enemy...</p>
                </CardContent>
            </Card>
        );
    }

    if (!enemy) {
        return (
            <Card className="bg-gradient-to-br from-zinc-900/50 to-zinc-950/50 border border-zinc-800/30 backdrop-blur-sm shadow-xl overflow-hidden group hover:border-zinc-700/50 transition-all duration-500">
                <div className="absolute inset-0 bg-gradient-to-br from-orange-600/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                <CardContent className="flex flex-col items-center justify-center py-20 relative">
                    <div className="relative mb-6">
                        <div className="absolute inset-0 bg-gradient-to-r from-zinc-600 to-zinc-700 rounded-full blur-2xl opacity-30" />
                        <Skull className="w-20 h-20 text-zinc-600 relative z-10 animate-float" />
                    </div>
                    <p className="text-zinc-500 text-lg font-semibold">No Enemy Generated</p>
                    <p className="text-zinc-600 text-sm mt-2">Select options and generate</p>
                </CardContent>
            </Card>
        );
    }

    const getTypeColor = (type: string) => {
        const colors: Record<string, string> = {
            boss: 'from-red-600 to-red-800',
            elite: 'from-purple-600 to-purple-800',
            miniboss: 'from-orange-600 to-orange-800',
            regular: 'from-blue-600 to-blue-800',
            creature: 'from-green-600 to-green-800',
            undead: 'from-gray-600 to-gray-800'
        };
        return colors[type] || 'from-zinc-600 to-zinc-800';
    };

    const getTypeIcon = (type: string) => {
        if (type === 'boss') return <Crown className="w-5 h-5" />;
        if (type === 'elite') return <Star className="w-5 h-5" />;
        return <Skull className="w-5 h-5" />;
    };

    const maxStat = Math.max(enemy.hp / 10, enemy.damage * 10, enemy.defense * 10);

    return (
        <Card className="bg-gradient-to-br from-zinc-900/90 to-zinc-950/90 border border-zinc-800/50 backdrop-blur-sm shadow-2xl overflow-hidden group hover:border-orange-600/30 transition-all duration-500 animate-fade-in">
            {/* Glowing header background */}
            <div className="absolute top-0 inset-x-0 h-40 bg-gradient-to-b from-orange-600/10 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />

            <CardHeader className="relative border-b border-zinc-800/50 pb-6">
                <div className="flex items-start justify-between mb-4">
                    <div className="flex-1">
                        <CardTitle className="text-3xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-orange-300 via-orange-400 to-red-400 mb-2 animate-gradient-x">
                            {enemy.name}
                        </CardTitle>
                        <CardDescription className="text-zinc-400 flex items-center gap-2">
                            Region: <span className="text-orange-400 font-semibold capitalize">{enemy.region}</span>
                        </CardDescription>
                    </div>

                    <div className="relative">
                        <div className={`absolute inset-0 bg-gradient-to-r ${getTypeColor(enemy.type)} rounded-lg blur-lg opacity-50`} />
                        <Badge className={`bg-gradient-to-r ${getTypeColor(enemy.type)} text-white border-none shadow-lg px-3 py-1.5 relative z-10 flex items-center gap-1`}>
                            {getTypeIcon(enemy.type)}
                            <span className="font-bold capitalize">{enemy.type}</span>
                        </Badge>
                    </div>
                </div>

                {/* Stats Grid */}
                <div className="grid grid-cols-3 gap-3 mt-6">
                    <div className="bg-gradient-to-br from-red-900/20 to-red-950/20 border border-red-800/30 rounded-lg p-3 backdrop-blur-sm hover:border-red-700/50 transition-all duration-300 group/stat">
                        <div className="flex items-center gap-2 mb-2">
                            <div className="p-1.5 bg-red-900/30 rounded-md group-hover/stat:bg-red-800/40 transition-colors">
                                <Heart className="w-4 h-4 text-red-400" />
                            </div>
                            <span className="text-xs text-zinc-400 font-semibold uppercase tracking-wider">HP</span>
                        </div>
                        <p className="text-2xl font-bold text-red-300">{Math.round(enemy.hp)}</p>
                    </div>

                    <div className="bg-gradient-to-br from-orange-900/20 to-orange-950/20 border border-orange-800/30 rounded-lg p-3 backdrop-blur-sm hover:border-orange-700/50 transition-all duration-300 group/stat">
                        <div className="flex items-center gap-2 mb-2">
                            <div className="p-1.5 bg-orange-900/30 rounded-md group-hover/stat:bg-orange-800/40 transition-colors">
                                <Swords className="w-4 h-4 text-orange-400" />
                            </div>
                            <span className="text-xs text-zinc-400 font-semibold uppercase tracking-wider">DMG</span>
                        </div>
                        <p className="text-2xl font-bold text-orange-300">{Math.round(enemy.damage)}</p>
                    </div>

                    <div className="bg-gradient-to-br from-blue-900/20 to-blue-950/20 border border-blue-800/30 rounded-lg p-3 backdrop-blur-sm hover:border-blue-700/50 transition-all duration-300 group/stat">
                        <div className="flex items-center gap-2 mb-2">
                            <div className="p-1.5 bg-blue-900/30 rounded-md group-hover/stat:bg-blue-800/40 transition-colors">
                                <Shield className="w-4 h-4 text-blue-400" />
                            </div>
                            <span className="text-xs text-zinc-400 font-semibold uppercase tracking-wider">DEF</span>
                        </div>
                        <p className="text-2xl font-bold text-blue-300">{Math.round(enemy.defense)}</p>
                    </div>
                </div>

                {/* Behavior Badge */}
                <div className="mt-4 flex items-center gap-2">
                    <Zap className="w-4 h-4 text-yellow-500" />
                    <span className="text-zinc-400 text-sm">Behavior:</span>
                    <Badge variant="outline" className="border-yellow-700/50 text-yellow-400 capitalize bg-yellow-900/10">
                        {enemy.behaviour}
                    </Badge>
                </div>
            </CardHeader>

            <CardContent className="space-y-6 pt-6 relative">
                {/* Abilities */}
                {enemy.abilities && enemy.abilities.length > 0 && (
                    <div className="space-y-3">
                        <div className="flex items-center gap-2">
                            <div className="p-1.5 bg-orange-900/30 rounded-md">
                                <Flame className="w-4 h-4 text-orange-400" />
                            </div>
                            <h3 className="text-sm font-bold text-orange-400 uppercase tracking-wider">Abilities</h3>
                        </div>
                        <div className="flex flex-wrap gap-2">
                            {enemy.abilities.map((ability, index) => (
                                <Badge
                                    key={index}
                                    className="bg-gradient-to-r from-orange-900/40 to-red-900/40 text-orange-200 border border-orange-700/30 hover:border-orange-600/50 transition-all duration-300 hover:scale-105 capitalize"
                                >
                                    {ability}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Resistances */}
                {enemy.resistances && enemy.resistances.length > 0 && (
                    <div className="space-y-3">
                        <div className="flex items-center gap-2">
                            <div className="p-1.5 bg-blue-900/30 rounded-md">
                                <Shield className="w-4 h-4 text-blue-400" />
                            </div>
                            <h3 className="text-sm font-bold text-blue-400 uppercase tracking-wider">Resistances</h3>
                        </div>
                        <div className="flex flex-wrap gap-2">
                            {enemy.resistances.map((resistance, index) => (
                                <Badge
                                    key={index}
                                    className="bg-gradient-to-r from-blue-900/40 to-cyan-900/40 text-blue-200 border border-blue-700/30 hover:border-blue-600/50 transition-all duration-300 hover:scale-105 capitalize"
                                >
                                    {resistance}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Status Effects */}
                {enemy.statusEffects && enemy.statusEffects.length > 0 && (
                    <div className="space-y-3">
                        <div className="flex items-center gap-2">
                            <div className="p-1.5 bg-purple-900/30 rounded-md">
                                <Sparkles className="w-4 h-4 text-purple-400" />
                            </div>
                            <h3 className="text-sm font-bold text-purple-400 uppercase tracking-wider">Status Effects</h3>
                        </div>
                        <div className="flex flex-wrap gap-2">
                            {enemy.statusEffects.map((effect, index) => (
                                <Badge
                                    key={index}
                                    className="bg-gradient-to-r from-purple-900/40 to-pink-900/40 text-purple-200 border border-purple-700/30 hover:border-purple-600/50 transition-all duration-300 hover:scale-105 capitalize"
                                >
                                    {effect}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Weaknesses */}
                {enemy.weaknesses && enemy.weaknesses.length > 0 && (
                    <div className="space-y-3">
                        <div className="flex items-center gap-2">
                            <div className="p-1.5 bg-red-900/30 rounded-md">
                                <Skull className="w-4 h-4 text-red-400" />
                            </div>
                            <h3 className="text-sm font-bold text-red-400 uppercase tracking-wider">Weaknesses</h3>
                        </div>
                        <div className="flex flex-wrap gap-2">
                            {Array.from(enemy.weaknesses).map((weakness, index) => (
                                <Badge
                                    key={index}
                                    className="bg-gradient-to-r from-red-900/40 to-rose-900/40 text-red-200 border border-red-700/30 hover:border-red-600/50 transition-all duration-300 hover:scale-105 capitalize"
                                >
                                    {weakness}
                                </Badge>
                            ))}
                        </div>
                    </div>
                )}

                {/* Score Badge */}
                <div className="pt-4 border-t border-zinc-800/50">
                    <div className="flex items-center justify-between">
                        <span className="text-zinc-400 text-sm font-semibold">Difficulty Score</span>
                        <div className="relative">
                            <div className="absolute inset-0 bg-gradient-to-r from-orange-600 to-red-600 rounded-full blur-md opacity-50" />
                            <Badge className="bg-gradient-to-r from-orange-600 to-red-600 text-white font-bold text-lg px-4 py-1.5 relative z-10 shadow-lg">
                                {Math.round(enemy.score)}
                            </Badge>
                        </div>
                    </div>
                </div>
            </CardContent>
        </Card>
    );
}
