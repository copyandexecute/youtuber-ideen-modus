package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.holzkopf.BannerManager
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

class EndPhase(winner: Player?) : AbstractGamePhase(GamePhaseManager) {
    private val closeTime = 10

    init {
        gamePhaseManager.resetTimer()
        broadcast("$winner hat gewonnen!")
        onlinePlayers.forEach { it.teleport(Location(BannerManager.lobby, 0.0, 20.0, 0.0)) }
    }

    override fun tick(tick: Int) {
        val timeLeft = closeTime - tick

        if (timeLeft == 0) {
            broadcast("Server stoppt.")
            Bukkit.shutdown()
        } else {
            if (timeLeft == 10 || timeLeft <= 5)
                broadcast("Server schließt in ${timeLeft}s")
        }
    }

    override fun nextPhase(): AbstractGamePhase {
        TODO("Not yet implemented")
    }
}