package com.moba.master.entity

import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig

/**
 * Types of minions that spawn in waves.
 */
enum class MinionType {
    MELEE,
    CASTER,
    SIEGE
}

/**
 * A minion entity that marches down the lane and attacks enemies.
 */
class MinionEntity(
    id: String,
    position: Coordinate,
    team: Team,
    val minionType: MinionType
) : GameEntity(
    id = id,
    position = position,
    team = team,
    currentHP = when (minionType) {
        MinionType.MELEE -> GameConfig.MELEE_MINION_HP
        MinionType.CASTER -> GameConfig.CASTER_MINION_HP
        MinionType.SIEGE -> GameConfig.SIEGE_MINION_HP
    },
    maxHP = when (minionType) {
        MinionType.MELEE -> GameConfig.MELEE_MINION_HP
        MinionType.CASTER -> GameConfig.CASTER_MINION_HP
        MinionType.SIEGE -> GameConfig.SIEGE_MINION_HP
    },
    radius = GameConfig.MINION_RADIUS
) {
    /** Attack damage of this minion. */
    val attackDamage: Float = when (minionType) {
        MinionType.MELEE -> GameConfig.MELEE_MINION_AD
        MinionType.CASTER -> GameConfig.CASTER_MINION_AD
        MinionType.SIEGE -> GameConfig.SIEGE_MINION_AD
    }

    /** Gold reward for killing this minion. */
    val goldReward: Int = when (minionType) {
        MinionType.MELEE -> GameConfig.GOLD_PER_MINION_MELEE
        MinionType.CASTER -> GameConfig.GOLD_PER_MINION_CASTER
        MinionType.SIEGE -> GameConfig.GOLD_PER_MINION_SIEGE
    }

    /** Movement speed. */
    val moveSpeed: Float = GameConfig.MINION_MOVE_SPEED

    /** Attack range. */
    val attackRange: Float = GameConfig.MINION_ATTACK_RANGE

    /** Attack cooldown timer. */
    var attackCooldownRemaining: Float = 0f

    /** Target entity ID. */
    var targetEntityId: String? = null

    /** Whether this minion is attacking (stopped moving). */
    var isAttacking: Boolean = false

    /** Tick attack cooldown. Returns true if ready. */
    fun tickAttackCooldown(deltaSeconds: Float): Boolean {
        if (attackCooldownRemaining > 0f) {
            attackCooldownRemaining -= deltaSeconds
        }
        return attackCooldownRemaining <= 0f
    }

    /** Reset attack cooldown (1 attack per second for minions). */
    fun resetAttackCooldown() {
        attackCooldownRemaining = 1.0f
    }
}
