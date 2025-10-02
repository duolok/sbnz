// src/components/EnemyGenerator/CreateEnemyForm.tsx
import React, { useState } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Plus, X, CheckCircle, AlertCircle, Loader2, Sparkles, Flame, Shield, Zap } from 'lucide-react';
import { createEnemy } from '@/lib/api';

interface CreateEnemyFormProps {
    onEnemyCreated?: () => void;
}

export default function CreateEnemyForm({ onEnemyCreated }: CreateEnemyFormProps) {
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [formData, setFormData] = useState({
        name: '',
        type: 'regular',
        region: 'swamp',
        hp: 1000,
        damage: 100,
        defense: 50,
        behaviour: 'aggressive',
        score: 100
    });

    const [abilities, setAbilities] = useState<string[]>([]);
    const [resistances, setResistances] = useState<string[]>([]);
    const [statusEffects, setStatusEffects] = useState<string[]>([]);
    const [weaknesses, setWeaknesses] = useState<string[]>([]);

    const [newAbility, setNewAbility] = useState('');
    const [newResistance, setNewResistance] = useState('');
    const [newStatusEffect, setNewStatusEffect] = useState('');

    const regions = ['swamp', 'castle', 'mountain', 'desert', 'forest', 'cave'];
    const types = ['regular', 'elite', 'boss', 'miniboss', 'creature', 'undead'];
    const behaviours = ['aggressive', 'defensive', 'ranged', 'balanced', 'ambush'];

    const commonAbilities = ['melee', 'ranged', 'magic', 'stealth', 'flying', 'teleport', 'shield-bash'];
    const commonResistances = ['physical', 'magic', 'fire', 'ice', 'poison', 'lightning'];
    const commonStatusEffects = ['poison', 'burn', 'freeze', 'slow', 'bleed', 'stun'];

    const handleSubmit = async () => {
        if (!formData.name.trim()) {
            setError('Enemy name is required');
            return;
        }

        setLoading(true);
        setError(null);
        setSuccess(false);

        try {
            const enemyData = {
                ...formData,
                abilities,
                resistances,
                statusEffects,
                weaknesses: [...weaknesses]
            };

            await createEnemy(enemyData);

            setSuccess(true);
            setError(null);

            setTimeout(() => {
                resetForm();
                if (onEnemyCreated) onEnemyCreated();
            }, 2000);

        } catch (err: any) {
            setError(err.message || 'Failed to create enemy');
            setSuccess(false);
        } finally {
            setLoading(false);
        }
    };

    const resetForm = () => {
        setFormData({
            name: '',
            type: 'regular',
            region: 'swamp',
            hp: 1000,
            damage: 100,
            defense: 50,
            behaviour: 'aggressive',
            score: 100
        });
        setAbilities([]);
        setResistances([]);
        setStatusEffects([]);
        setWeaknesses([]);
        setSuccess(false);
    };

    const addItem = (value: string, list: string[], setter: (list: string[]) => void) => {
        if (value.trim() && !list.includes(value.trim())) {
            setter([...list, value.trim()]);
        }
    };

    const removeItem = (index: number, list: string[], setter: (list: string[]) => void) => {
        setter(list.filter((_, i) => i !== index));
    };

    return (
        <Card className="bg-gradient-to-br from-green-950/20 to-zinc-950/30 border border-green-800/30 backdrop-blur-sm shadow-2xl overflow-hidden group hover:border-green-700/50 transition-all duration-500">
            <div className="absolute inset-0 bg-gradient-to-br from-green-600/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />

            <CardHeader className="relative border-b border-zinc-800/50 pb-6">
                <div className="flex items-center gap-3 mb-2">
                    <div className="relative">
                        <div className="absolute inset-0 bg-gradient-to-r from-green-600 to-emerald-600 rounded-lg blur-lg opacity-50" />
                        <div className="relative p-2 bg-gradient-to-br from-green-900/80 to-emerald-900/80 rounded-lg">
                            <Plus className="w-6 h-6 text-green-300" />
                        </div>
                    </div>
                    <CardTitle className="text-3xl text-transparent bg-clip-text bg-gradient-to-r from-green-300 to-emerald-400">
                        Create New Enemy
                    </CardTitle>
                </div>
                <CardDescription className="text-zinc-400 flex items-center gap-2">
                    <Sparkles className="w-4 h-4 text-green-500" />
                    Create a legendary adversary for your world
                </CardDescription>
            </CardHeader>

            <CardContent className="space-y-6 pt-6 relative">
                {success && (
                    <Alert className="bg-gradient-to-r from-green-950/50 to-emerald-950/50 border border-green-700/50 backdrop-blur-sm animate-fade-in">
                        <CheckCircle className="h-4 w-4 text-green-400" />
                        <AlertDescription className="text-green-300 font-semibold">
                            Enemy forged successfully! The darkness grows stronger...
                        </AlertDescription>
                    </Alert>
                )}

                {error && (
                    <Alert className="bg-gradient-to-r from-red-950/50 to-red-900/50 border border-red-700/50 backdrop-blur-sm animate-shake">
                        <AlertCircle className="h-4 w-4 text-red-400" />
                        <AlertDescription className="text-red-300">{error}</AlertDescription>
                    </Alert>
                )}

                {/* Basic Info */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                        <Label htmlFor="name" className="text-zinc-300 font-semibold flex items-center gap-2">
                            <Flame className="w-4 h-4 text-orange-500" />
                            Name <span className="text-red-500">*</span>
                        </Label>
                        <Input
                            id="name"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            className="text-white bg-zinc-900/50 border-zinc-700/50 hover:border-green-600/50 focus:border-green-500 transition-all duration-300 backdrop-blur-sm"
                            placeholder="e.g., Shadow Knight"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="type" className="text-zinc-300 font-semibold">Type</Label>
                        <select
                            id="type"
                            value={formData.type}
                            onChange={(e) => setFormData({ ...formData, type: e.target.value })}
                            className="w-full bg-zinc-900/50 border border-zinc-700/50 hover:border-green-600/50 focus:border-green-500 rounded-md px-3 py-2 text-white transition-all duration-300 backdrop-blur-sm"
                        >
                            {types.map(type => (
                                <option key={type} value={type}>{type}</option>
                            ))}
                        </select>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="region" className="text-zinc-300 font-semibold flex items-center gap-2">
                            <Zap className="w-4 h-4 text-blue-500" />
                            Region <span className="text-red-500">*</span>
                        </Label>
                        <select
                            id="region"
                            value={formData.region}
                            onChange={(e) => setFormData({ ...formData, region: e.target.value })}
                            className="w-full bg-zinc-900/50 border border-zinc-700/50 hover:border-green-600/50 focus:border-green-500 rounded-md px-3 py-2 text-white transition-all duration-300 backdrop-blur-sm"
                        >
                            {regions.map(region => (
                                <option key={region} value={region}>{region}</option>
                            ))}
                        </select>
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="behaviour" className="text-zinc-300 font-semibold">Behaviour</Label>
                        <select
                            id="behaviour"
                            value={formData.behaviour}
                            onChange={(e) => setFormData({ ...formData, behaviour: e.target.value })}
                            className="w-full bg-zinc-900/50 border border-zinc-700/50 hover:border-green-600/50 focus:border-green-500 rounded-md px-3 py-2 text-white transition-all duration-300 backdrop-blur-sm"
                        >
                            {behaviours.map(behaviour => (
                                <option key={behaviour} value={behaviour}>{behaviour}</option>
                            ))}
                        </select>
                    </div>
                </div>

                {/* Stats */}
                <div className="grid grid-cols-2 md:grid-cols-4 gap-4 p-4 bg-gradient-to-br from-zinc-900/50 to-zinc-950/30 rounded-lg border border-zinc-800/50">
                    <div className="space-y-2">
                        <Label htmlFor="hp" className="text-red-400 font-semibold text-xs uppercase tracking-wider">HP</Label>
                        <Input
                            id="hp"
                            type="number"
                            min="1"
                            value={formData.hp}
                            onChange={(e) => setFormData({ ...formData, hp: parseInt(e.target.value) })}
                            className="text-white bg-zinc-900/70 border-red-700/30 focus:border-red-500"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="damage" className="text-orange-400 font-semibold text-xs uppercase tracking-wider">Damage</Label>
                        <Input
                            id="damage"
                            type="number"
                            min="1"
                            value={formData.damage}
                            onChange={(e) => setFormData({ ...formData, damage: parseInt(e.target.value) })}
                            className="text-white bg-zinc-900/70 border-orange-700/30 focus:border-orange-500"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="defense" className="text-blue-400 font-semibold text-xs uppercase tracking-wider">Defense</Label>
                        <Input
                            id="defense"
                            type="number"
                            min="0"
                            value={formData.defense}
                            onChange={(e) => setFormData({ ...formData, defense: parseInt(e.target.value) })}
                            className="text-white bg-zinc-900/70 border-blue-700/30 focus:border-blue-500"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label htmlFor="score" className="text-yellow-400 font-semibold text-xs uppercase tracking-wider">Score</Label>
                        <Input
                            id="score"
                            type="number"
                            min="0"
                            value={formData.score}
                            onChange={(e) => setFormData({ ...formData, score: parseInt(e.target.value) })}
                            className="text-white bg-zinc-900/70 border-yellow-700/30 focus:border-yellow-500"
                        />
                    </div>
                </div>

                {/* Abilities */}
                <div className="space-y-3 p-4 bg-gradient-to-br from-orange-950/20 to-zinc-950/20 rounded-lg border border-orange-800/30">
                    <div className="flex items-center gap-2">
                        <Flame className="w-5 h-5 text-orange-400" />
                        <Label className="text-orange-400 font-bold uppercase tracking-wider">Abilities</Label>
                    </div>
                    <div className="flex gap-2">
                        <Input
                            value={newAbility}
                            onChange={(e) => setNewAbility(e.target.value)}
                            onKeyPress={(e) => {
                                if (e.key === 'Enter') {
                                    e.preventDefault();
                                    addItem(newAbility, abilities, setAbilities);
                                    setNewAbility('');
                                }
                            }}
                            className="text-white bg-zinc-900/50 border-zinc-700/50 focus:border-orange-500"
                            placeholder="Type and press Enter"
                        />
                        <Button
                            type="button"
                            onClick={() => {
                                addItem(newAbility, abilities, setAbilities);
                                setNewAbility('');
                            }}
                            className="text-white bg-gradient-to-r from-orange-600 to-red-600 hover:from-orange-700 hover:to-red-700 shadow-lg shadow-orange-600/30"
                        >
                            <Plus className="w-4 h-4" />
                        </Button>
                    </div>
                    <div className="flex flex-wrap gap-2">
                        {commonAbilities.map(ability => (
                            <Badge
                                key={ability}
                                variant="outline"
                                className="text-white cursor-pointer hover:bg-orange-900/30 hover:border-orange-500/50 border-zinc-700/50 transition-all duration-300 hover:scale-105"
                                onClick={() => addItem(ability, abilities, setAbilities)}
                            >
                                {ability}
                            </Badge>
                        ))}
                    </div>
                    <div className="flex flex-wrap gap-2 mt-2">
                        {abilities.map((ability, i) => (
                            <Badge key={i} className="bg-gradient-to-r from-orange-600 to-red-600 text-white shadow-lg">
                                {ability}
                                <X
                                    className="w-3 h-3 ml-1 cursor-pointer hover:text-red-200"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        removeItem(i, abilities, setAbilities);
                                    }}
                                />
                            </Badge>
                        ))}
                    </div>
                </div>

                {/* Resistances */}
                <div className="space-y-3 p-4 bg-gradient-to-br from-blue-950/20 to-zinc-950/20 rounded-lg border border-blue-800/30">
                    <div className="flex items-center gap-2">
                        <Shield className="w-5 h-5 text-blue-400" />
                        <Label className="text-white text-blue-400 font-bold uppercase tracking-wider">Resistances</Label>
                    </div>
                    <div className="flex gap-2">
                        <Input
                            value={newResistance}
                            onChange={(e) => setNewResistance(e.target.value)}
                            onKeyPress={(e) => {
                                if (e.key === 'Enter') {
                                    e.preventDefault();
                                    addItem(newResistance, resistances, setResistances);
                                    setNewResistance('');
                                }
                            }}
                            className="text-white bg-zinc-900/50 border-zinc-700/50 focus:border-blue-500"
                            placeholder="Type and press Enter"
                        />
                        <Button
                            type="button"
                            onClick={() => {
                                addItem(newResistance, resistances, setResistances);
                                setNewResistance('');
                            }}
                            className="text-white bg-gradient-to-r from-blue-600 to-cyan-600 hover:from-blue-700 hover:to-cyan-700 shadow-lg shadow-blue-600/30"
                        >
                            <Plus className="w-4 h-4" />
                        </Button>
                    </div>
                    <div className="flex flex-wrap gap-2">
                        {commonResistances.map(resistance => (
                            <Badge
                                key={resistance}
                                variant="outline"
                                className="text-white cursor-pointer hover:bg-blue-900/30 hover:border-blue-500/50 border-zinc-700/50 transition-all duration-300 hover:scale-105"
                                onClick={() => addItem(resistance, resistances, setResistances)}
                            >
                                {resistance}
                            </Badge>
                        ))}
                    </div>
                    <div className="flex flex-wrap gap-2 mt-2">
                        {resistances.map((resistance, i) => (
                            <Badge key={i} className="bg-gradient-to-r from-blue-600 to-cyan-600 text-white shadow-lg">
                                {resistance}
                                <X
                                    className="text-white w-3 h-3 ml-1 cursor-pointer hover:text-blue-200"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        removeItem(i, resistances, setResistances);
                                    }}
                                />
                            </Badge>
                        ))}
                    </div>
                </div>

                {/* Status Effects */}
                <div className="text-white space-y-3 p-4 bg-gradient-to-br from-purple-950/20 to-zinc-950/20 rounded-lg border border-purple-800/30">
                    <div className="flex items-center gap-2">
                        <Sparkles className="w-5 h-5 text-purple-400" />
                        <Label className="text-white text-purple-400 font-bold uppercase tracking-wider">Status Effects</Label>
                    </div>
                    <div className="flex gap-2">
                        <Input
                            value={newStatusEffect}
                            onChange={(e) => setNewStatusEffect(e.target.value)}
                            onKeyPress={(e) => {
                                if (e.key === 'Enter') {
                                    e.preventDefault();
                                    addItem(newStatusEffect, statusEffects, setStatusEffects);
                                    setNewStatusEffect('');
                                }
                            }}
                            className="text-white bg-zinc-900/50 border-zinc-700/50 focus:border-purple-500"
                            placeholder="Type and press Enter"
                        />
                        <Button
                            type="button"
                            onClick={() => {
                                addItem(newStatusEffect, statusEffects, setStatusEffects);
                                setNewStatusEffect('');
                            }}
                            className="text-white bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 shadow-lg shadow-purple-600/30"
                        >
                            <Plus className="w-4 h-4" />
                        </Button>
                    </div>
                    <div className="flex flex-wrap gap-2">
                        {commonStatusEffects.map(effect => (
                            <Badge
                                key={effect}
                                variant="outline"
                                className="text-white cursor-pointer hover:bg-purple-900/30 hover:border-purple-500/50 border-zinc-700/50 transition-all duration-300 hover:scale-105"
                                onClick={() => addItem(effect, statusEffects, setStatusEffects)}
                            >
                                {effect}
                            </Badge>
                        ))}
                    </div>
                    <div className="flex flex-wrap gap-2 mt-2">
                        {statusEffects.map((effect, i) => (
                            <Badge key={i} className="bg-gradient-to-r from-purple-600 to-pink-600 text-white shadow-lg">
                                {effect}
                                <X
                                    className="w-3 h-3 ml-1 cursor-pointer hover:text-purple-200"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        removeItem(i, statusEffects, setStatusEffects);
                                    }}
                                />

                            </Badge>
                        ))}
                    </div>
                </div>

                {/* Submit Buttons */}
                <div className="flex gap-4 pt-4">
                    <Button
                        onClick={handleSubmit}
                        disabled={loading}
                        className="text-white flex-1 bg-gradient-to-r from-green-600 to-emerald-600 hover:from-green-700 hover:to-emerald-700 text-white font-bold py-4 text-lg shadow-xl shadow-green-600/30 hover:shadow-green-600/50 transition-all duration-300 hover:scale-[1.02] active:scale-[0.98]"
                    >
                        {loading ? (
                            <>
                                <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                                Forging...
                            </>
                        ) : (
                            <>
                                <Plus className="mr-2 h-5 w-5" />
                                Forge Enemy
                            </>
                        )}
                    </Button>

                    <Button
                        onClick={resetForm}
                        variant="outline"
                        className="border-zinc-700/50 hover:bg-zinc-800/50 hover:border-zinc-600 transition-all duration-300 px-8"
                    >
                        Reset
                    </Button>
                </div>
            </CardContent>
        </Card>
    );
}
