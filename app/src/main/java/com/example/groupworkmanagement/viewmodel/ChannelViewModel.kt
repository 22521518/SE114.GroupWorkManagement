package com.example.groupworkmanagement.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.groupworkmanagement.data.model.AChannel
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.data.model.AMember
import com.example.groupworkmanagement.data.model.AMessage
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.data.model.ROLE
import com.example.groupworkmanagement.utils.CONSTANT
import com.example.groupworkmanagement.utils.handleException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChannelViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore,
): ViewModel() {
    val channelInstance = mutableStateOf<AChannel?>(null)
    val groupInstance = mutableStateOf<AGroup?>(null)

    //CHAT
    val listMessage = mutableStateOf<List<AMessage>>(listOf())
    private var isListenToMessage: ListenerRegistration? = null

    //MEMBER
    val memberList = mutableStateOf<List<AMember>>(listOf())
    private var isListenToMemberList: ListenerRegistration? = null

    //NODE--------------------------------------------------------
    private val userNode = store.collection(CONSTANT.USER_NODE)
    private val privateListNode = store.collection(CONSTANT.PRIAVTE_LIST_NODE)
    private val publicListNode = store.collection(CONSTANT.PUBLIC_LIST_NODE)
    private val channelNode = store.collection(CONSTANT.CHANNEL_NODE)
    private val groupNode = store.collection(CONSTANT.GROUP_NODE)

    //PROCESSING
    val processing = mutableStateOf(false)

    //LISTENER
    fun deactivateListener() {
        isListenToMemberList = null
        isListenToMessage = null

        memberList.value = listOf()
        listMessage.value = listOf()

        channelInstance.value = null
        groupInstance.value = null
    }

    fun getChannel(id: String, isPublic: Boolean) {
        processing.value = true
        channelNode.document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if (value != null) {
                    channelInstance.value = value.toObject<AChannel>()
                    activateListener(isPublic)
                }
                println(channelInstance.value.toString())
            }
    }

    private fun activateListener(isPublic: Boolean) {
        try {
            listenToMemberList(isPublic)
            listenToMessageList()
        } catch (ex: Exception) {
            handleException(ex, ex.message)
        }
    }

    private fun listenToMessageList() {
        channelInstance.value?.let{channel ->
            isListenToMessage = channelNode.document(channel.channelId!!)
                .collection(CONSTANT.CHANNEL_CHAT)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                        println("failed to connect chat list " + memberList.value)
                    }
                    if (value != null) {
                        listMessage.value = value.documents.mapNotNull {
                            it.toObject<AMessage>()
                        }.sortedBy {
                            LocalDateTime.parse(it.timestamp, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        }
                        println("is connect to chat list " + listMessage.value)
                    }
                }

        }
    }

    private fun listenToMemberList(isPublic: Boolean) {
        if(isPublic) listenToPublicMemberList()
        else listenToPrivateMemberList()
    }

    private fun listenToPrivateMemberList() {
       channelInstance.value?.let {channel ->
           isListenToMemberList = privateListNode.document(channel.listMemberId!!)
               .collection(CONSTANT.MEMBER_NODE)
               .addSnapshotListener { value, error ->
                   if (error != null) {
                       handleException(error, error.message)
                       println("failconnett  list " + memberList.value)
                   }
                   if (value != null) {
                       if (value.documents.size != memberList.value.size)
                           updatePrivateMember()

                       memberList.value = value.documents.mapNotNull {
                           it.toObject<AMember>()
                       }
                       println("is connect to member channel list " + memberList.value)
                   }
               }
       }
    }

    private fun listenToPublicMemberList() {
        channelInstance.value?.let {channel ->
            isListenToMemberList = publicListNode.document(channel.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                        println("failconnett  list " + memberList.value)
                    }
                    if (value != null) {
                        if (value.documents.size != memberList.value.size)
                            updatePublicMember()

                        memberList.value = value.documents.mapNotNull {
                            it.toObject<AMember>()
                        }
                        println("is connect to member channel list " + memberList.value)
                    }
                }
        }
    }

    //MEMBER FUNCTION
    val insertList = mutableStateOf<List<AUser>>(listOf())
    fun insertMember(list: List<AUser>) {
        channelInstance.value?.let {channel->
            processing.value = true
            list.forEach { mem ->
                val tmp = AMember(
                    user = mem,
                    role = ROLE.member
                )

                privateListNode.document(channel.listMemberId!!)
                    .collection(CONSTANT.MEMBER_NODE)
                    .document(tmp.user.uid).set(tmp)
                    .addOnCompleteListener {
                        processing.value = false
                    }
            }
        }
    }

    fun removeMember(uid: String) {
        channelInstance.value?.let {channel ->
            processing.value = true
            privateListNode.document(channel.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE).document(uid).delete()
                .addOnCompleteListener {
                    processing.value = false
                }
        }
    }

    private fun updatePrivateMember() {
        groupInstance.value?.let {gr->
            channelInstance.value?.let {ch ->
                processing.value = true
                privateListNode.document(ch.listMemberId!!)
                    .collection(CONSTANT.MEMBER_NODE).count().get(AggregateSource.SERVER)
                    .addOnCompleteListener { task->
                        if(task.isSuccessful) {
                            ch.totalMember = task.result.count.toInt()
                            channelNode.document(ch.channelId!!).set(ch)

                            groupNode.document(gr.groupId!!)
                                .collection(CONSTANT.PRIAVTE_LIST_NODE)
                                .document(ch.channelId!!).set(ch)

                        }
                        processing.value = false
                    }

            }
        }
    }

    private fun updatePublicMember() {
        groupInstance.value?.let {gr->
            channelInstance.value?.let {ch ->
                processing.value = true
                publicListNode.document(ch.listMemberId!!)
                    .collection(CONSTANT.MEMBER_NODE).count().get(AggregateSource.SERVER)
                    .addOnCompleteListener { task->
                        if(task.isSuccessful) {
                            ch.totalMember = task.result.count.toInt()
                            channelNode.document(ch.channelId!!).set(ch)

                            groupNode.document(gr.groupId!!)
                                .collection(CONSTANT.PUBLIC_LIST_NODE)
                                .document(ch.channelId!!).set(ch)

                        }
                        processing.value = false
                    }

            }
        }
    }

    //CHAT
    fun sendMessage(msg: String, user: AUser) {
        channelInstance.value?.let { channel->
            val msgId = channelNode.document(channel.channelId!!)
                .collection(CONSTANT.CHANNEL_CHAT).document().id
            val temp = AMessage(
                messId = msgId,
                content = msg,
                creator = user
            )

            channelNode.document(channel.channelId!!)
                .collection(CONSTANT.CHANNEL_CHAT).document(temp.messId).set(temp)
        }
    }
}