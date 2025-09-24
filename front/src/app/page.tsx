'use client';
import React, { useState, useEffect } from 'react';
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
  Sparkles,
  Loader2,
  Info,
  Zap,
  Target,
  Skull,
  Shield,
  Gamepad2
} from 'lucide-react';
import GameContextForm from '@/components/EnemyGenerator/GameContextForm';
import EnemyCard from '@/components/EnemyGenerator/EnemyCard';
import { GameContext, Enemy, BackwardQuery } from '@/types/enemy';
import { generateEnemyForward, generateEnemyBackward, mockGenerateEnemy } from '@/lib/api';

export default function HomePage() {
  const [activeTab, setActiveTab] = useState('forward');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [generatedEnemy, setGeneratedEnemy] = useState<Enemy | null>(null);
  const [targetEnemyName, setTargetEnemyName] = useState('Iron Lord');
  const [useMockData, setUseMockData] = useState(true); // Za development bez backend-a

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
      setError('Greška pri generisanju neprijatelja. Pokušajte ponovo.');
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
        // Mock za backward chaining
        enemy = await mockGenerateEnemy();
        enemy.name = targetEnemyName; // Override sa target imenom
      } else {
        const query: BackwardQuery = {
          targetEnemy: targetEnemyName,
          context: gameContext
        };
        enemy = await generateEnemyBackward(query);
      }
      setGeneratedEnemy(enemy);
    } catch (err) {
      setError('Neprijatelj nije pronađen ili uslovi nisu ispunjeni.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const testPresets = [
    {
      name: 'Močvara - Noć',
      icon: '🌿',
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
      name: 'Zamak - Boss Fight',
      icon: '🏰',
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
      name: 'Planina - Vetar',
      icon: '⛰️',
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
    <div className="min-h-screen bg-gradient-to-br from-black via-zinc-900 to-black">
      {/* Background decorations */}
      <div className="fixed inset-0 overflow-hidden pointer-events-none">
        <div className="absolute top-20 left-20 w-96 h-96 bg-orange-900 rounded-full opacity-5 blur-3xl" />
        <div className="absolute bottom-20 right-20 w-96 h-96 bg-red-900 rounded-full opacity-5 blur-3xl" />
      </div>

      <div className="relative z-10 container mx-auto p-4 md:p-8">
        {/* Header */}
        <div className="text-center mb-8 md:mb-12">
          <div className="flex justify-center mb-4">
            <div className="p-4 bg-gradient-to-br from-orange-900 to-red-900 rounded-full shadow-2xl pulse-glow">
              <Swords className="w-12 h-12 text-orange-400" />
            </div>
          </div>
          <h1 className="text-4xl md:text-6xl font-bold mb-4 bg-gradient-to-r from-orange-400 to-red-500 text-transparent bg-clip-text">
            Soulslike Enemy Generator
          </h1>
          <p className="text-lg md:text-xl text-zinc-400">
            Sistem za pametno generisanje protivnika baziran na znanju
          </p>
        </div>

        {/* Mock Data Alert */}
        {useMockData && (
          <Alert className="mb-6 bg-zinc-900 border-yellow-900">
            <Info className="h-4 w-4 text-yellow-500" />
            <AlertDescription className="text-zinc-300">
              <strong className="text-yellow-500">Development Mode:</strong> Koriste se mock podaci.
              Za pravu integraciju sa backend-om, podesite <code className="text-orange-400">useMockData = false</code> i pokrenite Spring Boot server.
            </AlertDescription>
          </Alert>
        )}

        {/* Main Content */}
        <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
          {/* Left Panel - Configuration */}
          <div className="lg:col-span-2 space-y-6">
            <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
              <TabsList className="grid w-full grid-cols-2 bg-zinc-900 border border-zinc-800">
                <TabsTrigger
                  value="forward"
                  className="data-[state=active]:bg-orange-900 data-[state=active]:text-orange-200"
                >
                  <ArrowRight className="w-4 h-4 mr-2" />
                  Forward Chaining
                </TabsTrigger>
                <TabsTrigger
                  value="backward"
                  className="data-[state=active]:bg-orange-900 data-[state=active]:text-orange-200"
                >
                  <ArrowLeft className="w-4 h-4 mr-2" />
                  Backward Chaining
                </TabsTrigger>
              </TabsList>

              <TabsContent value="forward" className="space-y-6">
                <Alert className="bg-zinc-900 border-zinc-800">
                  <Zap className="h-4 w-4 text-yellow-500" />
                  <AlertDescription className="text-zinc-300">
                    <strong className="text-yellow-500">Forward Chaining:</strong> Generiše neprijatelja na osnovu konteksta igre koristeći pravila i template-e.
                  </AlertDescription>
                </Alert>

                <GameContextForm context={gameContext} onChange={setGameContext} />

                <Button
                  onClick={handleForwardGeneration}
                  disabled={loading}
                  className="w-full bg-gradient-to-r from-orange-600 to-red-600 hover:from-orange-700 hover:to-red-700 text-white font-bold py-3 text-lg"
                >
                  {loading ? (
                    <>
                      <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                      Generisanje u toku...
                    </>
                  ) : (
                    <>
                      <Sparkles className="mr-2 h-5 w-5" />
                      Generiši Neprijatelja (Forward)
                    </>
                  )}
                </Button>
              </TabsContent>

              <TabsContent value="backward" className="space-y-6">
                <Alert className="bg-zinc-900 border-zinc-800">
                  <Target className="h-4 w-4 text-purple-500" />
                  <AlertDescription className="text-zinc-300">
                    <strong className="text-purple-500">Backward Chaining:</strong> Pokušava da generiše specifičnog neprijatelja proveravajući da li su uslovi ispunjeni.
                  </AlertDescription>
                </Alert>

                <Card className="souls-border bg-gradient-to-br from-zinc-900 to-black">
                  <CardHeader>
                    <CardTitle className="text-xl text-orange-400">Ciljani Neprijatelj</CardTitle>
                    <CardDescription className="text-zinc-400">
                      Unesite ime specifičnog neprijatelja kojeg želite da genišete
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label className="text-zinc-300">Ime neprijatelja</Label>
                      <Input
                        type="text"
                        value={targetEnemyName}
                        onChange={(e) => setTargetEnemyName(e.target.value)}
                        placeholder="npr. Iron Lord, Swamp Witch..."
                        className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700"
                      />
                    </div>

                    <div className="space-y-2">
                      <Label className="text-zinc-400 text-sm">Predloženi neprijatelji:</Label>
                      <div className="flex flex-wrap gap-2">
                        {['Iron Lord', 'Swamp Witch', 'Mountain Harpy', 'Court Wizard'].map((name) => (
                          <Badge
                            key={name}
                            variant="outline"
                            className="cursor-pointer hover:bg-zinc-800 border-zinc-700"
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
                  className="w-full bg-gradient-to-r from-purple-600 to-pink-600 hover:from-purple-700 hover:to-pink-700 text-white font-bold py-3 text-lg"
                >
                  {loading ? (
                    <>
                      <Loader2 className="mr-2 h-5 w-5 animate-spin" />
                      Traženje neprijatelja...
                    </>
                  ) : (
                    <>
                      <Target className="mr-2 h-5 w-5" />
                      Pronađi Neprijatelja (Backward)
                    </>
                  )}
                </Button>
              </TabsContent>
            </Tabs>

            {/* Test Presets */}
            <Card className="souls-border bg-gradient-to-br from-zinc-900 to-black">
              <CardHeader>
                <CardTitle className="text-lg text-orange-400 flex items-center gap-2">
                  <Gamepad2 className="w-5 h-5" />
                  Brzi Test Preseti
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                  {testPresets.map((preset) => (
                    <Button
                      key={preset.name}
                      variant="outline"
                      className="justify-start hover:bg-zinc-800 border-zinc-700"
                      onClick={() => setGameContext(preset.context)}
                    >
                      <span className="mr-2">{preset.icon}</span>
                      {preset.name}
                    </Button>
                  ))}
                </div>
              </CardContent>
            </Card>
          </div>

          {/* Right Panel - Results */}
          <div className="space-y-6">
            {error && (
              <Alert variant="destructive" className="bg-red-950 border-red-900">
                <Skull className="h-4 w-4" />
                <AlertDescription>{error}</AlertDescription>
              </Alert>
            )}

            <div className="sticky top-8">
              <EnemyCard enemy={generatedEnemy} loading={loading} />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
