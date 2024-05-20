package com.example.groupworkmanagement.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.groupworkmanagement.utils.getToday

data class AGroup (
    var groupId: String?="",
    var groupName: String?="",
    var creator: AUser=AUser(),
    var listMemberId: String?="",
    var totalMember: Int = 0,
    var imageUrl: String? = null,
    var createdAt: String? = getToday(),
) {
    fun toMap() = mapOf(
        "groupId" to groupId,
        "groupName" to groupName,
        "creatorId" to creator,
        "listMemberId" to listMemberId,
        "totalMember" to totalMember,
        "createdAt" to createdAt,
    )
}