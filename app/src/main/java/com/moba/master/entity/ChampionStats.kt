package com.moba.master.entity

import com.moba.master.engine.GameConfig
import com.moba.master.items.Item

/**
 * Aggregated combat stats for a champion.
 * Base stats + item bonuses are combined into this value object.
 */
data class ChampionStats(
    val maxHP: Float,
    val attackDamage: Float,
    val abilityPower: Float,
    val armor: Float,
    val magicResist: Float,
    val attackSpeed: Float,
    val moveSpeed: Float,
    val attackRange: Float,
    val lifesteal: Float,
    val critChance: Float,
    val critDamage: Float,
    val hpRegen: Float,
    // Special effect flags
    val hasAoeBurn: Boolean = false,
    val hasDamageReflect: Boolean = false,
    val hasRevive: Boolean = false,
    val hasStasisActive: Boolean = false,
    val hasPercentMaxHpBurn: Boolean = false
) {
    companion object {
        /** Create base stats from GameConfig defaults. */
        fun base(): ChampionStats = ChampionStats(
            maxHP = GameConfig.BASE_HP,
            attackDamage = GameConfig.BASE_AD,
            abilityPower = GameConfig.BASE_AP,
            armor = GameConfig.BASE_ARMOR,
            magicResist = GameConfig.BASE_MAGIC_RESIST,
            attackSpeed = GameConfig.BASE_ATTACK_SPEED,
            moveSpeed = GameConfig.BASE_MOVE_SPEED,
            attackRange = GameConfig.BASE_ATTACK_RANGE,
            lifesteal = GameConfig.BASE_LIFESTEAL,
            critChance = GameConfig.BASE_CRIT_CHANCE,
            critDamage = GameConfig.BASE_CRIT_DAMAGE,
            hpRegen = 0f
        )

        /** Calculate stats from base + a list of items. */
        fun fromBaseAndItems(items: List<Item>): ChampionStats {
            var stats = base()
            for (item in items) {
                stats = stats.applyItem(item)
            }
            return stats
        }
    }

    /** Apply a single item's bonuses to these stats. */
    fun applyItem(item: Item): ChampionStats = copy(
        maxHP = maxHP + item.bonusMaxHP,
        attackDamage = attackDamage + item.bonusAD,
        abilityPower = abilityPower + item.bonusAP,
        armor = armor + item.bonusArmor,
        magicResist = magicResist + item.bonusMagicResist,
        attackSpeed = attackSpeed + item.bonusAttackSpeed,
        moveSpeed = moveSpeed + item.bonusMoveSpeed,
        lifesteal = lifesteal + item.bonusLifesteal,
        critChance = critChance + item.bonusCritChance,
        hpRegen = hpRegen + item.bonusHPRegen,
        hasAoeBurn = hasAoeBurn || item.hasAoeBurn,
        hasDamageReflect = hasDamageReflect || item.hasDamageReflect,
        hasRevive = hasRevive || item.hasRevive,
        hasStasisActive = hasStasisActive || item.hasStasisActive,
        hasPercentMaxHpBurn = hasPercentMaxHpBurn || item.hasPercentMaxHpBurn
    )
}
