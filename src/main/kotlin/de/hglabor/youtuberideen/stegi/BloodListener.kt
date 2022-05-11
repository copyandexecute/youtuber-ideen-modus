package de.hglabor.youtuberideen.stegi

import de.hglabor.youtuberideen.Manager
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*
import kotlin.random.Random

object BloodListener {
    init {
        listen<EntityDamageByEntityEvent> { event ->
            if (event.damager !is Player) return@listen
            val player = event.damager as Player
            if (player.inventory.itemInMainHand.type.name.endsWith("_SWORD") ||
                player.inventory.itemInMainHand.type.name.endsWith("_AXE")
            ) {
                event.entity.world.playEffect(event.entity.location, Effect.STEP_SOUND, Material.REDSTONE_BLOCK)
                event.entity.getNearbyBlocks(2)
                    .filter { it.getRelative(BlockFace.UP).type.isAir && it.type.isSolid }
                    .shuffled()
                    .take(Random.nextInt(0, 5))
                    .forEach {
                        val above = it.getRelative(BlockFace.UP)
                        above.type = Material.REDSTONE_WIRE
                        above.setMetadata("blood", FixedMetadataValue(Manager, event.entity.uniqueId))
                        taskRunLater(20 * Random.nextLong(1, 6)) {
                            above.removeMetadata("blood", Manager)
                            if (above.type == Material.REDSTONE_WIRE) {
                                above.type = Material.AIR
                            }
                        }
                    }
            }
        }
        listen<PlayerMoveEvent> {
            val to = it.to?.clone()?.add(0.0, 0.0, 0.0) ?: return@listen
            if (to.block.hasMetadata("blood") && to.block.type == Material.REDSTONE_WIRE) {
                val uuid = to.block.getMetadata("blood").first().value() as UUID
                if (uuid != it.player.uniqueId) {
                    it.player.addPotionEffect(PotionEffect(PotionEffectType.POISON, 20, 1))
                }
            }
        }
    }

    private fun Entity.getNearbyBlocks(radius: Int): MutableList<Block> {
        val toReturn = mutableListOf<Block>()

        val px = location.blockX
        val py = location.blockY
        val pz = location.blockZ

        for (x in px - radius until px + radius) {
            for (y in py - radius until py + radius) {
                for (z in pz - radius until pz + radius) {
                    toReturn.add(world.getBlockAt(x, y, z))
                }
            }
        }

        return toReturn
    }
}
