@file:Suppress("UnstableApiUsage")

package ru.darkrpmc

import org.bukkit.Bukkit
import java.util.logging.Logger

/** Экземпляр плагина */
lateinit var plugin: Lobby

/** Экземпляр логера */
lateinit var logger: Logger

/** Экземпляр сервера */
val server = Bukkit.getServer()

fun setupVars(pl: Lobby) {
    plugin = pl
    logger = pl.logger
}