package com.example.coalguard.data.model

enum class UserRole(val value: String, val title: String) {
    MINE_OFFICIAL("mine_official", "Mine Safety Officer"),
    REGULATOR("regulator", "DGMS Regulator"),
    CORPORATE("corporate", "Corporate HQ Admin");

    companion object {
        fun fromString(role: String?): UserRole {
            return when (role?.lowercase()) {
                "corporate", "corporate_admin", "admin" -> CORPORATE
                "regulator", "inspector", "dgms" -> REGULATOR
                else -> MINE_OFFICIAL
            }
        }
    }
}
