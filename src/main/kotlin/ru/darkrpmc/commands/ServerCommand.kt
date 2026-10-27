package ru.darkrpmc.commands

import com.google.common.io.ByteStreams
import com.mojang.brigadier.arguments.StringArgumentType.getString
import com.mojang.brigadier.arguments.StringArgumentType.word
import com.mojang.brigadier.exceptions.CommandSyntaxException.BUILT_IN_EXCEPTIONS
import io.papermc.paper.command.brigadier.Commands
import io.papermc.paper.command.brigadier.Commands.argument
import io.papermc.paper.command.brigadier.Commands.literal
import org.bukkit.entity.Player
import ru.darkrpmc.data.config.Settings
import ru.darkrpmc.plugin

@Suppress("UnstableApiUsage")
object ServerCommand {
    fun register(commands: Commands) {
        commands.register(
            literal("server")
                .requires { it.sender is Player }
                .then(argument("сервер", word())
                    .suggests { _, builder ->
                        Settings.servers.forEach(builder::suggest)
                        return@suggests builder.buildFuture()
                    }
                    .executes { ctx ->
                        val server = getString(ctx, "сервер")
                        if (server !in Settings.servers) throw BUILT_IN_EXCEPTIONS.dispatcherUnknownArgument().create()
                        val out = ByteStreams.newDataOutput()
                        out.writeUTF("Connect")
                        out.writeUTF(server)
                        (ctx.source.sender as Player)
                            .sendPluginMessage(plugin, "BungeeCord", out.toByteArray())
                        return@executes 1
                    }
                ).build(),
            "Служит для присоединения к серверам",
            setOf("serv", "s")
        )
    }
}