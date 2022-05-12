package de.hglabor.youtuberideen.game

import IGamePhaseManager
import de.hglabor.youtuberideen.game.phases.LobbyPhase
import net.axay.kspigot.runnables.task
import java.util.concurrent.atomic.AtomicInteger

object GamePhaseManager : IGamePhaseManager {
    private val timer = AtomicInteger()
    override var phase: AbstractGamePhase = LobbyPhase()

    private fun run() = task(period = 20L) { phase.tick(timer.getAndIncrement()) }
    override fun resetTimer() = timer.set(0)

    init {
        run()
    }
}