package com.moba.master.ai

import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig
import com.moba.master.entity.*

/**
 * The Finite State Machine system that drives champion AI behavior.
 * This is a pure logic system - it reads entity states and produces
 * movement/attack decisions without any rendering dependencies.
 */
object FSMSystem {

    /**
     * Update a champion's AI state and produce movement/attack commands.
     * Called every tick by the game engine.
     *
     * @param champion The champion to update.
     * @param allies List of allied champions.
     * @param enemies List of enemy champions.
     * @param alliedMinions List of allied minions.
     * @param enemyMinions List of enemy minions.
     * @param alliedTowers List of allied towers.
     * @param enemyTowers List of enemy towers.
     * @param enemyNexus The enemy nexus.
     * @param laneTarget The target position on the lane to move towards.
     * @param deltaSeconds Time elapsed since last tick.
     */
    fun updateChampion(
        champion: ChampionEntity,
        allies: List<ChampionEntity>,
        enemies: List<ChampionEntity>,
        alliedMinions: List<MinionEntity>,
        enemyMinions: List<MinionEntity>,
        alliedTowers: List<TowerEntity>,
        enemyTowers: List<TowerEntity>,
        enemyNexus: NexusEntity?,
        laneTarget: Coordinate,
        deltaSeconds: Float
    ) {
        if (!champion.isAlive) {
            champion.aiState = AIState.STATE_DEAD
            champion.respawnTimer -= deltaSeconds
            if (champion.respawnTimer <= 0f) {
                val spawnPos = if (champion.team == Team.BLUE) GameConfig.BLUE_SPAWN else GameConfig.RED_SPAWN
                champion.respawn(spawnPos)
            }
            return
        }

        // Apply tactical override from player
        val order = champion.tacticalOrder
        if (order != null) {
            applyTacticalOrder(champion, order, enemies, enemyMinions, enemyTowers, enemyNexus, laneTarget, deltaSeconds)
            return
        }

        // ── Autonomous AI State Transitions ──
        evaluateStateTransition(champion, enemies, enemyMinions, enemyTowers)

        // ── Execute Current State ──
        when (champion.aiState) {
            AIState.STATE_MOVING_TO_LANE -> {
                moveToPosition(champion, laneTarget, deltaSeconds)
                // Check for enemies in range
                val nearestEnemy = findNearestEnemy(champion, enemies, enemyMinions)
                if (nearestEnemy != null && champion.position.distanceTo(nearestEnemy.position) <= champion.stats.attackRange) {
                    champion.aiState = AIState.STATE_FARMING_MINIONS
                }
            }

            AIState.STATE_FARMING_MINIONS -> {
                // Prioritize minions, attack nearest enemy in range
                val target = findBestMinionTarget(champion, enemyMinions)
                    ?: findNearestEnemy(champion, enemies, null)
                if (target != null) {
                    if (champion.position.distanceTo(target.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = target.id
                        // Attack handled by combat system
                    } else {
                        moveToPosition(champion, target.position, deltaSeconds)
                    }
                } else {
                    // No enemies, push forward
                    moveToPosition(champion, laneTarget, deltaSeconds)
                }
            }

            AIState.STATE_PUSHING_TOWER -> {
                val targetTower = findNearestTower(champion, enemyTowers)
                if (targetTower != null && targetTower.isAlive) {
                    if (champion.position.distanceTo(targetTower.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = targetTower.id
                    } else {
                        moveToPosition(champion, targetTower.position, deltaSeconds)
                    }
                } else {
                    // Tower destroyed, push to nexus
                    if (enemyNexus != null && enemyNexus.isAlive) {
                        if (champion.position.distanceTo(enemyNexus.position) <= champion.stats.attackRange) {
                            champion.targetEntityId = enemyNexus.id
                        } else {
                            moveToPosition(champion, enemyNexus.position, deltaSeconds)
                        }
                    } else {
                        champion.aiState = AIState.STATE_MOVING_TO_LANE
                    }
                }
            }

            AIState.STATE_GANKING -> {
                // Move towards the nearest enemy champion
                val targetEnemy = findNearestEnemy(champion, enemies, null)
                if (targetEnemy != null) {
                    if (champion.position.distanceTo(targetEnemy.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = targetEnemy.id
                        champion.aiState = AIState.STATE_TEAMFIGHTING
                    } else {
                        moveToPosition(champion, targetEnemy.position, deltaSeconds)
                    }
                } else {
                    champion.aiState = AIState.STATE_MOVING_TO_LANE
                }
            }

            AIState.STATE_TEAMFIGHTING -> {
                // Attack nearest enemy champion
                val targetEnemy = findNearestEnemy(champion, enemies, null)
                if (targetEnemy != null && targetEnemy.isAlive) {
                    if (champion.position.distanceTo(targetEnemy.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = targetEnemy.id
                    } else {
                        moveToPosition(champion, targetEnemy.position, deltaSeconds)
                    }
                } else {
                    // No enemies left, go back to farming
                    champion.aiState = AIState.STATE_FARMING_MINIONS
                }
            }

            AIState.STATE_RETREATING -> {
                val spawnPos = if (champion.team == Team.BLUE) GameConfig.BLUE_SPAWN else GameConfig.RED_SPAWN
                moveToPosition(champion, spawnPos, deltaSeconds)
                if (champion.position.distanceTo(spawnPos) < 50f) {
                    // Heal to full when at base
                    champion.currentHP = champion.stats.maxHP
                    champion.aiState = AIState.STATE_MOVING_TO_LANE
                }
            }

            AIState.STATE_DEAD -> {
                // Handled above
            }
        }
    }

    /** Apply a player-issued tactical order, overriding autonomous AI. */
    private fun applyTacticalOrder(
        champion: ChampionEntity,
        order: TacticalOrder,
        enemies: List<ChampionEntity>,
        enemyMinions: List<MinionEntity>,
        enemyTowers: List<TowerEntity>,
        enemyNexus: NexusEntity?,
        laneTarget: Coordinate,
        deltaSeconds: Float
    ) {
        when (order) {
            TacticalOrder.FOCUS_FARM -> {
                champion.aiState = AIState.STATE_FARMING_MINIONS
                val target = findBestMinionTarget(champion, enemyMinions)
                if (target != null) {
                    if (champion.position.distanceTo(target.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = target.id
                    } else {
                        moveToPosition(champion, target.position, deltaSeconds)
                    }
                } else {
                    moveToPosition(champion, laneTarget, deltaSeconds)
                }
            }

            TacticalOrder.PUSH_TOWER -> {
                champion.aiState = AIState.STATE_PUSHING_TOWER
                val targetTower = findNearestTower(champion, enemyTowers)
                if (targetTower != null && targetTower.isAlive) {
                    if (champion.position.distanceTo(targetTower.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = targetTower.id
                    } else {
                        moveToPosition(champion, targetTower.position, deltaSeconds)
                    }
                } else if (enemyNexus != null && enemyNexus.isAlive) {
                    if (champion.position.distanceTo(enemyNexus.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = enemyNexus.id
                    } else {
                        moveToPosition(champion, enemyNexus.position, deltaSeconds)
                    }
                }
            }

            TacticalOrder.DEFEND_BASE -> {
                champion.aiState = AIState.STATE_FARMING_MINIONS
                val ownTower = if (champion.team == Team.BLUE) GameConfig.BLUE_TOWER_POS else GameConfig.RED_TOWER_POS
                if (champion.position.distanceTo(ownTower) > 200f) {
                    moveToPosition(champion, ownTower, deltaSeconds)
                } else {
                    // Stay near tower and attack anything in range
                    val target = findNearestEnemy(champion, enemies, enemyMinions)
                    if (target != null && champion.position.distanceTo(target.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = target.id
                    }
                }
            }

            TacticalOrder.HELP_GANK -> {
                champion.aiState = AIState.STATE_GANKING
                val targetEnemy = findNearestEnemy(champion, enemies, null)
                if (targetEnemy != null) {
                    moveToPosition(champion, targetEnemy.position, deltaSeconds)
                }
            }

            TacticalOrder.FORCE_TEAMFIGHT -> {
                champion.aiState = AIState.STATE_TEAMFIGHTING
                val targetEnemy = findNearestEnemy(champion, enemies, null)
                if (targetEnemy != null) {
                    if (champion.position.distanceTo(targetEnemy.position) <= champion.stats.attackRange) {
                        champion.targetEntityId = targetEnemy.id
                    } else {
                        moveToPosition(champion, targetEnemy.position, deltaSeconds)
                    }
                }
            }

            TacticalOrder.RETREAT_HEAL -> {
                champion.aiState = AIState.STATE_RETREATING
                val spawnPos = if (champion.team == Team.BLUE) GameConfig.BLUE_SPAWN else GameConfig.RED_SPAWN
                moveToPosition(champion, spawnPos, deltaSeconds)
            }
        }
    }

    /** Evaluate whether the champion should change state based on environment. */
    private fun evaluateStateTransition(
        champion: ChampionEntity,
        enemies: List<ChampionEntity>,
        enemyMinions: List<MinionEntity>,
        enemyTowers: List<TowerEntity>
    ) {
        val hpPercent = champion.currentHP / champion.stats.maxHP

        // Retreat if low HP
        if (hpPercent < 0.2f && champion.aiState != AIState.STATE_RETREATING) {
            champion.aiState = AIState.STATE_RETREATING
            return
        }

        // Check for nearby enemy champions
        val nearbyEnemy = enemies.find { enemy ->
            enemy.isAlive && champion.position.distanceTo(enemy.position) < 300f
        }

        if (nearbyEnemy != null) {
            // If we're already pushing or farming, switch to teamfight
            if (champion.aiState in listOf(
                    AIState.STATE_FARMING_MINIONS,
                    AIState.STATE_PUSHING_TOWER,
                    AIState.STATE_MOVING_TO_LANE
                )
            ) {
                champion.aiState = AIState.STATE_TEAMFIGHTING
            }
        }
    }

    /** Move the champion towards a target position. */
    private fun moveToPosition(champion: ChampionEntity, target: Coordinate, deltaSeconds: Float) {
        val distance = champion.position.distanceTo(target)
        if (distance > 5f) {
            val moveAmount = champion.stats.moveSpeed * deltaSeconds
            champion.position = champion.position.moveTowards(target, moveAmount)
        }
    }

    /** Find the nearest enemy champion or minion. */
    private fun findNearestEnemy(
        champion: ChampionEntity,
        enemies: List<ChampionEntity>,
        minions: List<MinionEntity>?
    ): GameEntity? {
        var nearest: GameEntity? = null
        var nearestDist = Float.MAX_VALUE

        for (enemy in enemies) {
            if (!enemy.isAlive) continue
            val dist = champion.position.distanceTo(enemy.position)
            if (dist < nearestDist) {
                nearestDist = dist
                nearest = enemy
            }
        }

        if (minions != null) {
            for (minion in minions) {
                if (!minion.isAlive) continue
                val dist = champion.position.distanceTo(minion.position)
                if (dist < nearestDist) {
                    nearestDist = dist
                    nearest = minion
                }
            }
        }

        return nearest
    }

    /** Find the best minion to target (lowest HP for last-hitting). */
    private fun findBestMinionTarget(champion: ChampionEntity, enemyMinions: List<MinionEntity>): MinionEntity? {
        return enemyMinions
            .filter { it.isAlive && champion.position.distanceTo(it.position) < 400f }
            .minByOrNull { it.currentHP }
    }

    /** Find the nearest enemy tower. */
    private fun findNearestTower(champion: ChampionEntity, towers: List<TowerEntity>): TowerEntity? {
        return towers
            .filter { it.isAlive }
            .minByOrNull { champion.position.distanceTo(it.position) }
    }
}
