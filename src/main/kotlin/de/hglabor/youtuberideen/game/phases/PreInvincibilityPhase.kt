package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.game.GamePhaseManager.user
import de.hglabor.youtuberideen.holzkopf.BannerManager
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.geometry.toSimple
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*

class PreInvincibilityPhase : AbstractGamePhase(GamePhaseManager) {
    private val countdown = 5

    init {
        gamePhaseManager.resetTimer()
        listeners += listen<BlockBreakEvent> { it.isCancelled = true }
        listeners += listen<BlockPlaceEvent> { it.isCancelled = true }
        listeners += listen<EntityDamageEvent> { it.isCancelled = true }
        listeners += listen<FoodLevelChangeEvent> { it.isCancelled = true }
        listeners += listen<PlayerInteractEvent> { it.isCancelled = true }
        listeners += listen<PlayerDropItemEvent> { it.isCancelled = true }
        listeners += listen<PlayerMoveEvent> {
            if (it.player.world == BannerManager.lobby) {
                return@listen
            }
            //Player was just moving mouse
            if (it.to.distanceSquared(it.from) == 0.0) {
                return@listen
            }
            val spawnLoc = it.player.user.spawnLocation ?: return@listen
            val standingBlock = it.player.location.block.getRelative(BlockFace.DOWN)
            if (standingBlock.location.toSimple() != spawnLoc) {
                val loc = Location(
                    SkyIslandGenerator.world,
                    spawnLoc.x + 0.5,
                    spawnLoc.y + 1,
                    spawnLoc.z + 0.5,
                )
                loc.pitch = it.player.location.pitch
                loc.yaw = it.player.location.yaw
                it.player.teleport(loc)
            }
        }
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
    }

    override fun tick(tick: Int) {
        val timeLeft = countdown - tick
        if (timeLeft == 0) {
            startNextPhase()
        } else {
            broadcast("${ChatColor.GRAY}Die Runde beginnt in ${ChatColor.YELLOW}${timeLeft}s")
        }
    }

    override fun nextPhase(): AbstractGamePhase = InvincibilityPhase()
}
