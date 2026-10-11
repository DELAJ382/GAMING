package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MfaMethod
import com.example.ui.MfaChallenge
import com.example.ui.theme.*

@Composable
fun MfaVerificationDialog(
    challenge: MfaChallenge,
    onDismiss: () -> Unit,
    onVerify: (String) -> Boolean
) {
    var codeInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccessMessage by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = DelajCardBg,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, DelajSurfaceBorder),
            shadowElevation = 16.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("mfa_dialog")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(DelajVoltGreen.copy(alpha = 0.15f))
                        .border(1.5.dp, DelajVoltGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (challenge.method) {
                            MfaMethod.SECURITY_PIN -> Icons.Default.Lock
                            MfaMethod.AUTHENTICATOR_APP -> Icons.Default.VpnKey
                            MfaMethod.SMS_OTP -> Icons.Default.Sms
                            MfaMethod.EMAIL_OTP -> Icons.Default.Email
                        },
                        contentDescription = "Security MFA",
                        tint = DelajVoltGreen,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = challenge.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = DelajTextWhite,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = challenge.subtitle,
                    fontSize = 12.sp,
                    color = DelajTextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Amount Pill
                Surface(
                    color = DelajElevatedBg,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Amount to Release:",
                            fontSize = 13.sp,
                            color = DelajTextSecondary
                        )
                        Text(
                            text = challenge.amountText,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = DelajVoltGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Input
                val isPin = challenge.method == MfaMethod.SECURITY_PIN
                val maxLen = if (isPin) 4 else 6

                Text(
                    text = if (isPin) "Enter 4-Digit Security PIN (Default: 1234)" else "Enter 6-Digit Verification Code",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = DelajTextWhite,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = codeInput,
                    onValueChange = {
                        if (it.length <= maxLen && it.all { char -> char.isDigit() }) {
                            codeInput = it
                            errorMessage = null
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mfa_input"),
                    visualTransformation = if (isPin) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = if (isPin) "••••" else "123456",
                            color = DelajTextMuted
                        )
                    },
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

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = errorMessage ?: "",
                        fontSize = 12.sp,
                        color = DelajCrimson,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick helpers for frictionless experience
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isPin) "Quick Test: Use 1234" else "Quick Test: 778899",
                        fontSize = 11.sp,
                        color = DelajGold,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                codeInput = if (isPin) "1234" else "778899"
                                errorMessage = null
                            }
                            .padding(4.dp)
                    )

                    // Biometric Simulation Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .clickable {
                                // Biometric passes immediately
                                val verified = onVerify(if (isPin) "1234" else "778899")
                                if (!verified) errorMessage = "Biometric mismatch"
                            }
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Biometric Auth",
                            tint = DelajVoltGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Use Biometrics",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = DelajVoltGreen
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = DelajTextSecondary
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DelajSurfaceBorder)
                    ) {
                        Text("Cancel", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val success = onVerify(codeInput)
                            if (!success) {
                                errorMessage = if (isPin) "Invalid PIN. Try 1234." else "Invalid OTP code. Must be 6 digits."
                            }
                        },
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .testTag("mfa_submit"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DelajVoltGreen,
                            contentColor = DelajDarkBg
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Confirm & Release",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
