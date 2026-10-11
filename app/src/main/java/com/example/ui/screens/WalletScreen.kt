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
import com.example.data.model.PaymentMethod
import com.example.data.model.UserProfile
import com.example.ui.ScreenTab
import com.example.ui.theme.*

@Composable
fun WalletScreen(
    userProfile: UserProfile,
    onDeposit: (Double, PaymentMethod, String) -> Unit,
    onWithdraw: (Double, String, String, String) -> Unit,
    onTransfer: (String, Double, String) -> Unit,
    onNavigate: (ScreenTab) -> Unit
) {
    var activeTab by remember { mutableStateOf("DEPOSIT") } // "DEPOSIT", "WITHDRAW", "TRANSFER"

    // Deposit state
    var depositAmountText by remember { mutableStateOf("50") }
    var selectedDepositMethod by remember { mutableStateOf(PaymentMethod.BANK_TRANSFER) }

    // Withdraw state
    var withdrawAmountText by remember { mutableStateOf("100") }
    var selectedBank by remember { mutableStateOf("GTBank") }
    var accountNumber by remember { mutableStateOf("0124891044") }
    var accountName by remember { mutableStateOf("Wahab Akinade") }

    // Transfer state
    var recipientId by remember { mutableStateOf("DLB-409122") }
    var transferAmountText by remember { mutableStateOf("25") }
    var transferNote by remember { mutableStateOf("DelajBet transfer") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Balance Card
        item {
            Surface(
                color = DelajCardBg,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, DelajSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                listOf(DelajCardBg, DelajElevatedBg)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTAL WALLET BALANCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$${String.format("%.2f", userProfile.balance)}",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                color = DelajVoltGreen
                            )
                        }

                        // KYC Tier Pill
                        Surface(
                            color = DelajGreenDark,
                            shape = RoundedCornerShape(20.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DelajVoltGreen)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser,
                                    contentDescription = "KYC Tier",
                                    tint = DelajVoltGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = userProfile.kycTier.tierName.take(6),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DelajVoltGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    HorizontalDivider(color = DelajSurfaceBorder, thickness = 1.dp)

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Bonus Credits", fontSize = 10.sp, color = DelajTextMuted)
                            Text(
                                text = "$${String.format("%.2f", userProfile.bonusBalance)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajGold
                            )
                        }

                        Column {
                            Text(text = "Daily Limit", fontSize = 10.sp, color = DelajTextMuted)
                            Text(
                                text = "$${String.format("%.0f", userProfile.kycTier.dailyLimit)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextWhite
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "User ID", fontSize = 10.sp, color = DelajTextMuted)
                            Text(
                                text = userProfile.userId,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajVoltGreen
                            )
                        }
                    }
                }
            }
        }

        // Action Tabs: Deposit, Withdraw, Transfer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WalletTabButton(
                    title = "Deposit",
                    icon = Icons.Default.AddCircle,
                    isSelected = activeTab == "DEPOSIT",
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = "DEPOSIT" }
                )
                WalletTabButton(
                    title = "Withdraw",
                    icon = Icons.Default.ArrowCircleUp,
                    isSelected = activeTab == "WITHDRAW",
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = "WITHDRAW" }
                )
                WalletTabButton(
                    title = "Transfer",
                    icon = Icons.Default.SwapHoriz,
                    isSelected = activeTab == "TRANSFER",
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = "TRANSFER" }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- DEPOSIT CONTENT ---
        if (activeTab == "DEPOSIT") {
            item {
                Surface(
                    color = DelajCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "INSTANT DEPOSIT (FUND WALLET)",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajTextWhite
                        )
                        Text(
                            text = "Zero processing fees • Instant account crediting",
                            fontSize = 11.sp,
                            color = DelajTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Amount Quick Chips
                        Text(text = "Select Amount ($)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DelajTextWhite)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(10, 25, 50, 100, 250, 500).forEach { amt ->
                                Surface(
                                    color = if (depositAmountText == amt.toString()) DelajVoltGreen else DelajElevatedBg,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .clickable { depositAmountText = amt.toString() }
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "$$amt",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (depositAmountText == amt.toString()) DelajDarkBg else DelajTextWhite
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = depositAmountText,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) depositAmountText = it },
                            label = { Text("Custom Deposit Amount") },
                            prefix = { Text("$", color = DelajVoltGreen, fontWeight = FontWeight.Bold) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(14.dp))

                        // Payment Methods
                        Text(text = "Payment Method", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DelajTextWhite)
                        Spacer(modifier = Modifier.height(8.dp))

                        val methods = listOf(
                            PaymentMethod.BANK_TRANSFER to "Instant Bank Virtual Transfer",
                            PaymentMethod.DEBIT_CARD to "Debit / Credit Card (Visa/Mastercard)",
                            PaymentMethod.OPAY to "OPay / PalmPay Instant Pay",
                            PaymentMethod.CRYPTO to "USDT Crypto TRC20"
                        )

                        methods.forEach { (method, title) ->
                            val isSelected = selectedDepositMethod == method
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
                                    .clickable { selectedDepositMethod = method }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { selectedDepositMethod = method },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = DelajVoltGreen,
                                            unselectedColor = DelajTextMuted
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isSelected) DelajVoltGreen else DelajTextWhite
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = depositAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    onDeposit(amt, selectedDepositMethod, "DelajBet Instant Gateway")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("confirm_deposit_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DelajVoltGreen,
                                contentColor = DelajDarkBg
                            )
                        ) {
                            Text(
                                text = "COMPLETE DEPOSIT ($${depositAmountText})",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // --- WITHDRAW CONTENT ---
        if (activeTab == "WITHDRAW") {
            item {
                Surface(
                    color = DelajCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "INSTANT WITHDRAWAL",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = DelajTextWhite
                            )

                            // Security Shield Tag
                            Surface(
                                color = DelajGreenDark,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "🔒 2FA PROTECTED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DelajVoltGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Direct payout to bank account • Instant delivery",
                            fontSize = 11.sp,
                            color = DelajTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = withdrawAmountText,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) withdrawAmountText = it },
                            label = { Text("Withdrawal Amount ($)") },
                            prefix = { Text("$", color = DelajVoltGreen, fontWeight = FontWeight.Bold) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
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

                        // Bank selector quick chips
                        Text(text = "Select Destination Bank", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DelajTextWhite)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val banks = listOf("GTBank", "Access Bank", "Zenith Bank", "First Bank", "Kuda Microfinance", "Revolut")
                            items(banks) { b ->
                                val isSelected = selectedBank == b
                                Surface(
                                    color = if (isSelected) DelajVoltGreen else DelajElevatedBg,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .clickable { selectedBank = b }
                                ) {
                                    Text(
                                        text = b,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) DelajDarkBg else DelajTextWhite,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = accountNumber,
                            onValueChange = { if (it.all { c -> c.isDigit() }) accountNumber = it },
                            label = { Text("Account Number") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
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

                        OutlinedTextField(
                            value = accountName,
                            onValueChange = { accountName = it },
                            label = { Text("Account Holder Full Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = withdrawAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0) {
                                    onWithdraw(amt, selectedBank, accountNumber, accountName)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("request_withdrawal_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DelajVoltGreen,
                                contentColor = DelajDarkBg
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTHORIZE WITHDRAWAL ($${withdrawAmountText})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // --- TRANSFER CONTENT ---
        if (activeTab == "TRANSFER") {
            item {
                Surface(
                    color = DelajCardBg,
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DELAJBET P2P TRANSFER",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = DelajTextWhite
                            )

                            Surface(
                                color = DelajGreenDark,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "⚡ INSTANT P2P",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DelajVoltGreen,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = "Send funds directly to friends or bettors via User ID",
                            fontSize = 11.sp,
                            color = DelajTextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Known users quick select
                        Text(text = "Frequent Beneficiaries", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = DelajTextWhite)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "DLB-409122" to "@Dave_Winner",
                                "DLB-771239" to "@Bola_Stakes",
                                "DLB-559012" to "@Chidi_Goals"
                            ).forEach { (id, name) ->
                                Surface(
                                    color = if (recipientId == id) DelajVoltGreen else DelajElevatedBg,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { recipientId = id }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = name,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (recipientId == id) DelajDarkBg else DelajTextWhite
                                        )
                                        Text(
                                            text = id,
                                            fontSize = 8.sp,
                                            color = if (recipientId == id) DelajDarkBg else DelajTextMuted
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = recipientId,
                            onValueChange = { recipientId = it },
                            label = { Text("Recipient User ID or Phone Number") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
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

                        OutlinedTextField(
                            value = transferAmountText,
                            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) transferAmountText = it },
                            label = { Text("Transfer Amount ($)") },
                            prefix = { Text("$", color = DelajVoltGreen, fontWeight = FontWeight.Bold) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth(),
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

                        OutlinedTextField(
                            value = transferNote,
                            onValueChange = { transferNote = it },
                            label = { Text("Note / Memo (Optional)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val amt = transferAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0 && recipientId.isNotBlank()) {
                                    onTransfer(recipientId, amt, transferNote)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("send_transfer_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DelajVoltGreen,
                                contentColor = DelajDarkBg
                            )
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AUTHORIZE P2P TRANSFER ($${transferAmountText})",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // Shortcut to History
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = DelajElevatedBg,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clickable { onNavigate(ScreenTab.HISTORY) }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Transactions",
                            tint = DelajVoltGreen
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "View Transaction History",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = DelajTextWhite
                            )
                            Text(
                                text = "Access deposits, withdrawals, transfers & receipts",
                                fontSize = 11.sp,
                                color = DelajTextSecondary
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Go",
                        tint = DelajTextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun WalletTabButton(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) DelajVoltGreen else DelajCardBg,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) DelajVoltGreen else DelajSurfaceBorder
        ),
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) DelajDarkBg else DelajVoltGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) DelajDarkBg else DelajTextWhite
            )
        }
    }
}
