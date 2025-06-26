package com.example.smartzone.helpers

import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.mockito.Mockito.*
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

class RegistrationHelperTest {

    private lateinit var helper: RegistrationHelper

    private lateinit var mockAuth: FirebaseAuth
    private lateinit var mockFirestore: FirebaseFirestore
    private lateinit var mockTask: Task<AuthResult>
    private lateinit var mockAuthResult: AuthResult
    private lateinit var mockFirebaseUser: FirebaseUser
    private lateinit var mockCollection: CollectionReference
    private lateinit var mockDocument: DocumentReference

    @Before
    fun setup() {
        mockAuth = mock()
        mockFirestore = mock()
        mockTask = mock()
        mockAuthResult = mock()
        mockFirebaseUser = mock()
        mockCollection = mock()
        mockDocument = mock()

        helper = RegistrationHelper(mockAuth, mockFirestore)

        `when`(mockFirestore.collection("users")).thenReturn(mockCollection)
        `when`(mockCollection.document("mockUserId")).thenReturn(mockDocument)
    }

    @Test
    fun registerUser_success_calls_onSuccess() {
        val email = "test@example.com"
        val password = "Password123"
        val firstName = "Test"
        val lastName = "User"
        val education = "College"
        val birthDate = "01/01/2000"

        var successCalled = false
        var failMessage: String? = null

        val mockVoidTask = mock<Task<Void>>()

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockDocument.set(any())).thenReturn(mockVoidTask)

        `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(mockAuthResult)
            `when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
            `when`(mockFirebaseUser.uid).thenReturn("mockUserId")
            listener.onComplete(mockTask)
            mockTask
        }

        `when`(mockVoidTask.addOnSuccessListener(any())).thenAnswer {
            val listener = it.getArgument<com.google.android.gms.tasks.OnSuccessListener<Void>>(0)
            listener.onSuccess(null)
            mockVoidTask
        }

        `when`(mockVoidTask.addOnFailureListener(any())).thenReturn(mockVoidTask)

        helper.registerUser(email, password, firstName, lastName, education, birthDate,
            onSuccess = { successCalled = true },
            onFailure = { failMessage = it })

        assertTrue(successCalled)
        assertNull(failMessage)
    }

    @Test
    fun registerUser_userIdIsNull_callsOnFailure() {
        val email = "test@example.com"
        val password = "Password123"

        var successCalled = false
        var failMessage: String? = null

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(mockAuthResult)
            `when`(mockAuthResult.user).thenReturn(null)
            listener.onComplete(mockTask)
            mockTask
        }

        helper.registerUser(email, password, "Test", "User", "College", "01/01/2000",
            onSuccess = { successCalled = true },
            onFailure = { failMessage = it })

        assertFalse(successCalled)
        assertEquals("User ID is null", failMessage)
    }

    @Test
    fun registerUser_authFails_callsOnFailure() {
        val email = "test@example.com"
        val password = "Wrongpass1"

        var successCalled = false
        var failMessage: String? = null

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            `when`(mockTask.isSuccessful).thenReturn(false)
            `when`(mockTask.exception).thenReturn(Exception("Authentication failed"))
            listener.onComplete(mockTask)
            mockTask
        }

        helper.registerUser(email, password, "Test", "User", "College", "01/01/2000",
            onSuccess = { successCalled = true },
            onFailure = { failMessage = it })

        assertFalse(successCalled)
        assertEquals("Authentication failed", failMessage)
    }

    @Test
    fun registerUser_firestoreFails_callsOnFailure() {
        val email = "test@example.com"
        val password = "Password123"
        val firstName = "Test"
        val lastName = "User"
        val education = "College"
        val birthDate = "01/01/2000"

        var successCalled = false
        var failMessage: String? = null

        val firestoreError = Exception("Firestore write failed")
        val mockVoidTask = mock<Task<Void>>()

        `when`(mockAuth.createUserWithEmailAndPassword(email, password)).thenReturn(mockTask)
        `when`(mockDocument.set(any())).thenReturn(mockVoidTask)

        `when`(mockTask.addOnCompleteListener(any())).thenAnswer { invocation ->
            val listener = invocation.getArgument<OnCompleteListener<AuthResult>>(0)
            `when`(mockTask.isSuccessful).thenReturn(true)
            `when`(mockTask.result).thenReturn(mockAuthResult)
            `when`(mockAuthResult.user).thenReturn(mockFirebaseUser)
            `when`(mockFirebaseUser.uid).thenReturn("mockUserId")
            listener.onComplete(mockTask)
            mockTask
        }

        `when`(mockVoidTask.addOnFailureListener(any())).thenAnswer {
            val listener = it.getArgument<com.google.android.gms.tasks.OnFailureListener>(0)
            listener.onFailure(firestoreError)
            mockVoidTask
        }

        `when`(mockVoidTask.addOnSuccessListener(any())).thenReturn(mockVoidTask)

        helper.registerUser(email, password, firstName, lastName, education, birthDate,
            onSuccess = { successCalled = true },
            onFailure = { failMessage = it })

        assertFalse(successCalled)
        assertEquals("Firestore write failed", failMessage)
    }
}
