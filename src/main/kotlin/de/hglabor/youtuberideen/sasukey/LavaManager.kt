package de.hglabor.youtuberideen.sasukey

import de.hglabor.youtuberideen.wichtiger.SkyIslandGenerator
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.events.isRightClick
import net.axay.kspigot.extensions.geometry.SimpleLocation3D
import net.minecraft.world.level.block.Blocks
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.player.PlayerInteractEvent

object LavaManager {
    val defaultLavaLevel = SkyIslandGenerator.MIN_Y_ISLAND / 2
    var currentLavaLevel = defaultLavaLevel

    fun interactEvent() = listen<PlayerInteractEvent> {
        if (it.item?.type != Material.LILY_PAD) return@listen
        if (!it.action.isRightClick) return@listen

        val targetBlock = it.player.getTargetBlockExact(5, FluidCollisionMode.SOURCE_ONLY)

        if (targetBlock?.type == Material.LAVA) {
            val spaceAbove = targetBlock.getRelative(BlockFace.UP)
            spaceAbove.setType(Material.LILY_PAD, false)
            spaceAbove.world.playSound(
                spaceAbove.location,
                Material.LILY_PAD.createBlockData().soundGroup.placeSound,
                1f,
                1f
            )
        }
    }

    fun riseLava(howMuch: Int = 1) {
        val size = SkyIslandGenerator.world.worldBorder.size.toInt() / 2
        for (x in -size until size) {
            for (z in -size until size) {
                repeat(howMuch) {
                    val y = currentLavaLevel + it
                    val state = SkyIslandGenerator.world.getBlockState(x, y, z)
                    if (state.type == Material.LAVA) return@repeat
                    if (state.type.isAir) {
                        SkyIslandGenerator.placeableBlocks.add(SkyIslandGenerator.PlaceableBlock(
                            SkyIslandGenerator.world,
                            SimpleLocation3D(x, y, z),
                            Blocks.LAVA.defaultBlockState(), 2
                        ))
                    }
                }
            }
        }
        currentLavaLevel += howMuch
    }
}
