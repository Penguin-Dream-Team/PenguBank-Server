package club.pengubank.models

import org.ktorm.schema.Table
import org.ktorm.schema.int
import org.ktorm.schema.timestamp
import org.ktorm.schema.varchar

object User : Table<Nothing>("users") {
    val id = int("id").primaryKey()
    val email = varchar("email")
    val password = varchar("password")
    val registered_at = timestamp("registered_at")
}