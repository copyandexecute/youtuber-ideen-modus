package de.hglabor.youtuberideen.wichtiger

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import de.hglabor.youtuberideen.Manager
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.geometry.SimpleLocation3D
import net.axay.kspigot.extensions.geometry.toSimple
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.event.player.PlayerJoinEvent
import java.io.File
import java.util.logging.Logger
import kotlin.random.Random

object SkyIslandGenerator {
    private val world: World
    private val schematicFolder: File = File("${Manager.dataFolder}/schematics/")
    private val schematics = mutableMapOf<String, Map<SimpleLocation3D, BlockState>>()
    private val placeableBlocks = mutableListOf<PlaceableBlock>()
    private val islandLocations = mutableSetOf<Location>()
    private var MAX_MS_PER_TICK = 20L
    private var ISLAND_DISTANCE = 100

    init {
        Bukkit.getWorld("world_pvp")?.worldFolder?.deleteRecursively()

        world = WorldCreator("world_pvp").generator(VoidGenerator()).createWorld()!!
        world.worldBorder.size = 80 * 2.0

        schematicFolder.mkdirs()

        loadIslandSchematics()

        populate()

        listen<PlayerJoinEvent> {
            it.player.teleport(world.spawnLocation)
        }

        listen<ServerTickStartEvent> {
            val stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK
            while (placeableBlocks.isNotEmpty() && System.currentTimeMillis() <= stopTime) {
                placeableBlocks.removeFirstOrNull()?.setBlockInNativeChunk(world)
                if (placeableBlocks.isEmpty()) {
                    Manager.logger.info("Alle Inseln wurden platziert")
                }
            }
        }
    }

    private fun populate() {
        val size = world.worldBorder.size.toInt() / 2

        repeat(Random.nextInt((50 * size) / 100, (75 * size) / 100)) {
            val pasteLoc = getSafeArenaSpawn()
            val schematic = schematics.values.randomOrNull() ?: return@repeat

            if (!world.worldBorder.isInside(pasteLoc)) return@repeat

            islandLocations.add(pasteLoc)

            schematic.forEach { (loc, state) ->
                val location = pasteLoc.clone().add(loc.x, loc.y, loc.z)
                placeableBlocks.add(PlaceableBlock(location.toSimple(), state))
            }
        }
    }

    private fun loadIslandSchematics() {
        schematicFolder.listFiles { _, name -> name.endsWith(".schem") }.forEach {
            schematics[it.nameWithoutExtension] = SchematicReader.parseSchematic(it.inputStream())
            Manager.logger.info("${it.name} wurde geladen")
        }
    }

    private fun getSafeArenaSpawn(): Location {
        val size = world.worldBorder.size.toInt() / 2
        val x = Random.nextInt(-size, size).toDouble()
        val z = Random.nextInt(-size, size).toDouble()
        val location = Location(world, x, Random.nextInt(100, 200).toDouble(), z)
        return if (islandLocations.any { it.distanceSquared(location) < ISLAND_DISTANCE }) getSafeArenaSpawn() else location
    }

    data class PlaceableBlock(val loc: SimpleLocation3D, val state: BlockState) {

        fun setBlockInNativeWorld(world: World, x: Int, y: Int, z: Int, state: BlockState, applyPhysics: Boolean) {
            val nmsWorld: Level = (world as CraftWorld).handle
            val bp = BlockPos(x, y, z)
            nmsWorld.setBlock(bp, state, if (applyPhysics) 3 else 2)
        }

        fun setBlockInNativeChunk(world: World, applyPhysics: Boolean = false) {
            val nmsWorld: Level = (world as CraftWorld).handle
            val chunk = nmsWorld.getChunk(loc.x.toInt() shr 4, loc.z.toInt() shr 4)
            val bp = BlockPos(loc.x, loc.y, loc.z)
            chunk.setBlockState(bp, state, applyPhysics)
        }
    }
}
