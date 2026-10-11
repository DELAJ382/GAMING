package com.example.data.model

enum class MfaMethod(val title: String, val subtitle: String) {
    AUTHENTICATOR_APP("Authenticator App", "Google Authenticator, Authy or Duo TOTP code"),
    SMS_OTP("SMS Verification", "Instant one-time passcode sent to your registered phone"),
    EMAIL_OTP("Email Verification", "Instant verification link or code to your email"),
    SECURITY_PIN("4-Digit Transaction PIN", "Quick secure PIN required for withdrawals and transfers")
}

enum class KycTier(val tierName: String, val dailyLimit: Double) {
    TIER_1("Tier 1 (Phone Verified)", 500.0),
    TIER_2("Tier 2 (ID Verified)", 5000.0),
    TIER_3("Tier 3 (Enhanced Limits)", 50000.0)
}

data class LoginSession(
    val deviceName: String,
    val ipAddress: String,
    val location: String,
    val lastActive: String,
    val isCurrentDevice: Boolean
)

data class PushNotificationPreference(
    val scoreUpdates: Boolean = true,
    val goalAlerts: Boolean = true,
    val redCards: Boolean = true,
    val betSettlements: Boolean = true,
    val paymentAlerts: Boolean = true,
    val soundAndVibrate: Boolean = true
)

data class UserProfile(
    val userId: String = "DLB-894102",
    val username: String = "akin_champion",
    val fullName: String = "Wahab Akinade",
    val phone: String = "+234 812 *** 7890",
    val email: String = "akinadewahab31@gmail.com",
    val balance: Double = 1250.75,
    val bonusBalance: Double = 150.00,
    val kycTier: KycTier = KycTier.TIER_2,
    val is2faEnabled: Boolean = true,
    val activeMfaMethod: MfaMethod = MfaMethod.SECURITY_PIN,
    val transactionPinSet: Boolean = true,
    val biometricEnabled: Boolean = true,
    val notificationPrefs: PushNotificationPreference = PushNotificationPreference()
)

data class LiveScoreNotification(
    val id: String,
    val matchId: String,
    val matchTitle: String,
    val scoreText: String,
    val detail: String, // e.g., "⚽ GOAL! Bukayo Saka (82')"
    val timestamp: Long = System.currentTimeMillis()
)
