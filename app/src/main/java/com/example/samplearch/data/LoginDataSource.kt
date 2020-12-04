package com.example.samplearch.data

import android.security.keystore.UserNotAuthenticatedException
import com.example.samplearch.data.model.LoggedInUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val users: List<Pair<String, String>> =
        listOf(
            Pair("gfreitas@teste.com", "1234567"),
            Pair("gfreitas2@teste.com", "1234")
        )

    suspend fun login(username: String, password: String): Result<LoggedInUser> {
        delay(2000)
            try {
                users.find { existingUser -> existingUser.first == username }?.let {
                    return if (it.second == password) {
                        val fakeUser =
                            LoggedInUser(java.util.UUID.randomUUID().toString(), "Jane Doe")
                        Result.Success(fakeUser)
                    } else {
                        Result.Error(UserNotAuthenticatedException("User not found"))
                    }
                }
                return Result.Error(UserNotAuthenticatedException("User not found"))
            } catch (e: Throwable) {
                return Result.Error(IOException("Error logging in", e))
            }
    }

    fun logout() {
        // TODO: revoke authentication
    }
}