package de.hglabor.youtuberideen

import de.hglabor.youtuberideen.bastighg.CakeListener
import de.hglabor.youtuberideen.bastighg.SkinChanger
import de.hglabor.youtuberideen.hugo.BearManager
import de.hglabor.youtuberideen.sasukey.LilyPadListener
import de.hglabor.youtuberideen.seltix.PvPBotManager
import de.hglabor.youtuberideen.veto.GolemManager
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import net.axay.kspigot.main.KSpigot

class YoutuberIdeen : KSpigot() {
    companion object {
        lateinit var INSTANCE: YoutuberIdeen
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        SkyIslandGenerator
        GolemManager
        PvPBotManager
        LilyPadListener
        BearManager
        SkinChanger
        CakeListener
        logger.info("The Plugin was enabled!")
    }

    override fun shutdown() {
        logger.info("The Plugin was disabled!")
    }
}

val Manager by lazy { YoutuberIdeen.INSTANCE }

