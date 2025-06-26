package com.example.smartzone.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import com.example.smartzone.FirebaseTestInitializer
import com.example.smartzone.R
import com.example.smartzone.ToastMatcher
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegistrationActivityTest {

    @Before
    fun setUp() {
        FirebaseTestInitializer.initIfNeeded()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    fun waitFor(delay: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints() = isRoot()
            override fun getDescription() = "Wait for $delay milliseconds."
            override fun perform(uiController: androidx.test.espresso.UiController, view: android.view.View?) {
                uiController.loopMainThreadForAtLeast(delay)
            }
        }
    }




    @Test
    fun allFieldsAreDisplayed() {
        ActivityScenario.launch(RegistrationActivity::class.java).use {
            onView(isRoot()).perform(waitFor(3000))

            onView(withId(R.id.firstNameEditText)).check(matches(isDisplayed()))
            onView(withId(R.id.lastNameEditText)).check(matches(isDisplayed()))
            onView(withId(R.id.emailEditText)).check(matches(isDisplayed()))
            onView(withId(R.id.passwordEditText)).check(matches(isDisplayed()))
            onView(withId(R.id.educationLevelSpinner)).check(matches(isDisplayed()))
            onView(withId(R.id.dateOfBirthEditText)).check(matches(isDisplayed()))
            onView(withId(R.id.registerButton)).check(matches(isDisplayed()))
            onView(withId(R.id.goToLogin)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun clickGoToLoginText_opensLoginActivity() {
        ActivityScenario.launch(RegistrationActivity::class.java)

        onView(withId(R.id.goToLogin)).perform(click())

        Thread.sleep(1000)

        intended(hasComponent(LoginActivity::class.java.name))
    }



}