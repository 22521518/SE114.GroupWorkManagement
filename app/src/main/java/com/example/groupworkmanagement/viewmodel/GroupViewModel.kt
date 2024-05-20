package com.example.groupworkmanagement.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.groupworkmanagement.data.model.AChannel
import com.example.groupworkmanagement.data.model.AGroup
import com.example.groupworkmanagement.data.model.AMember
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
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val store: FirebaseFirestore,
): ViewModel() {
    //TEMP GROUP--------------------------------------------------------

    val groupInstance = mutableStateOf<AGroup?>(null)
    //CHANNEL
    val publicChannel = mutableStateOf<List<AChannel>>(listOf())
    private var isListenToPublic: ListenerRegistration? = null
    val privateChannel = mutableStateOf<List<AChannel>>(listOf())
    private var isListenToPrivate: ListenerRegistration? = null

    //TASK
    private val taskChannel = mutableStateOf<List<ATaskChannel>>(listOf())
    val inProcessTaskChannel = mutableStateOf<List<ATaskChannel>>(listOf())
    val doneTaskChannel = mutableStateOf<List<ATaskChannel>>(listOf())
    val lateTaskChannel = mutableStateOf<List<ATaskChannel>>(listOf())
    private var isListenToTaskChannel: ListenerRegistration? = null

    //Member
    val memberList = mutableStateOf<List<AMember>>(listOf())
    private var isListenToMemberList: ListenerRegistration? = null
    //NODE--------------------------------------------------------
    private val taskNode = store.collection(CONSTANT.TASK_NODE)
    private val userNode = store.collection(CONSTANT.USER_NODE)
    private val publicListNode = store.collection(CONSTANT.PUBLIC_LIST_NODE)
    private val privateListNode = store.collection(CONSTANT.PRIAVTE_LIST_NODE)
    private val channelNode = store.collection(CONSTANT.CHANNEL_NODE)
    private val groupNode = store.collection(CONSTANT.GROUP_NODE)

    // Processing
    val processing = mutableStateOf(false)

    //Listening event
    fun deactivateListener() {
        isListenToPrivate = null
        isListenToPublic = null
        isListenToMemberList = null
        isListenToTaskChannel = null

        memberList.value = listOf()
        privateChannel.value = listOf()
        publicChannel.value = listOf()

        taskChannel.value = listOf()
        inProcessTaskChannel.value = listOf()
        lateTaskChannel.value = listOf()
        doneTaskChannel.value = listOf()

        groupInstance.value = null
    }
    fun activeListener() {
        try {
            processing.value = true
            listenToPublicChannel()
            listenToPrivateChannel()
            listenToTaskChannel()
            listenToMemberList()
        } catch (ex: Exception) {
            handleException(ex, ex.message)
        } finally {
            processing.value = false
        }
    }

    fun getGroup(id: String) {
        processing.value = true
        store.collection(CONSTANT.GROUP_NODE).document(id)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    handleException(error, error.message)
                }
                if (value != null) {
                    groupInstance.value = value.toObject<AGroup>()
                    activeListener()
                    processing.value = false
                }
            }
    }

    private fun listenToMemberList() {
        val group = groupInstance.value
        group?.let {
            isListenToMemberList = publicListNode.document(it.listMemberId!!)
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
                        println("is connect to member list " + memberList.value)
                    }
                }
        }
    }

    private fun listenToTaskChannel() {
        val group = groupInstance.value
        group?.let {gr->
            isListenToTaskChannel = groupNode.document(gr.groupId!!)
                .collection(CONSTANT.TASK_LIST_NODE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                    }
                    if (value != null) {
                        taskChannel.value = value.documents.mapNotNull {
                            it.toObject<ATaskChannel>()
                        }
                        filterTask()
                    }
                }
        }
    }

    private fun listenToPublicChannel() {
        val group = groupInstance.value
        group?.let {gr->
            isListenToPublic = groupNode.document(gr.groupId!!)
                .collection(CONSTANT.PUBLIC_LIST_NODE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                    }
                    if (value != null) {
                        publicChannel.value = value.documents.mapNotNull {
                            it.toObject<AChannel>()
                        }
                    }
                }
        }
    }

    private fun listenToPrivateChannel() {
        val group = groupInstance.value
        group?.let {gr->
            isListenToPrivate = groupNode.document(gr.groupId!!)
                .collection(CONSTANT.PRIAVTE_LIST_NODE)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        handleException(error, error.message)
                    }
                    if (value != null) {
                        privateChannel.value = value.documents.mapNotNull {
                            it.toObject<AChannel>()
                        }
                    }
                }
        }
    }

    //update member
    private fun updateMember() {
        auth.currentUser?.uid.let { uid ->
            processing.value = true
            groupInstance.value?.let {gr->
                publicListNode.document(gr.listMemberId!!)
                    .collection(CONSTANT.MEMBER_NODE).count().get(AggregateSource.SERVER)
                    .addOnCompleteListener {task->
                        if(task.isSuccessful) {
                            gr.totalMember = task.result.count.toInt()
                            groupNode.document(gr.groupId!!).set(gr)

                            if(gr.creator.uid == uid){
                                userNode.document(uid)
                                    .collection(CONSTANT.USER_GROUP_NODE)
                                    .document(gr.groupId!!).set(gr)
                            } else {
                                userNode.document(uid!!)
                                    .collection(CONSTANT.USER_OTHER_GROUP_NODE)
                                    .document(gr.groupId!!).set(gr)
                            }
                        }
                        processing.value = false
                    }
            }
        }
    }

    //MEMBER FUNCTION
    val insertList = mutableStateOf<List<AUser>>(listOf())
    fun insertMember(list: List<AUser>) {
        groupInstance.value?.let {
            processing.value = true
            list.forEach { mem ->
                insertGroupToUser(mem.uid)
                val temp = AMember(
                    user = mem,
                    role = ROLE.member
                )
                publicListNode.document(it.listMemberId!!)
                    .collection(CONSTANT.MEMBER_NODE)
                    .document(mem.uid).set(temp)
                    .addOnCompleteListener {
                        processing.value = false
                    }
            }
        }
    }

    fun leaveTheGroup() {
        auth.currentUser?.let {user->
            groupInstance.value?.let{gr ->
                processing.value = true
                val tmp = memberList.value.filter { it.user.uid == user.uid }[0]
                if (tmp.role != ROLE.admin) {
                    removeGroupFromUser(tmp.user.uid)
                    removeFromPrivateList(tmp.user.uid)

                    publicListNode.document(gr.listMemberId!!)
                        .collection(CONSTANT.MEMBER_NODE)
                        .document(tmp.user.uid).delete()
                } else {
                    removeTheGroup()
                }
                processing.value = false
            }
        }
    }

    private fun removeFromPrivateList(uid: String) {
        privateChannel.value.forEach { channel ->
            privateListNode.document(channel.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE)
                .document(uid).delete()
        }
    }

    fun removeMember(uid: String) {
        auth.currentUser?.let {user ->
            if (user.uid != uid) {
                groupInstance.value?.let {
                    processing.value = true
                    removeGroupFromUser(uid)
                    removeFromPrivateList(uid)
                    publicListNode.document(it.listMemberId!!)
                        .collection(CONSTANT.MEMBER_NODE)
                        .document(uid).delete()
                        .addOnCompleteListener {
                            processing.value = false
                        }
                }
            } else {
                leaveTheGroup()
                println("leave")
            }
        }
    }

    private fun removeTheGroup() {
        auth.currentUser?.let {user ->
            groupInstance.value?.let { group ->
                removeChannel()
                deactivateListener()
                userNode.document(user.uid)
                    .collection(CONSTANT.USER_GROUP_NODE).document(group.groupId!!).delete()
                groupNode.document(group.groupId!!).delete()
            }
        }
    }

    private fun removeChannel() {
        try {
            privateChannel.value.forEach { channel->
                channelNode.document(channel.channelId!!).delete()
                groupNode.document(groupInstance.value?.groupId!!)
                    .collection(CONSTANT.PRIAVTE_LIST_NODE)
                    .document(channel.channelId!!).delete()
                removePrivateList()
            }
            publicChannel.value.forEach {channel->
                channelNode.document(channel.channelId!!).delete()
                groupNode.document(groupInstance.value?.groupId!!)
                    .collection(CONSTANT.PUBLIC_LIST_NODE)
                    .document(channel.channelId!!).delete()
            }
            removePublicList()

        } catch (ex: Exception) {
            handleException(ex, ex.message)
        }


    }

    private fun removePublicList() {
        groupInstance.value?.let {group ->
            removeGroupFromUser(memberList.value)
            publicListNode.document(group.listMemberId!!).delete()
        }
    }

    private fun removePrivateList() {
        privateChannel.value.forEach { channel ->
            privateListNode.document(channel.listMemberId!!).delete()
        }
    }

    private fun removeGroupFromUser(list: List<AMember>) {
        list.forEach { mem ->
            removeGroupFromUser(mem.user.uid)
        }
    }

    private fun removeGroupFromUser(uid: String) {
        groupInstance.value?.let{
            userNode.document(uid)
                .collection(CONSTANT.USER_OTHER_GROUP_NODE).document(it.groupId!!)
                .delete()
            publicListNode.document(it.listMemberId!!)
                .collection(CONSTANT.MEMBER_NODE).document(uid).delete()
        }
    }

    private fun insertGroupToUser(uid: String) {
        groupInstance.value?.let {
            userNode.document(uid)
                .collection(CONSTANT.USER_OTHER_GROUP_NODE).document(it.groupId!!)
                .set(it)
        }
    }

    ///CHANNEL FUNCTION
    val channelName = mutableStateOf("")
    suspend fun createPublicChannel(name: String){
        val user = getUserData()
        user?.let {val group = groupInstance.value
            group?.let {
                processing.value = true
                val id = channelNode.document().id
                val tmp = AChannel(
                    channelId = id,
                    channelName = name,
                    creator = user,
                    createdAt = getToday(),
                    listMemberId = group.listMemberId,
                    totalMember = group.totalMember
                )
                channelNode.document(tmp.channelId!!).set(tmp)
                groupNode.document(it.groupId!!)
                    .collection(CONSTANT.PUBLIC_LIST_NODE).document(tmp.channelId!!)
                    .set(tmp)
                    .addOnCompleteListener {
                        processing.value = false
                        println("dm loi o day ne con bo + ${tmp}")
                    }
            }
        }
    }

    suspend fun createPrivateChannel(name: String, list: List<AUser>) {
        val user = getUserData()
        user?.let {
            val group = groupInstance.value
            group?.let {
                processing.value = true
                val listId = createNewPrivateList(ls = list, ROLE.admin)
                val id = channelNode.document().id
                val tmp = AChannel(
                    channelId = id,
                    channelName = name,
                    creator = user,
                    createdAt = getToday(),
                    listMemberId = listId,
                    totalMember = list.size,
                )
                try {
                    channelNode.document(tmp.channelId!!).set(tmp)
                    groupNode.document(it.groupId!!)
                        .collection(CONSTANT.PRIAVTE_LIST_NODE).document(tmp.channelId!!).set(tmp)
                        .addOnCompleteListener {
                            processing.value = false
                        }
                } catch (ex: Exception) {
                    handleException(ex, ex.message)
                }finally {
                    processing.value = false
                }
            }
        }
    }
    // TASK FUNCTION
    //FILTER
    private fun filterTask() {
        val inProcessList = arrayListOf<ATaskChannel>()
        val lateList = arrayListOf<ATaskChannel>()
        val doneList = arrayListOf<ATaskChannel>()

        taskChannel.value.forEach { task->
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
            if (task.deadline!! < getToday() && task.progress < 100) temp.status = TASK_STATUS.LATE
            else if (task.deadline < getToday() && task.progress == 100) temp.status = TASK_STATUS.DONE
            else if (task.deadline >= getToday()) temp.status = TASK_STATUS.IN_PROGRESS

            when(temp.status){
                TASK_STATUS.LATE ->
                    lateList.add(temp)
                TASK_STATUS.IN_PROGRESS ->
                    inProcessList.add(temp)
                else ->
                    doneList.add(temp)
            }
        }
        inProcessTaskChannel.value = inProcessList
        lateTaskChannel.value = lateList
        doneTaskChannel.value = doneList
    }

    //TASK FUNCTION
    val taskName = mutableStateOf("")
    val selectedTask = mutableStateOf<ATaskChannel?>(null)

    fun extendTask(task: ATaskChannel, deadline: String) {
        auth.currentUser?.let {
            groupInstance.value?.let {
                processing.value = true
                val tmp = ATaskChannel(
                    channelId = task.channelId,
                    channelName = task.channelName,
                    listMemberId = task.listMemberId,
                    group = task.group,
                    creator = task.creator,
                    progress = task.progress,
                    status = TASK_STATUS.IN_PROGRESS,
                    deadline = deadline,
                    totalMember = task.totalMember
                )
                taskNode.document(tmp.channelId!!).set(tmp)
                    .addOnCompleteListener {
                        processing.value = false
                        println("EXTECND")
                    }
                groupNode.document(it.groupId!!)
                    .collection(CONSTANT.TASK_LIST_NODE).document(tmp.channelId).set(tmp)
            }
        }
    }

    fun createNewTaskChannel(name: String, task: ATaskChannel, deadline: String) {
        groupInstance.value?.let{
            processing.value = true
            val id = taskNode.document().id
            val tmp = ATaskChannel(
                channelId = id,
                channelName = name,
                listMemberId = task.listMemberId,
                group = task.group,
                creator = task.creator,
                progress = 0,
                status = TASK_STATUS.LATE,
                deadline = deadline,
                totalMember = task.totalMember
            )
            taskNode.document(tmp.channelId!!).set(tmp)
            groupNode.document(it.groupId!!)
                .collection(CONSTANT.TASK_LIST_NODE).document(tmp.channelId).set(tmp)
                .addOnCompleteListener {
                    processing.value = false
                }
        }
    }

    suspend fun createNewTaskChannel(name: String, leaderList: List<AUser>, memberList: List<AUser>, deadline: String) {
        val user = getUserData()
        user?.let {
            val group = groupInstance.value
            group?.let {
                processing.value = true
                val listId = createNewPrivateList(listOf())
                addMemberToTaskList(listId = listId!!, memberList)
                addLeaderToTaskList(listId = listId, leaderList)
                val id = taskNode.document().id
                val tmp = ATaskChannel(
                    channelId = id,
                    channelName = name,
                    listMemberId = listId,
                    group = it,
                    creator = user,
                    progress = 100,
                    status = TASK_STATUS.LATE,
                    deadline = "2024-05-01",//deadline,
                    totalMember = leaderList.size + memberList.size + 1
                )
                try {
                    taskNode.document(tmp.channelId!!).set(tmp)
                    groupNode.document(it.groupId!!)
                        .collection(CONSTANT.TASK_LIST_NODE).document(tmp.channelId).set(tmp)
                        .addOnCompleteListener {
                            processing.value = false
                        }
                    println(tmp.toString())
                } catch (ex: Exception) {
                    handleException(ex, ex.message)
                } finally {
                    processing.value = false
                }
            }
        }
    }

    //LIST FUNCTION
    val leaders = mutableStateOf<List<AUser>>(listOf())
    private fun addLeaderToTaskList(listId: String, ls: List<AUser>) {
        ls.forEach { mem->
            val tmp = AMember(
                user = mem,
                role = ROLE.leader
            )
            privateListNode.document(listId)
                .collection(CONSTANT.MEMBER_NODE)
                .document(tmp.user.uid).set(tmp)
        }
    }
    val members = mutableStateOf<List<AUser>>(listOf())
    private fun addMemberToTaskList(listId: String, ls: List<AUser>) {
        ls.forEach { mem->
            val tmp = AMember(
                user = mem,
                role = ROLE.member
            )
            privateListNode.document(listId)
                .collection(CONSTANT.MEMBER_NODE)
                .document(tmp.user.uid).set(tmp)
        }
    }

    private suspend fun createNewPrivateList(ls: List<AUser>, role: ROLE = ROLE.leader): String? {
        val res = CompletableDeferred<String?>()
        val admin = getUserData()
        admin?.let {it ->
            val id = privateListNode.document().id
            privateListNode.document(id).set(mapOf("listId" to id))
            //ADD MEMBER
            ls.forEach { otherMem ->
                val tmp = AMember(
                    user = otherMem,
                    role = ROLE.member
                )
                privateListNode.document(id)
                    .collection(CONSTANT.MEMBER_NODE)
                    .document(tmp.user.uid).set(tmp).await()

            }
            val mem = AMember(
                user = it,
                role = role
            )
            //ADD ADMIN
            privateListNode.document(id)
                .collection(CONSTANT.MEMBER_NODE)
                .document(mem.user.uid).set(mem).await()

            res.complete(id)
        }
        res.complete(null)
        return res.await()
    }
    //TEMP -------------------------------------------------------
    private suspend fun getUserData(): AUser? {
        val res = CompletableDeferred<AUser?>()
        auth.currentUser?.uid?.let { id ->
            userNode.document(id).get().addOnSuccessListener {
                res.complete(it.toObject<AUser>())
            }
        }
        return res.await()
    }
}