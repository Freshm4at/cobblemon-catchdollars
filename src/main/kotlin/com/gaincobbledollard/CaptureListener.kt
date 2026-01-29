package com.gaincobbledollard

import com.cobblemon.mod.common.Cobblemon
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.pokemon.Pokemon
import com.cobblemon.mod.common.api.battles.model.actor.ActorType
import com.cobblemon.mod.common.api.pokedex.PokedexEntryProgress 
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import net.minecraft.server.MinecraftServer
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.UUID
import kotlin.math.round

object CaptureListener {
    
    // Cache pour stocker le mapping Numéro Pokédex -> Bucket de rareté
    private val pokemonBucketCache = mutableMapOf<Int, String>()
    private var cacheInitialized = false
    
    fun register() {
        
        GainCobbleDollardCapture.LOGGER.debug("[Gain-cobbledollard] Tentative d'enregistrement de l'écouteur de capture...")
        
        CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL) { event ->
            GainCobbleDollardCapture.LOGGER.info("[Gain-cobbledollard] Événement POKEMON_CAPTURED reçu!")
            
            // Initialiser le cache au premier événement de capture
            if (!cacheInitialized) {
                initializeBucketCache()
            }
            event.player
            handleCapture(event.player, event.pokemon)
        }
        CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.NORMAL) { event ->
            GainCobbleDollardCapture.LOGGER.info("[Gain-cobbledollard] Événement BATTLE_VICTORY reçu!")
            val server = Cobblemon.implementation.server()
           
            if(event.losers.first().type == ActorType.WILD && event.winners.first().type == ActorType.PLAYER) {
                
                event.winners.forEach { actor ->
                    
                    val player = getPlayerByUUID(actor.uuid, server)
                    
                    event.losers.forEach { defeatedActor ->
                        
                        val pokemon = defeatedActor.pokemonList.firstOrNull()
                        if (pokemon != null && player != null) {
                            handleVictory(player, pokemon.originalPokemon)
                        } else {
                            GainCobbleDollardCapture.LOGGER.warn("[BATTLE_VICTORY] Pokemon ou player null - skip")
                        }
                    }
                }
            } else {
                GainCobbleDollardCapture.LOGGER.info("[BATTLE_VICTORY] ✗ Condition WILD non remplie - pas de traitement")
            }
        }

        CobblemonEvents.POKEDEX_DATA_CHANGED_POST.subscribe(Priority.NORMAL) { event ->
            val server = Cobblemon.implementation.server()
            GainCobbleDollardCapture.LOGGER.debug("[Gain-cobbledollard] Événement POKEDEX_DATA_CHANGED_POST reçu!")
            // Ne déclencher que si le statut "caught" vient de passer de false à true
            if (event.knowledge == PokedexEntryProgress.CAUGHT) {
                val player = getPlayerByUUID(event.playerUUID, server)
                val pokemon = event.dataSource.pokemon
                handlePokedexEntry(player, pokemon)
            } else {
                GainCobbleDollardCapture.LOGGER.debug(
                    "[Gain-cobbledollard] Pokédex changé mais pas une nouvelle capture - ignoré"
                )
            }
        }
    }


    
    private fun handleCapture(player: ServerPlayerEntity?, pokemon: Pokemon?) {
        GainCobbleDollardCapture.LOGGER.debug("[Gain-cobbledollard] Événement de capture détecté - Joueur: {}, Pokemon: {}", player?.name?.string, pokemon?.species?.name)
        
        if (player == null || pokemon == null) {
            GainCobbleDollardCapture.LOGGER.warn("[Gain-cobbledollard] Joueur ou Pokemon null - annulation")
            return
        }
        
        // Calculer le bucket une seule fois
        val bucket = getSpawnBucket(pokemon)
        
        // Calculer la récompense avec le bucket
        val reward = calculateReward(pokemon, bucket)
        
        // Obtenir la couleur selon le bucket
        val rarityColor = getBucketColor(pokemon, bucket)
        
        // Donner l'argent au joueur
        if (reward > 0) {
            val rewardInt = reward.toInt()
            
            RewardHandler.giveReward(player, reward)
            
            // Message au joueur dans le chat
            if (Config.showCaptureChat) {
                val message = try {
                    String.format(
                        Config.captureMessage,
                        pokemon.species.name,
                        rewardInt
                    )
                } catch (e: Exception) {
                    // Fallback si le format est incorrect
                    "§6Vous avez capturé ${pokemon.species.name} et gagné $rewardInt CobbleDollards!"
                }
                player.sendMessage(
                    Text.literal(message).formatted(Formatting.GOLD),
                    false
                )
            }
            
            // Afficher un titre au centre de l'écran
            val title = Text.literal("")
                .append(Text.literal(pokemon.species.name).formatted(rarityColor, Formatting.BOLD))
            
            // Ajouter "✨ Shiny" si le Pokémon est shiny
            if (pokemon.shiny) {
                title.append(Text.literal(" ✨ Shiny").formatted(Formatting.GOLD, Formatting.ITALIC))
            }
            
            title.append(Text.literal(" lvl${pokemon.level} capturé").formatted(Formatting.GRAY))
            
            val subtitle = Text.literal("+$rewardInt CobbleDollards")
                .formatted(Formatting.YELLOW)
            
            // Utiliser le TitleQueueManager pour éviter les chevauchements
            TitleQueueManager.sendTitle(
                player,
                TitleQueueManager.TitleData(
                    title = title,
                    subtitle = subtitle,
                    fadeIn = 10,
                    stay = 70,
                    fadeOut = 20,
                    priority = 5  // Priorité normale pour les captures
                )
            )
            
            GainCobbleDollardCapture.LOGGER.info(
                "{} a capturé {} (Niveau {}) et a gagné {} CobbleDollards",
                player.name.string,
                pokemon.species.name,
                pokemon.level,
                rewardInt
            )
        }
    }

    private fun handleVictory(player: ServerPlayerEntity?, pokemon: Pokemon?) {
        GainCobbleDollardCapture.LOGGER.debug("[Gain-cobbledollard] Événement de victoire détecté - Joueur: {}, Pokemon: {}", player?.name?.string, pokemon?.species?.name)
        
        if (player == null || pokemon == null) {
            GainCobbleDollardCapture.LOGGER.warn("[Gain-cobbledollard] Joueur ou Pokemon null - annulation")
            return
        }
        
        // Calculer le bucket une seule fois
        val bucket = getSpawnBucket(pokemon)
        
        // Calculer la récompense avec le bucket
        val reward = calculateRewardVictory(pokemon)
        
        // Obtenir la couleur selon le bucket
        val rarityColor = getBucketColor(pokemon, bucket)
        
        // Donner l'argent au joueur
        if (reward > 0) {
            val rewardInt = reward.toInt()
            
            RewardHandler.giveReward(player, reward)
            
            // Message au joueur dans le chat
            if (Config.showCaptureChat) {
                val message = try {
                    String.format(
                        Config.victoryMessage,
                        pokemon.species.name,
                        rewardInt
                    )
                } catch (e: Exception) {
                    // Fallback si le format est incorrect
                    "§6Vous avez vaincu ${pokemon.species.name} et gagné $rewardInt CobbleDollards!"
                }
                player.sendMessage(
                    Text.literal(message).formatted(Formatting.GOLD),
                    false
                )
            }
            
            // Afficher un titre au centre de l'écran
            val title = Text.literal("")
                .append(Text.literal(pokemon.species.name).formatted(rarityColor, Formatting.BOLD))
            
            // Ajouter "✨ Shiny" si le Pokémon est shiny
            if (pokemon.shiny) {
                title.append(Text.literal(" ✨ Shiny").formatted(Formatting.GOLD, Formatting.ITALIC))
            }
            
            title.append(Text.literal(" lvl${pokemon.level} vaincu").formatted(Formatting.GRAY))
            
            val subtitle = Text.literal("+$rewardInt CobbleDollards")
                .formatted(Formatting.YELLOW)
            
            // Utiliser le TitleQueueManager pour éviter les chevauchements
            TitleQueueManager.sendTitle(
                player,
                TitleQueueManager.TitleData(
                    title = title,
                    subtitle = subtitle,
                    fadeIn = 10,
                    stay = 70,
                    fadeOut = 20,
                    priority = 5  // Priorité normale pour les captures
                )
            )
            
            GainCobbleDollardCapture.LOGGER.info(
                "{} a vaincu {} (Niveau {}) et a gagné {} CobbleDollards",
                player.name.string,
                pokemon.species.name,
                pokemon.level,
                rewardInt
            )
        }
    }

    private fun handlePokedexEntry(player: ServerPlayerEntity?, pokemon: Pokemon?) {
        GainCobbleDollardCapture.LOGGER.debug("[Gain-cobbledollard] Événement de nouvelle entrée pokedex détecté - Joueur: {}, Pokemon: {}", player?.name?.string, pokemon?.species?.name)
        
        if (player == null || pokemon == null) {
            GainCobbleDollardCapture.LOGGER.warn("[Gain-cobbledollard] Joueur ou Pokemon null - annulation")
            return
        }
        
        // Calculer le bucket une seule fois
        val bucket = getSpawnBucket(pokemon)
        
        var reward = when(bucket.lowercase()) {
            "common" -> Config.baseRewardPokedex
            "uncommon" -> Config.baseRewardUncommunPokedex
            "rare" -> Config.baseRewardRarePokedex
            "ultra-rare" -> Config.baseRewardUltraRarePokedex
            else -> Config.baseRewardPokedex
        }
        if(pokemon.isLegendary()) reward = Config.baseRewardLegendaryPokedex
        
        
        // Obtenir la couleur selon le bucket
        val rarityColor = getBucketColor(pokemon, bucket)
        
        // Donner l'argent au joueur
        if (reward > 0) {
            val rewardInt = reward.toInt()
            
            RewardHandler.giveReward(player, reward)
            
            // Message au joueur dans le chat
            if (Config.showPokedexChat) {
                val message = try {
                    String.format(
                        Config.pokedexMessage,
                        pokemon.species.name,
                        rewardInt
                    )
                } catch (e: Exception) {
                    // Fallback si le format est incorrect
                    "§6Vous avez ajouté ${pokemon.species.name} au pokédex et gagné $rewardInt CobbleDollards!"
                }
                player.sendMessage(
                    Text.literal(message).formatted(Formatting.GOLD),
                    false
                )
            }
            // Afficher un titre au centre de l'écran
            if (Config.showPokedexTitle) {
                val title = Text.literal("")
                    .append(Text.literal(pokemon.species.name).formatted(rarityColor, Formatting.BOLD))
                
                title.append(Text.literal(" ${Config.getMessage("addedToPokedex")}").formatted(Formatting.GRAY))
                
                val subtitle = Text.literal("+$rewardInt CobbleDollards")
                    .formatted(Formatting.YELLOW)
                
                // Utiliser le TitleQueueManager pour éviter les chevauchements
                TitleQueueManager.sendTitle(
                    player,
                    TitleQueueManager.TitleData(
                        title = title,
                        subtitle = subtitle,
                        fadeIn = 10,
                        stay = 70,
                        fadeOut = 20,
                        priority = 10  // Priorité haute pour les entrées pokédex
                    )
                )
            }
            
            GainCobbleDollardCapture.LOGGER.info(
                "{} a ajouté au pokédex: {} et a gagné {} CobbleDollards",
                player.name.string,
                pokemon.species.name,
                rewardInt
            )
        }
    }
    
    private fun calculateReward(pokemon: Pokemon, bucket: String): Double {
        val level = pokemon.level
        
        // Calculer la base: récompense de base + bonus de niveau
        val levelBonus = level * Config.levelMultiplier
        val baseAmount = getBaseRewardForBucket(pokemon, bucket) + levelBonus
        
        // Déterminer le multiplicateur de rareté
        val rarityMultiplier = getBucketMultiplier(pokemon, bucket)
        
        // Calculer la récompense totale
        var totalReward = baseAmount * rarityMultiplier
        
        if (pokemon.shiny) totalReward *= Config.shinyMultiplier
        
        // Arrondir et appliquer le plafond
        val roundedReward = round(totalReward * 100.0) / 100.0
        val finalReward = kotlin.math.min(Config.maxReward, roundedReward)
        
        GainCobbleDollardCapture.LOGGER.debug(
            "[Gain-cobbledollard] Calcul récompense - Pokemon: {}, Niveau: {}, Bucket: {}, Base: {}, Multiplicateur: {}, Final: {}",
            pokemon.species.name, level, bucket, baseAmount, rarityMultiplier, finalReward
        )
        
        return finalReward
    }

    private fun calculateRewardVictory(pokemon: Pokemon): Double {
        val level = pokemon.level
        
        // Calculer la base: récompense de base + bonus de niveau
        val totalReward = level * Config.baseVictoryLevelMultiplier

        
        // Arrondir et appliquer le plafond
        val roundedReward = round(totalReward * 100.0) / 100.0
        val finalReward = kotlin.math.min(Config.maxReward, roundedReward)
        
        return finalReward
    }
    
    private fun getBucketMultiplier(pokemon: Pokemon, bucket: String): Double {
        // Priorité aux types spéciaux
        if (pokemon.isLegendary()) return Config.legendaryMultiplier
        if (pokemon.isUltraBeast()) return Config.ultraBeastMultiplier
        
        // Sinon utiliser le bucket
        return when (bucket.lowercase()) {
            "common" -> Config.commonMultiplier
            "uncommon" -> Config.uncommonMultiplier
            "rare" -> Config.rareMultiplier
            "ultra-rare" -> Config.epicMultiplier
            else -> Config.commonMultiplier
        }
    }

    private fun getBaseRewardForBucket(pokemon: Pokemon, bucket: String): Double {
        if (pokemon.isLegendary()) return Config.baseRewardLegendary

        return when (bucket.lowercase()) {
            "common" -> Config.baseRewardCommun
            "uncommon" -> Config.baseRewardUncommun
            "rare" -> Config.baseRewardRare
            "ultra-rare" -> Config.baseRewardUltraRare
            else -> Config.baseReward
        }
    }
    
    private fun getBucketColor(pokemon: Pokemon, bucket: String): Formatting {
        if (pokemon.isLegendary()) return Formatting.GOLD
        if (pokemon.isUltraBeast()) return Formatting.DARK_RED

        return when (bucket.lowercase()) {
            "common" -> Formatting.GRAY
            "uncommon" -> Formatting.GREEN
            "rare" -> Formatting.BLUE
            "ultra-rare" -> Formatting.DARK_PURPLE
            else -> Formatting.GRAY
        }
    }
    
    
    private fun getSpawnBucket(pokemon: Pokemon): String {
        // Récupérer le numéro national du Pokédex
        val nationalDex = pokemon.species.nationalPokedexNumber
        
        // Chercher dans le cache
        val bucket = pokemonBucketCache[nationalDex]
        
        if (bucket != null) {
            GainCobbleDollardCapture.LOGGER.debug(
                "[Gain-cobbledollard] Bucket trouvé pour {} (#{}) : {}",
                pokemon.species.name, nationalDex, bucket
            )
            return bucket
        }
        
        // Fallback: utiliser le catch rate comme approximation
        GainCobbleDollardCapture.LOGGER.warn(
            "[Gain-cobbledollard] Bucket non trouvé pour {} (#{}), utilisation du catch rate",
            pokemon.species.name, nationalDex
        )
        
        val catchRate = pokemon.species.catchRate
        return when {
            catchRate >= 190 -> "common"
            catchRate >= 120 -> "uncommon"
            catchRate >= 45 -> "rare"
            else -> "ultra-rare"
        }
    }
    
    private fun initializeBucketCache() {
        try {
            GainCobbleDollardCapture.LOGGER.info("[Gain-cobbledollard] Chargement du fichier pokemon_buckets.json...")
            
            // Lire le JSON depuis les resources
            val jsonStream = javaClass.classLoader.getResourceAsStream("pokemon_buckets.json")
            
            if (jsonStream != null) {
                val jsonContent = jsonStream.bufferedReader().use { it.readText() }
                val gson = Gson()
                val type = object : TypeToken<Map<String, String>>() {}.type
                val bucketMap: Map<String, String> = gson.fromJson(jsonContent, type)
                
                // Convertir les clés String en Int et stocker dans le cache
                bucketMap.forEach { (pokedexNumber, bucket) ->
                    try {
                        pokemonBucketCache[pokedexNumber.toInt()] = bucket.lowercase()
                    } catch (e: NumberFormatException) {
                        // Ignorer les entrées mal formées
                    }
                }
                
                GainCobbleDollardCapture.LOGGER.info(
                    "[Gain-cobbledollard] Cache chargé: {} Pokémon avec leurs raretés",
                    pokemonBucketCache.size
                )
            } else {
                GainCobbleDollardCapture.LOGGER.error("[Gain-cobbledollard] Fichier pokemon_buckets.json introuvable dans les resources!")
            }
            
            cacheInitialized = true
            
        } catch (e: Exception) {
            GainCobbleDollardCapture.LOGGER.error("[Gain-cobbledollard] Erreur chargement cache: {}", e.message)
            e.printStackTrace()
            cacheInitialized = true
        }
    }
    
    private fun getPlayerByUUID(uuid: UUID, server: MinecraftServer?): ServerPlayerEntity? {
        return server?.playerManager?.getPlayer(uuid)
    }
}
