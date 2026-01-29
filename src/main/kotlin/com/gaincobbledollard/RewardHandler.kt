package com.gaincobbledollard

import net.minecraft.server.network.ServerPlayerEntity

object RewardHandler {
    
    fun giveReward(player: ServerPlayerEntity, amount: Double) {
        try {
            // Arrondir à l'unité
            val roundedAmount = amount.toInt()
            
            // Utiliser une commande silencieuse pour donner l'argent via CobbleDollards
            val source = player.server.commandSource.withSilent()
            val command = "cobbledollars give ${player.name.string} $roundedAmount"
            
            GainCobbleDollardCapture.LOGGER.debug(
                "[Gain-cobbledollard] Attribution de {} CobbleDollards à {}",
                roundedAmount, player.name.string
            )
            
            player.server.commandManager.executeWithPrefix(source, command)
            
        } catch (e: Exception) {
            GainCobbleDollardCapture.LOGGER.error(
                "Erreur lors de l'attribution de la récompense à ${player.name.string}: ${e.message}"
            )
            e.printStackTrace()
        }
    }
}
