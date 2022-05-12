package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.bastighg.CakeManager
import de.hglabor.youtuberideen.castcrafter.LevitationManager
import de.hglabor.youtuberideen.einfachgustaf.PlayerHider
import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.sasukey.LilyPadManager
import de.hglabor.youtuberideen.seltix.PvPBotManager
import de.hglabor.youtuberideen.stegi.BloodManager
import org.bukkit.entity.Player

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
        listeners += PlayerHider.playerMoveEvent
        listeners += BearManager.flameEvent
        listeners += LilyPadManager.interactEvent
        listeners += PvPBotManager.entityDamageByEntityEvent
    }

    override fun tick(tick: Int) {
        TODO("Not yet implemented")
    }

    override fun nextPhase(): AbstractGamePhase = EndPhase(winner)
}