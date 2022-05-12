package de.hglabor.youtuberideen.sasukey

import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.events.isRightClick
import org.bukkit.FluidCollisionMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.player.PlayerInteractEvent

object LilyPadManager {
    val interactEvent = listen<PlayerInteractEvent> {
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
}
