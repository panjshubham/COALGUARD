package com.example.coalguard.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.coalguard.data.model.UserRole

class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("coalguard_session", Context.MODE_PRIVATE)

    fun saveUserSession(userId: String, email: String, role: UserRole, mineId: Long? = 42L) {
        prefs.edit().apply {
            putString("USER_ID", userId)
            putString("EMAIL", email)
            putString("ROLE", role.value)
            putLong("MINE_ID", mineId ?: 42L)
            apply()
        }
    }

    fun getRole(): UserRole {
        val roleStr = prefs.getString("ROLE", UserRole.MINE_OFFICIAL.value)
        return UserRole.fromString(roleStr)
    }

    fun setRole(role: UserRole) {
        prefs.edit().putString("ROLE", role.value).apply()
    }

    fun getMineId(): Long = prefs.getLong("MINE_ID", 42L)

    fun getEmail(): String = prefs.getString("EMAIL", "official@coalguard.in") ?: "official@coalguard.in"

    fun isCorporateAdmin(): Boolean = getRole() == UserRole.CORPORATE

    fun isRegulator(): Boolean = getRole() == UserRole.REGULATOR

    fun isMineOfficial(): Boolean = getRole() == UserRole.MINE_OFFICIAL

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
