package com.example.groupworkmanagement.data.model

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

data class AMessage(
    var messId: String="",
    var content: String="",
    var creator: AUser= AUser(),
    var timestamp: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
)
