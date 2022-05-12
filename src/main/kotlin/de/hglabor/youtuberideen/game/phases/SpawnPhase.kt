package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

class SpawnPhase : AbstractGamePhase(GamePhaseManager) {
    private val toTeleport: MutableSet<UUID> = onlinePlayers.map { it.uniqueId }.toMutableSet()
    private val toTeleportAmount = toTeleport.size

    init {
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
        broadcast("Teleportiere Spieler")
    }

    override fun tick(tick: Int) {
        if (toTeleport.isEmpty()) {
            broadcast("Alle Spieler teleportiert")
            startNextPhase()
            return
        }

        val teleported = mutableSetOf<UUID>()
        toTeleport.take(6).forEach {
            teleported.add(it)
            //TODO tp to skywars spawn
        }
        toTeleport.removeAll(teleported)
        broadcast("${toTeleportAmount - toTeleport.size}/${toTeleportAmount} Spieler teleportiert")
    }

    override fun nextPhase(): AbstractGamePhase = PreInvincibilityPhase()
}