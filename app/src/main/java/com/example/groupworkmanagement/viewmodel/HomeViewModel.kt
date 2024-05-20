package com.example.groupworkmanagement.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.groupworkmanagement.data.model.AChannel
import com.example.groupworkmanagement.data.model.AChatRoom
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.data.model.AMember
import com.example.groupworkmanagement.data.model.AMessage
import com.example.groupworkmanagement.data.model.ATask
import com.example.groupworkmanagement.data.model.ATaskChannel
import com.example.groupworkmanagement.data.model.AUser
import com.example.groupworkmanagement.data.model.ROLE
import com.example.groupworkmanagement.data.model.TASK_STATUS
import com.example.groupworkmanagement.utils.CONSTANT
import com.example.groupworkmanagement.utils.getToday
import com.example.groupworkmanagement.utils.handleException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore,
): ViewModel()  {
    //TEMP
    val insertList = mutableStateOf<List<AUser>>(listOf())

    //USER
    val currentUser = mutableStateOf<AUser?>(null)
    private var isListenToUser: ListenerRegistration? = null

    //CHAT
    val chatList = mutableStateOf<List<AChatRoom>>(listOf())
    private var isListenToChatList: ListenerRegistration?= null

    val currentChatRoom = mutableStateOf<AChatRoom?>(null)
    val messList = mutableStateOf<List<AMessage>>(listOf())
    private var isListenToMessList: ListenerRegistration?= null

    //TASK
    val userTask = mutableStateOf<List<ATask>>(listOf())
    private var isListenToTaskUser: ListenerRegistration? = null
    val doneTask = mutableStateOf<List<ATask>>(listOf())
    val lateTask = mutableStateOf<List<ATask>>(listOf())
    val inProgressTask = mutableStateOf<List<ATask>>(listOf())

    //GROUP
    val userGroup = mutableStateOf<List<AGroup>>(listOf())
    private var isListenToGroupUser: ListenerRegistration? = null
    val otherGroup = mutableStateOf<List<AGroup>>(listOf())
    private var isListenToOtherGroupUser: ListenerRegistration? = null

    //NODE
    private val userNode = store.collection(CONSTANT.USER_NODE)
    private val groupNode = store.collection(CONSTANT.GROUP_NODE)
    private val publicListNode = store.collection(CONSTANT.PUBLIC_LIST_NODE)
    private val channelNode = store.collection(CONSTANT.CHANNEL_NODE)
    private val taskNode = store.collection(CONSTANT.TASK_NODE)
    private val chatNode = store.collection(CONSTANT.CHAT_NODE)


    // Processing
    val processing = mutableStateOf(false)

    fun logOut() {
        deactivateListener()
    }

    fun activateListener() {
        try {
            auth.currentUser?.uid?.let { it ->
                listenToUser(it)
            }
        } catch (ex: Exception) {
            handleException(ex, ex.message)
        }
    }

    // destructor
    private fun deactivateListener() {
        currentUser.value = null;
        isListenToUser = null

        chatList.value = listOf()
        isListenToChatList = null

        currentChatRoom.value = null
        messList.value = listOf()
        isListenToMessList = null

        userGroup.value = listOf()
        isListenToGroupUser = null

        otherGroup.value = listOf()
        isListenToOtherGroupUser = null

        userTask.value = listOf()
        doneTask.value = listOf()
        lateTask.value = listOf()
        inProgressTask.value = listOf()
        isListenToTaskUser = null
    }

    fun deactivateChatRoom() {
        messList.value = listOf()
        currentChatRoom.value = null
        isListenToMessList = null
    }

    private fun listenToUser(uid: String) {
        processing.value = true
        isListenToUser = userNode.document(uid)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error ,error.message.toString())
                }
                if (value != null) {
                    val tmp = value.toObject<AUser>()
                    currentUser.value = tmp
                    listenToUserGroup(uid)
                    listenToUserOtherGroup(uid)
                    listenToUserTask(uid)
                    listenToChatList()
                    processing.value = false
                    println("listen to user" + currentUser.value.toString())
                }
            }
    }

    private fun listenToChatList() {
        currentUser.value?.let {currentUser->
            processing.value = true
            isListenToChatList = chatNode.where(
                Filter.or(
                    Filter.equalTo("user1.uid", currentUser.uid),
                    Filter.equalTo("user2.uid", currentUser.uid),
                )
            ).addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if (value != null) {
                    chatList.value = value.documents.mapNotNull {
                        it.toObject<AChatRoom>()
                    }
                }
                processing.value = false
            }
        }
    }

    fun getChatRoom(roomId: String) {
        currentUser.value?.let {currentUser->
//            processing.value = true
            isListenToMessList = chatNode.document(roomId)
                .collection(CONSTANT.MESSAGE_LIST)
                .addSnapshotListener { value, error ->
                    if(error != null) {
                        handleException(error, error.message)
                    }
                    if (value != null) {
                        messList.value = value.documents.mapNotNull {
                            it.toObject<AMessage>()
                        }.sortedBy {
                            LocalDateTime.parse(it.timestamp, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        }
                    }
                    processing.value = false
                }

        }
    }

    private fun listenToUserGroup(uid: String) {
        processing.value = true
        isListenToGroupUser = userNode.document(uid)
            .collection(CONSTANT.USER_GROUP_NODE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if(value != null) {
                    userGroup.value = value.documents.mapNotNull {
                        it.toObject<AGroup>()
                    }
                    processing.value = false
                    Log.d("Listen to gr user", userGroup.value.toString())
                }
            }
    }

    private fun listenToUserOtherGroup(uid: String) {
        processing.value = true
        isListenToOtherGroupUser = userNode.document(uid)
            .collection(CONSTANT.USER_OTHER_GROUP_NODE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if(value != null) {
                    otherGroup.value = value.documents.mapNotNull {
                        it.toObject<AGroup>()
                    }
                    processing.value = false
                    Log.d("Listen to other gr user", otherGroup.value.toString())
                }
            }
    }

    private fun listenToUserTask(uid: String) {
        processing.value = true
        isListenToTaskUser = userNode.document(uid)
            .collection(CONSTANT.TASK_LIST_NODE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if(value != null) {
                    userTask.value = value.documents.mapNotNull {
                        it.toObject<ATask>()
                    }
                    filterTask()
                    processing.value = false
                }

            }
    }

    private fun filterTask() {
        val inProcessList = arrayListOf<ATask>()
        val lateList = arrayListOf<ATask>()
        val doneList = arrayListOf<ATask>()

        userTask.value.forEach { tsk->
            val tmp = ATask (
                taskId = tsk.taskId,
                name = tsk.name,
                taskChannel = tsk.taskChannel,
                member = tsk.member,
                group = tsk.group,
                status = tsk.status,
                deadline = tsk.deadline,
            )
            if (tsk.deadline < getToday() && tsk.status != TASK_STATUS.DONE) tmp.status = TASK_STATUS.LATE

            when(tmp.status){
                TASK_STATUS.LATE ->
                    lateList.add(tmp)
                TASK_STATUS.IN_PROGRESS ->
                    inProcessList.add(tmp)
                else ->
                    doneList.add(tmp)
            }

            inProgressTask.value = inProcessList
            lateTask.value = lateList
            doneTask.value = doneList
        }
    }

    suspend fun reverseTask(tsk: ATask) {
        auth.currentUser?.let{user ->
            processing.value = true
            val tmp = ATask (
                taskId = tsk.taskId,
                name = tsk.name,
                taskChannel = tsk.taskChannel,
                member = tsk.member,
                group = tsk.group,
                status = TASK_STATUS.IN_PROGRESS,
                deadline = tsk.deadline,
            )

            taskNode.document(tmp.taskChannel.channelId!!)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.taskId).set(tmp)
            userNode.document(user.uid)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.taskId).set(tmp).await()
            filterTask()
            updateProgress(tmp.taskChannel)
            processing.value = false
        }
    }

    suspend fun completeTask(tsk: ATask) {
        auth.currentUser?.let{user ->
            processing.value = true
            val tmp = ATask (
                taskId = tsk.taskId,
                name = tsk.name,
                taskChannel = tsk.taskChannel,
                member = tsk.member,
                group = tsk.group,
                status = TASK_STATUS.DONE,
                deadline = tsk.deadline,
            )

            taskNode.document(tmp.taskChannel.channelId!!)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.taskId).set(tmp)
            userNode.document(user.uid)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.taskId).set(tmp).await()
            filterTask()
            updateProgress(tmp.taskChannel)
            processing.value = false
        }
    }

    private suspend fun  updateProgress(task: ATaskChannel) {
        val taskList = getTaskList(task)
        val haveDone = taskList.filter { it.status == TASK_STATUS.DONE }.size
        val total = taskList.size
        val temp = ATaskChannel(
            channelId = task.channelId,
            channelName = task.channelName,
            listMemberId = task.listMemberId,
            group = task.group,
            creator = task.creator,
            progress = task.progress,
            status = task.status,
            deadline = task.deadline,
            totalMember = task.totalMember
        )
        if(total == 0) {
            return
        }
        else if (temp.progress != (haveDone * 100 / total)) {
            temp.progress = (haveDone * 100 / total)
            taskNode.document(task.channelId!!).set(temp)
            groupNode.document(temp.group.groupId!!)
                .collection(CONSTANT.TASK_LIST_NODE).document(temp.channelId!!).set(temp)
        }
    }

    private suspend fun getTaskList(task: ATaskChannel): List<ATask> {
        val res = CompletableDeferred<List<ATask>>()
        taskNode.document(task.channelId!!).collection(CONSTANT.TASK_LIST_NODE)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if (value != null) {
                    val tmp = value.documents.mapNotNull {
                        it.toObject<ATask>()
                    }
                   res.complete(tmp)
                }
            }
        return res.await()
    }

    val groupName = mutableStateOf("")
    suspend fun createGroup(name: String, ls: List<AUser>) {
        currentUser.value?.let { curUser->
            processing.value = true
            val listId = createNewPublicList(ls)
            listId?.let { listid ->
                val grId = groupNode.document().id
                val gr = AGroup(
                    groupId = grId,
                    groupName = name,
                    creator =  curUser,
                    listMemberId = listid,
                    totalMember = 1 + ls.size,
                    imageUrl = null,
                    createdAt = getToday()
                )

                groupNode.document(grId).set(gr)
                userNode.document(curUser.uid)
                    .collection(CONSTANT.USER_GROUP_NODE).document(grId).set(gr)
                ls.forEach {
                    addNewGroup(it.uid, gr)
                }
                createGeneralPublicChannel(group = gr, listId, onCompleteListener = {
                    processing.value = false
                    insertList.value = listOf()
                })
            }
        }
    }

    private fun addNewGroup(uid: String, group: AGroup) {
        userNode.document(uid)
            .collection(CONSTANT.USER_OTHER_GROUP_NODE).document(group.groupId!!).set(group)
    }

    private suspend fun createNewPublicList(ls: List<AUser>): String? {
        val res = CompletableDeferred<String?>()
        currentUser.value?.uid?.let { uid ->
            val id = publicListNode.document().id

            ls.forEach { otherMem ->
                val tmp = AMember(
                    user = otherMem,
                    role = ROLE.member
                )
                publicListNode.document(id)
                    .collection(CONSTANT.MEMBER_NODE)
                    .document(tmp.user.uid).set(tmp).await()

            }

            val mem = AMember(
                user = currentUser.value!!,
                role = ROLE.admin
            )
            publicListNode.document(id).set(mapOf("listId" to id))
            publicListNode.document(id)
                .collection(CONSTANT.MEMBER_NODE)
                .document(mem.user.uid).set(mem).await()

            res.complete(id)
        }
        res.complete(null)
        return res.await()
    }

    private fun createGeneralPublicChannel(group: AGroup, listId: String, onCompleteListener: () -> Unit) {
        val tmp = createPublicChannel(name="chung", group = group, listId = listId)
        groupNode.document(group.groupId!!)
            .collection(CONSTANT.PUBLIC_LIST_NODE).document(tmp.channelId!!)
            .set(tmp)
            .addOnCompleteListener {
                onCompleteListener()
            }
    }

    private fun createPublicChannel(name: String, group: AGroup, listId: String): AChannel {
        val id = channelNode.document().id
        val tmp = AChannel(
            channelId = id,
            channelName = name,
            creator = group.creator,
            createdAt = getToday(),
            listMemberId = listId,
            totalMember = group.totalMember
        )

        channelNode.document(tmp.channelId!!).set(tmp)
        groupNode.document(group.groupId!!)
            .collection(CONSTANT.PUBLIC_LIST_NODE).document(tmp.channelId!!)
            .set(tmp)
        return  tmp
    }

    //CHAT
    val insertMem = mutableStateOf<AUser?>(null)
    suspend fun addChat(uid: String) {
        currentUser.value?.let {currentUser ->
            val response = chatNode.where(Filter.or(
                Filter.and(
                    Filter.equalTo("user1.uid", uid),
                    Filter.equalTo("user2.uid", currentUser.uid)
                ),
                Filter.and(
                    Filter.equalTo("user1.uid", currentUser.uid),
                    Filter.equalTo("user2.uid", uid)
                ),
            )).get().await()
            if (response.isEmpty) {
                val roomer = getUserData(uid)
                roomer?.let {partner ->
                    val roomId = chatNode.document().id
                    val temp = AChatRoom(
                        roomId = roomId,
                        user1 = currentUser,
                        user2 = partner
                    )

                    chatNode.document(temp.roomId).set(temp)
                }
            }
        }
    }

    //CHAT
    fun sendMessage(roomId: String, msg: String) {
        currentUser.value?.let {currentUser ->
            val msgId = chatNode.document(roomId)
                .collection(CONSTANT.MESSAGE_LIST).document().id

            val temp = AMessage(
                messId = msgId,
                content = msg,
                creator = currentUser
            )

            chatNode.document(roomId)
                .collection(CONSTANT.MESSAGE_LIST).document(temp.messId).set(temp)
        }
    }


    //TEMP -------------------------------------------------------
    private suspend fun getUserData(uid: String): AUser? {
        val res = CompletableDeferred<AUser?>()
        userNode.document(uid).get().addOnSuccessListener {
            res.complete(it.toObject<AUser>())
        }
        return res.await()
    }
}