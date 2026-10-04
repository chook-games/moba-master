package com.moba.master.ai

/**
 * Tactical override commands issued by the player (manager/coach).
 * These override the champion's autonomous AI behavior.
 */
enum class TacticalOrder {
    /** Focus on last-hitting minions and farming gold. */
    FOCUS_FARM,

    /** Push the lane and attack the enemy tower. */
    PUSH_TOWER,

    /** Defend the base / stay near own tower. */
    DEFEND_BASE,

    /** Move to assist an ally or gank an enemy lane. */
    HELP_GANK,

    /** Force an engagement / team fight. */
    FORCE_TEAMFIGHT,

    /** Retreat to base to heal and recover. */
    RETREAT_HEAL
}
