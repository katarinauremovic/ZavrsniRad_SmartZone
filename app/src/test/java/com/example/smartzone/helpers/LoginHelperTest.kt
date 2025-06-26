package com.example.smartzone.helpers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class LoginHelperTest {

    private lateinit var helper: LoginHelper
    private val mockAuth: FirebaseAuth = mock()
    private val mockTask: Task<AuthResult> = mock()

    @Before
    fun setup() {
        helper = LoginHelper(mockAuth)
    }

    @Test
    fun loginUser_emptyFields_callsOnFailure() {
        var called = false
        var message = ""

        helper.loginUser("", "", onSuccess = {}, onFailure = {
            called = true
            message = it
        })

        assert(called)
        assert(message == "Please fill in all fields")
    }

    @Test
    fun loginUser_success_callsOnSuccess() {
        val email = "test@example.com"
        val password = "password123"
        var successCalled = false

        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)

        `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
            `when`(mockTask.isSuccessful).thenReturn(true)
            listener.onComplete(mockTask)
            mockTask
        }

        helper.loginUser(email, password,
            onSuccess = { successCalled = true },
            onFailure = { }
        )

        assert(successCalled)
    }

    @Test
    fun loginUser_failure_callsOnFailure() {
        val email = "test@example.com"
        val password = "wrongpassword"
        var failCalled = false
        var failMessage: String? = null

        val exception = Exception("Login failed")

        `when`(mockAuth.signInWithEmailAndPassword(email, password)).thenReturn(mockTask)

        `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<com.google.android.gms.tasks.OnCompleteListener<AuthResult>>(0)
            `when`(mockTask.isSuccessful).thenReturn(false)
            `when`(mockTask.exception).thenReturn(exception)
            listener.onComplete(mockTask)
            mockTask
        }

        helper.loginUser(email, password,
            onSuccess = { },
            onFailure = {
                failCalled = true
                failMessage = it
            }
        )

        assert(failCalled)
        assert(failMessage == "Login failed")
    }
}
