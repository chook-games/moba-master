package com.moba.master.items

/**
 * Immutable data class representing a purchasable parody item.
 * Each item provides flat stat bonuses that are applied to a champion.
 */
data class Item(
    val id: String,
    val name: String,
    val description: String,
    val cost: Int,
    // ── Stat Bonuses ──
    val bonusAD: Float = 0f,
    val bonusAP: Float = 0f,
    val bonusMaxHP: Float = 0f,
    val bonusArmor: Float = 0f,
    val bonusMagicResist: Float = 0f,
    val bonusAttackSpeed: Float = 0f,
    val bonusLifesteal: Float = 0f,
    val bonusCritChance: Float = 0f,
    val bonusMoveSpeed: Float = 0f,
    val bonusHPRegen: Float = 0f,
    // ── Special Effects (flags for combat system) ──
    val hasAoeBurn: Boolean = false,
    val hasDamageReflect: Boolean = false,
    val hasRevive: Boolean = false,
    val hasStasisActive: Boolean = false,
    val hasPercentMaxHpBurn: Boolean = false,
    val hasTripleStat: Boolean = false
)
