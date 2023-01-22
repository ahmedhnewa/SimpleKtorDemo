package com.ahmedhnewa.data.user

interface UserDataSource {
    suspend fun getUserByEmail(username: String): User?
    suspend fun getUserById(userId: String): User?
    suspend fun insertUser(user: User): Boolean
    suspend fun verifyEmailAccount(email: String): Boolean
}