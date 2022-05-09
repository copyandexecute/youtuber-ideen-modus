package de.hglabor.youtuberideen.wichtiger

import com.mojang.brigadier.StringReader
import net.axay.kspigot.extensions.geometry.SimpleLocation3D
import net.minecraft.commands.arguments.blocks.BlockStateParser
import net.minecraft.nbt.NbtIo
import net.minecraft.world.level.block.state.BlockState
import java.io.InputStream

object SchematicReader {
    fun parseSchematic(inputStream: InputStream, ignoreAir: Boolean = true): Map<SimpleLocation3D, BlockState> {
        val map = mutableMapOf<SimpleLocation3D, BlockState>()
        val blockPalette = mutableMapOf<Int, BlockState>()

        val nbt = NbtIo.readCompressed(inputStream)
        val width = nbt.getInt("Width")
        val height = nbt.getInt("Height")
        val length = nbt.getInt("Length")
        val blocks = nbt.getByteArray("BlockData")
        val palette = nbt.getCompound("Palette")


        palette.allKeys.forEach { key ->
            val state = BlockStateParser(StringReader(key), true).parse(true).state ?: return@forEach
            blockPalette[palette.getInt(key)] = state
        }

        blocks.indices.forEach { i ->
            val state = blockPalette[blocks[i].toInt()] ?: return@forEach
            if (ignoreAir && state.isAir) return@forEach

            val x = i % (width * length) % width
            val y = i / (width * length)
            val z = i % (width * length) / width

            map[SimpleLocation3D(x, y, z)] = state
        }
        return map
    }
}
