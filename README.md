# Gain CobbleDollard Capture

Un mod Minecraft Fabric qui récompense les joueurs avec des CobbleDollards lorsqu'ils capturent/vainquent des Pokémon!

## Caractéristiques

- 💰 Gagnez des CobbleDollards en capturant des Pokémon
- 💰 Gagnez des CobbleDollards en vainquant des Pokémon
- 💰 Gagnez des CobbleDollards en ajoutant des Pokémon dans le pokedex
- 📈 Récompenses basées sur le niveau du Pokémon
- ⭐ Bonus pour les Pokémon rares, légendaires et shiny
- ⚙️ Configuration personnalisable

## Prérequis

- Minecraft 1.21.1
- Fabric Loader 0.16.9+
- Fabric API
- Cobblemon 1.7.1+
- CobbleDollards 2.0.0+

## Installation

1. Téléchargez le fichier `.jar` depuis les releases
2. Placez-le dans le dossier `mods` de votre serveur/client Minecraft
3. Assurez-vous que Cobblemon et CobbleDollards sont également installés
4. Démarrez le jeu/serveur

## Configuration

Le fichier de configuration se trouve dans `config/cobblemon-catchdollars.json`

### Paramètres par défaut

```json
{
  "baseReward": 50.0,
  "baseRewardCommun": 50.0,
  "baseRewardUncommun": 100.0,
  "baseRewardRare": 500.0,
  "baseRewardUltraRare": 1000.0,
  "baseRewardLegendary": 2000.0,
  "baseRewardPokedex": 200.0,
  "baseRewardCommunPokedex": 200.0,
  "baseRewardUncommunPokedex": 400.0,
  "baseRewardRarePokedex": 1000.0,
  "baseRewardUltraRarePokedex": 1500.0,
  "baseRewardLegendaryPokedex": 3000.0,
  "levelMultiplier": 15.0,
  "maxReward": 15000.0,
  "commonMultiplier": 1.0,
  "uncommonMultiplier": 2.0,
  "rareMultiplier": 3.5,
  "epicMultiplier": 5.0,
  "legendaryMultiplier": 8.0,
  "ultraBeastMultiplier": 6.0,
  "shinyMultiplier": 1.5,
  "baseVictoryLevelMultiplier": 125.0,
  "language": "fr",
  "captureMessage": "§6Vous avez capturé %s et gagné %d CobbleDollards!",
  "pokedexMessage": "§6Vous avez complété une entrée du Pokédex avec %s et gagné %d CobbleDollards!",
  "victoryMessage": "§6Vous avez vaincu %s et gagné %d CobbleDollards!",
  "showCaptureTitle": true,
  "showPokedexTitle": true,
  "showCaptureChat": true,
  "showPokedexChat": true
}
```

### Explication des paramètres

- **baseReward**: Récompense de base pour une capture
- **baseRewardCommun**: Récompense de base pour une capture de Pokémon commun
- **baseRewardUncommun**: Récompense de base pour une capture de Pokémon peu commun
- **baseRewardRare**: Récompense de base pour une capture de Pokémon rare
- **baseRewardUltraRare**: Récompense de base pour une capture de Pokémon ultra rare
- **baseRewardLegendary**: Récompense de base pour une capture de Pokémon légendaire
- **baseRewardPokedex**: Récompense de base pour une entrée Pokédex
- **baseRewardCommunPokedex**: Récompense de base pour une entrée Pokédex d’un Pokémon commun
- **baseRewardUncommunPokedex**: Récompense de base pour une entrée Pokédex d’un Pokémon peu commun
- **baseRewardRarePokedex**: Récompense de base pour une entrée Pokédex d’un Pokémon rare
- **baseRewardUltraRarePokedex**: Récompense de base pour une entrée Pokédex d’un Pokémon ultra rare
- **baseRewardLegendaryPokedex**: Récompense de base pour une entrée Pokédex d’un Pokémon légendaire
- **levelMultiplier**: Multiplicateur par niveau du Pokémon
- **maxReward**: Récompense maximale possible
- **commonMultiplier**: Multiplicateur pour les Pokémon communs
- **uncommonMultiplier**: Multiplicateur pour les Pokémon peu communs
- **rareMultiplier**: Multiplicateur pour les Pokémon rares
- **epicMultiplier**: Multiplicateur pour les Pokémon épiques
- **legendaryMultiplier**: Multiplicateur pour les Pokémon légendaires
- **ultraBeastMultiplier**: Multiplicateur pour les Ultra-Chimères
- **shinyMultiplier**: Multiplicateur pour les Pokémon shiny
- **baseVictoryLevelMultiplier**: Multiplicateur par niveau pour les récompenses de victoire
- **language**: Langue des messages (ex: fr, en)
- **captureMessage**: Message affiché lors d'une capture (utilise %s pour le nom et %d pour le montant)
- **pokedexMessage**: Message affiché lors d'une entrée Pokédex (utilise %s pour le nom et %d pour le montant)
- **victoryMessage**: Message affiché lors d'une victoire (utilise %s pour le nom et %d pour le montant)
- **showCaptureTitle**: Affiche un titre à l’écran lors d’une capture
- **showPokedexTitle**: Affiche un titre à l’écran lors d’une entrée Pokédex
- **showCaptureChat**: Affiche le message de capture dans le chat
- **showPokedexChat**: Affiche le message Pokédex dans le chat

## Calcul des récompenses

La formule de calcul pour la capture est:
```
Récompense = (baseReward + (niveau × levelMultiplier)) × multiplicateur de rareté
```

### Exemples

- Pikachu niveau 10 (commun): (10 + 10×2) × 1.0 = **30 CobbleDollards**
- Dragonite niveau 50 (rare): (10 + 50×2) × 2.0 = **220 CobbleDollards**
- Mewtwo niveau 70 (légendaire): (10 + 70×2) × 5.0 = **750 CobbleDollards**
- Pikachu Shiny niveau 10: (10 + 10×2) × 10.0 = **300 CobbleDollards**

## Compilation

Pour compiler le mod:

```bash
./gradlew build
```

Le fichier `.jar` sera généré dans `build/libs/`

## Licence

MIT

## Support

Pour tout problème ou suggestion, ouvrez une issue sur le repository GitHub.
