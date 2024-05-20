package com.example.groupworkmanagement.data.model

data class AChannel(
    var channelId: String?="",
    var channelName: String?="",
    var creator: AUser = AUser(),
    var createdAt: String?="",
    var listMemberId: String?="",
    var totalMember: Int = 0,
) {
    fun toMap() = mapOf(
        "channelId" to channelId,
        "channelName" to channelName,
        "creator" to creator,
        "createdAt" to createdAt,
        "listMemberId" to listMemberId,
        "totalMember" to totalMember,
    )
}