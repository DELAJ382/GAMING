package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.ui.theme.*

@Composable
fun DelajBetTopBar(
    userProfile: UserProfile,
    onDepositClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSecurityClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Surface(
        color = DelajDarkBg,
        shadowElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onProfileClick() }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(DelajVoltGreen, DelajGold)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "DelajBet Logo",
                        tint = DelajDarkBg,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "DELAJ",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = DelajTextWhite,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "BET",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = DelajVoltGreen,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "LIVE SPORTS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = DelajGold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Wallet Balance & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Balance Pill
                Surface(
                    color = DelajCardBg,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                    modifier = Modifier
                        .clickable { onDepositClick() }
                        .testTag("balance_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "BALANCE",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextMuted
                            )
                            Text(
                                text = "$${String.format("%.2f", userProfile.balance)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = DelajVoltGreen
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(DelajVoltGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Quick Deposit",
                                tint = DelajDarkBg,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Security Shield Action
                IconButton(
                    onClick = onSecurityClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DelajCardBg)
                        .testTag("security_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "MFA Security",
                        tint = if (userProfile.is2faEnabled) DelajVoltGreen else DelajGold,
                        modifier = Modifier.size(19.dp)
                    )
                }

                // Notifications Bell
                IconButton(
                    onClick = onNotificationsClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(DelajCardBg)
                        .testTag("notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Score Notifications",
                        tint = DelajTextWhite,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
