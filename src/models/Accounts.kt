package models

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.transactions.transaction

// Table Object
object Accounts : IntIdTable() {
    val balance = integer("balance")
}

// Database Object DAO
class AccountEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<AccountEntity>(Accounts)

    var balance by Accounts.balance

    private val sentTransactions by TransactionEntity referrersOn Transactions.accountId
    private val receivedTransactions by TransactionEntity referrersOn Transactions.destinationId
    private val users by UserEntity referrersOn Users.accountId

    private val user: UserEntity? get() = users.firstOrNull()

    private val transactions: List<TransactionEntity> get() =
        (sentTransactions + receivedTransactions).sortedBy(TransactionEntity::createdAt)

    private fun transactionsMap(id: Int): List<TransactionResponse> = transaction {
        transactions.map { it.toTransactionResponse(id) }
    }

    override fun toString(): String = "Account($id, $balance, ${ transactions.joinToString( ", " ) { it.toString(id.value) } })"
    fun toAccountResponse() = AccountResponse(id.value, balance, transactionsMap(id.value))
    fun getUserEmail() = user?.email ?: "unknown"
}

// JSON Object DTO
data class AccountResponse(
    val id: Int,
    val balance: Int,
    val transactions: List<TransactionResponse>
)
