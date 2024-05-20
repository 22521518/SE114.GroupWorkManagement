package com.example.groupworkmanagement.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.groupworkmanagement.data.model.AChannel
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
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.tasks.await
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class TaskChannelViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore,
): ViewModel() {

    //GROUP--------------------------------------------------------


    //TEMP--------------------------------------------------------

    val taskChannelInstance =  mutableStateOf<ATaskChannel?>(null)

    //CHAT
    val listMessage = mutableStateOf<List<AMessage>>(listOf())
    private var isListenToMessage: ListenerRegistration? = null

    //MEMBER
    val memberList = mutableStateOf<List<AMember>>(listOf())
    private var isListenToMemberList: ListenerRegistration? = null

    //TASK
    val taskList = mutableStateOf<List<ATask>>(listOf())
    private var isListenToTaskList: ListenerRegistration? = null


    val myTaskList = mutableStateOf<List<ATask>>(listOf())
    val lateTaskList = mutableStateOf<List<ATask>>(listOf())
    val doneTaskList = mutableStateOf<List<ATask>>(listOf())
    val inProcessTaskList = mutableStateOf<List<ATask>>(listOf())


    //NODE--------------------------------------------------------
    private val userNode = store.collection(CONSTANT.USER_NODE)
    private val privateListNode = store.collection(CONSTANT.PRIAVTE_LIST_NODE)
    private val publicListNode = store.collection(CONSTANT.PUBLIC_LIST_NODE)
    private val taskNode = store.collection(CONSTANT.TASK_NODE)
    private val groupNode = store.collection(CONSTANT.GROUP_NODE)

    //PROCESSING
    val processing = mutableStateOf(false)

    //LISTENER
    fun deactivateListener() {

        memberList.value = listOf()
        isListenToMemberList = null

        taskList.value = listOf()
        isListenToTaskList = null

        listMessage.value = listOf()
        isListenToMessage = null

        myTaskList.value = listOf()
        inProcessTaskList.value = listOf()
        lateTaskList.value = listOf()
        doneTaskList.value = listOf()
        taskChannelInstance.value = null
    }

    fun getChannel(id: String) {
        processing.value = true
        taskNode.document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if (value != null) {
                    taskChannelInstance.value = value.toObject<ATaskChannel>()
                    activateListener()
                }
            }
    }

    private fun activateListener() {
        try {
            listenToMemberList()
            listenToTaskList()
            listenToMessageList()
        } catch (ex: Exception) {
            handleException(ex, ex.message)
        }
    }

    private fun listenToMessageList() {
        taskChannelInstance.value?.let{channel ->
            isListenToMessage = taskNode.document(channel.channelId!!)
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

    private fun listenToTaskList() {
        taskChannelInstance.value?.let { channel ->
            isListenToTaskList = taskNode.document(channel.channelId!!)
                .collection(CONSTANT.TASK_LIST_NODE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                    }
                    if (value != null) {
                        taskList.value = value.documents.mapNotNull {
                            it.toObject<ATask>()
                        }
                        println("Listeennn")
                        filterTask()
                        updateProgress()
                    }
                }

        }
    }

    private fun listenToMemberList() {
        taskChannelInstance.value?.let {channel ->
            isListenToMemberList = privateListNode.document(channel.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                        println("failconnett  list " + memberList.value)
                    }
                    if (value != null) {
                        if (value.documents.size != memberList.value.size)
                            updateMember()

                        memberList.value = value.documents.mapNotNull {
                            it.toObject<AMember>()
                        }
                        println("is connect to member channel list " + memberList.value)
                    }
                }
        }
    }

    private fun updateMember() {
        taskChannelInstance.value?.let {ch ->
            processing.value = true
            val gr = ch.group
            privateListNode.document(ch.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE).count().get(AggregateSource.SERVER)
                .addOnCompleteListener { task->
                    if(task.isSuccessful) {
                        ch.totalMember = task.result.count.toInt()

                        taskNode.document(ch.channelId!!).set(ch)
                        groupNode.document(gr.groupId!!)
                            .collection(CONSTANT.TASK_LIST_NODE)
                            .document(ch.channelId).set(ch)
                    }
                    processing.value = false
                }
        }
    }

    private fun updateProgress() {
        taskChannelInstance.value?.let {task ->
            val haveDone = taskList.value.filter { it.status == TASK_STATUS.DONE }.size
            val total = taskList.value.size
            var temp = ATaskChannel(
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
    }

    private fun filterTask() {
        myTaskList.value = taskList.value.filter { it.member.uid == auth.currentUser?.uid }
        val thelist = taskList.value.filter { it.member.uid != auth.currentUser?.uid }
        val inProcessList = arrayListOf<ATask>()
        val lateList = arrayListOf<ATask>()
        val doneList = arrayListOf<ATask>()

        thelist.forEach { tsk ->
            val temp = ATask(
                name = tsk.name,
                taskChannel = taskChannelInstance.value!!,
                member = tsk.member,
                group = taskChannelInstance.value!!.group,
                status = tsk.status,
                deadline = tsk.deadline
            )
            if (tsk.deadline < getToday() && tsk.status != TASK_STATUS.DONE) temp.status = TASK_STATUS.LATE

            when(temp.status){
                TASK_STATUS.LATE ->
                    lateList.add(temp)
                TASK_STATUS.IN_PROGRESS ->
                    inProcessList.add(temp)
                else ->
                    doneList.add(temp)
            }
            inProcessTaskList.value = inProcessList
            lateTaskList.value = lateList
            doneTaskList.value = doneList
        }
    }

    //MEMBER FUNCTION
    val insertList = mutableStateOf<List<AUser>>(listOf())
    fun insertMember(list: List<AUser>) {
        taskChannelInstance.value?.let { channel ->
            processing.value = true
            list.forEach {  user ->
                val tmp = AMember(
                    user = user,
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
        taskChannelInstance.value?.let {channel ->
            processing.value = true
            privateListNode.document(channel.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE).document(uid).delete()
                .addOnCompleteListener {
                    processing.value = false
                }
        }
    }

    //PERSONAL TASK FUNCTION
    val taskName = mutableStateOf("")
    val insertMem = mutableStateOf<AUser?>(null)
    fun createPersonalTask(name: String, mem: AUser, deadline: String) {
        taskChannelInstance.value?.let {ch ->
            processing.value = true
            val id = taskNode.document().id
            val tmp = ATask (
                taskId = id,
                name = name,
                taskChannel = ch,
                member = mem,
                group = ch.group,
                status = TASK_STATUS.IN_PROGRESS,
                deadline = deadline,
            )

            taskNode.document(ch.channelId!!)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.taskId).set(tmp)
            userNode.document(mem.uid)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.taskId).set(tmp)

            insertMem.value = null
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
            updateProgress()
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
            updateProgress()
            processing.value = false
        }
    }

    //CHAT
    fun sendMessage(msg: String, user: AUser) {
        taskChannelInstance.value?.let { channel->
            val msgId = taskNode.document(channel.channelId!!)
                .collection(CONSTANT.CHANNEL_CHAT).document().id
            val temp = AMessage(
                messId = msgId,
                content = msg,
                creator = user
            )

            taskNode.document(channel.channelId)
                .collection(CONSTANT.CHANNEL_CHAT).document(temp.messId).set(temp)
        }
    }
}