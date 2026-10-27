package ru.darkrpmc.data.config

import org.bukkit.configuration.ConfigurationSection
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.darkrpmc.data.database.AccountsTable
import ru.darkrpmc.data.database.PerServerDataTable
import ru.darkrpmc.logger
import ru.darkrpmc.plugin
import ru.darkrpmc.server

fun loadConfig() {
    val cfg = plugin.config
    val errors = TreeNode("config")

    if (!cfg.contains("servers")) errors += "Отсутствует список доступных серверов (servers)"
    else {
        if (!cfg.isList("servers"))
            errors += "servers {${cfg.get("servers")!!::class.simpleName}} не является списком"
        else Settings.servers = cfg.getStringList("servers")
    }

    Settings.modVersion =
        cfg.getString("mod-version", errors, default = Settings.modVersion, forceString = true)!!

    /// Сообщения ///

    val mErrors = TreeNode("messages")
    val mSection = cfg.getConfigurationSection("messages", errors)!!

    Settings.Messages.outOfBounds =
        mSection.getString("out-of-bounds", mErrors, default = Settings.Messages.outOfBounds)!!

    if (!mErrors.isEmpty()) errors += mErrors

    /// База данных ///

    val dbProblems = TreeNode("database")
    val dbSection = cfg.getConfigurationSection("database", errors)!!

    val url = dbSection.getString("url", dbProblems, false)
    val user = dbSection.getString("user", dbProblems, false)
    val password = dbSection.getString("password", dbProblems, false)

    if (!dbProblems.isEmpty()) errors += dbProblems

    ///-///

    if (errors.isEmpty()) logger.info("Параметры успешно загружены из файла")
    else logger.severe("Проблемы при чтении файла конфигурации\n$errors")

    if (url != null && user != null && password != null) {
        try {
            Database.connect(url, user = user, password = password)
            transaction { SchemaUtils.create(AccountsTable, PerServerDataTable) }
            return
        } catch (e: ClassNotFoundException) {
            logger.severe("Не удалось подключиться к базе данных: Не найден класс драйвера ${e.message}")
        } catch (e: Exception) {
            logger.severe("Не удалось подключиться к базе данных: $e")
        }
    }

    server.shutdown()
}


/**
 * Получает строковое значение.
 *
 * @param path Путь к строке в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если строка не найдена.
 * @return Строковое значение или `null`, если строка не найдена и не указан [default].
 */
private fun ConfigurationSection.getString(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: String? = null,
    forceString: Boolean = false
): String? {
    val def = default ?: if (useCfgDefault) defaultSection?.getString(path) else null
    return if (!contains(path)) {
        errorContainer += "Отсутствует $path"
        def
    } else if (!(forceString || isString(path))) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является строкой"
        def
    } else getString(path)
}

/**
 * Получает подраздел текущего раздела.
 *
 * @param path Путь к разделу в конфигурации.
 * @param errorContainer Контейнер для сбора ошибок.
 * @param useCfgDefault Если `true`, использовать значения по умолчанию из конфигурации.
 * @param default Значение по умолчанию, если раздел не найден.
 * @return [ConfigurationSection] или `null`, если раздел не найден и не указан [default].
 */
fun ConfigurationSection.getConfigurationSection(
    path: String,
    errorContainer: TreeNode,
    useCfgDefault: Boolean = true,
    default: ConfigurationSection? = null
): ConfigurationSection? {
    val def = default ?: if (useCfgDefault) defaultSection?.getConfigurationSection(path) else null
    return if (!contains(path)) {
        errorContainer += "Отсутствует раздел $path"
        def
    } else if (!isConfigurationSection(path)) {
        errorContainer += "$path {${get(path)!!::class.simpleName}} не является разделом"
        def
    } else getConfigurationSection(path)
}