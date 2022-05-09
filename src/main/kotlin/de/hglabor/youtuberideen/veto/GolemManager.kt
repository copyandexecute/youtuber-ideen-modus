package de.hglabor.youtuberideen.veto

import de.hglabor.youtuberideen.Manager
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import org.bukkit.event.player.PlayerJoinEvent

//Buuuutz digga
object GolemManager {
    init {
        listen<PlayerJoinEvent> {
            //Purpur hahahahah YOOOO
            it.player.addAttachment(Manager, "allow.ride.iron_golem", true)
        }

        command("flyinggolem") {
            runs {
                FlyingIronGolem(this.world).spawnAt(this.player.location)
            }
        }
    }
}
