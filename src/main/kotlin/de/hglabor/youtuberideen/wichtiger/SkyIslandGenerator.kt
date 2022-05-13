package de.hglabor.youtuberideen.wichtiger

import com.destroystokyo.paper.event.server.ServerTickStartEvent
import de.hglabor.youtuberideen.Manager
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.geometry.SimpleLocation3D
import net.axay.kspigot.extensions.geometry.toSimple
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bukkit.*
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.metadata.FixedMetadataValue
import java.io.File
import kotlin.random.Random

object SkyIslandGenerator {

    interface Workload {
        fun execute()
    }

    val world: World = WorldCreator("world_pvp").generator(VoidGenerator()).createWorld()!!
    private val schematicFolder: File = File("${Manager.dataFolder}/schematics/")
    private val schematics = mutableMapOf<String, Map<SimpleLocation3D, BlockState>>()
    val placeableBlocks = mutableListOf<Workload>()
    private val islandLocations = mutableSetOf<Location>()
    private var MAX_MS_PER_TICK = 20L
    private var ISLAND_DISTANCE = 100
    val spawnLocations = mutableSetOf<SimpleLocation3D>()
    var MIN_Y_ISLAND = 100

    init {
        world.worldBorder.size = 80 * 2.0
        world.time = 1200
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false)

        schematicFolder.mkdirs()

        loadIslandSchematics()

        populate()

        listen<ServerTickStartEvent> {
            val stopTime = System.currentTimeMillis() + MAX_MS_PER_TICK
            while (placeableBlocks.isNotEmpty() && System.currentTimeMillis() <= stopTime) {
                val workload = placeableBlocks.removeFirstOrNull()
                workload?.execute()
                if (workload is PlaceableBlock) {
                    if (workload.state.bukkitMaterial == Material.BEDROCK) {
                        spawnLocations.add(workload.loc)
                    } else if (workload.state.bukkitMaterial == Material.CHEST) {
                        world.getBlockAt(workload.loc.toLocation(world))
                            .setMetadata("lootchest", FixedMetadataValue(Manager, null))
                    }
                }
                if (placeableBlocks.isEmpty()) {
                    //Manager.logger.info("Alle Inseln wurden platziert")
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
                placeableBlocks.add(PlaceableBlock(world, location.toSimple(), state))
            }
        }
    }

    private fun loadIslandSchematics() {
        schematicFolder.listFiles { _, name -> name.endsWith(".schem") }?.forEach {
            schematics[it.nameWithoutExtension] = SchematicReader.parseSchematic(it.inputStream())
            Manager.logger.info("${it.name} wurde geladen")
        }
    }

    private fun getSafeArenaSpawn(): Location {
        val size = world.worldBorder.size.toInt() / 2
        val x = Random.nextInt(-size, size).toDouble()
        val z = Random.nextInt(-size, size).toDouble()
        val location = Location(world, x, Random.nextInt(MIN_Y_ISLAND, 200).toDouble(), z)
        return if (islandLocations.any { it.distanceSquared(location) < ISLAND_DISTANCE }) getSafeArenaSpawn() else location
    }

    fun SimpleLocation3D.toLocation(world: World): Location = Location(world, x, y, z)

    data class PlaceableBlock(val world: World, val loc: SimpleLocation3D, val state: BlockState, val method: Int = 1) :
        Workload {
        private fun setBlockInNativeChunk(world: World, applyPhysics: Boolean = false) {
            val nmsWorld: Level = (world as CraftWorld).handle
            val chunk = nmsWorld.getChunk(loc.x.toInt() shr 4, loc.z.toInt() shr 4)
            val bp = BlockPos(loc.x, loc.y, loc.z)
            chunk.setBlockState(bp, state, applyPhysics)
        }

        private fun setBlockInNativeWorld(world: World, applyPhysics: Boolean = false) {
            val nmsWorld: Level = (world as CraftWorld).handle
            val bp = BlockPos(loc.x, loc.y, loc.z)
            nmsWorld.setBlock(bp, state, if (applyPhysics) 3 else 2)
        }

        override fun execute() {
            when (method) {
                1 -> setBlockInNativeChunk(world)
                2 -> setBlockInNativeWorld(world)
            }
        }
    }
}
