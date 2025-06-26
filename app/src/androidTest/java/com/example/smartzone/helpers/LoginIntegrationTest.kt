package com.example.smartzone.helpers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartzone.FirebaseTestInitializer
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class LoginIntegrationTest {

    private lateinit var loginHelper: LoginHelper
    private lateinit var auth: FirebaseAuth

    private val testEmail = "testloginuser@example.com"
    private val testPassword = "Test1234"

    @Before
    fun setUp() = runBlocking {
        FirebaseTestInitializer.initIfNeeded()

        val testApp = FirebaseApp.getInstance("test")
        auth = FirebaseAuth.getInstance(testApp)
        loginHelper = LoginHelper(auth)


        try {
            auth.signInWithEmailAndPassword(testEmail, testPassword).await()
            auth.signOut()
        } catch (e: Exception) {
            auth.createUserWithEmailAndPassword(testEmail, testPassword).await()
            auth.signOut()
        }
    }

    @Test
    fun loginUser_success() {
        val latch = CountDownLatch(1)
        var success = false
        var error: String? = null

        loginHelper.loginUser(
            testEmail,
            testPassword,
            onSuccess = {
                success = true
                latch.countDown()
            },
            onFailure = {
                error = it
                latch.countDown()
            }
        )

        latch.await(10, TimeUnit.SECONDS)
        assertTrue("Login should succeed, but got error: $error", success)
    }

    @Test
    fun loginUser_failure_wrongPassword() {
        val latch = CountDownLatch(1)
        var success = false
        var error: String? = null

        loginHelper.loginUser(
            testEmail,
            "WrongPassword123",
            onSuccess = {
                success = true
                latch.countDown()
            },
            onFailure = {
                error = it
                latch.countDown()
            }
        )

        latch.await(10, TimeUnit.SECONDS)
        assertFalse("Login should fail with wrong password", success)
        assertNotNull("Error message should be provided", error)
    }

    @Test
    fun loginUser_failure_emptyFields() {
        val latch = CountDownLatch(1)
        var error: String? = null

        loginHelper.loginUser(
            "",
            "",
            onSuccess = { latch.countDown() },
            onFailure = {
                error = it
                latch.countDown()
            }
        )

        latch.await(10, TimeUnit.SECONDS)
        assertEquals("Please fill in all fields", error)
    }
}
