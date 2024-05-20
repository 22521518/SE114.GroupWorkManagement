package com.example.groupworkmanagement

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.utils.CONSTANT
import com.example.groupworkmanagement.utils.handleException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore,
): ViewModel() {
    init {
        listenToUserList()
    }

    //USER
    val allUserList = mutableStateOf<List<AUser>>(listOf())
    private var isListenToUserList: ListenerRegistration? = null

    //LISTENER
    private fun listenToUserList() {
        isListenToUserList = store.collection(CONSTANT.USER_NODE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if (value != null) {
                    allUserList.value = value.documents.mapNotNull {
                        it.toObject<AUser>()
                    }
                }
            }
    }
}