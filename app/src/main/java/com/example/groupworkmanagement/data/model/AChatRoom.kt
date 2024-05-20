package com.example.groupworkmanagement.data.model

data class AChatRoom(
    var roomId: String = "",
    var user1: AUser = AUser(),
    var user2: AUser = AUser(),
) {
    fun mapTo() = mapOf(
        "roomId" to roomId,
        "user1" to user1,
        "user2" to user2
    )
}
