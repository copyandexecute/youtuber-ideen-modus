package de.hglabor.youtuberideen.holzkopf

import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.give
import net.axay.kspigot.items.itemStack
import net.axay.kspigot.runnables.taskRunLater
import net.axay.kspigot.utils.mark
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

object BannerManager {
    val lobby = WorldCreator("world_lobby").type(WorldType.FLAT).createWorld()!!

    val interactEvent = listen<PlayerInteractEvent> {
        if (it.item?.type == Material.LOOM) {
            it.player.openInventory(Bukkit.createInventory(it.player, InventoryType.LOOM))
        } else if (it.item?.type?.name?.endsWith("_BANNER", true) == true) {
            it.player.addBannerAboveHead(it.item!!)
        }
    }
    val joinEvent = listen<PlayerJoinEvent> { event ->
        val player = event.player
        player.inventory.clear()

        player.inventory.addItem(itemStack(Material.LOOM) { mark("lobby") })

        repeat(8) {
            player.inventory.addItem(itemStack(Material.WHITE_BANNER) {
                amount = 16
                mark("lobby")
            })
        }

        Material.values().filter { it.name.endsWith("_BANNER_PATTERN", true) }
            .map { itemStack(it) { mark("lobby") } }.forEach {
                player.give(it)
            }

        Material.values().filter { it.name.endsWith("_DYE", true) }
            .map {
                itemStack(it) {
                    amount = 64
                    mark("lobby")
                }
            }.forEach {
                player.give(it)
            }

        player.teleport(lobby.spawnLocation)

        taskRunLater(20) {
            player.addBannerAboveHead(ItemStack(Material.BLACK_BANNER))
        }
    }

    private fun Player.addBannerAboveHead(banner: ItemStack) {
        passengers.forEach { it.remove() }
        val armorStand = world.spawnEntity(eyeLocation.add(0.0, 0.5, 0.0), EntityType.ARMOR_STAND) as ArmorStand
        armorStand.isSmall = true
        armorStand.isVisible = false
        armorStand.equipment?.helmet = banner
        addPassenger(armorStand)
    }
}
