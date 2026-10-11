package com.example.data.model

enum class SportType(val displayName: String) {
    FOOTBALL("Football"),
    BASKETBALL("Basketball"),
    TENNIS("Tennis")
}

enum class MatchStatus {
    LIVE,
    UPCOMING,
    FINISHED
}

enum class EventType {
    GOAL,
    YELLOW_CARD,
    RED_CARD,
    PENALTY,
    CORNER,
    DANGEROUS_ATTACK,
    KICKOFF,
    HALF_TIME,
    FULL_TIME
}

data class MatchEvent(
    val minute: Int,
    val teamName: String,
    val title: String,
    val description: String,
    val type: EventType
)

data class MatchStats(
    val possessionHome: Int = 50,
    val possessionAway: Int = 50,
    val shotsHome: Int = 0,
    val shotsAway: Int = 0,
    val shotsOnTargetHome: Int = 0,
    val shotsOnTargetAway: Int = 0,
    val cornersHome: Int = 0,
    val cornersAway: Int = 0,
    val dangerousAttacksHome: Int = 0,
    val dangerousAttacksAway: Int = 0,
    val yellowCardsHome: Int = 0,
    val yellowCardsAway: Int = 0,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0
)

data class MatchOdds(
    val homeWin: Double,
    val draw: Double,
    val awayWin: Double,
    val over25: Double = 1.85,
    val under25: Double = 1.95,
    val bttsYes: Double = 1.70, // Both teams to score (GG)
    val bttsNo: Double = 2.10,  // NG
    val doubleChance1X: Double = 1.28,
    val doubleChanceX2: Double = 1.45,
    val doubleChance12: Double = 1.30
)

data class Match(
    val id: String,
    val sport: SportType = SportType.FOOTBALL,
    val league: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeScore: Int,
    val awayScore: Int,
    val status: MatchStatus,
    val minute: Int, // e.g., 68'
    val odds: MatchOdds,
    val stats: MatchStats = MatchStats(),
    val events: List<MatchEvent> = emptyList(),
    val pitchStatus: String = "Normal Play", // e.g., "Dangerous Attack", "Corner Kick", "Penalty", "Goal!"
    val attackingTeam: String? = null,
    val streamChannelName: String = "DelajStream HD 1",
    val isStreamAvailable: Boolean = true,
    val isHot: Boolean = false
)
