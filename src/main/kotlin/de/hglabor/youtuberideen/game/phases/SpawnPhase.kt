package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.game.GamePhaseManager.user
import de.hglabor.youtuberideen.holzkopf.BannerManager
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator.toLocation
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.feedSaturate
import net.axay.kspigot.extensions.bukkit.heal
import net.axay.kspigot.extensions.geometry.SimpleLocation3D
import net.axay.kspigot.extensions.geometry.toSimple
import net.axay.kspigot.extensions.onlinePlayers
import org.bukkit.*
import org.bukkit.block.BlockFace
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.*
import java.util.*

class SpawnPhase : AbstractGamePhase(GamePhaseManager) {
    private val toTeleport: MutableSet<UUID> = onlinePlayers.map { it.uniqueId }.toMutableSet()
    private val toTeleportAmount: Int = toTeleport.size

    init {
        val spawns = SkyIslandGenerator.spawnLocations.shuffled()
            .filter { SkyIslandGenerator.world.worldBorder.isInside(it.toLocation(SkyIslandGenerator.world)) }
            .take(toTeleportAmount)
        toTeleport.forEachIndexed { index, uuid -> Bukkit.getPlayer(uuid)?.user?.spawnLocation = spawns[index] }

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
        broadcast("Teleportiere Spieler")
    }

    override fun tick(tick: Int) {
        if (toTeleport.isEmpty()) {
            broadcast("Alle Spieler teleportiert")
            startNextPhase()
            return
        }

        val teleported = mutableSetOf<UUID>()
        toTeleport.take(6).forEach { uuid ->
            teleported.add(uuid)
            Bukkit.getPlayer(uuid)?.apply {
                val loc = user.spawnLocation?.toLocation(SkyIslandGenerator.world) ?: return@forEach
                passengers.forEach { removePassenger(it) } //OMG CANT TELEPORT WITH PASSENGERS BÖÖÖ
                teleport(loc.add(0.5, 1.0, 0.5))
                inventory.clear()
                heal()
                feedSaturate()
                fireTicks = 0
                gameMode = GameMode.SURVIVAL
            }
        }
        toTeleport.removeAll(teleported)
        broadcast("${toTeleportAmount - toTeleport.size}/${toTeleportAmount} Spieler teleportiert")
    }

    override fun nextPhase(): AbstractGamePhase = PreInvincibilityPhase()
}
