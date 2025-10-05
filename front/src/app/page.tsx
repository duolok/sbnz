'use client';
import React, { useState } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Alert, AlertDescription } from '@/components/ui/alert';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Badge } from '@/components/ui/badge';
import {
  Swords,
  ArrowRight,
  ArrowLeft,
  Loader2,
  Info,
  Plus,
  Flame,
  Skull,
  Zap
} from 'lucide-react';
import GameContextForm from '@/components/EnemyGenerator/GameContextForm';
import EnemyCard from '@/components/EnemyGenerator/EnemyCard';
import CreateEnemyForm from '@/components/EnemyGenerator/CreateEnemyForm';
import { GameContext, Enemy, BackwardQuery } from '@/types/enemy';
import { generateEnemyForward, generateEnemyBackward, mockGenerateEnemy } from '@/lib/api';

export default function HomePage() {
  const [activeTab, setActiveTab] = useState('forward');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [generatedEnemy, setGeneratedEnemy] = useState<Enemy | null>(null);
  const [targetEnemyName, setTargetEnemyName] = useState('Iron Lord');
  const [useMockData, setUseMockData] = useState(false);

  const [gameContext, setGameContext] = useState<GameContext>({
    region: 'swamp',
    difficulty: 'medium-hard',
    weather: 'fog',
    timeOfDay: 'night',
    player: {
      name: 'BossHunter',
      level: 28,
      playerClass: 'DEX',
      weaponType: 'katana'
    }
  });

  const handleForwardGeneration = async () => {
    setLoading(true);
    setError(null);

    try {
      let enemy: Enemy;
      if (useMockData) {
        enemy = await mockGenerateEnemy();
      } else {
        enemy = await generateEnemyForward(gameContext);
      }
      setGeneratedEnemy(enemy);
    } catch (err) {
      setError('Error generating enemy. Try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleBackwardGeneration = async () => {
    setLoading(true);
    setError(null);

    try {
      let enemy: Enemy;
      if (useMockData) {
        enemy = await mockGenerateEnemy();
        enemy.name = targetEnemyName;
      } else {
        const query: BackwardQuery = {
          targetEnemy: targetEnemyName,
          context: gameContext
        };
        enemy = await generateEnemyBackward(query);
      }
      setGeneratedEnemy(enemy);
    } catch (err) {
      setError('Enemy not found or conditions not met.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEnemyCreated = () => {
    setActiveTab('forward');
  };

  const testPresets = [
    {
      name: 'Swamp - Night',
      icon: '🌿',
      gradient: 'from-green-900/20 to-emerald-900/20',
      context: {
        region: 'swamp',
        difficulty: 'medium-hard',
        weather: 'fog',
        timeOfDay: 'night',
        player: {
          name: 'SwampExplorer',
          level: 25,
          playerClass: 'DEX' as const,
          weaponType: 'katana'
        }
      }
    },
    {
      name: 'Castle - Boss',
      icon: '🏰',
      gradient: 'from-slate-900/20 to-zinc-900/20',
      context: {
        region: 'castle',
        difficulty: 'hard',
        weather: 'clear',
        timeOfDay: 'day',
        player: {
          name: 'KnightSlayer',
          level: 45,
          playerClass: 'STRENGTH' as const,
          weaponType: 'greatsword'
        }
      }
    },
    {
      name: 'Mountain - Wind',
      icon: '⛰️',
      gradient: 'from-blue-900/20 to-cyan-900/20',
      context: {
        region: 'mountain',
        difficulty: 'medium',
        weather: 'wind',
        timeOfDay: 'dawn',
        player: {
          name: 'Mountaineer',
          level: 30,
          playerClass: 'MAGE' as const,
          weaponType: 'staff'
        }
      }
    }
  ];

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-950 via-zinc-950 to-black relative overflow-hidden">
      {/* Animated Background */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-20 w-96 h-96 bg-gradient-to-r from-orange-600/10 to-red-600/10 rounded-full blur-3xl animate-pulse" />
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-gradient-to-r from-purple-600/10 to-pink-600/10 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '1s' }} />
        <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[600px] bg-gradient-to-r from-amber-600/5 to-orange-600/5 rounded-full blur-3xl animate-pulse" style={{ animationDelay: '2s' }} />
      </div>

      {/* Floating Particles */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        {[...Array(20)].map((_, i) => (
          <div
            key={i}
            className="absolute w-1 h-1 bg-orange-500/20 rounded-full animate-float"
            style={{
              left: `${Math.random() * 100}%`,
              top: `${Math.random() * 100}%`,
              animationDelay: `${Math.random() * 5}s`,
              animationDuration: `${10 + Math.random() * 10}s`
            }}
          />
        ))}
      </div>

      <div className="relative z-10 container mx-auto p-4 md:p-8">
        {/* Header with Animation */}
        <div className="text-center mb-12">
          <div className="flex justify-center mb-6">
            <div className="relative group">
              <div className="absolute inset-0 bg-gradient-to-r from-orange-500 to-red-600 rounded-full blur-xl opacity-75 group-hover:opacity-100 transition-opacity duration-500" />
              <div className="relative p-6 bg-gradient-to-br from-orange-900/90 to-red-900/90 rounded-full shadow-2xl backdrop-blur-sm border border-orange-500/20">
                <Swords className="w-16 h-16 text-orange-300" />
              </div>
            </div>
          </div>

          <h1 className="text-5xl md:text-7xl font-bold mb-4 bg-gradient-to-r from-orange-300 via-orange-400 to-red-500 text-transparent bg-clip-text">
            Soulslike Enemy Generator
          </h1>
        </div>
        {/* Main Content Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Panel - Configuration */}
          <div className="lg:col-span-2 space-y-6">
            <div className="animate-fade-in-up" style={{ animationDelay: '0.4s' }}>
              <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
                <TabsList className="grid w-full grid-cols-3 bg-gradient-to-r from-zinc-900/90 to-zinc-950/90 border border-zinc-800/50 backdrop-blur-sm p-1 rounded-xl">
                  <TabsTrigger
                    value="forward"
                    className="text-white data-[state=active]:bg-gradient-to-r data-[state=active]:from-orange-600 data-[state=active]:to-red-600 data-[state=active]:text-white data-[state=active]:shadow-lg data-[state=active]:shadow-orange-600/50 rounded-lg transition-all duration-300"
                  >
                    <ArrowRight className="w-4 h-4 mr-2" />
                    <span className="font-semibold">Forward</span>
                  </TabsTrigger>
                  <TabsTrigger
                    value="backward"
                    className="text-white data-[state=active]:bg-gradient-to-r data-[state=active]:from-purple-600 data-[state=active]:to-pink-600 data-[state=active]:text-white data-[state=active]:shadow-lg data-[state=active]:shadow-purple-600/50 rounded-lg transition-all duration-300"
                  >
                    <ArrowLeft className="w-4 h-4 mr-2" />
                    <span className="font-semibold">Backward</span>
                  </TabsTrigger>
                  <TabsTrigger
                    value="create"
                    className="text-white data-[state=active]:bg-gradient-to-r data-[state=active]:from-green-600 data-[state=active]:to-emerald-600 data-[state=active]:text-white data-[state=active]:shadow-lg data-[state=active]:shadow-green-600/50 rounded-lg transition-all duration-300"
                  >
                    <Plus className="w-4 h-4 mr-2" />
                    <span className="font-semibold">Create</span>
                  </TabsTrigger>
                </TabsList>

                <TabsContent value="forward" className="space-y-6 mt-6">
                  <GameContextForm context={gameContext} onChange={setGameContext} />

                  <Button
                    onClick={handleForwardGeneration}
                    disabled={loading}
                    className="text-white w-full bg-gradient-to-r from-orange-600 to-red-600 hover:from-orange-700 hover:to-red-700 text-white font-bold py-4 text-lg shadow-xl shadow-orange-600/30 hover:shadow-orange-600/50 transition-all duration-300 hover:scale-[1.02] active:scale-[0.98] border border-orange-500/20"
                  >
                    {loading ? (
                      <>
                        <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                        Forging Enemy...
                      </>
                    ) : (
                      <>
                        <Zap className="mr-2 h-5 w-5" />
                        Generate Enemy
                      </>
                    )}
                  </Button>
                </TabsContent>

                <TabsContent value="backward" className="space-y-6 mt-6">
                  <Card className="bg-gradient-to-br from-purple-950/30 to-zinc-950/30 border border-purple-700/30 backdrop-blur-sm shadow-xl shadow-purple-900/20 overflow-hidden group hover:border-purple-600/50 transition-all duration-300">
                    <div className="absolute inset-0 bg-gradient-to-br from-purple-600/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
                    <CardHeader className="relative">
                      <CardTitle className="text-2xl text-transparent bg-clip-text bg-gradient-to-r from-purple-300 to-pink-300 flex items-center gap-2">
                        <Skull className="w-6 h-6 text-purple-400" />
                        Target Enemy
                      </CardTitle>
                      <CardDescription className="text-zinc-400">
                        Enter the name of an enemy to seek in the darkness
                      </CardDescription>
                    </CardHeader>
                    <CardContent className="space-y-4 relative">
                      <div className="space-y-2">
                        <Label className="text-zinc-300 font-semibold">Enemy Name</Label>
                        <Input
                          type="text"
                          value={targetEnemyName}
                          onChange={(e) => setTargetEnemyName(e.target.value)}
                          placeholder="e.g. Iron Lord, Swamp Witch..."
                          className="text-white bg-zinc-900/50 border-zinc-700/50 hover:border-purple-600/50 focus:border-purple-500 transition-colors duration-300 backdrop-blur-sm"
                        />
                      </div>

                      <div className="space-y-2">
                        <Label className="text-zinc-400 text-sm font-semibold">Suggested Enemies:</Label>
                        <div className="flex flex-wrap gap-2">
                          {['Iron Lord', 'Swamp Witch', 'Mountain Harpy', 'Court Wizard', 'AUTO_COUNTER'].map((name) => (
                            <Badge
                              key={name}
                              variant="outline"
                              className="text-white cursor-pointer hover:bg-purple-900/30 hover:border-purple-500/50 border-zinc-700/50 transition-all duration-300 hover:scale-105 active:scale-95"
                              onClick={() => setTargetEnemyName(name)}
                            >
                              {name}
                            </Badge>
                          ))}
                        </div>
                      </div>
                    </CardContent>
                  </Card>

                  <GameContextForm context={gameContext} onChange={setGameContext} />

                  <Button
                    onClick={handleBackwardGeneration}
                    disabled={loading}
                    className="w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white font-bold py-4 text-lg shadow-xl shadow-purple-600/30 hover:shadow-purple-600/50 transition-all duration-300 hover:scale-[1.02] active:scale-[0.98] border border-purple-500/20"
                  >
                    {loading ? (
                      <>
                        <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                        Seeking Enemy...
                      </>
                    ) : (
                      <>
                        <Skull className="mr-2 h-5 w-5" />
                        Find Enemy
                      </>
                    )}
                  </Button>
                </TabsContent>

                <TabsContent value="create" className="space-y-6 mt-6">
                  <CreateEnemyForm onEnemyCreated={handleEnemyCreated} />
                </TabsContent>
              </Tabs>
            </div>

            {/* Test Presets */}
            <Card className="bg-gradient-to-br from-zinc-900/50 to-zinc-950/50 border border-zinc-800/50 backdrop-blur-sm shadow-xl overflow-hidden group hover:border-zinc-700/50 transition-all duration-300 animate-fade-in-up" style={{ animationDelay: '0.5s' }}>
              <div className="absolute inset-0 bg-gradient-to-br from-orange-600/5 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500" />
              <CardHeader className="relative">
                <CardTitle className="text-lg text-transparent bg-clip-text bg-gradient-to-r from-orange-300 to-red-400 flex items-center gap-2">
                  <Zap className="w-5 h-5 text-orange-400" />
                  Quick Start Presets
                </CardTitle>
              </CardHeader>
              <CardContent className="relative">
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                  {testPresets.map((preset) => (
                    <Button
                      key={preset.name}
                      variant="outline"
                      className={`text-white justify-start bg-gradient-to-br ${preset.gradient} border-zinc-700/50 hover:border-zinc-600 hover:scale-105 transition-all duration-300 active:scale-95 backdrop-blur-sm`}
                      onClick={() => {
                        setGameContext(preset.context);
                        setActiveTab('forward');
                      }}
                    >
                      <span className="mr-2 text-xl">{preset.icon}</span>
                      <span className="font-semibold">{preset.name}</span>
                    </Button>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Right Panel - Results */}
          <div className="space-y-6 animate-fade-in-up" style={{ animationDelay: '0.6s' }}>
            {error && (
              <Alert variant="destructive" className="bg-gradient-to-r from-red-950/50 to-red-900/50 border-red-800/50 backdrop-blur-sm shadow-xl shadow-red-900/20 animate-shake">
                <Skull className="h-4 w-4 text-red-400" />
                <AlertDescription className="text-red-200">{error}</AlertDescription>
              </Alert>
            )}

            <div className="sticky top-8">
              <EnemyCard enemy={generatedEnemy} loading={loading} />
            </div>
          </div>
        </div>
      </div>

      <style jsx global>{`
        @keyframes gradient-x {
          0%, 100% {
            background-size: 200% 200%;
            background-position: left center;
          }
          50% {
            background-size: 200% 200%;
            background-position: right center;
          }
        }

        @keyframes float {
          0%, 100% {
            transform: translateY(0px) translateX(0px);
            opacity: 0.3;
          }
          25% {
            opacity: 0.6;
          }
          50% {
            transform: translateY(-20px) translateX(10px);
            opacity: 0.8;
          }
          75% {
            opacity: 0.6;
          }
        }

        @keyframes fade-in-down {
          0% {
            opacity: 0;
            transform: translateY(-20px);
          }
          100% {
            opacity: 1;
            transform: translateY(0);
          }
        }

        @keyframes fade-in-up {
          0% {
            opacity: 0;
            transform: translateY(20px);
          }
          100% {
            opacity: 1;
            transform: translateY(0);
          }
        }

        @keyframes fade-in {
          0% {
            opacity: 0;
          }
          100% {
            opacity: 1;
          }
        }

        @keyframes shake {
          0%, 100% { transform: translateX(0); }
          10%, 30%, 50%, 70%, 90% { transform: translateX(-5px); }
          20%, 40%, 60%, 80% { transform: translateX(5px); }
        }

        .animate-gradient-x {
          animation: gradient-x 5s ease infinite;
        }

        .animate-float {
          animation: float 10s ease-in-out infinite;
        }

        .animate-fade-in-down {
          animation: fade-in-down 0.6s ease-out;
        }

        .animate-fade-in-up {
          animation: fade-in-up 0.6s ease-out;
        }

        .animate-fade-in {
          animation: fade-in 0.8s ease-out;
        }

        .animate-shake {
          animation: shake 0.5s ease-in-out;
        }
      `}</style>
    </div>
  );
}
