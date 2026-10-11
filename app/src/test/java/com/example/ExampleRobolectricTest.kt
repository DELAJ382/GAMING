package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PaymentMethod
import com.example.data.repository.DelajBetRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches DelajBet`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("DelajBet", appName)
    }

    @Test
    fun `test deposit increases wallet balance`() {
        val repo = DelajBetRepository.getInstance()
        val initialBalance = repo.userProfile.value.balance
        repo.deposit(100.0, PaymentMethod.BANK_TRANSFER, "Test Deposit")
        assertEquals(initialBalance + 100.0, repo.userProfile.value.balance, 0.01)
    }

    @Test
    fun `test pin verification`() {
        val repo = DelajBetRepository.getInstance()
        assertTrue(repo.verifyPin("1234"))
    }
}
