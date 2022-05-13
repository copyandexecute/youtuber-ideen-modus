package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.holzkopf.BannerManager
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class EndPhase(winner: Player?) : AbstractGamePhase(GamePhaseManager) {
    private val closeTime = 10

    init {
        listeners += listen<PlayerJoinEvent> {
            it.joinMessage = null
            it.player.gameMode = GameMode.SPECTATOR
        }
        listeners += listen<PlayerQuitEvent> {
            it.quitMessage = null
            if (it.player.gameMode != GameMode.SPECTATOR) {
                broadcast("${ChatColor.RED}${it.player.name} hat das Spiel verlassen.")
            }
        }
        listeners += listen<EntityDamageEvent> { it.isCancelled = true }
        gamePhaseManager.resetTimer()
        broadcast("$winner hat gewonnen!")
        onlinePlayers.forEach {
            it.inventory.clear()
            it.gameMode = GameMode.SURVIVAL
            it.heal()
            it.feedSaturate()
            it.fireTicks = 0
            it.teleport(Location(BannerManager.lobby, 0.0, 20.0, 0.0))
        }
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
