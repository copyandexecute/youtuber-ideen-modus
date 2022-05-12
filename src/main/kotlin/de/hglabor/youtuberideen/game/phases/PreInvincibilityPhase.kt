package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import net.axay.kspigot.event.listen
import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*

class PreInvincibilityPhase : AbstractGamePhase(GamePhaseManager) {
    private val countdown = 4

    init {
        gamePhaseManager.resetTimer()
        listeners += listen<BlockBreakEvent> { it.isCancelled = true }
        listeners += listen<BlockPlaceEvent> { it.isCancelled = true }
        listeners += listen<EntityDamageEvent> { it.isCancelled = true }
        listeners += listen<FoodLevelChangeEvent> { it.isCancelled = true }
        listeners += listen<PlayerInteractEvent> { it.isCancelled = true }
        listeners += listen<PlayerDropItemEvent> { it.isCancelled = true }
        listeners += listen<PlayerMoveEvent> {
            //Player was just moving mouse
            if (it.to.distanceSquared(it.from) == 0.0) {
                return@listen
            }
            if (it.player.user.spawnBlock != null) {
                val standingBlock = it.player.location.block.getRelative(BlockFace.DOWN)
                if (it.player.location.block == it.player.user.spawnBlock) {
                    return@listen
                } else if (standingBlock != it.player.user.spawnBlock) {
                    val loc = it.player.user.spawnBlock!!.location.clone().add(0.5, 1.0, 0.5)
                    loc.pitch = it.player.location.pitch
                    loc.yaw = it.player.location.yaw
                    it.player.teleport(loc)
                }
            }
        }
        listeners += listen<PlayerJoinEvent> { }
        listeners += listen<PlayerLoginEvent> { }
        listeners += listen<PlayerQuitEvent> {}
    }

    override fun tick(tick: Int) {
        val timeLeft = countdown - tick
        if (timeLeft == 0) {
            startNextPhase()
        } else {
            broadcast("Die Runde beginnt in ${timeLeft}s")
        }
    }

    override fun nextPhase(): AbstractGamePhase = InvincibilityPhase()
}