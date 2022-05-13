package de.hglabor.youtuberideen.game

import IGamePhaseManager
import de.hglabor.youtuberideen.game.phases.LobbyPhase
import net.axay.kspigot.extensions.geometry.SimpleLocation3D
import net.axay.kspigot.runnables.task
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

object GamePhaseManager : IGamePhaseManager {
    private val users = mutableMapOf<UUID, User>()

    private val timer = AtomicInteger()
    override var phase: AbstractGamePhase = LobbyPhase()

    private fun run() = task(period = 20L) { phase.tick(timer.getAndIncrement()) }
    override fun resetTimer() = timer.set(0)

    init {
        run()
    }

    data class User(val uuid: UUID) {
        var spawnLocation: SimpleLocation3D? = null
    }

    val Player.user get() = users.computeIfAbsent(uniqueId) { User(uniqueId) }
}
