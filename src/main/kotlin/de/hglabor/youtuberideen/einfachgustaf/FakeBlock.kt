package de.hglabor.youtuberideen.einfachgustaf

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.item.FallingBlockEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.data.BlockData
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class FakeBlock(world: World, val blockData: BlockData) :
    ArmorStand(EntityType.ARMOR_STAND, (world as CraftWorld).handle) {

    init {
        isMarker = true
        isNoGravity = true
        isSmall = true
        isSilent = true
        isInvulnerable = true
        isInvisible = true
    }

    fun spawnAt(location: Location): FakeBlock {
        level.addFreshEntity(this)
        bukkitLivingEntity.addPotionEffect(PotionEffect(PotionEffectType.INVISIBILITY, Int.MAX_VALUE, 10, false, false))
        bukkitEntity.teleport(location)

        val fallingBlock = CustomFallingBlock(level, bukkitLivingEntity.location, (blockData as CraftBlockData).state)
        this.level.addFreshEntity(fallingBlock, CreatureSpawnEvent.SpawnReason.CUSTOM)

        fallingBlock.startRiding(this)

        return this
    }
}

class CustomFallingBlock(level: Level, loc: Location, state: BlockState) :
    FallingBlockEntity(level, loc.x, loc.y, loc.z, state) {

    init {
        dropItem = false
        isInvulnerable = true
        time = 1
        hurtEntities = false
        isInvisible = true
    }

    override fun tick() {
    }
}
