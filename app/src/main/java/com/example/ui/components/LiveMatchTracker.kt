package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.theme.*

@Composable
fun LiveMatchTracker(
    match: Match,
    selections: List<BetSelection>,
    onSelectOdds: (BetSelection) -> Unit
) {
    var trackerMode by remember { mutableStateOf("RADAR") } // "RADAR" or "STREAM"
    var isMuted by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("live_match_tracker")
    ) {
        // Stream / Radar Header Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DelajElevatedBg)
                    .padding(2.dp)
            ) {
                Surface(
                    color = if (trackerMode == "STREAM") DelajVoltGreen else Color.Transparent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { trackerMode = "STREAM" }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LiveTv,
                            contentDescription = "Video Stream",
                            tint = if (trackerMode == "STREAM") DelajDarkBg else DelajTextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "HD STREAM",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trackerMode == "STREAM") DelajDarkBg else DelajTextWhite
                        )
                    }
                }

                Surface(
                    color = if (trackerMode == "RADAR") DelajVoltGreen else Color.Transparent,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.clickable { trackerMode = "RADAR" }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsSoccer,
                            contentDescription = "Pitch Radar",
                            tint = if (trackerMode == "RADAR") DelajDarkBg else DelajTextWhite,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "2D RADAR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (trackerMode == "RADAR") DelajDarkBg else DelajTextWhite
                        )
                    }
                }
            }

            // Stream Channel Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(DelajCrimson)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = match.streamChannelName,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DelajTextSecondary
                )
            }
        }

        // Live Viewer Canvas / Video Simulation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .padding(horizontal = 12.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(DelajCardBg)
                .border(1.dp, DelajSurfaceBorder, RoundedCornerShape(14.dp))
        ) {
            if (trackerMode == "STREAM") {
                // Video Simulation View
                LiveVideoStreamPlayer(
                    match = match,
                    isMuted = isMuted,
                    onToggleMute = { isMuted = !isMuted }
                )
            } else {
                // Interactive 2D Pitch Radar
                LivePitchRadar(match = match)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // In-Play Quick Odds Bar
        Surface(
            color = DelajCardBg,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⚡ IN-PLAY QUICK BET",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = DelajVoltGreen
                    )
                    Text(
                        text = "1X2 Markets",
                        fontSize = 10.sp,
                        color = DelajTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                val matchTitle = "${match.homeTeam} vs ${match.awayTeam}"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OddsButton(
                        label = match.homeTeam,
                        subLabel = "Home Win",
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
                        label = "Draw",
                        subLabel = "Tie",
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
                        label = match.awayTeam,
                        subLabel = "Away Win",
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
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Match Stats Bars
        MatchStatsSection(match = match)
    }
}

@Composable
fun LiveVideoStreamPlayer(
    match: Match,
    isMuted: Boolean,
    onToggleMute: () -> Unit
) {
    // Scanline & stadium light animation
    val infiniteTransition = rememberInfiniteTransition(label = "streamPulse")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "waveOffset"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF09121E), Color(0xFF02070D))
                )
            )
    ) {
        // Stadium backdrop effect
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Stadium glow arc
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFF00E676).copy(alpha = 0.15f), Color.Transparent),
                    center = Offset(width * 0.5f, height * 0.3f),
                    radius = width * 0.6f
                )
            )

            // Scanning radar wave line
            val lineY = height * waveOffset
            drawLine(
                color = Color(0xFF00E676).copy(alpha = 0.25f),
                start = Offset(0f, lineY),
                end = Offset(width, lineY),
                strokeWidth = 2f
            )
        }

        // Live Match Bug (Scoreboard Top Left)
        Surface(
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(DelajCrimson)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${match.homeTeam.take(3).uppercase()} ${match.homeScore} - ${match.awayScore} ${match.awayTeam.take(3).uppercase()}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${match.minute}'",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = DelajGold
                )
            }
        }

        // Watermark Top Right
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = DelajVoltGreen.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajVoltGreen)
            ) {
                Text(
                    text = "DELAJ LIVE HD",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = DelajVoltGreen,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            IconButton(
                onClick = onToggleMute,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Mute Audio",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Center Action Callout
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(DelajVoltGreen.copy(alpha = 0.2f))
                    .border(1.5.dp, DelajVoltGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = "Live Action",
                    tint = DelajVoltGreen,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajVoltGreen.copy(alpha = 0.5f))
            ) {
                Text(
                    text = match.pitchStatus,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = DelajTextWhite,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }
        }

        // Bottom Stream Overlay Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.6f))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎙️ Live English Commentary • Stadium 4K Feed",
                fontSize = 10.sp,
                color = DelajTextSecondary
            )
            Text(
                text = "34.8k viewers",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = DelajVoltGreen
            )
        }
    }
}

@Composable
fun LivePitchRadar(match: Match) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F2618)) // Grass Dark Green
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val linePaint = Color.White.copy(alpha = 0.35f)
            val strokeWidth = 2.dp.toPx()

            // Outer field boundary
            drawRect(
                color = linePaint,
                topLeft = Offset(12.dp.toPx(), 12.dp.toPx()),
                size = Size(width - 24.dp.toPx(), height - 24.dp.toPx()),
                style = Stroke(strokeWidth)
            )

            // Halfway line
            drawLine(
                color = linePaint,
                start = Offset(width / 2, 12.dp.toPx()),
                end = Offset(width / 2, height - 12.dp.toPx()),
                strokeWidth = strokeWidth
            )

            // Center circle
            drawCircle(
                color = linePaint,
                center = Offset(width / 2, height / 2),
                radius = 32.dp.toPx(),
                style = Stroke(strokeWidth)
            )

            // Left penalty box
            drawRect(
                color = linePaint,
                topLeft = Offset(12.dp.toPx(), height * 0.25f),
                size = Size(width * 0.16f, height * 0.5f),
                style = Stroke(strokeWidth)
            )

            // Right penalty box
            drawRect(
                color = linePaint,
                topLeft = Offset(width - 12.dp.toPx() - width * 0.16f, height * 0.25f),
                size = Size(width * 0.16f, height * 0.5f),
                style = Stroke(strokeWidth)
            )

            // Ball Position simulation
            val ballX = if (match.attackingTeam == match.homeTeam) width * 0.72f else if (match.attackingTeam == match.awayTeam) width * 0.28f else width * 0.5f
            val ballY = height * 0.5f

            // Ball pulse circle
            drawCircle(
                color = Color(0xFF00E676).copy(alpha = 0.3f),
                center = Offset(ballX, ballY),
                radius = 16.dp.toPx()
            )
            drawCircle(
                color = Color.White,
                center = Offset(ballX, ballY),
                radius = 6.dp.toPx()
            )
        }

        // Teams Label on Pitch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${match.homeTeam} (${match.homeScore})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Surface(
                color = Color.Black.copy(alpha = 0.6f),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "${match.awayTeam} (${match.awayScore})",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        // Center Status Banner
        Surface(
            color = Color.Black.copy(alpha = 0.85f),
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DelajVoltGreen),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
        ) {
            Text(
                text = "⚡ ${match.pitchStatus}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = DelajVoltGreen,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun MatchStatsSection(match: Match) {
    val stats = match.stats

    Surface(
        color = DelajCardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "MATCH STATISTICS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                color = DelajTextWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatBar(
                title = "Ball Possession",
                homeVal = "${stats.possessionHome}%",
                awayVal = "${stats.possessionAway}%",
                homePercent = stats.possessionHome / 100f
            )

            Spacer(modifier = Modifier.height(8.dp))

            val totalShots = (stats.shotsHome + stats.shotsAway).coerceAtLeast(1)
            StatBar(
                title = "Shots on Target",
                homeVal = "${stats.shotsOnTargetHome}",
                awayVal = "${stats.shotsOnTargetAway}",
                homePercent = stats.shotsOnTargetHome.toFloat() / (stats.shotsOnTargetHome + stats.shotsOnTargetAway).coerceAtLeast(1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatBar(
                title = "Dangerous Attacks",
                homeVal = "${stats.dangerousAttacksHome}",
                awayVal = "${stats.dangerousAttacksAway}",
                homePercent = stats.dangerousAttacksHome.toFloat() / (stats.dangerousAttacksHome + stats.dangerousAttacksAway).coerceAtLeast(1)
            )

            Spacer(modifier = Modifier.height(8.dp))

            StatBar(
                title = "Corner Kicks",
                homeVal = "${stats.cornersHome}",
                awayVal = "${stats.cornersAway}",
                homePercent = stats.cornersHome.toFloat() / (stats.cornersHome + stats.cornersAway).coerceAtLeast(1)
            )
        }
    }
}

@Composable
fun StatBar(
    title: String,
    homeVal: String,
    awayVal: String,
    homePercent: Float
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = homeVal, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DelajVoltGreen)
            Text(text = title, fontSize = 10.sp, color = DelajTextSecondary)
            Text(text = awayVal, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DelajGold)
        }

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(DelajElevatedBg)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(homePercent.coerceIn(0.05f, 0.95f))
                    .background(DelajVoltGreen)
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - homePercent).coerceIn(0.05f, 0.95f))
                    .background(DelajGold)
            )
        }
    }
}
