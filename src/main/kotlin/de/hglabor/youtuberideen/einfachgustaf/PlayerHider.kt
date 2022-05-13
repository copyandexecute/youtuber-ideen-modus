package de.hglabor.youtuberideen.einfachgustaf

import de.hglabor.youtuberideen.game.GamePhaseManager.sendMsg
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.appear
import net.axay.kspigot.extensions.bukkit.disappear
import net.axay.kspigot.extensions.geometry.blockLoc
import net.axay.kspigot.extensions.onlinePlayers
import net.axay.kspigot.runnables.task
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerMoveEvent
import java.util.*

object PlayerHider {
    private val prevPositions = mutableMapOf<UUID, Location>()
    private val afkCounter = mutableMapOf<UUID, Int>()
    private val fakeBlocks = mutableMapOf<UUID, FakeBlock>()
    private const val AFK_TIME = 4

    init {
        task(period = 20) {
            onlinePlayers.filter { it.gameMode == GameMode.SURVIVAL }.forEach {
                val item = it.inventory.itemInMainHand
                if (prevPositions[it.uniqueId] != it.location.blockLoc || (!item.type.isBlock || item.type.isAir)) {
                    prevPositions[it.uniqueId] = it.location.blockLoc
                    afkCounter[it.uniqueId] = 0
                } else {
                    val counter = afkCounter.computeIfAbsent(it.uniqueId) { 0 }
                    afkCounter[it.uniqueId] = Math.min(AFK_TIME, counter + 1)
                }
                it.hideAsBlock(afkCounter[it.uniqueId]!!)
            }
        }
    }

    fun playerMoveEvent() = listen<PlayerMoveEvent> {
        val fakeBlock = fakeBlocks[it.player.uniqueId] ?: return@listen
        if (it.to.blockLoc != prevPositions[it.player.uniqueId]) {
            fakeBlock.passengers.forEach { entity -> entity.discard() }
            fakeBlock.discard()
            it.player.world.playSound(it.player.location, fakeBlock.blockData.soundGroup.breakSound, 1f, 1f)
            it.player.world.playEffect(
                it.to.blockLoc.add(0.0, 0.5, 0.0),
                Effect.STEP_SOUND,
                fakeBlock.blockData.material
            )
            fakeBlocks.remove(it.player.uniqueId)
            it.player.appear()
            it.player.sendMsg("${ChatColor.YELLOW}Sichtbar!")
        }
    }

    private fun Player.hideAsBlock(afkTime: Int) {
        val item = inventory.itemInMainHand
        if (!item.type.isBlock) return
        if (item.type.isAir) return

        if (afkTime == AFK_TIME) {
            if (!fakeBlocks.containsKey(uniqueId)) {
                fakeBlocks[uniqueId] =
                    FakeBlock(this.world, item.type.createBlockData()).spawnAt(location.blockLoc.add(0.5, 0.0, 0.5))
                playSound(location, item.type.createBlockData().soundGroup.placeSound, 1f, 1f)
                sendMsg("${ChatColor.YELLOW}Getarnt!")
                disappear()
            }
        } else if (afkTime > 0) {
            sendMsg("${ChatColor.GRAY}Du musst noch ${ChatColor.YELLOW}${AFK_TIME - afkTime}s ${ChatColor.GRAY}stehenbleiben, um dich als Block zu tarnen")
        }
    }
}
