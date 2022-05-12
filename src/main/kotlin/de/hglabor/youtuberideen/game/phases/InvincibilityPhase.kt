package de.hglabor.youtuberideen.game.phases

import de.hglabor.youtuberideen.einfachgustaf.PlayerHider
import de.hglabor.youtuberideen.game.AbstractGamePhase
import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.sasukey.LilyPadManager

class InvincibilityPhase : AbstractGamePhase(GamePhaseManager) {
    init {
        gamePhaseManager.resetTimer()
        PlayerHider
        listeners += PlayerHider.playerMoveEvent
        listeners += BearManager.flameEvent
        listeners += LilyPadManager.interactEvent
    }

    override fun tick(tick: Int) {
        TODO("Not yet implemented")
    }

    override fun nextPhase(): AbstractGamePhase = PvPPhase()
}