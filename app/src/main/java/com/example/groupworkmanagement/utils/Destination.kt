package com.example.groupworkmanagement.utils

sealed class Destination(val route: String) {
    object LoginScreen: Destination(route = "/login")
    object SignUpScreen: Destination(route = "/signup")
    object Home: Destination(route = "/home")

    //Home
    object HomeTask: Destination(route = "/task")
    object HomeCalendar: Destination(route = "/calendar")
    object HomeMe: Destination(route = "/me")
    object HomeChat: Destination(route = "/chat")
    object HomeAddChat: Destination(route="/chat-add")
    object HomeChatRoom: Destination(route="/chat/{id}") {
        fun createRoute(id: String) = "/chat/${id}"
    }
    object HomeGroup: Destination(route = "/group")
    object GroupRoom: Destination(route = "/group/{id}") {
        fun createRoute(id: String) = "/group/${id}"
    }
    object HomeAdd: Destination(route = "/add")
    //Group
    object GroupMember: Destination(route = "/member")
    object GroupTask: Destination(route = "/task")
    object  GroupChannel: Destination(route = "/channel")
    object GroupAddOutsider: Destination(route = "/add-out")
    object GroupAddInsider: Destination(route = "/add-in")
    object GroupAddLeader:Destination(route="/add-leader")
    object GroupAddMember:Destination(route="/add-mem")
    object  GroupChannelRoom: Destination(route = "/channel/{id}/{public}") {
        fun createRoute(id: String, public: Boolean = true) = "/channel/${id}/${public}"
    }
    object  GroupTaskChannelRoom: Destination(route = "/channel/{id}") {
        fun createRoute(id: String) = "/channel/${id}"
    }

    //Channel
    object ChannelHome: Destination(route = "/chat")
    object ChannelMember: Destination(route = "/member")
    object ChannelAddMember: Destination(route = "/add")

    //Task
    object TaskHome: Destination(route="/chat")
    object TaskMember: Destination(route = "/member")
    object TaskAssignment: Destination(route = "/assignment")
    object TaskCalendar: Destination(route = "/calendar")
    object TaskAddMember: Destination(route = "/add")
    object TaskAddSingleMember: Destination(route = "/single-add")

    //Chat

}