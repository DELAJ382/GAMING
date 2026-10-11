package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class DelajBetRepository private constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)

    // User Profile & Wallet State
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    // Matches List State
    private val _matches = MutableStateFlow<List<Match>>(emptyList())
    val matches = _matches.asStateFlow()

    // Bet Slip State
    private val _betSlipSelections = MutableStateFlow<List<BetSelection>>(emptyList())
    val betSlipSelections = _betSlipSelections.asStateFlow()

    // Placed Bets History
    private val _placedBets = MutableStateFlow<List<PlacedBet>>(emptyList())
    val placedBets = _placedBets.asStateFlow()

    // Transaction History
    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions = _transactions.asStateFlow()

    // Instant Score / Goal Notification Flow
    private val _instantNotifications = MutableSharedFlow<LiveScoreNotification>(extraBufferCapacity = 5)
    val instantNotifications = _instantNotifications.asSharedFlow()

    // Active Live Stream Match ID
    private val _selectedLiveMatchId = MutableStateFlow<String?>(null)
    val selectedLiveMatchId = _selectedLiveMatchId.asStateFlow()

    // Known Users for P2P transfers
    val knownUsers = listOf(
        Pair("DLB-409122", "Dave_Winner"),
        Pair("DLB-771239", "Bola_Stakes"),
        Pair("DLB-559012", "Chidi_Goals"),
        Pair("DLB-331045", "Samuel_Odds")
    )

    init {
        initializeInitialData()
        startLiveSimulationEngine()
    }

    private fun initializeInitialData() {
        // Initial Matches
        val sampleMatches = listOf(
            Match(
                id = "m1",
                sport = SportType.FOOTBALL,
                league = "Premier League",
                homeTeam = "Arsenal",
                awayTeam = "Chelsea",
                homeScore = 2,
                awayScore = 1,
                status = MatchStatus.LIVE,
                minute = 74,
                odds = MatchOdds(homeWin = 1.42, draw = 4.10, awayWin = 6.80, over25 = 1.35, under25 = 3.20, bttsYes = 1.55, bttsNo = 2.45),
                stats = MatchStats(
                    possessionHome = 62, possessionAway = 38,
                    shotsHome = 14, shotsAway = 6,
                    shotsOnTargetHome = 7, shotsOnTargetAway = 3,
                    cornersHome = 8, cornersAway = 2,
                    dangerousAttacksHome = 58, dangerousAttacksAway = 29,
                    yellowCardsHome = 1, yellowCardsAway = 3
                ),
                events = listOf(
                    MatchEvent(12, "Arsenal", "⚽ GOAL", "Bukayo Saka strikes from outside the box!", EventType.GOAL),
                    MatchEvent(34, "Chelsea", "⚽ GOAL", "Cole Palmer penalty conversion", EventType.PENALTY),
                    MatchEvent(61, "Arsenal", "⚽ GOAL", "Kai Havertz header into top corner", EventType.GOAL),
                    MatchEvent(69, "Chelsea", "🟨 Yellow Card", "Moises Caicedo reckless tackle", EventType.YELLOW_CARD)
                ),
                pitchStatus = "Arsenal Dangerous Attack",
                attackingTeam = "Arsenal",
                streamChannelName = "DelajStream HD 1 - Emirates Arena",
                isHot = true
            ),
            Match(
                id = "m2",
                sport = SportType.FOOTBALL,
                league = "UEFA Champions League",
                homeTeam = "Real Madrid",
                awayTeam = "Bayern Munich",
                homeScore = 1,
                awayScore = 1,
                status = MatchStatus.LIVE,
                minute = 58,
                odds = MatchOdds(homeWin = 2.05, draw = 3.35, awayWin = 3.40, over25 = 1.62, under25 = 2.25, bttsYes = 1.48, bttsNo = 2.60),
                stats = MatchStats(
                    possessionHome = 54, possessionAway = 46,
                    shotsHome = 11, shotsAway = 9,
                    shotsOnTargetHome = 5, shotsOnTargetAway = 4,
                    cornersHome = 5, cornersAway = 4,
                    dangerousAttacksHome = 44, dangerousAttacksAway = 40,
                    yellowCardsHome = 2, yellowCardsAway = 1
                ),
                events = listOf(
                    MatchEvent(22, "Bayern Munich", "⚽ GOAL", "Harry Kane clinical finish", EventType.GOAL),
                    MatchEvent(49, "Real Madrid", "⚽ GOAL", "Vinicius Jr explosive burst & finish", EventType.GOAL)
                ),
                pitchStatus = "Bayern Counter Attack",
                attackingTeam = "Bayern Munich",
                streamChannelName = "DelajStream HD 2 - Bernabeu Live",
                isHot = true
            ),
            Match(
                id = "m3",
                sport = SportType.FOOTBALL,
                league = "La Liga",
                homeTeam = "Barcelona",
                awayTeam = "Atletico Madrid",
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.LIVE,
                minute = 32,
                odds = MatchOdds(homeWin = 1.88, draw = 3.50, awayWin = 4.20, over25 = 1.75, under25 = 2.05, bttsYes = 1.65, bttsNo = 2.20),
                stats = MatchStats(
                    possessionHome = 68, possessionAway = 32,
                    shotsHome = 7, shotsAway = 2,
                    shotsOnTargetHome = 3, shotsOnTargetAway = 1,
                    cornersHome = 4, cornersAway = 1,
                    dangerousAttacksHome = 31, dangerousAttacksAway = 12
                ),
                events = listOf(
                    MatchEvent(1, "Barcelona", "Kickoff", "First half started", EventType.KICKOFF),
                    MatchEvent(28, "Barcelona", "Corner Kick", "Dangerous set piece cleared", EventType.CORNER)
                ),
                pitchStatus = "Barcelona In Possession (Midfield)",
                attackingTeam = "Barcelona",
                streamChannelName = "DelajStream HD 3 - Camp Nou Live"
            ),
            Match(
                id = "m4",
                sport = SportType.BASKETBALL,
                league = "NBA",
                homeTeam = "LA Lakers",
                awayTeam = "Golden State Warriors",
                homeScore = 98,
                awayScore = 94,
                status = MatchStatus.LIVE,
                minute = 41, // Q4
                odds = MatchOdds(homeWin = 1.65, draw = 15.0, awayWin = 2.25, over25 = 1.90, under25 = 1.90),
                pitchStatus = "Lakers Possession - Fast Break",
                streamChannelName = "DelajStream Courtside Live",
                isHot = true
            ),
            Match(
                id = "m5",
                sport = SportType.FOOTBALL,
                league = "Premier League",
                homeTeam = "Manchester City",
                awayTeam = "Liverpool",
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.UPCOMING,
                minute = 0,
                odds = MatchOdds(homeWin = 2.15, draw = 3.60, awayWin = 3.20, over25 = 1.55, under25 = 2.40, bttsYes = 1.45, bttsNo = 2.70),
                streamChannelName = "Super Sunday Special"
            ),
            Match(
                id = "m6",
                sport = SportType.FOOTBALL,
                league = "Serie A",
                homeTeam = "Inter Milan",
                awayTeam = "Juventus",
                homeScore = 0,
                awayScore = 0,
                status = MatchStatus.UPCOMING,
                minute = 0,
                odds = MatchOdds(homeWin = 2.20, draw = 3.25, awayWin = 3.50, over25 = 2.05, under25 = 1.78, bttsYes = 1.82, bttsNo = 1.95)
            )
        )
        _matches.value = sampleMatches
        _selectedLiveMatchId.value = "m1" // Default selected live stream

        // Initial Transactions
        val now = System.currentTimeMillis()
        val sampleTransactions = listOf(
            Transaction(
                id = "tx-1",
                reference = "DLB-DEP-78192301",
                timestamp = now - 3600 * 1000 * 2,
                amount = 500.0,
                fee = 0.0,
                type = TransactionType.DEPOSIT,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                destinationOrSource = "GTBank Instant Transfer",
                note = "Funding via Virtual Account"
            ),
            Transaction(
                id = "tx-2",
                reference = "DLB-BET-99120392",
                timestamp = now - 3600 * 1000 * 4,
                amount = 50.0,
                type = TransactionType.BET_PLACED,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.DEBIT_CARD,
                destinationOrSource = "Acca: 4 Legs",
                note = "Bet Slip #991203"
            ),
            Transaction(
                id = "tx-3",
                reference = "DLB-WIN-44810239",
                timestamp = now - 3600 * 1000 * 12,
                amount = 320.50,
                type = TransactionType.BET_WON,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                destinationOrSource = "Won Slip #882190",
                note = "Man City vs Spurs Over 2.5 Won"
            ),
            Transaction(
                id = "tx-4",
                reference = "DLB-TRF-11029384",
                timestamp = now - 3600 * 1000 * 24,
                amount = 100.0,
                type = TransactionType.TRANSFER_SENT,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.DELAJ_P2P,
                destinationOrSource = "To @Dave_Winner (DLB-409122)",
                note = "P2P instant transfer"
            ),
            Transaction(
                id = "tx-5",
                reference = "DLB-WTH-55201948",
                timestamp = now - 3600 * 1000 * 48,
                amount = 250.0,
                fee = 0.0,
                type = TransactionType.WITHDRAWAL,
                status = TransactionStatus.COMPLETED,
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                destinationOrSource = "Access Bank •••• 8109",
                note = "Payout sent to verified account"
            )
        )
        _transactions.value = sampleTransactions

        // Initial Bets
        val samplePlacedBets = listOf(
            PlacedBet(
                id = "BET-991203",
                timestamp = now - 3600 * 1000 * 2,
                selections = listOf(
                    BetSelection("m1", "Arsenal vs Chelsea", "Premier League", "1X2", "Arsenal", 1.42),
                    BetSelection("m2", "Real Madrid vs Bayern Munich", "UEFA Champions League", "GG/NG", "Yes (GG)", 1.48)
                ),
                stake = 50.0,
                totalOdds = 2.10,
                bonusRate = 0.05,
                bonusAmount = 5.25,
                potentialPayout = 110.25,
                status = BetStatus.RUNNING,
                cashOutValue = 78.40
            ),
            PlacedBet(
                id = "BET-882190",
                timestamp = now - 3600 * 1000 * 12,
                selections = listOf(
                    BetSelection("m-old", "Man City vs Tottenham", "Premier League", "Total Goals", "Over 2.5", 1.65)
                ),
                stake = 200.0,
                totalOdds = 1.65,
                bonusRate = 0.0,
                bonusAmount = 0.0,
                potentialPayout = 330.0,
                status = BetStatus.WON,
                settledTime = now - 3600 * 1000 * 10
            )
        )
        _placedBets.value = samplePlacedBets
    }

    private fun startLiveSimulationEngine() {
        scope.launch {
            val possiblePitches = listOf(
                "Dangerous Attack in 18-yard box",
                "Corner Kick awarded",
                "Foul in Midfield - Free Kick",
                "Fast Counter-Attack",
                "Shot on Target - Saved by Goalkeeper!",
                "VAR Reviewing penalty call",
                "Ball out for Throw-in",
                "Build-up play in Defensive Half"
            )

            while (true) {
                delay(4000) // update every 4 seconds
                _matches.update { currentList ->
                    currentList.map { match ->
                        if (match.status != MatchStatus.LIVE) return@map match

                        var newMinute = match.minute
                        if (Random.nextInt(4) == 0 && newMinute < 90) {
                            newMinute += 1
                        }

                        val randEvent = Random.nextInt(25)
                        var newHomeScore = match.homeScore
                        var newAwayScore = match.awayScore
                        val newEvents = match.events.toMutableList()
                        var newPitchStatus = match.pitchStatus
                        var newAttackingTeam = match.attackingTeam
                        val stats = match.stats

                        when (randEvent) {
                            0 -> { // Goal for Home!
                                newHomeScore += 1
                                val scorer = if (match.homeTeam.contains("Arsenal")) "Bukayo Saka" else if (match.homeTeam.contains("Real")) "Jude Bellingham" else "Striker"
                                val goalEvent = MatchEvent(newMinute, match.homeTeam, "⚽ GOAL!", "$scorer scores! Electric atmosphere in stadium!", EventType.GOAL)
                                newEvents.add(0, goalEvent)
                                newPitchStatus = "⚽ GOAL SCORED by ${match.homeTeam}!"
                                newAttackingTeam = match.homeTeam

                                // Push Notification trigger!
                                if (_userProfile.value.notificationPrefs.goalAlerts) {
                                    scope.launch {
                                        _instantNotifications.emit(
                                            LiveScoreNotification(
                                                id = UUID.randomUUID().toString(),
                                                matchId = match.id,
                                                matchTitle = "${match.homeTeam} vs ${match.awayTeam}",
                                                scoreText = "$newHomeScore - $newAwayScore",
                                                detail = "⚽ GOAL! ${match.homeTeam} ($scorer $newMinute')"
                                            )
                                        )
                                    }
                                }
                            }
                            1 -> { // Goal for Away!
                                if (Random.nextInt(3) == 0) {
                                    newAwayScore += 1
                                    val scorer = if (match.awayTeam.contains("Chelsea")) "Nkunku" else if (match.awayTeam.contains("Bayern")) "Musiala" else "Striker"
                                    val goalEvent = MatchEvent(newMinute, match.awayTeam, "⚽ GOAL!", "$scorer finishes brilliant through ball!", EventType.GOAL)
                                    newEvents.add(0, goalEvent)
                                    newPitchStatus = "⚽ GOAL SCORED by ${match.awayTeam}!"
                                    newAttackingTeam = match.awayTeam

                                    if (_userProfile.value.notificationPrefs.goalAlerts) {
                                        scope.launch {
                                            _instantNotifications.emit(
                                                LiveScoreNotification(
                                                    id = UUID.randomUUID().toString(),
                                                    matchId = match.id,
                                                    matchTitle = "${match.homeTeam} vs ${match.awayTeam}",
                                                    scoreText = "$newHomeScore - $newAwayScore",
                                                    detail = "⚽ GOAL! ${match.awayTeam} ($scorer $newMinute')"
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                            2, 3 -> {
                                val team = if (Random.nextBoolean()) match.homeTeam else match.awayTeam
                                newPitchStatus = "${team}: ${possiblePitches.random()}"
                                newAttackingTeam = team
                            }
                            4 -> {
                                // Corner
                                val isHome = Random.nextBoolean()
                                val cornerEvent = MatchEvent(newMinute, if (isHome) match.homeTeam else match.awayTeam, "Corner Kick", "In-swinging corner into the box", EventType.CORNER)
                                newEvents.add(0, cornerEvent)
                                newPitchStatus = "${cornerEvent.teamName} Corner Kick"
                            }
                            else -> {
                                newPitchStatus = possiblePitches.random()
                            }
                        }

                        // Slight odds fluctuation simulation for live excitement
                        val oddsFluctuation = (Random.nextDouble(-0.04, 0.04))
                        val updatedHomeOdds = (match.odds.homeWin + oddsFluctuation).coerceIn(1.05, 25.0)
                        val roundedOdds = Math.round(updatedHomeOdds * 100.0) / 100.0

                        match.copy(
                            minute = newMinute,
                            homeScore = newHomeScore,
                            awayScore = newAwayScore,
                            events = newEvents.take(15),
                            pitchStatus = newPitchStatus,
                            attackingTeam = newAttackingTeam,
                            odds = match.odds.copy(homeWin = roundedOdds),
                            stats = stats.copy(
                                dangerousAttacksHome = stats.dangerousAttacksHome + (if (newAttackingTeam == match.homeTeam) 1 else 0),
                                dangerousAttacksAway = stats.dangerousAttacksAway + (if (newAttackingTeam == match.awayTeam) 1 else 0)
                            )
                        )
                    }
                }

                // Update running cashout values dynamically
                _placedBets.update { list ->
                    list.map { bet ->
                        if (bet.status == BetStatus.RUNNING && bet.cashOutValue != null) {
                            val delta = Random.nextDouble(-2.0, 3.5)
                            val newVal = (bet.cashOutValue + delta).coerceIn(bet.stake * 0.4, bet.potentialPayout * 0.95)
                            bet.copy(cashOutValue = Math.round(newVal * 100.0) / 100.0)
                        } else {
                            bet
                        }
                    }
                }
            }
        }
    }

    fun selectLiveMatch(matchId: String) {
        _selectedLiveMatchId.value = matchId
    }

    // --- BET SLIP ACTIONS ---
    fun toggleSelection(selection: BetSelection) {
        val current = _betSlipSelections.value
        val exists = current.any { it.matchId == selection.matchId && it.marketName == selection.marketName && it.selectionName == selection.selectionName }
        if (exists) {
            _betSlipSelections.value = current.filterNot { it.matchId == selection.matchId && it.marketName == selection.marketName }
        } else {
            // Replace if same match and same market, else add
            val filtered = current.filterNot { it.matchId == selection.matchId && it.marketName == selection.marketName }
            _betSlipSelections.value = filtered + selection
        }
    }

    fun removeSelection(matchId: String, marketName: String) {
        _betSlipSelections.value = _betSlipSelections.value.filterNot { it.matchId == matchId && it.marketName == marketName }
    }

    fun clearBetSlip() {
        _betSlipSelections.value = emptyList()
    }

    fun calculateAccaBonusRate(legCount: Int): Double {
        return when {
            legCount <= 1 -> 0.0
            legCount == 2 -> 0.05 // 5%
            legCount == 3 -> 0.10 // 10%
            legCount == 4 -> 0.15 // 15%
            legCount in 5..7 -> 0.25 // 25%
            legCount in 8..10 -> 0.45 // 45%
            legCount in 11..15 -> 0.75 // 75%
            else -> 1.50 // 150% Super Boost
        }
    }

    fun placeBet(stake: Double): Result<PlacedBet> {
        val user = _userProfile.value
        if (user.balance < stake) {
            return Result.failure(Exception("Insufficient balance. Please deposit to place bet."))
        }
        val selections = _betSlipSelections.value
        if (selections.isEmpty()) {
            return Result.failure(Exception("Your betslip is empty."))
        }

        val totalOdds = selections.fold(1.0) { acc, sel -> acc * sel.odds }
        val roundedOdds = Math.round(totalOdds * 100.0) / 100.0
        val bonusRate = calculateAccaBonusRate(selections.size)
        val baseReturn = stake * roundedOdds
        val bonusAmount = baseReturn * bonusRate
        val potentialPayout = Math.round((baseReturn + bonusAmount) * 100.0) / 100.0

        val newBet = PlacedBet(
            id = "BET-" + Random.nextInt(100000, 999999),
            timestamp = System.currentTimeMillis(),
            selections = selections,
            stake = stake,
            totalOdds = roundedOdds,
            bonusRate = bonusRate,
            bonusAmount = Math.round(bonusAmount * 100.0) / 100.0,
            potentialPayout = potentialPayout,
            status = BetStatus.RUNNING,
            cashOutValue = Math.round(stake * 0.92 * 100.0) / 100.0
        )

        // Deduct balance
        _userProfile.update { it.copy(balance = it.balance - stake) }
        _placedBets.update { listOf(newBet) + it }

        // Record transaction
        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            reference = "DLB-STK-${Random.nextInt(10000000, 99999999)}",
            timestamp = System.currentTimeMillis(),
            amount = stake,
            type = TransactionType.BET_PLACED,
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.DEBIT_CARD,
            destinationOrSource = "Slip #${newBet.id} (${selections.size} legs)",
            note = "Stake deduction"
        )
        _transactions.update { listOf(tx) + it }
        clearBetSlip()

        return Result.success(newBet)
    }

    fun cashOutBet(betId: String): Result<Double> {
        val bet = _placedBets.value.find { it.id == betId } ?: return Result.failure(Exception("Bet not found"))
        if (bet.status != BetStatus.RUNNING) return Result.failure(Exception("Bet already settled or cashed out"))
        val cashOutAmount = bet.cashOutValue ?: return Result.failure(Exception("Cash out unavailable right now"))

        _placedBets.update { list ->
            list.map {
                if (it.id == betId) it.copy(status = BetStatus.CASHED_OUT, settledTime = System.currentTimeMillis()) else it
            }
        }

        _userProfile.update { it.copy(balance = it.balance + cashOutAmount) }

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            reference = "DLB-CSH-${Random.nextInt(10000000, 99999999)}",
            timestamp = System.currentTimeMillis(),
            amount = cashOutAmount,
            type = TransactionType.BET_CASHOUT,
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            destinationOrSource = "Early Cash Out (#${bet.id})",
            note = "Cashed out successfully"
        )
        _transactions.update { listOf(tx) + it }

        return Result.success(cashOutAmount)
    }

    // --- WALLET & PAYMENT ACTIONS ---
    fun deposit(amount: Double, method: PaymentMethod, sourceInfo: String): Transaction {
        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            reference = "DLB-DEP-${Random.nextInt(10000000, 99999999)}",
            timestamp = System.currentTimeMillis(),
            amount = amount,
            type = TransactionType.DEPOSIT,
            status = TransactionStatus.COMPLETED,
            paymentMethod = method,
            destinationOrSource = sourceInfo,
            note = "Direct Account Funding"
        )

        _userProfile.update { it.copy(balance = it.balance + amount) }
        _transactions.update { listOf(tx) + it }

        // Send payment alert if configured
        if (_userProfile.value.notificationPrefs.paymentAlerts) {
            scope.launch {
                _instantNotifications.emit(
                    LiveScoreNotification(
                        id = UUID.randomUUID().toString(),
                        matchId = "wallet",
                        matchTitle = "Deposit Successful",
                        scoreText = "+$${String.format("%.2f", amount)}",
                        detail = "Your DelajBet wallet has been credited via ${method.displayName}."
                    )
                )
            }
        }
        return tx
    }

    fun withdraw(amount: Double, bankName: String, accountNumber: String, accountName: String): Result<Transaction> {
        val user = _userProfile.value
        if (user.balance < amount) {
            return Result.failure(Exception("Insufficient funds. Available: $${String.format("%.2f", user.balance)}"))
        }
        if (amount > user.kycTier.dailyLimit) {
            return Result.failure(Exception("Withdrawal exceeds ${user.kycTier.tierName} daily limit of $${user.kycTier.dailyLimit}. Upgrade KYC in settings."))
        }

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            reference = "DLB-WTH-${Random.nextInt(10000000, 99999999)}",
            timestamp = System.currentTimeMillis(),
            amount = amount,
            fee = 0.0,
            type = TransactionType.WITHDRAWAL,
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.BANK_TRANSFER,
            destinationOrSource = "$bankName ($accountNumber) - $accountName",
            note = "Instant withdrawal payout"
        )

        _userProfile.update { it.copy(balance = it.balance - amount) }
        _transactions.update { listOf(tx) + it }

        if (_userProfile.value.notificationPrefs.paymentAlerts) {
            scope.launch {
                _instantNotifications.emit(
                    LiveScoreNotification(
                        id = UUID.randomUUID().toString(),
                        matchId = "wallet",
                        matchTitle = "Withdrawal Dispatched",
                        scoreText = "-$${String.format("%.2f", amount)}",
                        detail = "Sent to $bankName ($accountNumber). Funds will reflect instantly."
                    )
                )
            }
        }
        return Result.success(tx)
    }

    fun peerTransfer(recipientIdOrPhone: String, amount: Double, note: String): Result<Transaction> {
        val user = _userProfile.value
        if (user.balance < amount) {
            return Result.failure(Exception("Insufficient balance for transfer."))
        }
        if (recipientIdOrPhone.trim().isEmpty()) {
            return Result.failure(Exception("Please enter a valid DelajBet User ID or phone number."))
        }

        val recipientName = knownUsers.find { it.first.equals(recipientIdOrPhone.trim(), ignoreCase = true) }?.second
            ?: "@${recipientIdOrPhone.trim()}"

        val tx = Transaction(
            id = UUID.randomUUID().toString(),
            reference = "DLB-TRF-${Random.nextInt(10000000, 99999999)}",
            timestamp = System.currentTimeMillis(),
            amount = amount,
            type = TransactionType.TRANSFER_SENT,
            status = TransactionStatus.COMPLETED,
            paymentMethod = PaymentMethod.DELAJ_P2P,
            destinationOrSource = "To: $recipientName ($recipientIdOrPhone)",
            note = if (note.isBlank()) "P2P instant transfer" else note
        )

        _userProfile.update { it.copy(balance = it.balance - amount) }
        _transactions.update { listOf(tx) + it }

        return Result.success(tx)
    }

    // --- MFA & SECURITY ACTIONS ---
    fun verifyPin(pin: String): Boolean {
        // Default transaction PIN is 1234
        return pin == "1234"
    }

    fun verifyOtp(otp: String): Boolean {
        // Any 6-digit OTP or standard code 778899 / 123456
        return otp.length == 6 && otp.all { it.isDigit() }
    }

    fun updateSecuritySettings(
        is2faEnabled: Boolean,
        preferredMethod: MfaMethod,
        biometricEnabled: Boolean
    ) {
        _userProfile.update {
            it.copy(
                is2faEnabled = is2faEnabled,
                activeMfaMethod = preferredMethod,
                biometricEnabled = biometricEnabled
            )
        }
    }

    fun updateNotificationPreferences(prefs: PushNotificationPreference) {
        _userProfile.update { it.copy(notificationPrefs = prefs) }
    }

    fun upgradeKycTier(tier: KycTier) {
        _userProfile.update { it.copy(kycTier = tier) }
    }

    companion object {
        @Volatile
        private var instance: DelajBetRepository? = null

        fun getInstance(): DelajBetRepository {
            return instance ?: synchronized(this) {
                instance ?: DelajBetRepository().also { instance = it }
            }
        }
    }
}
