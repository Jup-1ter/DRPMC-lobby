package ru.darkrpmc

import com.destroystokyo.paper.profile.PlayerProfile
import org.bukkit.BanEntry
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerMoveEvent
import ru.darkrpmc.data.config.Settings
import java.util.Date

@Suppress("Unused")
object WorldEvHandler : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onPlayerMove(event: PlayerMoveEvent) {
        val to = event.to
        if (to.x !in 6.0..11.0 || to.y !in .0..2.0 || to.z !in 6.0..11.0) {
            event.player.ban<BanEntry<in PlayerProfile>>(Settings.Messages.outOfBounds, null as Date?, "Анти-чит")
            event.player.banIp(Settings.Messages.outOfBounds, null as Date?, "Анти-чит", true)
        }
    }
}