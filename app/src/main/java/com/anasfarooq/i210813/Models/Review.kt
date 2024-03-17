package com.anasfarooq.i210813.Models

data class Review(
    val userId: String = "",
    val mentorName: String = "",
    val description: String = "",
    val stars: Float = 0f
)