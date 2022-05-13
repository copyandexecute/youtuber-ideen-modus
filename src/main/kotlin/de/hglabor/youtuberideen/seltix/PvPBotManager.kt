package de.hglabor.youtuberideen.seltix

import net.axay.kspigot.event.listen
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import java.util.*
import kotlin.random.Random.Default.nextFloat

object PvPBotManager {
    val botIds = mutableSetOf<UUID>()

    val entityDamageByEntityEvent = listen<EntityDamageByEntityEvent> {
        if (it.entity.uniqueId !in botIds) return@listen
        if (it.damager !is Player) return@listen

        val attacker = it.damager as Player
        val bot = (it.entity as CraftEntity).handle as PvPBot

        if (bot.isBlocking) {
            attacker.playSound(
                attacker.location,
                Sound.ITEM_SHIELD_BLOCK,
                1.0f,
                0.8f + nextFloat() * 0.4f
            )
            bot.blockUsingShield((attacker as CraftPlayer).handle)
            it.isCancelled = true
        }
    }
}
