package ru.darkrpmc

import io.papermc.paper.event.player.AsyncChatEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.DARK_AQUA
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.data.database.AccountsTable.selector

@Suppress("unused")
object MainEvHandler : Listener {
    private val serverKey = NamespacedKey(plugin, "server")
    private val serverItem = ItemStack.of(Material.HEART_OF_THE_SEA).apply { editMeta {
        it.itemName(Component.text("Сервер").color(DARK_AQUA))
        it.persistentDataContainer.set(serverKey, PersistentDataType.STRING, "server")
    }}

    @EventHandler(ignoreCancelled = true)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        event.joinMessage(null)
        val player = event.player
        server.onlinePlayers.forEach {
            if (it != player) {
                player.hidePlayer(plugin, it)
                it.hidePlayer(plugin, player)
            }
        }
        val inv = player.inventory
        inv.setItem(22, serverItem)
        inv.setItem(4, serverItem)
    }

    @EventHandler(ignoreCancelled = true)
    fun onPlayerLeave(event: PlayerQuitEvent) {
        event.quitMessage(null)
        val player = event.player
        transaction {
            AccountsTable.update({ selector(player.name) }) {
                it[lastIPColumn] = player.address!!.hostString
                it[lastSeenColumn] = CurrentDateTime
            }
        }
    }

    @EventHandler
    fun onPlayerChat(event: AsyncChatEvent) {
        if (!event.message().toString().startsWith('/')) event.isCancelled = true
    }

    @EventHandler
    fun onInvClick(event: InventoryClickEvent) {
        event.isCancelled = true
        val player = (event.whoClicked as? Player) ?: return
        val meta = (event.currentItem ?: return).itemMeta
        if (meta != null) {
            player.performCommand(
                "server ${meta.persistentDataContainer.get(serverKey, PersistentDataType.STRING) ?: return}"
            )
        }
    }

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (event.action == Action.PHYSICAL) return
        val meta = (event.item ?: return).itemMeta
        if (meta != null) {
            event.player.performCommand(
                "server ${meta.persistentDataContainer.get(serverKey, PersistentDataType.STRING) ?: return}"
            )
        }
    }
}