package com.example.data.model

enum class TransactionType(val displayName: String) {
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER_SENT("Transfer Out"),
    TRANSFER_RECEIVED("Transfer In"),
    BET_PLACED("Bet Stake"),
    BET_WON("Bet Win"),
    BET_CASHOUT("Cash Out")
}

enum class TransactionStatus {
    COMPLETED,
    PENDING,
    FAILED
}

enum class PaymentMethod(val displayName: String) {
    BANK_TRANSFER("Instant Bank Transfer"),
    DEBIT_CARD("Debit / Credit Card"),
    USSD("USSD Code"),
    OPAY("OPay Wallet"),
    PALMPAY("PalmPay Wallet"),
    CRYPTO("USDT (TRC20)"),
    DELAJ_P2P("DelajBet P2P Transfer")
}

data class Transaction(
    val id: String,
    val reference: String,
    val timestamp: Long,
    val amount: Double,
    val fee: Double = 0.0,
    val type: TransactionType,
    val status: TransactionStatus,
    val paymentMethod: PaymentMethod,
    val destinationOrSource: String, // e.g. "GTBank •••• 4912" or "To: @akin_king"
    val note: String = ""
)
