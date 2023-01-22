package com.ahmedhnewa.data.user

import org.bson.types.ObjectId
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class MongoUserDataSource(
    db: CoroutineDatabase
) : UserDataSource {
    private val users = db.getCollection<User>("users")
    override suspend fun getUserByEmail(username: String): User? {
        return users.findOne(User::email eq username)
    }

    override suspend fun getUserById(userId: String): User? {
        return users.findOneById(ObjectId(userId))
    }

    override suspend fun insertUser(user: User): Boolean {
        return users.insertOne(user).wasAcknowledged()
    }

    override suspend fun verifyEmailAccount(email: String): Boolean {
//        val oldUser = getUserByEmail(email) ?: return false
        return users.updateOne(User::email eq email, setValue(User::accountVerified, true))
            .wasAcknowledged()
    }
}