package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.KycTier
import com.example.data.model.MfaMethod
import com.example.data.model.PushNotificationPreference
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun SecurityScreen(
    userProfile: UserProfile,
    onUpdateSecurity: (Boolean, MfaMethod, Boolean) -> Unit,
    onUpdateNotifications: (PushNotificationPreference) -> Unit,
    onUpgradeKyc: (KycTier) -> Unit
) {
    var is2faEnabled by remember { mutableStateOf(userProfile.is2faEnabled) }
    var selectedMethod by remember { mutableStateOf(userProfile.activeMfaMethod) }
    var biometricEnabled by remember { mutableStateOf(userProfile.biometricEnabled) }

    var goalAlerts by remember { mutableStateOf(userProfile.notificationPrefs.goalAlerts) }
    var scoreUpdates by remember { mutableStateOf(userProfile.notificationPrefs.scoreUpdates) }
    var betSettlements by remember { mutableStateOf(userProfile.notificationPrefs.betSettlements) }
    var paymentAlerts by remember { mutableStateOf(userProfile.notificationPrefs.paymentAlerts) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("security_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "SECURITY & 2FA AUTHENTICATION",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DelajTextWhite
                )
                Text(
                    text = "Multi-factor protection for your funds, bets, and personal data",
                    fontSize = 11.sp,
                    color = DelajTextSecondary
                )
            }
        }

        // Security Health Score Card
        item {
            Surface(
                color = DelajCardBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(DelajVoltGreen.copy(alpha = 0.15f))
                            .border(1.5.dp, DelajVoltGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield",
                            tint = DelajVoltGreen,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = if (is2faEnabled) "Account Protected (98% Strong)" else "Protection Recommended",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (is2faEnabled) DelajVoltGreen else DelajGold
                        )
                        Text(
                            text = "Multi-factor authentication active on withdrawals & peer transfers",
                            fontSize = 11.sp,
                            color = DelajTextSecondary
                        )
                    }
                }
            }
        }

        // 2FA Methods Section
        item {
            Surface(
                color = DelajCardBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Multi-Factor Authentication (2FA)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextWhite
                            )
                            Text(
                                text = "Require verification to withdraw or transfer",
                                fontSize = 11.sp,
                                color = DelajTextSecondary
                            )
                        }

                        Switch(
                            checked = is2faEnabled,
                            onCheckedChange = {
                                is2faEnabled = it
                                onUpdateSecurity(it, selectedMethod, biometricEnabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DelajDarkBg,
                                checkedTrackColor = DelajVoltGreen,
                                uncheckedThumbColor = DelajTextMuted,
                                uncheckedTrackColor = DelajElevatedBg
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Preferred MFA Verification Method",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajTextWhite
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MfaMethod.values().forEach { method ->
                        val isSelected = selectedMethod == method
                        Surface(
                            color = if (isSelected) DelajVoltGreen.copy(alpha = 0.12f) else DelajElevatedBg,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) DelajVoltGreen else DelajSurfaceBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    selectedMethod = method
                                    onUpdateSecurity(is2faEnabled, method, biometricEnabled)
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedMethod = method
                                        onUpdateSecurity(is2faEnabled, method, biometricEnabled)
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = DelajVoltGreen,
                                        unselectedColor = DelajTextMuted
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = method.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DelajVoltGreen else DelajTextWhite
                                    )
                                    Text(
                                        text = method.subtitle,
                                        fontSize = 10.sp,
                                        color = DelajTextSecondary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Biometric Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Fingerprint,
                                contentDescription = "Biometrics",
                                tint = DelajVoltGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Biometric Fast-Unlock",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DelajTextWhite
                                )
                                Text(
                                    text = "Use Fingerprint / Face ID for instant authorization",
                                    fontSize = 10.sp,
                                    color = DelajTextSecondary
                                )
                            }
                        }

                        Switch(
                            checked = biometricEnabled,
                            onCheckedChange = {
                                biometricEnabled = it
                                onUpdateSecurity(is2faEnabled, selectedMethod, it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = DelajDarkBg,
                                checkedTrackColor = DelajVoltGreen
                            )
                        )
                    }
                }
            }
        }

        // Push Notifications Configuration Section
        item {
            Surface(
                color = DelajCardBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "INSTANT PUSH NOTIFICATIONS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajTextWhite
                    )
                    Text(
                        text = "Configure live alerts for goals, scores, and bet updates",
                        fontSize = 11.sp,
                        color = DelajTextSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    NotificationToggleRow(
                        title = "⚽ Instant Goal Alerts",
                        subtitle = "Push heads-up banner immediately when a goal is scored",
                        checked = goalAlerts,
                        onChecked = {
                            goalAlerts = it
                            onUpdateNotifications(
                                PushNotificationPreference(
                                    goalAlerts = it,
                                    scoreUpdates = scoreUpdates,
                                    betSettlements = betSettlements,
                                    paymentAlerts = paymentAlerts
                                )
                            )
                        }
                    )

                    NotificationToggleRow(
                        title = "📊 Live Match Score Updates",
                        subtitle = "Minute-by-minute updates for tracked fixtures",
                        checked = scoreUpdates,
                        onChecked = {
                            scoreUpdates = it
                            onUpdateNotifications(
                                PushNotificationPreference(
                                    goalAlerts = goalAlerts,
                                    scoreUpdates = it,
                                    betSettlements = betSettlements,
                                    paymentAlerts = paymentAlerts
                                )
                            )
                        }
                    )

                    NotificationToggleRow(
                        title = "🎉 Bet Settlements & Wins",
                        subtitle = "Alert when a placed bet wins or is settled",
                        checked = betSettlements,
                        onChecked = {
                            betSettlements = it
                            onUpdateNotifications(
                                PushNotificationPreference(
                                    goalAlerts = goalAlerts,
                                    scoreUpdates = scoreUpdates,
                                    betSettlements = it,
                                    paymentAlerts = paymentAlerts
                                )
                            )
                        }
                    )

                    NotificationToggleRow(
                        title = "💳 Payment & Payout Alerts",
                        subtitle = "Notifications for successful deposits and withdrawals",
                        checked = paymentAlerts,
                        onChecked = {
                            paymentAlerts = it
                            onUpdateNotifications(
                                PushNotificationPreference(
                                    goalAlerts = goalAlerts,
                                    scoreUpdates = scoreUpdates,
                                    betSettlements = betSettlements,
                                    paymentAlerts = it
                                )
                            )
                        }
                    )
                }
            }
        }

        // KYC Tier Section
        item {
            Surface(
                color = DelajCardBg,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "KYC Verification Level",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextWhite
                            )
                            Text(
                                text = "Current: ${userProfile.kycTier.tierName} ($${String.format("%.0f", userProfile.kycTier.dailyLimit)}/day)",
                                fontSize = 11.sp,
                                color = DelajVoltGreen
                            )
                        }

                        if (userProfile.kycTier != KycTier.TIER_3) {
                            Button(
                                onClick = {
                                    val next = if (userProfile.kycTier == KycTier.TIER_1) KycTier.TIER_2 else KycTier.TIER_3
                                    onUpgradeKyc(next)
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = DelajVoltGreen,
                                    contentColor = DelajDarkBg
                                )
                            ) {
                                Text("Upgrade Tier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = DelajTextWhite
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = DelajTextSecondary
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onChecked,
            colors = SwitchDefaults.colors(
                checkedThumbColor = DelajDarkBg,
                checkedTrackColor = DelajVoltGreen
            )
        )
    }
}
