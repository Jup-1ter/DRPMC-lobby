package ru.darkrpmc

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import org.bukkit.event.Listener
import org.bukkit.plugin.PluginManager
import ru.darkrpmc.commands.ServerCommand
import ru.darkrpmc.data.config.loadConfig

class Lobby : org.bukkit.plugin.java.JavaPlugin() {
    override fun onEnable() {
        setupVars(this)
        server.getWorld("world")!!.isAutoSave = false
        saveDefaultConfig()
        loadConfig()
        server.messenger.registerOutgoingPluginChannel(this, "BungeeCord")
        server.pluginManager.regEvHandlers(MainEvHandler, WorldEvHandler)
        regCommands()
    }

    private fun PluginManager.regEvHandlers(vararg handlers: Listener) =
        handlers.forEach { registerEvents(it, this@Lobby) }

    @Suppress("UnstableApiUsage")
    private fun regCommands() =
        lifecycleManager.registerEventHandler(LifecycleEvents.COMMANDS) { e ->
            ServerCommand.register(e.registrar())
        }
}