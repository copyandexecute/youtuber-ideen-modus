package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.bastighg.CakeManager
import de.hglabor.youtuberideen.castcrafter.LevitationManager
import de.hglabor.youtuberideen.einfachgustaf.PlayerHider
import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.sasukey.LavaManager
import de.hglabor.youtuberideen.seltix.PvPBotManager
import de.hglabor.youtuberideen.stegi.BloodManager
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import net.axay.kspigot.event.listen
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PvPPhase : AbstractGamePhase(GamePhaseManager) {
    private var winner: Player? = null

    init {
        listeners += LevitationManager.moveEvent
        listeners += LevitationManager.dismountEvent
        listeners += BloodManager.moveEvent
        listeners += BloodManager.damageByEntityEvent
        listeners += CakeManager.winEvent {
            winner = it
            startNextPhase()
        }
        listeners += PlayerHider.playerMoveEvent()
        listeners += BearManager.flameEvent()
        listeners += LavaManager.interactEvent()
        listeners += PvPBotManager.entityDamageByEntityEvent
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
        listeners += listen<PlayerDeathEvent> {
            if (it.player.gameMode == GameMode.SURVIVAL) {
                it.isCancelled = true
                it.drops.forEach { item -> it.player.world.dropItem(it.player.location, item) }
                broadcast("${it.deathMessage}")
                it.deathMessage(null)
                it.entity.passengers.clear()
                it.entity.gameMode = GameMode.SPECTATOR
                it.entity.teleport(it.entity.killer?.location ?: Location(SkyIslandGenerator.world, 0.0, 150.0, 0.0))
            }
        }
    }

    override fun tick(tick: Int) {
        if (LavaManager.currentLavaLevel < 200) {
            LavaManager.riseLava(1)
            if (LavaManager.currentLavaLevel.mod(10) == 0) {
                broadcast("Die Lava ist auf Höhe ${LavaManager.currentLavaLevel}")
            }
        }
    }

    override fun nextPhase(): AbstractGamePhase = EndPhase(winner)
}
