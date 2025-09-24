// src/components/EnemyGenerator/GameContextForm.tsx
import React from 'react';
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle
} from '@/components/ui/card';
import { Label } from '@/components/ui/label';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Slider } from '@/components/ui/slider';
import { RadioGroup, RadioGroupItem } from '@/components/ui/radio-group';
import {
    REGIONS,
    DIFFICULTIES,
    WEATHER_CONDITIONS,
    TIME_OF_DAY,
    PLAYER_CLASSES,
    WEAPON_TYPES,
    GameContext
} from '@/types/enemy';
import { User, MapPin, Cloud, Clock, Sword, Shield } from 'lucide-react';

interface GameContextFormProps {
    context: GameContext;
    onChange: (context: GameContext) => void;
}

const GameContextForm: React.FC<GameContextFormProps> = ({ context, onChange }) => {
    const handleChange = (field: keyof GameContext | string, value: any) => {
        if (field.includes('.')) {
            const [parent, child] = field.split('.');
            onChange({
                ...context,
                [parent]: {
                    ...context[parent as keyof GameContext],
                    [child]: value
                }
            });
        } else {
            onChange({
                ...context,
                [field]: value
            });
        }
    };

    return (
        <Card className="w-full souls-border bg-gradient-to-br from-zinc-900 to-black">
            <CardHeader className="pb-4">
                <CardTitle className="text-2xl souls-glow text-orange-400">
                    Konfiguriši Kontekst Igre
                </CardTitle>
                <CardDescription className="text-zinc-400">
                    Podesi parametre sveta i igrača za generisanje neprijatelja
                </CardDescription>
            </CardHeader>
            <CardContent className="space-y-6">
                {/* Region & Difficulty */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <MapPin className="w-4 h-4 text-orange-400" />
                            Region
                        </Label>
                        <Select
                            value={context.region}
                            onValueChange={(value) => handleChange('region', value)}
                        >
                            <SelectTrigger className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 transition-colors">
                                <SelectValue placeholder="Izaberi region" />
                            </SelectTrigger>
                            <SelectContent className="bg-zinc-800 border-zinc-700">
                                {REGIONS.map((region) => (
                                    <SelectItem key={region.value} value={region.value}>
                                        <span className="flex items-center gap-2">
                                            <span>{region.icon}</span>
                                            <span>{region.label}</span>
                                        </span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <Shield className="w-4 h-4 text-orange-400" />
                            Težina
                        </Label>
                        <Select
                            value={context.difficulty}
                            onValueChange={(value) => handleChange('difficulty', value)}
                        >
                            <SelectTrigger className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 transition-colors">
                                <SelectValue placeholder="Izaberi težinu" />
                            </SelectTrigger>
                            <SelectContent className="bg-zinc-800 border-zinc-700">
                                {DIFFICULTIES.map((diff) => (
                                    <SelectItem key={diff.value} value={diff.value}>
                                        <span className={diff.color}>{diff.label}</span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                {/* Weather & Time */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <Cloud className="w-4 h-4 text-orange-400" />
                            Vremenski uslovi
                        </Label>
                        <Select
                            value={context.weather}
                            onValueChange={(value) => handleChange('weather', value)}
                        >
                            <SelectTrigger className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 transition-colors">
                                <SelectValue placeholder="Izaberi vreme" />
                            </SelectTrigger>
                            <SelectContent className="bg-zinc-800 border-zinc-700">
                                {WEATHER_CONDITIONS.map((weather) => (
                                    <SelectItem key={weather.value} value={weather.value}>
                                        <span className="flex items-center gap-2">
                                            <span>{weather.icon}</span>
                                            <span>{weather.label}</span>
                                        </span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <Clock className="w-4 h-4 text-orange-400" />
                            Doba dana
                        </Label>
                        <Select
                            value={context.timeOfDay}
                            onValueChange={(value) => handleChange('timeOfDay', value)}
                        >
                            <SelectTrigger className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 transition-colors">
                                <SelectValue placeholder="Izaberi doba dana" />
                            </SelectTrigger>
                            <SelectContent className="bg-zinc-800 border-zinc-700">
                                {TIME_OF_DAY.map((time) => (
                                    <SelectItem key={time.value} value={time.value}>
                                        <span className="flex items-center gap-2">
                                            <span>{time.icon}</span>
                                            <span>{time.label}</span>
                                        </span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                </div>

                {/* Player Settings */}
                <div className="space-y-4 p-4 bg-zinc-900 rounded-lg border border-zinc-800">
                    <h3 className="text-lg font-semibold text-orange-400 flex items-center gap-2">
                        <User className="w-5 h-5" />
                        Podešavanja Igrača
                    </h3>

                    <div className="space-y-2">
                        <Label className="text-zinc-300">Ime igrača</Label>
                        <Input
                            type="text"
                            value={context.player.name}
                            onChange={(e) => handleChange('player.name', e.target.value)}
                            className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 transition-colors"
                            placeholder="Unesi ime igrača"
                        />
                    </div>

                    <div className="space-y-2">
                        <Label className="text-zinc-300">
                            Nivo igrača: <span className="text-orange-400 font-bold">{context.player.level}</span>
                        </Label>
                        <Slider
                            value={[context.player.level]}
                            onValueChange={(value) => handleChange('player.level', value[0])}
                            max={100}
                            min={1}
                            step={1}
                            className="py-4"
                        />
                        <div className="flex justify-between text-xs text-zinc-500">
                            <span>1</span>
                            <span>25</span>
                            <span>50</span>
                            <span>75</span>
                            <span>100</span>
                        </div>
                    </div>

                    <div className="space-y-2">
                        <Label className="text-zinc-300">Klasa igrača</Label>
                        <RadioGroup
                            value={context.player.playerClass}
                            onValueChange={(value) => handleChange('player.playerClass', value)}
                        >
                            {PLAYER_CLASSES.map((playerClass) => (
                                <div key={playerClass.value} className="flex items-start space-x-3 p-3 rounded-lg hover:bg-zinc-800 transition-colors">
                                    <RadioGroupItem value={playerClass.value} id={playerClass.value} className="mt-1" />
                                    <Label htmlFor={playerClass.value} className="cursor-pointer flex-1">
                                        <div className="flex items-center gap-2">
                                            <span>{playerClass.icon}</span>
                                            <span className="font-semibold text-orange-400">{playerClass.label}</span>
                                        </div>
                                        <p className="text-sm text-zinc-400 mt-1">{playerClass.description}</p>
                                    </Label>
                                </div>
                            ))}
                        </RadioGroup>
                    </div>

                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <Sword className="w-4 h-4 text-orange-400" />
                            Tip oružja
                        </Label>
                        <Select
                            value={context.player.weaponType}
                            onValueChange={(value) => handleChange('player.weaponType', value)}
                        >
                            <SelectTrigger className="bg-zinc-800 border-zinc-700 hover:bg-zinc-700 transition-colors">
                                <SelectValue placeholder="Izaberi oružje" />
                            </SelectTrigger>
                            <SelectContent className="bg-zinc-800 border-zinc-700">
                                {WEAPON_TYPES[context.player.playerClass]?.map((weapon) => (
                                    <SelectItem key={weapon} value={weapon}>
                                        <span className="capitalize">{weapon}</span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>
                </div>
            </CardContent>
        </Card>
    );
};

export default GameContextForm;
