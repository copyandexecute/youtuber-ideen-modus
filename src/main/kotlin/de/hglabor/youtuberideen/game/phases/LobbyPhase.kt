package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.bastighg.SkinChanger
import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.holzkopf.BannerManager
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.veto.GolemManager
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.worlds
import org.bukkit.GameRule
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent

class LobbyPhase : AbstractGamePhase(GamePhaseManager) {
    private val lobby: World = WorldCreator("world_lobby").type(WorldType.FLAT).createWorld()!!

    init {
        lobby.worldBorder.size = 30 * 2.0
        worlds.forEach {
            it.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
        }
        listeners += SkinChanger.playerJoinEvent
        listeners += listen<BlockBreakEvent> { it.isCancelled = true }
        listeners += listen<BlockPlaceEvent> { it.isCancelled = true }
        listeners += listen<EntityDamageEvent> { it.isCancelled = true }
        listeners += listen<FoodLevelChangeEvent> { it.isCancelled = true }
        listeners += BannerManager.joinEvent
        listeners += BannerManager.interactEvent
        listeners += BearManager.joinEvent
        listeners += GolemManager.joinEvent
    }

    override fun tick(tick: Int) {
        gamePhaseManager.resetTimer()
    }

    override fun nextPhase(): AbstractGamePhase = SpawnPhase()
}