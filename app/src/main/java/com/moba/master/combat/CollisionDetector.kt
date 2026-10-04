package com.moba.master.combat

import com.moba.master.engine.Coordinate
import com.moba.master.entity.GameEntity

/**
 * Simple 2D circle collision detection system.
 * All entities are treated as circles with a position and radius.
 */
object CollisionDetector {

    /**
     * Check if two entities are within attack range of each other.
     * Uses circle-to-circle distance check.
     */
    fun isInAttackRange(attacker: GameEntity, target: GameEntity): Boolean {
        val distance = attacker.position.distanceTo(target.position)
        return distance <= (attacker.radius + target.radius + getAttackRange(attacker))
    }

    /**
     * Check if a point is within a given range of an entity.
     */
    fun isInRange(entity: GameEntity, point: Coordinate, range: Float): Boolean {
        return entity.position.distanceTo(point) <= range
    }

    /**
     * Find the nearest target from a list that is within attack range.
     */
    fun findNearestInRange(
        entity: GameEntity,
        targets: List<GameEntity>,
        range: Float
    ): GameEntity? {
        return targets
            .filter { it.isAlive && entity.position.distanceTo(it.position) <= range }
            .minByOrNull { entity.position.distanceTo(it.position) }
    }

    /**
     * Check if two circles overlap (for collision/phasing).
     */
    fun doCirclesOverlap(a: GameEntity, b: GameEntity): Boolean {
        val distance = a.position.distanceTo(b.position)
        return distance <= (a.radius + b.radius)
    }

    /**
     * Get the effective attack range for an entity.
     */
    private fun getAttackRange(entity: GameEntity): Float {
        return when (entity) {
            is com.moba.master.entity.ChampionEntity -> entity.stats.attackRange
            is com.moba.master.entity.MinionEntity -> entity.attackRange
            is com.moba.master.entity.TowerEntity -> entity.attackRange
            else -> 50f
        }
    }
}
