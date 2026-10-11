package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetStatus
import com.example.data.model.PlacedBet
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MyBetsScreen(
    placedBets: List<PlacedBet>,
    onCashOut: (String) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("RUNNING") } // "RUNNING", "WON", "ALL"

    val filteredBets = when (selectedFilter) {
        "RUNNING" -> placedBets.filter { it.status == BetStatus.RUNNING }
        "WON" -> placedBets.filter { it.status == BetStatus.WON }
        "CASHED_OUT" -> placedBets.filter { it.status == BetStatus.CASHED_OUT }
        else -> placedBets
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("my_bets_screen"),
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
                    text = "MY BETS & SLIPS",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DelajTextWhite
                )
                Text(
                    text = "Track open bets in real-time or cash out early",
                    fontSize = 11.sp,
                    color = DelajTextSecondary
                )
            }
        }

        // Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "RUNNING" to "Open Bets (${placedBets.count { it.status == BetStatus.RUNNING }})",
                    "WON" to "Won",
                    "CASHED_OUT" to "Cashed Out",
                    "ALL" to "All"
                ).forEach { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        color = if (isSelected) DelajVoltGreen else DelajCardBg,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) DelajVoltGreen else DelajSurfaceBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFilter = key }
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                                color = if (isSelected) DelajDarkBg else DelajTextWhite
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (filteredBets.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No bets found in this section.",
                        color = DelajTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(filteredBets) { bet ->
                PlacedBetCard(
                    bet = bet,
                    onCashOut = { onCashOut(bet.id) }
                )
            }
        }
    }
}

@Composable
fun PlacedBetCard(
    bet: PlacedBet,
    onCashOut: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(bet.timestamp))

    Surface(
        color = DelajCardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("bet_card_${bet.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: ID and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "#${bet.id}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajTextWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(${bet.selections.size} Legs)",
                        fontSize = 11.sp,
                        color = DelajTextSecondary
                    )
                }

                Surface(
                    color = when (bet.status) {
                        BetStatus.RUNNING -> DelajGold.copy(alpha = 0.15f)
                        BetStatus.WON -> DelajVoltGreen.copy(alpha = 0.15f)
                        BetStatus.LOST -> DelajCrimson.copy(alpha = 0.15f)
                        BetStatus.CASHED_OUT -> DelajBlueAccent.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = bet.status.name,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = when (bet.status) {
                            BetStatus.RUNNING -> DelajGold
                            BetStatus.WON -> DelajVoltGreen
                            BetStatus.LOST -> DelajCrimson
                            BetStatus.CASHED_OUT -> DelajBlueAccent
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Text(
                text = formattedDate,
                fontSize = 10.sp,
                color = DelajTextMuted
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Legs summary
            bet.selections.forEach { sel ->
                Surface(
                    color = DelajElevatedBg,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = sel.matchTitle,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextWhite
                            )
                            Text(
                                text = "${sel.marketName}: ${sel.selectionName}",
                                fontSize = 10.sp,
                                color = DelajTextSecondary
                            )
                        }

                        Text(
                            text = String.format("%.2f", sel.odds),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajGold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Financial Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Stake", fontSize = 10.sp, color = DelajTextMuted)
                    Text(
                        text = "$${String.format("%.2f", bet.stake)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajTextWhite
                    )
                }

                Column {
                    Text(text = "Total Odds", fontSize = 10.sp, color = DelajTextMuted)
                    Text(
                        text = String.format("%.2f", bet.totalOdds),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajGold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Potential Return", fontSize = 10.sp, color = DelajTextMuted)
                    Text(
                        text = "$${String.format("%.2f", bet.potentialPayout)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajVoltGreen
                    )
                }
            }

            // Real-time Cash Out Button
            if (bet.status == BetStatus.RUNNING && bet.cashOutValue != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = onCashOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("cashout_button_${bet.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DelajGold,
                        contentColor = DelajDarkBg
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Cash Out",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "CASH OUT NOW ($${String.format("%.2f", bet.cashOutValue)})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
