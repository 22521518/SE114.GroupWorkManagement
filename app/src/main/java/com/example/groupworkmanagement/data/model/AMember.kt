package com.example.groupworkmanagement.data.model
data class AMember(
    val user: AUser = AUser(),
    val role: ROLE = ROLE.member,
)

enum class ROLE{
    admin, member, leader
}
