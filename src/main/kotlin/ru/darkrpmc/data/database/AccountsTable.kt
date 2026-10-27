package ru.darkrpmc.data.database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

/**
 *  Таблица аккаунтов игроков
 *
 * @property nameColumn Колонка ника
 * @property passwordColumn Колонка пароля. Длина в 476 байта обусловлена тем, что максимальная физически возможная
 * для ввода длина пароля при регистрации = 119 символов, а в UTF-8 каждый символ кодируется до 4 байтами
 * @property regIPColumn Колонка IP на момент регистрации
 * @property lastIPColumn Колонка IP на момент последней авторизации
 * @property lastSeenColumn Колонка момента последней авторизации
 * @property hadModColumn Колонка содержащая информацию о том, использовал ли игрок когда-либо модификацию DRPMC
 * @property firstHWIDColumn Колонка первого запечатлённого HWID
 * @property rankColumn Колонка административной роли
 * @property donateGroupColumn Колонка покупной привилегии
 *
 * @property primaryKey Первичный ключ таблицы. Представлен ником игрока (колонкой [nameColumn])
 */
object AccountsTable : Table() {
    val nameColumn = varchar("name", 16)
    private val passwordColumn = varchar("password", 476)
    private val regIPColumn = varchar("registration_ip", 39)
    val lastIPColumn = varchar("last_ip", 39)
    val lastSeenColumn = datetime("last_seen").defaultExpression(CurrentDateTime)
    private val hadModColumn = bool("had_mod").default(false)
    private val firstHWIDColumn = binary("first_hwid", 8).nullable()
        .check("!(first_hwid ^ had_mod)") { (hadModColumn and it.isNotNull()) or (not(hadModColumn) and it.isNull()) }
    private val rankColumn = integer("rank").default(0)
    private val donateGroupColumn = integer("donate_group").default(0)

    override val primaryKey = PrimaryKey(nameColumn)
    fun selector(name: String) = nameColumn eq name
}