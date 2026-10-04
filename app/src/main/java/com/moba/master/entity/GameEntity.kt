package com.moba.master.entity

import com.moba.master.engine.Coordinate

/**
 * Abstract base class for all entities in the game world.
 * Every entity has a unique ID, position, team affiliation, and health.
 */
abstract class GameEntity(
    open val id: String,
    open var position: Coordinate,
    open val team: Team,
    open var currentHP: Float,
    open val maxHP: Float,
    open val radius: Float
) {
    /** Whether this entity is alive and active in the simulation. */
    var isAlive: Boolean = true
        protected set

    /** Mark this entity as dead. */
    open fun die() {
        isAlive = false
    }

    /** Take damage, returns the actual damage dealt after mitigation. */
    open fun takeDamage(rawDamage: Float): Float {
        if (!isAlive) return 0f
        currentHP = (currentHP - rawDamage).coerceAtLeast(0f)
        if (currentHP <= 0f) die()
        return rawDamage
    }

    /** Heal the entity by a flat amount. */
    fun heal(amount: Float) {
        if (!isAlive) return
        currentHP = (currentHP + amount).coerceAtMost(maxHP)
    }
}
