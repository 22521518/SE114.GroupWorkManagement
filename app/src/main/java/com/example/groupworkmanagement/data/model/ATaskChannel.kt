package com.example.groupworkmanagement.data.model

data class ATaskChannel(
    val channelId: String? = "",
    val channelName: String = "", // = "NONAN123123132313213313E",
    var listMemberId: String?="",
    var group: AGroup= AGroup(),
    val creator: AUser = AUser(), // = AUser(name = "cau nho"),
    var progress: Int = 0,
    var status: TASK_STATUS = TASK_STATUS.IN_PROGRESS,
    val deadline: String? = null,
    var totalMember: Int=0,
) {
    fun toMap() = mapOf(
        "channelId" to channelId,
        "channelName" to channelName,
        "listMemberId" to listMemberId,
        "group" to group,
        "creator" to creator,
        "progress" to progress,
        "status" to status,
        "deadline" to deadline,
        "totalMember" to totalMember,

        )
}