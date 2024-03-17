package com.anasfarooq.i210813.Models

enum class MentorType {
    PersonalGrowth, Education, Entrepreneurship
}

data class Mentor(
    var id: String = "",
    val name: String = "",
    val description: String = "",
    val availability: String = "",
    val imagePath: String? = null,
    val videoPath: String? = null,
    val sessionPrice: Int = 1000,
    val title: String = "UX designer",
    val type: MentorType = MentorType.Education
)

