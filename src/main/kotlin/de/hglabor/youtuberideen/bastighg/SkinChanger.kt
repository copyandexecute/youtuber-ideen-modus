package de.hglabor.youtuberideen.bastighg

import com.mojang.authlib.properties.Property
import com.mojang.brigadier.arguments.StringArgumentType
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.appear
import net.axay.kspigot.extensions.bukkit.disappear
import net.axay.kspigot.runnables.async
import net.axay.kspigot.runnables.sync
import net.minecraft.core.Holder
import net.minecraft.network.protocol.game.ClientboundPlayerInfoPacket
import net.minecraft.network.protocol.game.ClientboundRespawnPacket
import net.minecraft.world.level.GameType
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.ContentType
import org.apache.http.entity.mime.MultipartEntityBuilder
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.util.EntityUtils
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.awt.color.ColorSpace
import java.awt.image.BufferedImage
import java.awt.image.ColorConvertOp
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import javax.imageio.ImageIO


object SkinChanger {
    private val skins = mutableMapOf<UUID, JSONObject>()

    init {
        listen<PlayerJoinEvent> {
            if (skins.containsKey(it.player.uniqueId)) {
                val textureObject = skins[it.player.uniqueId]!!
                it.player.changeSkin(textureObject["value"] as String, textureObject["signature"] as String)
            } else {
                async {
                    downloadAndChangeSkin(it.player, it.player.name)
                }
            }
        }

    }

    //jajajja blabla async future dings das chillt mal
    private fun downloadAndChangeSkin(player: Player, name: String) {
        runCatching {
            val image = ImageIO.read(URL("https://mineskin.eu/download/${name}"))
            val op = ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null)
            op.filter(image, image)
            val uploadedSkin = mineskinUpload(image)
            val textureObject = (uploadedSkin["data"] as JSONObject)["texture"] as JSONObject
            skins[player.uniqueId] = textureObject
            sync {
                player.changeSkin(textureObject["value"] as String, textureObject["signature"] as String)
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    private fun Player.changeSkin(value: String, signature: String) {
        val loc = location
        val profile = (this as CraftPlayer).handle.gameProfile
        val world = (world as CraftWorld).handle
        profile.properties.removeAll("textures")
        profile.properties.put("textures", Property("textures", value, signature))
        handle.connection.apply {
            send(ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.REMOVE_PLAYER, handle))
            send(ClientboundPlayerInfoPacket(ClientboundPlayerInfoPacket.Action.ADD_PLAYER, handle))
            send(
                ClientboundRespawnPacket(
                    Holder.Direct(world.dimensionType()),
                    world.dimension(),
                    world.seed,
                    GameType.byId(gameMode.value),
                    GameType.byId(gameMode.value),
                    false,
                    false,
                    false
                )
            )
        }
        updateInventory()
        teleport(loc)
        disappear()
        appear()
    }

    //danke an https://www.spigotmc.org/threads/small-util-class-for-uploading-skins-to-mineskin.406061/
    private fun mineskinUpload(skinImage: BufferedImage): JSONObject {
        val imageBuffer: ByteArray
        ByteArrayOutputStream().use { outputStream ->
            ImageIO.write(skinImage, "png", outputStream)
            outputStream.flush()
            imageBuffer = outputStream.toByteArray()
        }
        val client = HttpClientBuilder.create().build()
        val post = HttpPost("https://api.mineskin.org/generate/upload?visibility=1")

        post.entity =
            MultipartEntityBuilder.create().addBinaryBody("file", imageBuffer, ContentType.IMAGE_PNG, "").build()

        // Execute the POST request and parse the result as a JSON object
        val jsonObject = JSONParser().parse(EntityUtils.toString(client.execute(post).entity)) as JSONObject
        client.close()
        return jsonObject
    }
}
