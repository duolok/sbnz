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
import { Slider, SliderTrack, SliderRange, SliderThumb } from '@/components/ui/slider';
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
                    Configure Game Context
                </CardTitle>
                <CardDescription className="text-zinc-400">
                    Tweak world and player parameters to generate enemy
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
                                            <span class="text-white">{region.label}</span>
                                        </span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <Shield className="w-4 h-4 text-orange-400" />
                            Difficulty
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
                            Weather
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
                                            <span class="text-white">{weather.label}</span>
                                        </span>
                                    </SelectItem>
                                ))}
                            </SelectContent>
                        </Select>
                    </div>

                    <div className="space-y-2">
                        <Label className="flex items-center gap-2 text-zinc-300">
                            <Clock className="w-4 h-4 text-orange-400" />
                            Time of day
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
                                            <span class="text-white">{time.label}</span>
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
                        Player Configuration
                    </h3>
<div className="space-y-4">
    <Label className="text-zinc-300 text-lg font-semibold">
        Player Level: <span className="text-orange-400 font-bold ml-2">{context.player.level}</span>
    </Label>
    
    <div className="relative pt-8 pb-4">
        <Slider
            value={[context.player.level]}
            onValueChange={(value) => handleChange('player.level', value[0])}
            max={100}
            min={1}
            step={1}
            className="relative flex items-center select-none touch-none w-full h-6"
        >
        </Slider>
        
        {/* Enhanced level markers with better styling */}
        <div className="absolute -bottom-2 w-full flex justify-between px-2">
            {[1, 25, 50, 75, 100].map((marker) => (
                <div key={marker} className="flex flex-col items-center">
                    {/* Marker line */}
                    <div 
                        className={`w-0.5 h-2 mb-1 transition-all duration-300 ${
                            context.player.level >= marker 
                                ? 'bg-orange-400 h-3' 
                                : 'bg-zinc-500'
                        }`}
                    />
                    {/* Marker label */}
                    <span 
                        className={`text-xs font-medium transition-all duration-300 ${
                            context.player.level >= marker 
                                ? 'text-orange-300 font-bold scale-110' 
                                : 'text-zinc-400'
                        }`}
                    >
                        {marker}
                    </span>
                </div>
            ))}
        </div>
        
    </div>
    
    {/* Additional visual feedback */}
    <div className="flex justify-between items-center text-sm text-zinc-400 mt-6">
        <span className="flex items-center gap-1">
            <div className="w-2 h-2 bg-orange-400 rounded-full" />
            Beginner
        </span>
        <span className="flex items-center gap-1">
            <div className="w-2 h-2 bg-orange-500 rounded-full" />
            Intermediate
        </span>
        <span className="flex items-center gap-1">
            <div className="w-2 h-2 bg-orange-600 rounded-full" />
            Expert
        </span>
    </div>
</div>


                    <div className="space-y-2">
                        <Label className="text-zinc-300">Player class</Label>
                        <RadioGroup
                            value={context.player.playerClass}
                            onValueChange={(value) => handleChange('player.playerClass', value)}
                        >
                            {PLAYER_CLASSES.map((playerClass) => (
                                <div key={playerClass.value} className="flex items-start space-x-3 p-3 rounded-lg hover:bg-zinc-800 transition-colors">
                                    <RadioGroupItem 
                                        value={playerClass.value} 
                                        id={playerClass.value}
                                        className="mt-1 h-5 w-5 text-orange-500 bg-zinc-800 border-zinc-400 focus:ring-2 focus:ring-orange-300 focus:ring-offset-2 focus:ring-offset-zinc-900" 
                                    />


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
                            Weapon Type
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
                                        <span className="capitalize text-white">{weapon}</span>
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
