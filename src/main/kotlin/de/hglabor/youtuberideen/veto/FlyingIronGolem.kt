package de.hglabor.youtuberideen.veto

import de.hglabor.youtuberideen.Manager
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.control.MoveControl
import net.minecraft.world.entity.animal.IronGolem
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld

class FlyingIronGolem(world: World) : IronGolem(EntityType.IRON_GOLEM, (world as CraftWorld).handle) {
    init {
        //es wird immer geiler
        kotlin.runCatching {
            this.moveControl = Class.forName("org.purpurmc.purpur.controller.FlyingWithSpacebarMoveControllerWASD")
                .constructors
                .find { it.parameterCount == 1 }!!
                .newInstance(this) as MoveControl
        }.onFailure {
            Manager.logger.warning("Du benötigst Purpur als Server.jar")
        }
    }

    fun spawnAt(location: Location): FlyingIronGolem {
        level.addFreshEntity(this)
        bukkitEntity.teleport(location)
        return this
    }
}
