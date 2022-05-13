package de.hglabor.youtuberideen

import de.hglabor.youtuberideen.game.GamePhaseManager
import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import net.axay.kspigot.main.KSpigot
import org.bukkit.ChatColor

class YoutuberIdeen : KSpigot() {
    companion object {
        lateinit var INSTANCE: YoutuberIdeen
    }

    override fun load() {
        INSTANCE = this
    }

    override fun startup() {
        SkyIslandGenerator
        GamePhaseManager
        logger.info("The Plugin was enabled!")
    }

    override fun shutdown() {
        logger.info("The Plugin was disabled!")
    }
}

val Manager by lazy { YoutuberIdeen.INSTANCE }
val Prefix: String = "${ChatColor.GRAY}[${ChatColor.RED}Youtuber${ChatColor.WHITE}-Ideen${ChatColor.GRAY}]"

