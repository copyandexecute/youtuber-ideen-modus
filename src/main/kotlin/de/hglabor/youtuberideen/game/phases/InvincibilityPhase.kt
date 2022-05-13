package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.bastighg.CakeManager
import de.hglabor.youtuberideen.einfachgustaf.PlayerHider
import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.game.mechanic.ChestLoot
import de.hglabor.youtuberideen.holzkopf.BannerManager.addBannerAboveHead
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.sasukey.LavaManager
import de.hglabor.youtuberideen.seltix.PvPBot
import de.hglabor.youtuberideen.veto.FlyingIronGolem
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator.toLocation
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class InvincibilityPhase : AbstractGamePhase(GamePhaseManager) {
    private val invincibilityAmount: Int = 5

    init {
        broadcast("${ChatColor.YELLOW}Die Schutzzeit beginnt!")
        gamePhaseManager.resetTimer()
        PlayerHider
        listeners += PlayerHider.playerMoveEvent()
        listeners += BearManager.flameEvent()
        listeners += LavaManager.interactEvent()
        listeners += CakeManager.blockBreakEvent()
        listeners += ChestLoot.playerOpenChestEvent()
        listeners += listen<EntityDamageEvent> { it.isCancelled = true }
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
        taskRunLater(3) {
            onlinePlayers.forEach { it.addBannerAboveHead() }
        }

        val toSpawn = listOf(EntityType.IRON_GOLEM, EntityType.PANDA, EntityType.POLAR_BEAR, EntityType.ZOMBIE)
        val youtuber = listOf(
            "NoRiskk",
            "BastiGHG",
            "Sasukey",
            "Wichtiger",
            "ZweifachGustaf",
            "NQRMAN",
            "Stxgi",
            "Seltix",
            "Wichtiger",
            "LetsHugo",
        )

        SkyIslandGenerator.spawnLocations.forEach {
            val type = toSpawn.random()
            val loc = it.toLocation(SkyIslandGenerator.world).add(0.5, 1.0, 0.5)
            when (type) {
                EntityType.IRON_GOLEM -> FlyingIronGolem(SkyIslandGenerator.world).spawnAt(loc)
                EntityType.ZOMBIE -> PvPBot(SkyIslandGenerator.world, youtuber.random()).spawnAt(loc)
                else -> SkyIslandGenerator.world.spawnEntity(loc, type)
            }
        }
    }

    override fun tick(tick: Int) {
        val timeLeft = invincibilityAmount - tick
        if (tick == invincibilityAmount) {
            broadcast("${ChatColor.YELLOW}Die Schutzzeit ist vorbei!")
            startNextPhase()
        } else if (timeLeft.mod(5) == 0 || timeLeft <= 5) {
            broadcast("${ChatColor.GRAY}Die Schutzzeit endet in ${ChatColor.YELLOW}${timeLeft}s")
        }
    }

    override fun nextPhase(): AbstractGamePhase = PvPPhase()
}
