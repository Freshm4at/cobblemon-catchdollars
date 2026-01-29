package com.gaincobbledollard

import com.google.gson.GsonBuilder
import kotlinx.serialization.Serializable
import com.gaincobbledollard.models.ConfigData
import java.io.File
import java.io.FileReader
import java.io.FileWriter

object Config {
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private val CONFIG_FILE = File("config/gain-cobbledollard-capture.json")
    var properties = ConfigData()
    
    // Base rewards for capture
    var baseReward = 50.0
    var baseRewardCommun = 50.0
    var baseRewardUncommun = 100.0
    var baseRewardRare = 500.0
    var baseRewardUltraRare = 1000.0
    var baseRewardLegendary = 2000.0
    
    // Base rewards for pokedex
    var baseRewardPokedex = 200.0
    var baseRewardCommunPokedex = 200.0
    var baseRewardUncommunPokedex = 400.0
    var baseRewardRarePokedex = 1000.0
    var baseRewardUltraRarePokedex = 2500.0
    var baseRewardLegendaryPokedex = 5000.0
    
    // General config
    var levelMultiplier = 15.0
    var maxReward = 15000.0
    
    // Rarity multipliers
    var commonMultiplier = 1.0
    var uncommonMultiplier = 2.0
    var rareMultiplier = 3.5
    var epicMultiplier = 5.0
    var legendaryMultiplier = 8.0
    var ultraBeastMultiplier = 6.0
    var shinyMultiplier = 1.5

    // Victory rewards
    var baseVictoryLevelMultiplier = 50.0
    
    // Language settings
    var language = "fr" // "fr" ou "en"

    // Predefined messages by language
    private val messages = mapOf(
        "fr" to mapOf(
            "capture" to "§6Vous avez capturé %s et gagné %d CobbleDollards!",
            "pokedex" to "§6Vous avez complété une entrée du Pokédex avec %s et gagné %d CobbleDollards!",
            "victory" to "§6Vous avez vaincu %s et gagné %d CobbleDollards!",
            "captured" to "capturé",
            "defeated" to "vaincu",
            "addedToPokedex" to "ajouté au pokédex",
            "shiny" to "✨ Shiny"
        ),
        "en" to mapOf(
            "capture" to "§6You caught %s and earned %d CobbleDollars!",
            "pokedex" to "§6You completed a Pokédex entry with %s and earned %d CobbleDollars!",
            "victory" to "§6You defeated %s and earned %d CobbleDollars!",
            "captured" to "caught",
            "defeated" to "defeated",
            "addedToPokedex" to "added to pokedex",
            "shiny" to "✨ Shiny"
        )
    )
    
    // Helper function to get translated message
    fun getMessage(key: String): String {
        return messages[language]?.get(key) ?: messages["en"]?.get(key) ?: ""
    }
    
    // Display settings
    var showCaptureTitle = true
    var showPokedexTitle = true
    var showCaptureChat = true
    var showPokedexChat = true
    
    fun load() {
        if (!CONFIG_FILE.exists()) {
            save()
            GainCobbleDollardCapture.LOGGER.debug("Fichier de configuration créé: ${CONFIG_FILE.absolutePath}")
            return
        }
        
        try {
            FileReader(CONFIG_FILE).use { reader ->
                val data = GSON.fromJson(reader, ConfigData::class.java)
                
                data?.let {
                    // Base rewards for capture
                    baseReward = it.baseReward
                    baseRewardCommun = it.baseRewardCommun
                    baseRewardUncommun = it.baseRewardUncommun
                    baseRewardRare = it.baseRewardRare
                    baseRewardUltraRare = it.baseRewardUltraRare
                    baseRewardLegendary = it.baseRewardLegendary
                    
                    // Base rewards for pokedex
                    baseRewardPokedex = it.baseRewardPokedex
                    baseRewardCommunPokedex = it.baseRewardCommunPokedex
                    baseRewardUncommunPokedex = it.baseRewardUncommunPokedex
                    baseRewardRarePokedex = it.baseRewardRarePokedex
                    baseRewardUltraRarePokedex = it.baseRewardUltraRarePokedex
                    baseRewardLegendaryPokedex = it.baseRewardLegendaryPokedex
                    
                    // General config
                    levelMultiplier = it.levelMultiplier
                    maxReward = it.maxReward
                    
                    // Rarity multipliers
                    commonMultiplier = it.commonMultiplier
                    uncommonMultiplier = it.uncommonMultiplier
                    rareMultiplier = it.rareMultiplier
                    epicMultiplier = it.epicMultiplier
                    legendaryMultiplier = it.legendaryMultiplier
                    ultraBeastMultiplier = it.ultraBeastMultiplier
                    shinyMultiplier = it.shinyMultiplier

                    // Victory rewards
                    baseVictoryLevelMultiplier = it.baseVictoryLevelMultiplier
                    
                    // Language
                    language = it.language
                    
                    // Display settings
                    showCaptureTitle = it.showCaptureTitle
                    showPokedexTitle = it.showPokedexTitle
                    showCaptureChat = it.showCaptureChat
                    showPokedexChat = it.showPokedexChat
                }
            }
            
            GainCobbleDollardCapture.LOGGER.debug("Configuration chargée avec succès")
        } catch (e: Exception) {
            GainCobbleDollardCapture.LOGGER.error("Erreur lors du chargement de la configuration: ${e.message}")
        }
    }
    
    fun save() {
        CONFIG_FILE.parentFile?.mkdirs()
        
        val data = ConfigData(
            // Base rewards for capture
            baseReward = baseReward,
            baseRewardCommun = baseRewardCommun,
            baseRewardUncommun = baseRewardUncommun,
            baseRewardRare = baseRewardRare,
            baseRewardUltraRare = baseRewardUltraRare,
            baseRewardLegendary = baseRewardLegendary,
            
            // Base rewards for pokedex
            baseRewardPokedex = baseRewardPokedex,
            baseRewardCommunPokedex = baseRewardCommunPokedex,
            baseRewardUncommunPokedex = baseRewardUncommunPokedex,
            baseRewardRarePokedex = baseRewardRarePokedex,
            baseRewardUltraRarePokedex = baseRewardUltraRarePokedex,
            baseRewardLegendaryPokedex = baseRewardLegendaryPokedex,
            
            // General config
            levelMultiplier = levelMultiplier,
            maxReward = maxReward,
            
            // Rarity multipliers
            commonMultiplier = commonMultiplier,
            uncommonMultiplier = uncommonMultiplier,
            rareMultiplier = rareMultiplier,
            epicMultiplier = epicMultiplier,
            legendaryMultiplier = legendaryMultiplier,
            ultraBeastMultiplier = ultraBeastMultiplier,
            shinyMultiplier = shinyMultiplier,

            // Victory rewards
            baseVictoryLevelMultiplier = baseVictoryLevelMultiplier,
            
            // Language
            language = language,
            
            // Display settings
            showCaptureTitle = showCaptureTitle,
            showPokedexTitle = showPokedexTitle,
            showCaptureChat = showCaptureChat,
            showPokedexChat = showPokedexChat
        )
        
        try {
            FileWriter(CONFIG_FILE).use { writer ->
                GSON.toJson(data, writer)
            }
            GainCobbleDollardCapture.LOGGER.debug("Configuration sauvegardée")
        } catch (e: Exception) {
            GainCobbleDollardCapture.LOGGER.error("Erreur lors de la sauvegarde de la configuration: ${e.message}")
        }
    }
    
    
}
