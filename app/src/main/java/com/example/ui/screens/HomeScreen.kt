package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.BetSelection
import com.example.data.model.Match
import com.example.data.model.MatchStatus
import com.example.data.model.SportType
import com.example.ui.ScreenTab
import com.example.ui.components.MatchCard
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    matches: List<Match>,
    selections: List<BetSelection>,
    selectedFilter: String,
    onFilterChange: (String) -> Unit,
    onSelectOdds: (BetSelection) -> Unit,
    onWatchStream: (String) -> Unit,
    onNavigate: (ScreenTab) -> Unit
) {
    val liveMatches = matches.filter { it.status == MatchStatus.LIVE }
    val upcomingMatches = matches.filter { it.status == MatchStatus.UPCOMING }

    val filteredMatches = when (selectedFilter) {
        "LIVE" -> liveMatches
        "FOOTBALL" -> matches.filter { it.sport == SportType.FOOTBALL }
        "BASKETBALL" -> matches.filter { it.sport == SportType.BASKETBALL }
        else -> matches
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Hero Promo Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { onNavigate(ScreenTab.WALLET) }
            ) {
                // Background generated hero banner
                Image(
                    painter = painterResource(id = R.drawable.delajbet_hero_banner),
                    contentDescription = "DelajBet Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                // Overlay Text Content
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(14.dp)
                ) {
                    Surface(
                        color = DelajCrimson,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "WEEKEND SUPER ACCA BOOST",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "GET UP TO +300% MULTIPLE BONUS",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajVoltGreen
                    )
                    Text(
                        text = "Premier League • Champions League • Live Cash Out",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = DelajTextWhite
                    )
                }
            }
        }

        // Quick Shortcuts Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickActionChip(
                    icon = Icons.Default.LiveTv,
                    label = "Live Match",
                    color = DelajCrimson,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(ScreenTab.LIVE_STREAM) }
                )
                QuickActionChip(
                    icon = Icons.Default.AccountBalanceWallet,
                    label = "Deposit",
                    color = DelajVoltGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(ScreenTab.WALLET) }
                )
                QuickActionChip(
                    icon = Icons.Default.SwapHoriz,
                    label = "P2P Send",
                    color = DelajBlueAccent,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(ScreenTab.WALLET) }
                )
                QuickActionChip(
                    icon = Icons.Default.ReceiptLong,
                    label = "My Bets",
                    color = DelajGold,
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(ScreenTab.MY_BETS) }
                )
            }
        }

        // Category Filter Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Sports",
                    "LIVE" to "🔥 Live In-Play (${liveMatches.size})",
                    "FOOTBALL" to "⚽ Football",
                    "BASKETBALL" to "🏀 Basketball"
                )

                items(filters) { (key, label) ->
                    val isSelected = selectedFilter == key
                    Surface(
                        color = if (isSelected) DelajVoltGreen else DelajCardBg,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) DelajVoltGreen else DelajSurfaceBorder
                        ),
                        modifier = Modifier
                            .clickable { onFilterChange(key) }
                            .testTag("filter_$key")
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) DelajDarkBg else DelajTextWhite,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                        )
                    }
                }
            }
        }

        // Section Title: Live Games
        if (selectedFilter == "ALL" || selectedFilter == "LIVE") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
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
                            text = "LIVE IN-PLAY GAMES",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajTextWhite,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = "Instant Score Push Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = DelajVoltGreen
                    )
                }
            }

            items(liveMatches) { match ->
                MatchCard(
                    match = match,
                    selections = selections,
                    onSelectOdds = onSelectOdds,
                    onWatchStream = onWatchStream
                )
            }
        }

        // Section Title: Upcoming Games
        if (selectedFilter != "LIVE") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, top = 16.dp, bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "UPCOMING TOP FIXTURES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajTextWhite,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            items(upcomingMatches) { match ->
                MatchCard(
                    match = match,
                    selections = selections,
                    onSelectOdds = onSelectOdds,
                    onWatchStream = onWatchStream
                )
            }
        }
    }
}

@Composable
fun QuickActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = DelajCardBg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        modifier = modifier
            .height(58.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = color,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DelajTextWhite
            )
        }
    }
}
