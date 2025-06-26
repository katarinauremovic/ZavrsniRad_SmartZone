package com.example.smartzone.helpers

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartzone.FirebaseTestInitializer
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class RegistrationIntegrationTest {

    private lateinit var registrationHelper: RegistrationHelper
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    @Before
    fun setUp() {
        FirebaseTestInitializer.initIfNeeded()

        val testApp = FirebaseApp.getInstance("test")
        auth = FirebaseAuth.getInstance(testApp)
        firestore = FirebaseFirestore.getInstance(testApp)

        registrationHelper = RegistrationHelper(auth, firestore)
    }

    @Test
    fun registerUser_successfulRegistration_savesUserInFirestore() {
        val email = "testuser_${System.currentTimeMillis()}@example.com"
        val password = "Test1234"
        val firstName = "Test"
        val lastName = "User"
        val education = "College"
        val birthDate = "01.01.2000."

        val latch = CountDownLatch(1)
        var success = false
        var error: String? = null

        registrationHelper.registerUser(
            email,
            password,
            firstName,
            lastName,
            education,
            birthDate,
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

        assertTrue("Expected registration to succeed but got error: $error", success)

        val userId = auth.currentUser?.uid
        assertNotNull("User ID should not be null", userId)

        runBlocking {
            val snapshot = firestore.collection("users").document(userId!!).get().await()
            assertTrue("User document should exist", snapshot.exists())
            assertEquals(firstName, snapshot.getString("firstName"))
            assertEquals(email, snapshot.getString("email"))
        }
    }

    @Test
    fun registerUser_weakPassword_triggersFailure() {
        val email = "weakpass_${System.currentTimeMillis()}@example.com"
        val password = "abc" // preslabo
        val latch = CountDownLatch(1)
        var success = false
        var error: String? = null

        registrationHelper.registerUser(
            email, password, "Test", "User", "College", "01.01.2000.",
            onSuccess = {
                success = true
                latch.countDown()
            },
            onFailure = {
                error = it
                latch.countDown()
            }
        )

        latch.await(5, TimeUnit.SECONDS)
        assertFalse("Registration should have failed for weak password", success)
        assertTrue("Error message expected", error?.contains("Password must be at least") == true)
    }

    @Test
    fun registerUser_emailAlreadyInUse_triggersFailure() {
        val email = "duplicate_${System.currentTimeMillis()}@example.com"
        val password = "Test1234"

        val latch1 = CountDownLatch(1)
        registrationHelper.registerUser(
            email, password, "Test", "User", "College", "01.01.2000.",
            onSuccess = { latch1.countDown() },
            onFailure = { latch1.countDown() }
        )
        latch1.await(10, TimeUnit.SECONDS)

        val latch2 = CountDownLatch(1)
        var error: String? = null
        registrationHelper.registerUser(
            email, password, "Test", "User", "College", "01.01.2000.",
            onSuccess = { latch2.countDown() },
            onFailure = {
                error = it
                latch2.countDown()
            }
        )

        latch2.await(10, TimeUnit.SECONDS)
        assertNotNull("Expected failure due to duplicate email", error)
        assertTrue("Expected error to mention email already in use", error!!.contains("email") || error!!.contains("already"))
    }


}
