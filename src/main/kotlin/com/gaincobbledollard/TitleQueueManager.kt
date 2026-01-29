package com.gaincobbledollard

import kotlinx.coroutines.*
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.PriorityQueue

object TitleQueueManager {
    
    // Map pour stocker les queues de chaque joueur
    private val playerQueues = ConcurrentHashMap<UUID, TitleQueue>()
    
    // Scope pour les coroutines
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    data class TitleData(
        val title: Text,
        val subtitle: Text,
        val fadeIn: Int = 10,
        val stay: Int = 70,
        val fadeOut: Int = 20,
        val priority: Int = 0 // Plus la valeur est élevée, plus la priorité est haute
    )
    
    private class TitleQueue(val playerId: UUID) {
        private val queue = PriorityQueue<QueuedTitle>(compareByDescending { it.priority })
        private var processingJob: Job? = null
        private val lock = Any()
        
        data class QueuedTitle(
            val titleData: TitleData,
            val priority: Int,
            val timestamp: Long = System.currentTimeMillis()
        )
        
        fun addTitle(titleData: TitleData) {
            synchronized(lock) {
                queue.offer(QueuedTitle(titleData, titleData.priority))
            }
        }
        
        fun startProcessing(player: ServerPlayerEntity, scope: CoroutineScope) {
            // Annuler le job existant si présent
            processingJob?.cancel()
            
            processingJob = scope.launch {
                try {
                    // Attendre un court instant pour grouper les événements simultanés
                    delay(100)
                    
                    // Traiter tous les titres dans la queue par ordre de priorité
                    while (true) {
                        val queuedTitle = synchronized(lock) {
                            queue.poll()
                        } ?: break
                        
                        val titleData = queuedTitle.titleData
                        
                        // Envoyer le titre
                        player.networkHandler.sendPacket(
                            net.minecraft.network.packet.s2c.play.TitleS2CPacket(titleData.title)
                        )
                        player.networkHandler.sendPacket(
                            net.minecraft.network.packet.s2c.play.SubtitleS2CPacket(titleData.subtitle)
                        )
                        player.networkHandler.sendPacket(
                            net.minecraft.network.packet.s2c.play.TitleFadeS2CPacket(
                                titleData.fadeIn,
                                titleData.stay,
                                titleData.fadeOut
                            )
                        )
                        
                        // Attendre la durée d'affichage du titre
                        val totalDuration = (titleData.fadeIn + titleData.stay + titleData.fadeOut) * 50L
                        delay(totalDuration + 200) // Buffer entre les titres
                    }
                    
                } catch (e: CancellationException) {
                    GainCobbleDollardCapture.LOGGER.debug("[TitleQueue] Traitement annulé pour joueur {}", playerId)
                } catch (e: Exception) {
                    GainCobbleDollardCapture.LOGGER.error("[TitleQueue] Erreur lors du traitement: {}", e.message)
                } finally {
                    // Nettoyer si la queue est vide
                    if (synchronized(lock) { queue.isEmpty() }) {
                        playerQueues.remove(playerId)
                    }
                }
            }
        }
        
        fun cancel() {
            processingJob?.cancel()
            synchronized(lock) {
                queue.clear()
            }
        }
    }
    
    /**
     * Envoie un titre au joueur avec gestion de priorité
     * Priority: valeur plus élevée = priorité plus haute
     */
    fun sendTitle(player: ServerPlayerEntity, titleData: TitleData) {
        val playerId = player.uuid
        
        val queue = playerQueues.computeIfAbsent(playerId) { TitleQueue(playerId) }
        queue.addTitle(titleData)
        queue.startProcessing(player, scope)
    }
    
    /**
     * Nettoie les ressources (à appeler lors de l'arrêt du serveur si nécessaire)
     */
    fun shutdown() {
        playerQueues.values.forEach { it.cancel() }
        playerQueues.clear()
        scope.cancel()
    }
}
