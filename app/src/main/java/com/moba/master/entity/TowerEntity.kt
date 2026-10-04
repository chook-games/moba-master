package com.moba.master.entity

import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig

/**
 * A defensive tower that attacks enemy entities in range.
 */
class TowerEntity(
    id: String,
    position: Coordinate,
    team: Team
) : GameEntity(
    id = id,
    position = position,
    team = team,
    currentHP = GameConfig.TOWER_HP,
    maxHP = GameConfig.TOWER_HP,
    radius = GameConfig.TOWER_RADIUS
) {
    /** Tower attack damage. */
    val attackDamage: Float = GameConfig.TOWER_AD

    /** Tower attack range. */
    val attackRange: Float = GameConfig.TOWER_ATTACK_RANGE

    /** Tower attack speed. */
    val attackSpeed: Float = GameConfig.TOWER_ATTACK_SPEED

    /** Attack cooldown timer. */
    var attackCooldownRemaining: Float = 0f

    /** Current target entity ID. */
    var targetEntityId: String? = null

    /** Tick attack cooldown. Returns true if ready. */
    fun tickAttackCooldown(deltaSeconds: Float): Boolean {
        if (attackCooldownRemaining > 0f) {
            attackCooldownRemaining -= deltaSeconds
        }
        return attackCooldownRemaining <= 0f
    }

    /** Reset attack cooldown. */
    fun resetAttackCooldown() {
        attackCooldownRemaining = 1.0f / attackSpeed
    }
}
