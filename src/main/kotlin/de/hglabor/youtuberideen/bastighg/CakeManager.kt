package de.hglabor.youtuberideen.bastighg

import de.hglabor.youtuberideen.game.mechanic.LootTables
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import net.axay.kspigot.runnables.taskRunLater
import net.axay.kspigot.utils.addEffect
import net.axay.kspigot.utils.editMeta
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.entity.EntityType
import org.bukkit.entity.Firework
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import kotlin.random.Random

object CakeManager {
    fun winEvent(callBack: (Player) -> (Unit)) = listen<BlockPlaceEvent> {
        val block = it.block
        if (block.type != Material.CAKE) return@listen
        if (block.getRelative(BlockFace.DOWN, 1).type != Material.CAKE) return@listen
        if (block.getRelative(BlockFace.DOWN, 2).type != Material.CAKE) return@listen
        celebrateWinner(block.location)
        callBack.invoke(it.player)
    }

    fun blockBreakEvent() = listen<BlockBreakEvent> {
        val block = it.block
        if (block.type == Material.CAKE) {
            getRandomCakeIngridients().forEach { item -> block.world.dropItem(block.location, item) }
        }
    }

    fun getRandomCakeIngridients() =
        LootTables.cakeIngridients.shuffled().take(Random.nextInt(0, LootTables.cakeIngridients.size))

    private fun celebrateWinner(location: Location) {
        task(howOften = 3, period = 5) {
            val fireWork = location.world!!.spawnEntity(location, EntityType.FIREWORK) as Firework
            fireWork.editMeta {
                this.power = 2
                this.addEffect {
                    withTrail()
                    withColor(Color.AQUA)
                    flicker(true)
                }
            }
            taskRunLater(20) { fireWork.detonate() }
        }
    }
}
