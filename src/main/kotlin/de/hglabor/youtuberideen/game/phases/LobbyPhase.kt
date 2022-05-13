package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.bastighg.SkinChanger
import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.holzkopf.BannerManager
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.veto.GolemManager
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.requiresPermission
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.worlds
import org.bukkit.*
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class LobbyPhase : AbstractGamePhase(GamePhaseManager) {
    private val lobby: World = WorldCreator("world_lobby").type(WorldType.FLAT).createWorld()!!

    init {
        lobby.apply {
            setGameRule(GameRule.DO_MOB_SPAWNING, false)
            setGameRule(GameRule.DO_WEATHER_CYCLE, false)
            setGameRule(GameRule.DISABLE_RAIDS, true)
            setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            setGameRule(GameRule.DO_TRADER_SPAWNING, false)
            setGameRule(GameRule.DO_PATROL_SPAWNING, false)
        }
        lobby.worldBorder.size = 30 * 2.0
        worlds.forEach {
            it.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
            it.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
        }
        listeners += SkinChanger.playerJoinEvent
        listeners += listen<BlockBreakEvent> { it.isCancelled = true }
        listeners += listen<BlockPlaceEvent> { it.isCancelled = true }
        listeners += listen<EntityDamageEvent> { it.isCancelled = true }
        listeners += listen<FoodLevelChangeEvent> { it.isCancelled = true }
        listeners += BannerManager.joinEvent
        listeners += BannerManager.interactEvent
        listeners += BearManager.joinEvent()
        listeners += GolemManager.joinEvent
        listeners += listen<PlayerJoinEvent> {
            it.joinMessage = null
            broadcast("${ChatColor.GREEN}${it.player.name} hat das Spiel betreten.")
        }
        listeners += listen<PlayerQuitEvent> {
            it.quitMessage = null
            broadcast("${ChatColor.RED}${it.player.name} hat das Spiel verlassen.")
        }
        command("start") {
            requiresPermission("hglabor.admin")
            runs {
                startNextPhase()
            }
        }
    }

    override fun tick(tick: Int) {
        gamePhaseManager.resetTimer()
    }

    override fun nextPhase(): AbstractGamePhase = SpawnPhase()
}
