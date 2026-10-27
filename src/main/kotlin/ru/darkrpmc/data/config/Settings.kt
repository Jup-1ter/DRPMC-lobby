package ru.darkrpmc.data.config

/** Настройки плагина */
object Settings {
    /** Список серверов к которым может подключиться игрок */
    var servers = emptyList<String>()

    var modVersion = "1"

    object Messages {
        var outOfBounds = "Использование читов"
    }
}