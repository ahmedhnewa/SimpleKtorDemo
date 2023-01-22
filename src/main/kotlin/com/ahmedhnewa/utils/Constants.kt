package com.ahmedhnewa.utils

object Constants {
    const val DB_NAME = "app"
    object PATTERNS {
        const val PASSWORD = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@\$%^&*-]).{8,}\$"
        const val PHONE_NUMBER: String = "^07\\d{9}\$"
    }
}