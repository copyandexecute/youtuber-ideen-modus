package de.hglabor.youtuberideen.wichtiger

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.generator.ChunkGenerator
import org.bukkit.generator.WorldInfo
import java.util.*

class VoidGenerator : ChunkGenerator() {
    override fun generateBedrock(worldInfo: WorldInfo, random: Random, xPos: Int, zPos: Int, chunkData: ChunkData) {
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in worldInfo.minHeight..SkyIslandGenerator.MIN_Y_ISLAND / 2) {
                    chunkData.setBlock(x, y, z, Material.LAVA)
                }
            }
        }
    }

    override fun canSpawn(world: World, x: Int, z: Int): Boolean = true
    override fun getFixedSpawnLocation(world: World, random: Random): Location = Location(world, 0.0, 128.0, 0.0)
}
