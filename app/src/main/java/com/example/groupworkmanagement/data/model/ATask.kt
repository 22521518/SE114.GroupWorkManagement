package com.example.groupworkmanagement.data.model

import com.example.groupworkmanagement.utils.getToday


data class ATask (
    val taskId: String ="",
    val name: String="",
    val taskChannel: ATaskChannel = ATaskChannel(),
    val member: AUser=AUser(),
    val group: AGroup=AGroup(),
    var status: TASK_STATUS = TASK_STATUS.IN_PROGRESS,
    val deadline: String = getToday()
) {
    fun mapTo() = mapOf(
        "name" to name,
        "taskChannel" to taskChannel,
        "member" to member,
        "group" to group,
        "status" to status,
        "deadline" to deadline,
    )
}

enum class TASK_STATUS {
    DONE, LATE, IN_PROGRESS
}
