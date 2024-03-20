package com.anasfarooq.i210813.Models

data class Chat(
    val chatId: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    var message: String = "",
    val time: String = "",
    val imagePath: String = ""
)
