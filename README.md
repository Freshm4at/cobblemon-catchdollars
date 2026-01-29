# Gain CobbleDollard Capture

Un mod Minecraft Fabric qui récompense les joueurs avec des CobbleDollards lorsqu'ils capturent des Pokémon!

## Caractéristiques

- 💰 Gagnez des CobbleDollards en capturant des Pokémon
- 📈 Récompenses basées sur le niveau du Pokémon
- ⭐ Bonus pour les Pokémon rares, légendaires et shiny
- ⚙️ Configuration personnalisable
- 🌍 Support multilingue dans les messages

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

Le fichier de configuration se trouve dans `config/gain-cobbledollard-capture.json`

### Paramètres par défaut

```json
{
  "baseReward": 10.0,
  "levelMultiplier": 2.0,
  "commonMultiplier": 1.0,
  "uncommonMultiplier": 1.5,
  "rareMultiplier": 2.0,
  "epicMultiplier": 3.0,
  "legendaryMultiplier": 5.0,
  "ultraBeastMultiplier": 4.0,
  "shinyMultiplier": 10.0,
  "captureMessage": "§6Vous avez capturé %s et gagné %.2f CobbleDollards!"
}
```

### Explication des paramètres

- **baseReward**: Récompense de base pour une capture
- **levelMultiplier**: Multiplicateur par niveau du Pokémon
- **commonMultiplier**: Multiplicateur pour les Pokémon communs
- **uncommonMultiplier**: Multiplicateur pour les Pokémon peu communs
- **rareMultiplier**: Multiplicateur pour les Pokémon rares
- **epicMultiplier**: Multiplicateur pour les Pokémon épiques
- **legendaryMultiplier**: Multiplicateur pour les Pokémon légendaires
- **ultraBeastMultiplier**: Multiplicateur pour les Ultra-Chimères
- **shinyMultiplier**: Multiplicateur pour les Pokémon shiny
- **captureMessage**: Message affiché lors d'une capture (utilise %s pour le nom et %.2f pour le montant)

## Calcul des récompenses

La formule de calcul est:
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
