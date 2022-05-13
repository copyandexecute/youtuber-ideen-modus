package de.hglabor.youtuberideen.hugo

import de.hglabor.youtuberideen.Manager
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.task
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.util.BlockIterator
import org.bukkit.util.Vector
import kotlin.random.Random

object BearManager {
    fun joinEvent() = listen<PlayerJoinEvent> {
        //Purpur hahahahah YOOOO
        it.player.addAttachment(Manager, "allow.ride.polar_bear", true)
        it.player.addAttachment(Manager, "allow.ride.panda", true)
    }
    fun flameEvent() = listen<PlayerInteractEvent> {
        //TODO add cooldown...
        if (it.player.isInsideVehicle && (it.player.vehicle?.type == EntityType.POLAR_BEAR || it.player.vehicle?.type == EntityType.PANDA)) {
            it.player.shootFlames()
        }
    }

    private fun LivingEntity.shootFlames() {
        task(howOften = 10, period = 1) {
            val playerDirection = location.direction
            val particleVector = playerDirection.clone()
            playerDirection.multiply(8)
            val x = particleVector.x
            particleVector.x = -particleVector.z
            particleVector.z = x
            particleVector.divide(Vector(3, 3, 3))
            val particleLocation = particleVector.toLocation(world).add(location).add(0.0, 1.05, 0.0)

            world.playSound(location, Sound.ENTITY_BLAZE_SHOOT, 0.6f, 0.65f + Random.nextFloat() * 0.4f)

            for (i in 0..10) {
                this.shootSingleFlame(playerDirection, particleLocation)
            }

            runCatching {
                BlockIterator(this, 10).forEach { block ->
                    world.getNearbyEntities(block.location, 3.0, 3.0, 3.0)
                        .filterIsInstance<LivingEntity>()
                        .filter { hasLineOfSight(it) }
                        .filter { it != this }
                        .filter { it != vehicle }
                        .forEach {
                            it.fireTicks = 40
                        }
                }
            }
        }
    }

    private fun LivingEntity.shootSingleFlame(playerDirection: Vector, particleLocation: Location) {
        val particlePath = playerDirection.clone()
        particlePath.add(
            Vector(
                Math.random() - Math.random(),
                Math.random() - Math.random(),
                Math.random() - Math.random()
            )
        )
        val offsetLocation: Location = particlePath.toLocation(world)
        world.spawnParticle(
            Particle.FLAME,
            particleLocation,
            0,
            offsetLocation.x,
            offsetLocation.y,
            offsetLocation.z,
            0.1
        )
    }
}
