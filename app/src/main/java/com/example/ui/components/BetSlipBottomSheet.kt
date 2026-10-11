package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetSelection
import com.example.ui.theme.*

@Composable
fun BetSlipBar(
    selections: List<BetSelection>,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    if (selections.isEmpty()) return

    val totalOdds = selections.fold(1.0) { acc, sel -> acc * sel.odds }
    val roundedOdds = Math.round(totalOdds * 100.0) / 100.0

    Surface(
        color = DelajElevatedBg,
        shadowElevation = 12.dp,
        border = androidx.compose.foundation.BorderStroke(1.5.dp, DelajVoltGreen),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("betslip_bar")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(DelajVoltGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${selections.size}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajDarkBg
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "BET SLIP",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajTextWhite
                    )
                    Text(
                        text = "${selections.size} ${if (selections.size > 1) "Selections (Acca)" else "Selection"}",
                        fontSize = 11.sp,
                        color = DelajTextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total Odds",
                        fontSize = 10.sp,
                        color = DelajTextMuted
                    )
                    Text(
                        text = String.format("%.2f", roundedOdds),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajGold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Expand",
                    tint = DelajVoltGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun BetSlipDrawer(
    selections: List<BetSelection>,
    balance: Double,
    onRemoveSelection: (String, String) -> Unit,
    onClearAll: () -> Unit,
    onClose: () -> Unit,
    onPlaceBet: (Double) -> Unit
) {
    var stakeText by remember { mutableStateOf("10") }
    val stake = stakeText.toDoubleOrNull() ?: 0.0

    val totalOdds = selections.fold(1.0) { acc, sel -> acc * sel.odds }
    val roundedOdds = Math.round(totalOdds * 100.0) / 100.0

    // Acca Bonus
    val bonusRate = when {
        selections.size <= 1 -> 0.0
        selections.size == 2 -> 0.05
        selections.size == 3 -> 0.10
        selections.size == 4 -> 0.15
        selections.size in 5..7 -> 0.25
        else -> 0.50
    }
    val baseReturn = stake * roundedOdds
    val bonusAmount = baseReturn * bonusRate
    val potentialPayout = baseReturn + bonusAmount

    Surface(
        color = DelajCardBg,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        shadowElevation = 24.dp,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.78f)
            .testTag("betslip_drawer")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "BET SLIP",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajTextWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(DelajVoltGreen)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${selections.size}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajDarkBg
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Clear All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajCrimson,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable { onClearAll() }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Bet Slip",
                            tint = DelajTextWhite
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Selections List
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(selections) { item ->
                    Surface(
                        color = DelajElevatedBg,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.matchTitle,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DelajTextWhite
                                )
                                Text(
                                    text = "${item.marketName} • Selection: ${item.selectionName}",
                                    fontSize = 11.sp,
                                    color = DelajTextSecondary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = String.format("%.2f", item.odds),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DelajGold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(
                                    onClick = { onRemoveSelection(item.matchId, item.marketName) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove",
                                        tint = DelajTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Acca Bonus Banner
            if (selections.size >= 2) {
                Surface(
                    color = DelajGreenDark,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DelajVoltGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚡ ACCA BOOST:",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = DelajVoltGreen
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "+${(bonusRate * 100).toInt()}% Extra Payout",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajGold
                            )
                        }
                        Text(
                            text = "+$${String.format("%.2f", bonusAmount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajVoltGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Quick Stake Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(5, 10, 25, 50, 100).forEach { amt ->
                    Surface(
                        color = if (stakeText == amt.toString()) DelajVoltGreen else DelajElevatedBg,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(32.dp)
                            .clickable { stakeText = amt.toString() }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "$$amt",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (stakeText == amt.toString()) DelajDarkBg else DelajTextWhite
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stake Input Field
            OutlinedTextField(
                value = stakeText,
                onValueChange = {
                    if (it.isEmpty() || it.all { c -> c.isDigit() || c == '.' }) {
                        stakeText = it
                    }
                },
                label = { Text("Stake Amount ($)") },
                prefix = { Text("$", color = DelajVoltGreen, fontWeight = FontWeight.Bold) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("stake_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = DelajVoltGreen,
                    unfocusedBorderColor = DelajSurfaceBorder,
                    focusedTextColor = DelajTextWhite,
                    unfocusedTextColor = DelajTextWhite,
                    focusedContainerColor = DelajElevatedBg,
                    unfocusedContainerColor = DelajElevatedBg
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Totals Row
            Surface(
                color = DelajElevatedBg,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Odds:", fontSize = 12.sp, color = DelajTextSecondary)
                        Text(text = String.format("%.2f", roundedOdds), fontSize = 13.sp, fontWeight = FontWeight.Black, color = DelajGold)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Potential Payout:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = DelajTextWhite)
                        Text(
                            text = "$${String.format("%.2f", potentialPayout)}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajVoltGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Place Bet Button
            Button(
                onClick = { onPlaceBet(stake) },
                enabled = stake > 0 && selections.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("place_bet_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = DelajVoltGreen,
                    contentColor = DelajDarkBg,
                    disabledContainerColor = DelajElevatedBg,
                    disabledContentColor = DelajTextMuted
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "PLACE BET ($${String.format("%.2f", stake)})",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}
