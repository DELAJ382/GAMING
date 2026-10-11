package com.example.ui.screens

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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Transaction
import com.example.data.model.TransactionStatus
import com.example.data.model.TransactionType
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionsScreen(
    transactions: List<Transaction>,
    selectedType: TransactionType?,
    onFilterChange: (TransactionType?) -> Unit
) {
    var selectedTxForReceipt by remember { mutableStateOf<Transaction?>(null) }

    val filteredList = if (selectedType == null) {
        transactions
    } else {
        transactions.filter {
            when (selectedType) {
                TransactionType.DEPOSIT -> it.type == TransactionType.DEPOSIT
                TransactionType.WITHDRAWAL -> it.type == TransactionType.WITHDRAWAL
                TransactionType.TRANSFER_SENT -> it.type == TransactionType.TRANSFER_SENT || it.type == TransactionType.TRANSFER_RECEIVED
                TransactionType.BET_PLACED -> it.type == TransactionType.BET_PLACED || it.type == TransactionType.BET_WON || it.type == TransactionType.BET_CASHOUT
                else -> it.type == selectedType
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("transactions_screen"),
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
                    text = "TRANSACTION & PAYMENT HISTORY",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DelajTextWhite
                )
                Text(
                    text = "Audit trail of all deposits, withdrawals, transfers, and bets",
                    fontSize = 11.sp,
                    color = DelajTextSecondary
                )
            }
        }

        // Filter Pills
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf(
                    null to "All Records (${transactions.size})",
                    TransactionType.DEPOSIT to "Deposits",
                    TransactionType.WITHDRAWAL to "Withdrawals",
                    TransactionType.TRANSFER_SENT to "Transfers",
                    TransactionType.BET_PLACED to "Bets & Payouts"
                )

                items(filters) { (type, label) ->
                    val isSelected = selectedType == type
                    Surface(
                        color = if (isSelected) DelajVoltGreen else DelajCardBg,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) DelajVoltGreen else DelajSurfaceBorder
                        ),
                        modifier = Modifier
                            .clickable { onFilterChange(type) }
                            .testTag("tx_filter_${label.take(4)}")
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.SemiBold,
                            color = if (isSelected) DelajDarkBg else DelajTextWhite,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (filteredList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No transaction records found in this category.",
                        color = DelajTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(filteredList) { tx ->
                TransactionCard(
                    tx = tx,
                    onClick = { selectedTxForReceipt = tx }
                )
            }
        }
    }

    // Receipt Modal
    selectedTxForReceipt?.let { tx ->
        TransactionReceiptDialog(
            transaction = tx,
            onDismiss = { selectedTxForReceipt = null }
        )
    }
}

@Composable
fun TransactionCard(
    tx: Transaction,
    onClick: () -> Unit
) {
    val isPositive = tx.type == TransactionType.DEPOSIT || tx.type == TransactionType.BET_WON || tx.type == TransactionType.BET_CASHOUT || tx.type == TransactionType.TRANSFER_RECEIVED
    val amountColor = if (isPositive) DelajVoltGreen else DelajCrimson
    val sign = if (isPositive) "+" else "-"

    val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(tx.timestamp))

    Surface(
        color = DelajCardBg,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 5.dp)
            .clickable { onClick() }
            .testTag("tx_item_${tx.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        when (tx.type) {
                            TransactionType.DEPOSIT -> DelajVoltGreen.copy(alpha = 0.15f)
                            TransactionType.WITHDRAWAL -> DelajCrimson.copy(alpha = 0.15f)
                            TransactionType.TRANSFER_SENT, TransactionType.TRANSFER_RECEIVED -> DelajBlueAccent.copy(alpha = 0.15f)
                            else -> DelajGold.copy(alpha = 0.15f)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (tx.type) {
                        TransactionType.DEPOSIT -> Icons.Default.ArrowDownward
                        TransactionType.WITHDRAWAL -> Icons.Default.ArrowUpward
                        TransactionType.TRANSFER_SENT -> Icons.Default.Send
                        TransactionType.TRANSFER_RECEIVED -> Icons.Default.CallReceived
                        TransactionType.BET_PLACED -> Icons.Default.SportsSoccer
                        TransactionType.BET_WON, TransactionType.BET_CASHOUT -> Icons.Default.EmojiEvents
                    },
                    contentDescription = tx.type.displayName,
                    tint = amountColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tx.type.displayName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DelajTextWhite
                )
                Text(
                    text = tx.destinationOrSource,
                    fontSize = 11.sp,
                    color = DelajTextSecondary
                )
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = DelajTextMuted
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$sign$${String.format("%.2f", tx.amount)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = amountColor
                )

                Spacer(modifier = Modifier.height(2.dp))

                Surface(
                    color = when (tx.status) {
                        TransactionStatus.COMPLETED -> DelajVoltGreen.copy(alpha = 0.15f)
                        TransactionStatus.PENDING -> DelajGold.copy(alpha = 0.15f)
                        TransactionStatus.FAILED -> DelajCrimson.copy(alpha = 0.15f)
                    },
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = tx.status.name,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (tx.status) {
                            TransactionStatus.COMPLETED -> DelajVoltGreen
                            TransactionStatus.PENDING -> DelajGold
                            TransactionStatus.FAILED -> DelajCrimson
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionReceiptDialog(
    transaction: Transaction,
    onDismiss: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM dd, yyyy • HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(transaction.timestamp))

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DelajCardBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DelajSurfaceBorder),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(DelajVoltGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = DelajDarkBg,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "TRANSACTION RECEIPT",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = DelajTextWhite
                )

                Text(
                    text = "DelajBet Instant Settlement System",
                    fontSize = 11.sp,
                    color = DelajTextMuted
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "$${String.format("%.2f", transaction.amount)}",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    color = DelajVoltGreen
                )

                Spacer(modifier = Modifier.height(14.dp))

                HorizontalDivider(color = DelajSurfaceBorder, thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                ReceiptRow(label = "Reference Code", value = transaction.reference)
                ReceiptRow(label = "Type", value = transaction.type.displayName)
                ReceiptRow(label = "Payment Channel", value = transaction.paymentMethod.displayName)
                ReceiptRow(label = "Destination / Source", value = transaction.destinationOrSource)
                ReceiptRow(label = "Status", value = transaction.status.name)
                ReceiptRow(label = "Processing Fee", value = "$0.00 (Free)")
                ReceiptRow(label = "Date & Time", value = formattedDate)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DelajVoltGreen,
                        contentColor = DelajDarkBg
                    )
                ) {
                    Text("Close Receipt", fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
fun ReceiptRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = DelajTextSecondary)
        Text(
            text = value,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DelajTextWhite,
            maxLines = 1
        )
    }
}
