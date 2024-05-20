package com.example.groupworkmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.utils.CONSTANT
import com.example.groupworkmanagement.utils.handleException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore,
): ViewModel() {
    private val userNode = store.collection(CONSTANT.USER_NODE)

    private fun validateInputAuth(email: String, password: String): Boolean {
        return (email.isNotEmpty() && password.isNotEmpty() && password.length >= 6)
    }

    fun getCurrent(): String? {
        return auth.currentUser?.uid
    }

    suspend fun logIn(email: String, password: String,) {
        if(validateInputAuth(email, password)) {
           try {
               val res = auth.signInWithEmailAndPassword(email, password).await()
               res.user?.uid?.let {uid->
                   if (!isUserExisted(uid))
                       createOrUpdateNewUser(name = uid, uid = uid, email = email)
               }
           } catch (ex: Exception) {
               handleException(ex, ex.message)
           }
        }
        else {
            Log.d("LOG IN", "INVALID")
        }
    }

    suspend fun signUp(name: String, email: String, password: String, ) {
        if (validateInputAuth(email, password)) {
            val res = auth.createUserWithEmailAndPassword(email, password).await()
            res.user?.uid?.let {uid ->
                createOrUpdateNewUser(name = name, email = email, uid = uid)
            }
        } else {
            Log.d("SIGN UP", "INVALID INPUT")
        }
    }

    fun logOut() {
        auth.signOut()
    }

    private suspend fun isUserExisted(uid: String): Boolean {
        val res = CompletableDeferred<Boolean>()
        val response = userNode.document(uid).get().await()

        if (response.exists())
            res.complete(true)
        else
            res.complete(false)

        return res.await()
    }

    private fun createOrUpdateNewUser(uid: String, email: String, name: String) {
        val tmp = AUser(
            uid = uid,
            email = email,
            name = name,
        )
        userNode.document(uid).set(tmp)
    }
}