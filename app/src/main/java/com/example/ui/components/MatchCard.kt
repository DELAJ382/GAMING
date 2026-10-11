package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetSelection
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.ui.theme.*

@Composable
fun MatchCard(
    match: Match,
    selections: List<BetSelection>,
    onSelectOdds: (BetSelection) -> Unit,
    onWatchStream: (String) -> Unit
) {
    val matchTitle = "${match.homeTeam} vs ${match.awayTeam}"

    Surface(
        color = DelajCardBg,
        shape = RoundedCornerShape(14.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .testTag("match_card_${match.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Header: League & Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = match.league,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DelajTextSecondary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (match.status == MatchStatus.LIVE) {
                        // Live Pulse Pill
                        Surface(
                            color = DelajCrimsonDark,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DelajCrimson)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(DelajCrimson)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE ${match.minute}'",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    } else {
                        Surface(
                            color = DelajElevatedBg,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Today 20:00",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = DelajTextMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    // Live Stream Button
                    if (match.isStreamAvailable) {
                        Surface(
                            color = DelajVoltGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DelajVoltGreen),
                            modifier = Modifier.clickable { onWatchStream(match.id) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircle,
                                    contentDescription = "Live Stream",
                                    tint = DelajVoltGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "STREAM",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DelajVoltGreen
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams & Score
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onWatchStream(match.id) },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Teams Column
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = match.homeTeam,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DelajTextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = match.awayTeam,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DelajTextWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Scores Box (if live)
                if (match.status == MatchStatus.LIVE) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "${match.homeScore}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajVoltGreen
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${match.awayScore}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajVoltGreen
                        )
                    }
                }
            }

            // Pitch commentary ticker if live
            if (match.status == MatchStatus.LIVE && match.pitchStatus.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(DelajElevatedBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⚡ ${match.pitchStatus}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = DelajGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Odds Row 1: 1X2 Market
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OddsButton(
                    label = "1",
                    subLabel = match.homeTeam.take(3).uppercase(),
                    odds = match.odds.homeWin,
                    isSelected = selections.any { it.matchId == match.id && it.marketName == "1X2" && it.selectionName == match.homeTeam },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectOdds(
                            BetSelection(
                                matchId = match.id,
                                matchTitle = matchTitle,
                                league = match.league,
                                marketName = "1X2",
                                selectionName = match.homeTeam,
                                odds = match.odds.homeWin
                            )
                        )
                    }
                )

                OddsButton(
                    label = "X",
                    subLabel = "Draw",
                    odds = match.odds.draw,
                    isSelected = selections.any { it.matchId == match.id && it.marketName == "1X2" && it.selectionName == "Draw" },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectOdds(
                            BetSelection(
                                matchId = match.id,
                                matchTitle = matchTitle,
                                league = match.league,
                                marketName = "1X2",
                                selectionName = "Draw",
                                odds = match.odds.draw
                            )
                        )
                    }
                )

                OddsButton(
                    label = "2",
                    subLabel = match.awayTeam.take(3).uppercase(),
                    odds = match.odds.awayWin,
                    isSelected = selections.any { it.matchId == match.id && it.marketName == "1X2" && it.selectionName == match.awayTeam },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectOdds(
                            BetSelection(
                                matchId = match.id,
                                matchTitle = matchTitle,
                                league = match.league,
                                marketName = "1X2",
                                selectionName = match.awayTeam,
                                odds = match.odds.awayWin
                            )
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Odds Row 2: Over/Under 2.5 & GG (BTTS)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OddsButton(
                    label = "Over 2.5",
                    subLabel = "Goals",
                    odds = match.odds.over25,
                    isSelected = selections.any { it.matchId == match.id && it.marketName == "Total Goals" && it.selectionName == "Over 2.5" },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectOdds(
                            BetSelection(
                                matchId = match.id,
                                matchTitle = matchTitle,
                                league = match.league,
                                marketName = "Total Goals",
                                selectionName = "Over 2.5",
                                odds = match.odds.over25
                            )
                        )
                    }
                )

                OddsButton(
                    label = "Under 2.5",
                    subLabel = "Goals",
                    odds = match.odds.under25,
                    isSelected = selections.any { it.matchId == match.id && it.marketName == "Total Goals" && it.selectionName == "Under 2.5" },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectOdds(
                            BetSelection(
                                matchId = match.id,
                                matchTitle = matchTitle,
                                league = match.league,
                                marketName = "Total Goals",
                                selectionName = "Under 2.5",
                                odds = match.odds.under25
                            )
                        )
                    }
                )

                OddsButton(
                    label = "GG (Yes)",
                    subLabel = "Both Score",
                    odds = match.odds.bttsYes,
                    isSelected = selections.any { it.matchId == match.id && it.marketName == "GG/NG" && it.selectionName == "GG (Yes)" },
                    modifier = Modifier.weight(1f),
                    onClick = {
                        onSelectOdds(
                            BetSelection(
                                matchId = match.id,
                                matchTitle = matchTitle,
                                league = match.league,
                                marketName = "GG/NG",
                                selectionName = "GG (Yes)",
                                odds = match.odds.bttsYes
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun OddsButton(
    label: String,
    subLabel: String,
    odds: Double,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) DelajVoltGreen else DelajOddsBg,
        label = "oddsBg"
    )
    val textColor = if (isSelected) DelajOddsSelectedText else DelajTextWhite
    val oddsColor = if (isSelected) DelajOddsSelectedText else DelajGold

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) DelajVoltGreen else DelajSurfaceBorder
        ),
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    maxLines = 1
                )
                Text(
                    text = subLabel,
                    fontSize = 8.sp,
                    color = if (isSelected) DelajDarkBg.copy(alpha = 0.8f) else DelajTextMuted,
                    maxLines = 1
                )
            }

            Text(
                text = String.format("%.2f", odds),
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = oddsColor
            )
        }
    }
}
