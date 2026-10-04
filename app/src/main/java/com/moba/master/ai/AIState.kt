package com.moba.master.ai

/**
 * Finite State Machine states for champion AI behavior.
 * The champion transitions between these states based on
 * distance to enemies, HP percentage, and player tactical orders.
 */
enum class AIState {
    /** Moving from spawn to the lane. */
    STATE_MOVING_TO_LANE,

    /** Attacking and last-hitting minions for gold. */
    STATE_FARMING_MINIONS,

    /** Pushing the lane, attacking enemy tower. */
    STATE_PUSHING_TOWER,

    /** Moving to assist an ally or gank an enemy. */
    STATE_GANKING,

    /** Engaging in a team fight against enemy champions. */
    STATE_TEAMFIGHTING,

    /** Retreating to base to heal. */
    STATE_RETREATING,

    /** Dead, waiting to respawn. */
    STATE_DEAD
}
