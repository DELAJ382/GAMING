package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BetStatus
import com.example.data.model.MatchStatus
import com.example.ui.BettingViewModel
import com.example.ui.ScreenTab
import com.example.ui.components.*
import com.example.ui.screens.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private val viewModel: BettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            DelajBetTheme {
                val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
                val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
                val matches by viewModel.matches.collectAsStateWithLifecycle()
                val selectedLiveMatch by viewModel.selectedLiveMatch.collectAsStateWithLifecycle()
                val betSlipSelections by viewModel.betSlipSelections.collectAsStateWithLifecycle()
                val placedBets by viewModel.placedBets.collectAsStateWithLifecycle()
                val transactions by viewModel.transactions.collectAsStateWithLifecycle()
                val selectedSportFilter by viewModel.selectedSportFilter.collectAsStateWithLifecycle()
                val isBetSlipExpanded by viewModel.isBetSlipExpanded.collectAsStateWithLifecycle()
                val txFilter by viewModel.txFilter.collectAsStateWithLifecycle()
                val mfaChallenge by viewModel.mfaChallenge.collectAsStateWithLifecycle()
                val activeNotification by viewModel.activeNotification.collectAsStateWithLifecycle()
                val userMessage by viewModel.userMessage.collectAsStateWithLifecycle()

                val snackbarHostState = remember { SnackbarHostState() }

                // Auto-dismiss user messages via Snackbar
                LaunchedEffect(userMessage) {
                    userMessage?.let { msg ->
                        snackbarHostState.showSnackbar(msg)
                        viewModel.clearUserMessage()
                    }
                }

                // Auto-dismiss live notifications after 6 seconds
                LaunchedEffect(activeNotification) {
                    if (activeNotification != null) {
                        delay(6000)
                        viewModel.dismissNotification()
                    }
                }

                // Handle system back navigation
                BackHandler(enabled = isBetSlipExpanded || currentScreen != ScreenTab.HOME) {
                    if (isBetSlipExpanded) {
                        viewModel.toggleBetSlip(false)
                    } else if (currentScreen != ScreenTab.HOME) {
                        viewModel.navigateTo(ScreenTab.HOME)
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DelajDarkBg)
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = DelajDarkBg,
                        snackbarHost = { SnackbarHost(snackbarHostState) },
                        topBar = {
                            DelajBetTopBar(
                                userProfile = userProfile,
                                onDepositClick = { viewModel.navigateTo(ScreenTab.WALLET) },
                                onNotificationsClick = { viewModel.navigateTo(ScreenTab.SECURITY) },
                                onSecurityClick = { viewModel.navigateTo(ScreenTab.SECURITY) },
                                onProfileClick = { viewModel.navigateTo(ScreenTab.SECURITY) }
                            )
                        },
                        bottomBar = {
                            Column {
                                // Floating Bet Slip Bar (above bottom bar if selections present)
                                if (betSlipSelections.isNotEmpty() && !isBetSlipExpanded) {
                                    BetSlipBar(
                                        selections = betSlipSelections,
                                        isExpanded = isBetSlipExpanded,
                                        onToggle = { viewModel.toggleBetSlip() }
                                    )
                                }

                                // Bottom Navigation Bar
                                DelajBottomNavigationBar(
                                    currentScreen = currentScreen,
                                    openBetsCount = placedBets.count { it.status == BetStatus.RUNNING },
                                    hasLiveMatches = matches.any { it.status == MatchStatus.LIVE },
                                    onTabSelected = { screen ->
                                        viewModel.navigateTo(screen)
                                        viewModel.toggleBetSlip(false)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            // Active Screen Content
                            when (currentScreen) {
                                ScreenTab.HOME -> {
                                    HomeScreen(
                                        matches = matches,
                                        selections = betSlipSelections,
                                        selectedFilter = selectedSportFilter,
                                        onFilterChange = { viewModel.setSportFilter(it) },
                                        onSelectOdds = { viewModel.toggleOddsSelection(it) },
                                        onWatchStream = { matchId -> viewModel.selectLiveMatchForStream(matchId) },
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }

                                ScreenTab.LIVE_STREAM -> {
                                    LiveStreamScreen(
                                        matches = matches,
                                        selectedMatch = selectedLiveMatch,
                                        selections = betSlipSelections,
                                        onSelectMatch = { viewModel.selectLiveMatchForStream(it) },
                                        onSelectOdds = { viewModel.toggleOddsSelection(it) }
                                    )
                                }

                                ScreenTab.WALLET -> {
                                    WalletScreen(
                                        userProfile = userProfile,
                                        onDeposit = { amount, method, source ->
                                            viewModel.deposit(amount, method, source)
                                        },
                                        onWithdraw = { amount, bank, accNo, accName ->
                                            viewModel.requestWithdrawal(amount, bank, accNo, accName)
                                        },
                                        onTransfer = { recipient, amount, note ->
                                            viewModel.requestTransfer(recipient, amount, note)
                                        },
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }

                                ScreenTab.HISTORY -> {
                                    TransactionsScreen(
                                        transactions = transactions,
                                        selectedType = txFilter,
                                        onFilterChange = { viewModel.setTxFilter(it) }
                                    )
                                }

                                ScreenTab.MY_BETS -> {
                                    MyBetsScreen(
                                        placedBets = placedBets,
                                        onCashOut = { viewModel.cashOutBet(it) }
                                    )
                                }

                                ScreenTab.SECURITY -> {
                                    SecurityScreen(
                                        userProfile = userProfile,
                                        onUpdateSecurity = { is2fa, method, bio ->
                                            viewModel.updateSecuritySettings(is2fa, method, bio)
                                        },
                                        onUpdateNotifications = { prefs ->
                                            viewModel.updateNotificationPreferences(prefs)
                                        },
                                        onUpgradeKyc = { tier ->
                                            viewModel.upgradeKyc(tier)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Push Notification Floating Toast Banner (Top Overlay)
                    NotificationBanner(
                        notification = activeNotification,
                        onDismiss = { viewModel.dismissNotification() },
                        onWatchClick = { matchId ->
                            viewModel.selectLiveMatchForStream(matchId)
                            viewModel.dismissNotification()
                        }
                    )

                    // Expanded Bet Slip Drawer (Modal Overlay)
                    if (isBetSlipExpanded) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { viewModel.toggleBetSlip(false) },
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Box(modifier = Modifier.clickable(enabled = false) {}) {
                                BetSlipDrawer(
                                    selections = betSlipSelections,
                                    balance = userProfile.balance,
                                    onRemoveSelection = { matchId, market ->
                                        viewModel.removeBetSlipSelection(matchId, market)
                                    },
                                    onClearAll = { viewModel.clearBetSlip() },
                                    onClose = { viewModel.toggleBetSlip(false) },
                                    onPlaceBet = { stake -> viewModel.placeBet(stake) }
                                )
                            }
                        }
                    }

                    // Secure MFA / 2FA Verification Dialog (Modal Challenge)
                    mfaChallenge?.let { challenge ->
                        MfaVerificationDialog(
                            challenge = challenge,
                            onDismiss = { viewModel.dismissMfaChallenge() },
                            onVerify = { input -> viewModel.verifyPinOrOtp(input) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DelajBottomNavigationBar(
    currentScreen: ScreenTab,
    openBetsCount: Int,
    hasLiveMatches: Boolean,
    onTabSelected: (ScreenTab) -> Unit
) {
    Surface(
        color = DelajDarkBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DelajNavItem(
                icon = Icons.Default.SportsSoccer,
                label = "Sports",
                isSelected = currentScreen == ScreenTab.HOME,
                onClick = { onTabSelected(ScreenTab.HOME) }
            )

            DelajNavItem(
                icon = Icons.Default.LiveTv,
                label = "Live",
                isSelected = currentScreen == ScreenTab.LIVE_STREAM,
                showPulse = hasLiveMatches,
                onClick = { onTabSelected(ScreenTab.LIVE_STREAM) }
            )

            DelajNavItem(
                icon = Icons.Default.ReceiptLong,
                label = "My Bets",
                isSelected = currentScreen == ScreenTab.MY_BETS,
                badgeCount = if (openBetsCount > 0) openBetsCount else null,
                onClick = { onTabSelected(ScreenTab.MY_BETS) }
            )

            DelajNavItem(
                icon = Icons.Default.AccountBalanceWallet,
                label = "Wallet",
                isSelected = currentScreen == ScreenTab.WALLET,
                onClick = { onTabSelected(ScreenTab.WALLET) }
            )

            DelajNavItem(
                icon = Icons.Default.History,
                label = "History",
                isSelected = currentScreen == ScreenTab.HISTORY,
                onClick = { onTabSelected(ScreenTab.HISTORY) }
            )

            DelajNavItem(
                icon = Icons.Default.Security,
                label = "Security",
                isSelected = currentScreen == ScreenTab.SECURITY,
                onClick = { onTabSelected(ScreenTab.SECURITY) }
            )
        }
    }
}

@Composable
fun DelajNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    showPulse: Boolean = false,
    badgeCount: Int? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) DelajVoltGreen else DelajTextMuted,
                modifier = Modifier.size(22.dp)
            )

            if (showPulse) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(DelajCrimson)
                )
            } else if (badgeCount != null) {
                Box(
                    modifier = Modifier
                        .offset(x = 6.dp, y = (-4).dp)
                        .clip(CircleShape)
                        .background(DelajVoltGreen)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "$badgeCount",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajDarkBg
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium,
            color = if (isSelected) DelajVoltGreen else DelajTextMuted
        )
    }
}
