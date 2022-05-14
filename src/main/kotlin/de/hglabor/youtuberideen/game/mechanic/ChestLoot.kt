package de.hglabor.youtuberideen.game.mechanic

import de.hglabor.youtuberideen.Manager
import net.axay.kspigot.event.listen
import org.bukkit.block.Chest
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import kotlin.random.Random

object ChestLoot {
    fun playerOpenChestEvent() = listen<PlayerInteractEvent> { event ->
        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return@listen
        }
        val block = event.clickedBlock ?: return@listen
        if (block.hasMetadata("lootgenerated")) return@listen
        if (!block.hasMetadata("lootchest")) return@listen

        if (block.state is Chest) {
            val chest = block.state as Chest
            repeat(26) { slot ->
                if (Random.nextInt(1, 100) >= 30) {
                    val probability = Random.nextInt(1, 100)
                    val items = LootTables.normal.filter { it.probability >= probability }.shuffled()
                    val lootItem = items.random()
                    val item = lootItem.itemStack
                    item.amount = Random.nextInt(lootItem.minAmount, lootItem.maxAmount)
                    chest.inventory.setItem(slot, item)
                }
            }
            block.setMetadata("lootgenerated", FixedMetadataValue(Manager, null))
        }
    }
}
