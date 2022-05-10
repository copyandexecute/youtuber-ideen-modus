package de.hglabor.youtuberideen.seltix

import com.mojang.brigadier.arguments.StringArgumentType
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import org.bukkit.Sound
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftEntity
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntitySpawnEvent
import java.util.*
import kotlin.random.Random.Default.nextFloat

object PvPBotManager {
    private val botIds = mutableSetOf<UUID>()

    init {
        listen<EntityDamageByEntityEvent> {
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

        listen<EntitySpawnEvent> {
            it.isCancelled = it.entityType == EntityType.SLIME
        }

        command("pvpbot") {
            argument("name", StringArgumentType.greedyString()) {
                runs {
                    val name = getArgument<String>("name")
                    val bot = PvPBot(this.world, name).spawnAt(this.player.location)
                    botIds.add(bot.uuid)
                }
            }
        }
    }
}
