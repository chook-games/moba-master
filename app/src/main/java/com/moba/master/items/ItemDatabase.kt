package com.moba.master.items

/**
 * Catalogue of all purchasable parody items.
 * Each item is a stat-stick that instantly modifies champion stats on purchase.
 * Names are designed to avoid trademark issues while being clearly inspired by LoL items.
 */
object ItemDatabase {

    val ALL_ITEMS: List<Item> = listOf(
        // ── AD / Crit Items ──
        Item(
            id = "eternity_blade",
            name = "Eternity Blade",
            description = "Massively increases critical strike damage and attack damage.",
            cost = 3400,
            bonusAD = 70f,
            bonusCritChance = 0.25f
        ),
        Item(
            id = "soul_drinker",
            name = "Soul Drinker",
            description = "Grants heavy attack damage and lifesteal.",
            cost = 3400,
            bonusAD = 65f,
            bonusLifesteal = 0.18f
        ),
        Item(
            id = "blade_lost_sovereign",
            name = "Blade of the Lost Sovereign",
            description = "Strikes faster and steals movement speed.",
            cost = 3200,
            bonusAttackSpeed = 0.30f,
            bonusLifesteal = 0.10f
        ),

        // ── AP Items ──
        Item(
            id = "radagon_doomhat",
            name = "Radagon's Doomhat",
            description = "Massively amplifies ability power.",
            cost = 3600,
            bonusAP = 120f
        ),
        Item(
            id = "lysander_scourge",
            name = "Lysander's Scourge",
            description = "Burns enemies for a percentage of their max health.",
            cost = 3100,
            bonusAP = 80f,
            hasPercentMaxHpBurn = true
        ),
        Item(
            id = "chronos_pendant",
            name = "Chronos' Pendant",
            description = "Grants ability power and a stasis active effect.",
            cost = 2800,
            bonusAP = 80f,
            hasStasisActive = true
        ),

        // ── Tank / Defense Items ──
        Item(
            id = "warthog_plate",
            name = "Warthog's Plate",
            description = "Grants massive health and health regeneration.",
            cost = 3000,
            bonusMaxHP = 800f,
            bonusHPRegen = 2.0f
        ),
        Item(
            id = "sunburn_mantle",
            name = "Sunburn Mantle",
            description = "Burns nearby enemies with a fiery aura.",
            cost = 2800,
            bonusArmor = 60f,
            hasAoeBurn = true
        ),
        Item(
            id = "cactuar_carapace",
            name = "Cactuar Carapace",
            description = "Reflects a portion of incoming damage back at attackers.",
            cost = 2700,
            bonusArmor = 75f,
            hasDamageReflect = true
        ),
        Item(
            id = "valkyrie_protection",
            name = "Valkyrie's Protection",
            description = "Revives the champion upon death with a portion of health.",
            cost = 3100,
            bonusArmor = 45f,
            bonusMagicResist = 45f,
            hasRevive = true
        ),

        // ── Hybrid / Utility Items ──
        Item(
            id = "triad_might",
            name = "Triad Might",
            description = "Balanced hybrid stats for versatile champions.",
            cost = 3300,
            bonusAD = 30f,
            bonusAP = 30f,
            bonusAttackSpeed = 0.30f,
            hasTripleStat = true
        ),

        // ── Boots ──
        Item(
            id = "hermes_sandals",
            name = "Hermes' Sandals",
            description = "Increases movement speed and magic resistance.",
            cost = 1300,
            bonusMagicResist = 25f,
            bonusMoveSpeed = 45f
        ),
        Item(
            id = "ronin_boots",
            name = "Ronin Boots",
            description = "Increases movement speed and armor.",
            cost = 1200,
            bonusArmor = 30f,
            bonusMoveSpeed = 45f
        )
    )

    /** Lookup an item by its unique ID. */
    fun getItemById(id: String): Item? = ALL_ITEMS.find { it.id == id }

    /** Lookup an item by its display name. */
    fun getItemByName(name: String): Item? = ALL_ITEMS.find { it.name == name }
}
