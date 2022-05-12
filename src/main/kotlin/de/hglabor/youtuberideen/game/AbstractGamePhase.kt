package de.hglabor.youtuberideen.game

import IGamePhaseManager
import Tickable
import de.hglabor.youtuberideen.Prefix
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

abstract class AbstractGamePhase(protected val gamePhaseManager: IGamePhaseManager) {
    val listeners = mutableListOf<Listener>()
    val tickable = mutableListOf<Tickable>()
    abstract fun tick(tick: Int)
    abstract fun nextPhase(): AbstractGamePhase

    protected fun startNextPhase() {
        listeners.forEach { HandlerList.unregisterAll(it) }
        gamePhaseManager.phase = nextPhase()
    }

    protected fun broadcast(text: String) {
        net.axay.kspigot.extensions.broadcast("$Prefix $text")
    }

    protected fun executeTickables() = tickable.forEach { it.onTick() }
}