package com.anasfarooq.i210813.Models

data class UserProfile(
    var name: String,
    var email: String,
    var phone: String,
    var country: String,
    var city: String,
    var imagePath: String? = null
)
