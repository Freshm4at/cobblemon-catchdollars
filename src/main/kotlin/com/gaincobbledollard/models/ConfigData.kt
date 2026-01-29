package com.gaincobbledollard.models

data class ConfigData(
    // Base rewards for capture
    val baseReward: Double = 50.0,
    val baseRewardCommun: Double = 50.0,
    val baseRewardUncommun: Double = 100.0,
    val baseRewardRare: Double = 500.0,
    val baseRewardUltraRare: Double = 1000.0,
    val baseRewardLegendary: Double = 2000.0,
    
    // Base rewards for pokedex
    val baseRewardPokedex: Double = 200.0,
    val baseRewardCommunPokedex: Double = 200.0,
    val baseRewardUncommunPokedex: Double = 400.0,
    val baseRewardRarePokedex: Double = 1000.0,
    val baseRewardUltraRarePokedex: Double = 2500.0,
    val baseRewardLegendaryPokedex: Double = 5000.0,
    
    // General config
    val levelMultiplier: Double = 15.0,
    val maxReward: Double = 15000.0,
    
    // Rarity multipliers
    val commonMultiplier: Double = 1.0,
    val uncommonMultiplier: Double = 2.0,
    val rareMultiplier: Double = 3.5,
    val epicMultiplier: Double = 5.0,
    val legendaryMultiplier: Double = 8.0,
    val ultraBeastMultiplier: Double = 6.0,
    val shinyMultiplier: Double = 1.5,

    // Victory rewards
    val baseVictoryLevelMultiplier: Double = 50.0,

    // Language settings
    val language: String = "fr", // "fr" ou "en"
    
    // Display settings
    val showCaptureTitle: Boolean = true,
    val showPokedexTitle: Boolean = true,
    val showCaptureChat: Boolean = true,
    val showPokedexChat: Boolean = true
)