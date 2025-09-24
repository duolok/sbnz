// src/components/EnemyGenerator/TestScenarios.tsx
import React from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Badge } from '@/components/ui/badge';
import { GameContext } from '@/types/enemy';
import {
    Zap,
    Shield,
    Skull,
    Target,
    Swords,
    Moon,
    Sun,
    CloudRain,
    Wind
} from 'lucide-react';

interface Scenario {
    id: string;
    name: string;
    description: string;
    icon: React.ReactNode;
    tags: string[];
    context: GameContext;
    expectedEnemy?: string;
}

interface TestScenariosProps {
    onSelectScenario: (context: GameContext) => void;
}

const TestScenarios: React.FC<TestScenariosProps> = ({ onSelectScenario }) => {
    const scenarios: Scenario[] = [
        {
            id: 'boss-hunter',
            name: 'Boss Hunter',
            description: 'Visok nivo igrač spreman za boss borbu u zamku',
            icon: <Skull className="w-5 h-5 text-red-500" />,
            tags: ['Boss', 'Hard', 'Castle'],
            context: {
                region: 'castle',
                difficulty: 'hard',
                weather: 'clear',
                timeOfDay: 'day',
                player: {
                    name: 'BossSlayer',
                    level: 60,
                    playerClass: 'STRENGTH',
                    weaponType: 'greatsword'
                }
            },
            expectedEnemy: 'Iron Lord'
        },
        {
            id: 'swamp-explorer',
            name: 'Swamp Explorer',
            description: 'Srednji nivo istraživač močvare tokom magle',
            icon: <CloudRain className="w-5 h-5 text-green-500" />,
            tags: ['Poison', 'Medium', 'Stealth'],
            context: {
                region: 'swamp',
                difficulty: 'medium',
                weather: 'fog',
                timeOfDay: 'night',
                player: {
                    name: 'SwampRanger',
                    level: 25,
                    playerClass: 'DEX',
                    weaponType: 'bow'
                }
            },
            expectedEnemy: 'Swamp Goblin'
        },
        {
            id: 'mountain-climber',
            name: 'Mountain Climber',
            description: 'Planinarski uspon po vetru sa magijom',
            icon: <Wind className="w-5 h-5 text-blue-500" />,
            tags: ['Flying', 'Wind', 'Magic'],
            context: {
                region: 'mountain',
                difficulty: 'medium-hard',
                weather: 'wind',
                timeOfDay: 'dawn',
                player: {
                    name: 'WindMage',
                    level: 35,
                    playerClass: 'MAGE',
                    weaponType: 'staff'
                }
            },
            expectedEnemy: 'Mountain Harpy'
        },
        {
            id: 'night-stalker',
            name: 'Night Stalker',
            description: 'Noćni lovac u močvari sa katana build-om',
            icon: <Moon className="w-5 h-5 text-purple-500" />,
            tags: ['Night', 'Stealth', 'Poison'],
            context: {
                region: 'swamp',
                difficulty: 'hard',
                weather: 'rain',
                timeOfDay: 'night',
                player: {
                    name: 'NightBlade',
                    level: 40,
                    playerClass: 'DEX',
                    weaponType: 'katana'
                }
            },
            expectedEnemy: 'Swamp Witch'
        },
        {
            id: 'beginner-knight',
            name: 'Beginner Knight',
            description: 'Početnik u zamku sa osnovnom opremom',
            icon: <Shield className="w-5 h-5 text-gray-500" />,
            tags: ['Easy', 'Beginner', 'Castle'],
            context: {
                region: 'castle',
                difficulty: 'easy',
                weather: 'clear',
                timeOfDay: 'day',
                player: {
                    name: 'Novice',
                    level: 5,
                    playerClass: 'STRENGTH',
                    weaponType: 'mace'
                }
            }
        },
        {
            id: 'elite-mage',
            name: 'Elite Mage Duel',
            description: 'Mage vs Mage borba u zamku sa otpornošću na magiju',
            icon: <Zap className="w-5 h-5 text-yellow-500" />,
            tags: ['Magic', 'Hard', 'Resistance'],
            context: {
                region: 'castle',
                difficulty: 'hard',
                weather: 'clear',
                timeOfDay: 'night',
                player: {
                    name: 'Archmage',
                    level: 50,
                    playerClass: 'MAGE',
                    weaponType: 'catalyst'
                }
            },
            expectedEnemy: 'Court Wizard'
        }
    ];

    const getTagColor = (tag: string): string => {
        const colors: { [key: string]: string } = {
            'Boss': 'bg-red-900 text-red-200',
            'Hard': 'bg-orange-900 text-orange-200',
            'Medium': 'bg-yellow-900 text-yellow-200',
            'Easy': 'bg-green-900 text-green-200',
            'Night': 'bg-purple-900 text-purple-200',
            'Poison': 'bg-green-900 text-green-200',
            'Magic': 'bg-blue-900 text-blue-200',
            'Stealth': 'bg-gray-800 text-gray-300'
        };
        return colors[tag] || 'bg-zinc-800 text-zinc-300';
    };

    return (
        <Card className="souls-border bg-gradient-to-br from-zinc-900 to-black">
            <CardHeader>
                <CardTitle className="text-2xl souls-glow text-orange-400 flex items-center gap-2">
                    <Swords className="w-6 h-6" />
                    Test Scenariji
                </CardTitle>
                <CardDescription className="text-zinc-400">
                    Predefinisani scenariji za testiranje sistema generisanja
                </CardDescription>
            </CardHeader>
            <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    {scenarios.map((scenario) => (
                        <Card
                            key={scenario.id}
                            className="bg-zinc-900 border-zinc-800 hover:border-orange-900 transition-all cursor-pointer hover:shadow-lg"
                            onClick={() => onSelectScenario(scenario.context)}
                        >
                            <CardHeader className="pb-3">
                                <div className="flex items-start justify-between">
                                    <div className="flex items-center gap-2">
                                        {scenario.icon}
                                        <CardTitle className="text-lg text-zinc-200">
                                            {scenario.name}
                                        </CardTitle>
                                    </div>
                                </div>
                                <CardDescription className="text-zinc-400 text-sm mt-1">
                                    {scenario.description}
                                </CardDescription>
                            </CardHeader>
                            <CardContent className="pt-0 space-y-3">
                                <div className="flex flex-wrap gap-1">
                                    {scenario.tags.map((tag) => (
                                        <Badge
                                            key={tag}
                                            variant="secondary"
                                            className={`text-xs ${getTagColor(tag)}`}
                                        >
                                            {tag}
                                        </Badge>
                                    ))}
                                </div>

                                <div className="text-xs text-zinc-500 space-y-1">
                                    <div>
                                        <span className="text-zinc-600">Region:</span>
                                        <span className="ml-1 text-zinc-400 capitalize">{scenario.context.region}</span>
                                    </div>
                                    <div>
                                        <span className="text-zinc-600">Nivo:</span>
                                        <span className="ml-1 text-zinc-400">{scenario.context.player.level}</span>
                                    </div>
                                    <div>
                                        <span className="text-zinc-600">Klasa:</span>
                                        <span className="ml-1 text-zinc-400">{scenario.context.player.playerClass}</span>
                                    </div>
                                    {scenario.expectedEnemy && (
                                        <div>
                                            <span className="text-zinc-600">Očekivan:</span>
                                            <span className="ml-1 text-orange-400">{scenario.expectedEnemy}</span>
                                        </div>
                                    )}
                                </div>

                                <Button
                                    size="sm"
                                    className="w-full bg-zinc-800 hover:bg-zinc-700 text-zinc-300 mt-2"
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        onSelectScenario(scenario.context);
                                    }}
                                >
                                    Učitaj Scenario
                                </Button>
                            </CardContent>
                        </Card>
                    ))}
                </div>
            </CardContent>
        </Card>
    );
};

export default TestScenarios;
