package com.moba.master.entity

/**
 * Represents the two opposing teams.
 */
enum class Team {
    BLUE,
    RED;

    fun opponent(): Team = when (this) {
        BLUE -> RED
        RED -> BLUE
    }
}
