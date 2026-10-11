package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BetSelection
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.ui.components.LiveMatchTracker
import com.example.ui.theme.*

@Composable
fun LiveStreamScreen(
    matches: List<Match>,
    selectedMatch: Match?,
    selections: List<BetSelection>,
    onSelectMatch: (String) -> Unit,
    onSelectOdds: (BetSelection) -> Unit
) {
    val liveMatches = matches.filter { it.status == MatchStatus.LIVE }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("live_stream_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Match Selector Tabs
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(DelajCrimson)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DELAJ LIVE MATCH CENTER",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajTextWhite
                        )
                    }

                    Text(
                        text = "${liveMatches.size} Active Channels",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajVoltGreen
                    )
                }

                // Match Selection Carousel
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(liveMatches) { match ->
                        val isCurrent = match.id == selectedMatch?.id
                        Surface(
                            color = if (isCurrent) DelajVoltGreen else DelajCardBg,
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isCurrent) DelajVoltGreen else DelajSurfaceBorder
                            ),
                            modifier = Modifier
                                .clickable { onSelectMatch(match.id) }
                                .width(180.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = match.league,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isCurrent) DelajDarkBg else DelajTextSecondary
                                    )
                                    Text(
                                        text = "${match.minute}'",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isCurrent) DelajDarkBg else DelajCrimson
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${match.homeTeam} ${match.homeScore} - ${match.awayScore} ${match.awayTeam}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCurrent) DelajDarkBg else DelajTextWhite,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Active Match Tracker (Video Player / Radar)
        if (selectedMatch != null) {
            item {
                LiveMatchTracker(
                    match = selectedMatch,
                    selections = selections,
                    onSelectOdds = onSelectOdds
                )
            }

            // Live Events Commentary Feed
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = DelajCardBg,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "LIVE COMMENTARY & TIMELINE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = DelajTextWhite
                            )

                            Text(
                                text = "Auto-Updating",
                                fontSize = 10.sp,
                                color = DelajVoltGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (selectedMatch.events.isEmpty()) {
                            Text(
                                text = "Match kicking off soon...",
                                fontSize = 12.sp,
                                color = DelajTextMuted
                            )
                        } else {
                            selectedMatch.events.take(6).forEach { event ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Surface(
                                        color = DelajElevatedBg,
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = "${event.minute}'",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Black,
                                            color = DelajGold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "${event.title} - ${event.teamName}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = DelajTextWhite
                                        )
                                        Text(
                                            text = event.description,
                                            fontSize = 11.sp,
                                            color = DelajTextSecondary
                                        )
                                    }
                                }
                                HorizontalDivider(color = DelajElevatedBg, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        } else {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No live match selected.",
                        color = DelajTextMuted
                    )
                }
            }
        }
    }
}
