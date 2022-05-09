package de.hglabor.youtuberideen.wichtiger

import org.bukkit.Location
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import java.util.*

class VoidGenerator : ChunkGenerator() {
    override fun canSpawn(world: World, x: Int, z: Int): Boolean = true
    override fun getFixedSpawnLocation(world: World, random: Random): Location = Location(world, 0.0, 128.0, 0.0)
}
