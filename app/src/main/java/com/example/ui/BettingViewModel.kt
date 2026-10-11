package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.DelajBetRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class MfaChallenge(
    val title: String,
    val subtitle: String,
    val amountText: String,
    val method: MfaMethod,
    val onVerified: () -> Unit
)

enum class ScreenTab {
    HOME,
    LIVE_STREAM,
    MY_BETS,
    WALLET,
    HISTORY,
    SECURITY
}

class BettingViewModel : ViewModel() {

    private val repository = DelajBetRepository.getInstance()

    val userProfile = repository.userProfile
    val matches = repository.matches
    val betSlipSelections = repository.betSlipSelections
    val placedBets = repository.placedBets
    val transactions = repository.transactions
    val selectedLiveMatchId = repository.selectedLiveMatchId

    // UI Navigation & Filters
    private val _currentScreen = MutableStateFlow(ScreenTab.HOME)
    val currentScreen = _currentScreen.asStateFlow()

    private val _selectedSportFilter = MutableStateFlow<String>("ALL") // "ALL", "LIVE", "FOOTBALL", "BASKETBALL"
    val selectedSportFilter = _selectedSportFilter.asStateFlow()

    private val _isBetSlipExpanded = MutableStateFlow(false)
    val isBetSlipExpanded = _isBetSlipExpanded.asStateFlow()

    // Transaction History Filter
    private val _txFilter = MutableStateFlow<TransactionType?>(null) // null = ALL
    val txFilter = _txFilter.asStateFlow()

    // MFA Challenge Dialog
    private val _mfaChallenge = MutableStateFlow<MfaChallenge?>(null)
    val mfaChallenge = _mfaChallenge.asStateFlow()

    // Live Notification Toast
    private val _activeNotification = MutableStateFlow<LiveScoreNotification?>(null)
    val activeNotification = _activeNotification.asStateFlow()

    // User message snackbar
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage = _userMessage.asStateFlow()

    val selectedLiveMatch: StateFlow<Match?> = combine(matches, selectedLiveMatchId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull { it.status == MatchStatus.LIVE }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    init {
        // Collect instant notifications from repo
        viewModelScope.launch {
            repository.instantNotifications.collect { alert ->
                _activeNotification.value = alert
            }
        }
    }

    fun navigateTo(screen: ScreenTab) {
        _currentScreen.value = screen
    }

    fun setSportFilter(filter: String) {
        _selectedSportFilter.value = filter
    }

    fun selectLiveMatchForStream(matchId: String) {
        repository.selectLiveMatch(matchId)
        _currentScreen.value = ScreenTab.LIVE_STREAM
    }

    fun setTxFilter(type: TransactionType?) {
        _txFilter.value = type
    }

    fun toggleBetSlip(expanded: Boolean? = null) {
        _isBetSlipExpanded.value = expanded ?: !_isBetSlipExpanded.value
    }

    fun toggleOddsSelection(selection: BetSelection) {
        repository.toggleSelection(selection)
    }

    fun removeBetSlipSelection(matchId: String, marketName: String) {
        repository.removeSelection(matchId, marketName)
    }

    fun clearBetSlip() {
        repository.clearBetSlip()
    }

    fun placeBet(stake: Double) {
        val result = repository.placeBet(stake)
        result.onSuccess {
            _userMessage.value = "🎉 Bet placed successfully! Slip #${it.id}"
            _isBetSlipExpanded.value = false
        }.onFailure {
            _userMessage.value = "⚠️ ${it.message}"
        }
    }

    fun cashOutBet(betId: String) {
        val result = repository.cashOutBet(betId)
        result.onSuccess { amount ->
            _userMessage.value = "💰 Cashed out $${String.format("%.2f", amount)} successfully!"
        }.onFailure {
            _userMessage.value = "⚠️ ${it.message}"
        }
    }

    // --- SECURE PAYMENT FLOWS WITH MFA ---
    fun deposit(amount: Double, method: PaymentMethod, sourceInfo: String) {
        val tx = repository.deposit(amount, method, sourceInfo)
        _userMessage.value = "✅ Deposit of $${String.format("%.2f", amount)} received! Ref: ${tx.reference}"
    }

    fun requestWithdrawal(amount: Double, bankName: String, accountNumber: String, accountName: String) {
        val profile = userProfile.value
        if (profile.balance < amount) {
            _userMessage.value = "⚠️ Insufficient funds for withdrawal."
            return
        }

        // Trigger MFA Challenge
        _mfaChallenge.value = MfaChallenge(
            title = "Authorize Withdrawal",
            subtitle = "Security verification required for payout of funds to $bankName.",
            amountText = "$${String.format("%.2f", amount)}",
            method = profile.activeMfaMethod,
            onVerified = {
                val result = repository.withdraw(amount, bankName, accountNumber, accountName)
                result.onSuccess {
                    _userMessage.value = "🚀 Withdrawal of $${String.format("%.2f", amount)} processed! Sent to $bankName."
                }.onFailure {
                    _userMessage.value = "⚠️ ${it.message}"
                }
                _mfaChallenge.value = null
            }
        )
    }

    fun requestTransfer(recipientIdOrPhone: String, amount: Double, note: String) {
        val profile = userProfile.value
        if (profile.balance < amount) {
            _userMessage.value = "⚠️ Insufficient balance for transfer."
            return
        }

        // Trigger MFA Challenge
        _mfaChallenge.value = MfaChallenge(
            title = "Authorize DelajBet Transfer",
            subtitle = "Verify your identity to send funds to $recipientIdOrPhone.",
            amountText = "$${String.format("%.2f", amount)}",
            method = profile.activeMfaMethod,
            onVerified = {
                val result = repository.peerTransfer(recipientIdOrPhone, amount, note)
                result.onSuccess {
                    _userMessage.value = "💸 Transfer of $${String.format("%.2f", amount)} sent successfully!"
                }.onFailure {
                    _userMessage.value = "⚠️ ${it.message}"
                }
                _mfaChallenge.value = null
            }
        )
    }

    fun dismissMfaChallenge() {
        _mfaChallenge.value = null
    }

    fun verifyPinOrOtp(input: String): Boolean {
        val challenge = _mfaChallenge.value ?: return false
        val isValid = when (challenge.method) {
            MfaMethod.SECURITY_PIN -> repository.verifyPin(input)
            else -> repository.verifyOtp(input)
        }
        if (isValid) {
            challenge.onVerified()
            return true
        }
        return false
    }

    fun dismissNotification() {
        _activeNotification.value = null
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun updateSecuritySettings(
        is2faEnabled: Boolean,
        preferredMethod: MfaMethod,
        biometricEnabled: Boolean
    ) {
        repository.updateSecuritySettings(is2faEnabled, preferredMethod, biometricEnabled)
        _userMessage.value = "🛡️ Security & 2FA preferences updated successfully."
    }

    fun updateNotificationPreferences(prefs: PushNotificationPreference) {
        repository.updateNotificationPreferences(prefs)
        _userMessage.value = "🔔 Push notification preferences updated."
    }

    fun upgradeKyc(tier: KycTier) {
        repository.upgradeKycTier(tier)
        _userMessage.value = "✅ Account upgraded to ${tier.tierName}."
    }
}
