package com.gaincobbledollard

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object GainCobbleDollardCapture : ModInitializer {
    const val MOD_ID = "gain-cobbledollard-capture"
    val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        LOGGER.info("Initialisation de Gain CobbleDollard Capture")
        
        // Charger la configuration
        Config.load()
        
        // Enregistrer l'écouteur de capture
        CaptureListener.register()
        
        LOGGER.info("Gain CobbleDollard Capture initialisé avec succès!")
    }
}
