package com.example.data.model

data class BetSelection(
    val matchId: String,
    val matchTitle: String, // e.g. "Arsenal vs Chelsea"
    val league: String,
    val marketName: String, // e.g. "1X2", "Over/Under 2.5", "GG/NG"
    val selectionName: String, // e.g. "Arsenal", "Over 2.5", "Yes"
    val odds: Double
)

enum class BetStatus {
    RUNNING,
    WON,
    LOST,
    CASHED_OUT
}

data class PlacedBet(
    val id: String,
    val timestamp: Long,
    val selections: List<BetSelection>,
    val stake: Double,
    val totalOdds: Double,
    val bonusRate: Double, // e.g., 0.10 for 10% Acca bonus
    val bonusAmount: Double,
    val potentialPayout: Double,
    val status: BetStatus,
    val cashOutValue: Double? = null,
    val settledTime: Long? = null
)
