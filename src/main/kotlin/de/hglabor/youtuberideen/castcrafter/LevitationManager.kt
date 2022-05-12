package de.hglabor.youtuberideen.castcrafter

import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import net.axay.kspigot.utils.hasMark
import net.axay.kspigot.utils.mark
import org.bukkit.Material
import org.bukkit.entity.FallingBlock
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.util.Vector
import org.spigotmc.event.entity.EntityDismountEvent

object LevitationManager {
    val moveEvent = listen<PlayerMoveEvent> {
        val block = it.player.location.clone().add(0.0, -1.0, 0.0).block
        if (block.type.hasGravity()) {
            val data = block.blockData.clone()
            block.type = Material.AIR

            val fallingBlock = block.world.spawnFallingBlock(block.location.clone().add(0.5, 0.0, 0.5), data)
            fallingBlock.mark("castcrafter")
            fallingBlock.addPassenger(it.player)
            task(period = 1) { runnable ->
                if (fallingBlock.passengers.isEmpty()) {
                    runnable.cancel()
                    return@task
                }
                fallingBlock.velocity = Vector(0, 1, 0)
            }
        }
    }
    val dismountEvent = listen<EntityDismountEvent> {
        if (it.dismounted is FallingBlock && it.dismounted.hasMark("castcrafter")) {
            val fallingBlock = it.dismounted as FallingBlock
            fallingBlock.remove()
            val realBlock = fallingBlock.location.clone().add(0.0, -2.0, 0.0).block
            realBlock.setType(Material.GRASS_BLOCK, false)
            realBlock.world.playSound(
                realBlock.location,
                Material.GRASS_BLOCK.createBlockData().soundGroup.placeSound,
                1f,
                1f
            )
        }
    }
}
