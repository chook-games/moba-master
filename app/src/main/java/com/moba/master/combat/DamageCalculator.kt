package com.moba.master.combat

import com.moba.master.engine.GameConfig
import com.moba.master.entity.*
import kotlin.random.Random

/**
 * Pure combat math system.
 * Calculates damage, mitigation, lifesteal, and special effects.
 * No UI dependencies - purely logic.
 */
object DamageCalculator {

    /**
     * Calculate physical damage after armor mitigation.
     * Formula: DR = armor / (armor + 100)
     */
    fun calculatePhysicalDamage(rawAD: Float, targetArmor: Float): Float {
        val damageReduction = targetArmor / (targetArmor + GameConfig.ARMOR_DENOMINATOR)
        return rawAD * (1.0f - damageReduction.toFloat())
    }

    /**
     * Calculate magic damage after magic resist mitigation.
     */
    fun calculateMagicDamage(rawAP: Float, targetMR: Float): Float {
        val damageReduction = targetMR / (targetMR + GameConfig.MAGIC_RESIST_DENOMINATOR)
        return rawAP * (1.0f - damageReduction.toFloat())
    }

    /**
     * Process a champion's auto-attack against a target entity.
     *
     * @return The damage dealt.
     */
    fun processChampionAttack(
        attacker: ChampionEntity,
        target: GameEntity
    ): Float {
        if (!attacker.tickAttackCooldown(0f)) return 0f

        val stats = attacker.stats
        var rawDamage = stats.attackDamage

        // Critical strike check
        val isCrit = Random.nextFloat() < stats.critChance
        if (isCrit) {
            rawDamage *= stats.critDamage
        }

        // Calculate mitigated damage
        val mitigatedDamage = when (target) {
            is ChampionEntity -> calculatePhysicalDamage(rawDamage, target.stats.armor)
            is MinionEntity -> calculatePhysicalDamage(rawDamage, 0f) // Minions have 0 armor
            is TowerEntity -> calculatePhysicalDamage(rawDamage, 20f) // Towers have some armor
            is NexusEntity -> calculatePhysicalDamage(rawDamage, 10f)
            else -> rawDamage
        }

        // Apply damage
        val actualDamage = target.takeDamage(mitigatedDamage)

        // Lifesteal
        if (actualDamage > 0f && stats.lifesteal > 0f) {
            val healAmount = actualDamage * stats.lifesteal
            attacker.heal(healAmount)
        }

        // AoE Burn (Sunburn Mantle) - deals 1% max HP per tick to nearby enemies
        if (stats.hasAoeBurn) {
            // This is handled in the combat system per tick
        }

        // Reset attack cooldown
        attacker.resetAttackCooldown()

        return actualDamage
    }

    /**
     * Process a minion's attack against a target.
     */
    fun processMinionAttack(
        attacker: MinionEntity,
        target: GameEntity
    ): Float {
        if (!attacker.tickAttackCooldown(0f)) return 0f

        val rawDamage = attacker.attackDamage
        val mitigatedDamage = when (target) {
            is ChampionEntity -> calculatePhysicalDamage(rawDamage, target.stats.armor)
            is MinionEntity -> calculatePhysicalDamage(rawDamage, 0f)
            is TowerEntity -> calculatePhysicalDamage(rawDamage, 20f)
            is NexusEntity -> calculatePhysicalDamage(rawDamage, 10f)
            else -> rawDamage
        }

        val actualDamage = target.takeDamage(mitigatedDamage)
        attacker.resetAttackCooldown()
        return actualDamage
    }

    /**
     * Process a tower's attack against a target.
     */
    fun processTowerAttack(
        attacker: TowerEntity,
        target: GameEntity
    ): Float {
        if (!attacker.tickAttackCooldown(0f)) return 0f

        val rawDamage = attacker.attackDamage
        val mitigatedDamage = when (target) {
            is ChampionEntity -> calculatePhysicalDamage(rawDamage, target.stats.armor)
            is MinionEntity -> calculatePhysicalDamage(rawDamage, 0f)
            else -> rawDamage
        }

        val actualDamage = target.takeDamage(mitigatedDamage)
        attacker.resetAttackCooldown()
        return actualDamage
    }

    /**
     * Process AoE burn damage (Sunburn Mantle) for a champion.
     * Deals 1% of target's max HP as magic damage per second.
     */
    fun processAoeBurn(
        champion: ChampionEntity,
        enemies: List<GameEntity>,
        deltaSeconds: Float
    ) {
        if (!champion.stats.hasAoeBurn || !champion.isAlive) return

        val burnRange = 150f
        val burnDamagePercent = 0.01f // 1% max HP per second

        for (enemy in enemies) {
            if (!enemy.isAlive) continue
            val dist = champion.position.distanceTo(enemy.position)
            if (dist <= burnRange) {
                val burnDamage = when (enemy) {
                    is ChampionEntity -> enemy.stats.maxHP * burnDamagePercent * deltaSeconds
                    is MinionEntity -> enemy.maxHP * burnDamagePercent * deltaSeconds
                    else -> 50f * deltaSeconds
                }
                enemy.takeDamage(burnDamage)
            }
        }
    }

    /**
     * Process damage reflect (Cactuar Carapace).
     * Reflects 25% of incoming physical damage back at the attacker.
     */
    fun calculateReflectDamage(incomingDamage: Float): Float {
        return incomingDamage * 0.25f
    }

    /**
     * Process percent max HP burn (Lysander's Scourge).
     * Deals 1.5% of target's max HP per second as magic damage.
     */
    fun processPercentMaxHpBurn(
        attacker: ChampionEntity,
        target: GameEntity,
        deltaSeconds: Float
    ) {
        if (!attacker.stats.hasPercentMaxHpBurn || !attacker.isAlive) return
        if (!target.isAlive) return

        val maxHp = when (target) {
            is ChampionEntity -> target.stats.maxHP
            is MinionEntity -> target.maxHP
            is TowerEntity -> target.maxHP
            is NexusEntity -> target.maxHP
            else -> 1000f
        }

        val burnDamage = maxHp * 0.015f * deltaSeconds
        target.takeDamage(burnDamage)
    }
}
