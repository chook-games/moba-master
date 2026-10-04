package com.moba.master.data

import com.moba.master.engine.GameConfig

/**
 * Represents a champion template in the roster (before being spawned into a game).
 * Each champion has unique base stats, role, and display info.
 */
data class RosterChampion(
    val id: String,
    val displayName: String,
    val role: String,
    val title: String,
    val description: String,
    val baseHP: Float,
    val baseAD: Float,
    val baseAP: Float,
    val baseArmor: Float,
    val baseMagicResist: Float,
    val baseAttackSpeed: Float,
    val baseMoveSpeed: Float,
    val baseAttackRange: Float
)

/**
 * The champion roster — all playable champions available for selection.
 * Each has unique base stats for strategic diversity.
 */
object ChampionRoster {

    val allChampions: List<RosterChampion> = listOf(
        // ── Marksmen ──
        RosterChampion(
            id = "aetherblade",
            displayName = "Aetherblade",
            role = "Marksman",
            title = "The Radiant Gunner",
            description = "A master of light-infused firearms who rains death from afar.",
            baseHP = 1400f, baseAD = 62f, baseAP = 0f,
            baseArmor = 25f, baseMagicResist = 30f,
            baseAttackSpeed = 0.80f, baseMoveSpeed = 175f, baseAttackRange = 180f
        ),
        RosterChampion(
            id = "wildfire",
            displayName = "Wildfire",
            role = "Marksman",
            title = "The Inferno Slinger",
            description = "Leaves a trail of fire with every shot, burning enemies over time.",
            baseHP = 1350f, baseAD = 58f, baseAP = 10f,
            baseArmor = 22f, baseMagicResist = 28f,
            baseAttackSpeed = 0.85f, baseMoveSpeed = 180f, baseAttackRange = 170f
        ),

        // ── Mages ──
        RosterChampion(
            id = "voidcaller",
            displayName = "Voidcaller",
            role = "Mage",
            title = "The Abyss Whisperer",
            description = "Draws power from the void to unleash devastating magic.",
            baseHP = 1200f, baseAD = 25f, baseAP = 65f,
            baseArmor = 18f, baseMagicResist = 32f,
            baseAttackSpeed = 0.65f, baseMoveSpeed = 170f, baseAttackRange = 160f
        ),
        RosterChampion(
            id = "frostweaver",
            displayName = "Frostweaver",
            role = "Mage",
            title = "The Crystal Empress",
            description = "Freezes enemies in their tracks with piercing ice magic.",
            baseHP = 1150f, baseAD = 20f, baseAP = 70f,
            baseArmor = 16f, baseMagicResist = 34f,
            baseAttackSpeed = 0.60f, baseMoveSpeed = 165f, baseAttackRange = 165f
        ),

        // ── Tanks ──
        RosterChampion(
            id = "ironheart",
            displayName = "Ironheart",
            role = "Tank",
            title = "The Unbreakable Bulwark",
            description = "An immovable fortress that protects allies and crushes foes.",
            baseHP = 1900f, baseAD = 50f, baseAP = 0f,
            baseArmor = 45f, baseMagicResist = 40f,
            baseAttackSpeed = 0.55f, baseMoveSpeed = 160f, baseAttackRange = 120f
        ),
        RosterChampion(
            id = "stonewall",
            displayName = "Stonewall",
            role = "Tank",
            title = "The Mountain's Guardian",
            description = "Hard as rock and twice as stubborn — nothing gets past him.",
            baseHP = 2000f, baseAD = 48f, baseAP = 0f,
            baseArmor = 50f, baseMagicResist = 35f,
            baseAttackSpeed = 0.50f, baseMoveSpeed = 155f, baseAttackRange = 120f
        ),

        // ── Assassins ──
        RosterChampion(
            id = "shadowstep",
            displayName = "Shadowstep",
            role = "Assassin",
            title = "The Blade in the Dark",
            description = "Strikes from the shadows and vanishes before enemies can react.",
            baseHP = 1250f, baseAD = 68f, baseAP = 0f,
            baseArmor = 24f, baseMagicResist = 26f,
            baseAttackSpeed = 0.95f, baseMoveSpeed = 200f, baseAttackRange = 130f
        ),
        RosterChampion(
            id = "nightshade",
            displayName = "Nightshade",
            role = "Assassin",
            title = "The Venom Strike",
            description = "Deadly poison-tipped blades that melt even the toughest armor.",
            baseHP = 1200f, baseAD = 72f, baseAP = 5f,
            baseArmor = 22f, baseMagicResist = 24f,
            baseAttackSpeed = 1.00f, baseMoveSpeed = 195f, baseAttackRange = 125f
        ),

        // ── Supports ──
        RosterChampion(
            id = "tidecaller",
            displayName = "Tidecaller",
            role = "Support",
            title = "The Ocean's Grace",
            description = "Healing tides and protective waters keep her team fighting.",
            baseHP = 1300f, baseAD = 30f, baseAP = 40f,
            baseArmor = 20f, baseMagicResist = 30f,
            baseAttackSpeed = 0.70f, baseMoveSpeed = 175f, baseAttackRange = 150f
        ),
        RosterChampion(
            id = "stormbringer",
            displayName = "Stormbringer",
            role = "Support",
            title = "The Thunder Herald",
            description = "Calls down lightning to empower allies and shock enemies.",
            baseHP = 1350f, baseAD = 35f, baseAP = 45f,
            baseArmor = 22f, baseMagicResist = 32f,
            baseAttackSpeed = 0.72f, baseMoveSpeed = 178f, baseAttackRange = 155f
        ),

        // ── Fighters ──
        RosterChampion(
            id = "stormreaver",
            displayName = "Stormreaver",
            role = "Fighter",
            title = "The Tempest Warrior",
            description = "Charges into battle with lightning-infused strikes.",
            baseHP = 1600f, baseAD = 58f, baseAP = 15f,
            baseArmor = 32f, baseMagicResist = 28f,
            baseAttackSpeed = 0.78f, baseMoveSpeed = 185f, baseAttackRange = 135f
        ),
        RosterChampion(
            id = "duskblade",
            displayName = "Duskblade",
            role = "Fighter",
            title = "The Twilight Executioner",
            description = "Balances light and dark to cut down any foe.",
            baseHP = 1550f, baseAD = 60f, baseAP = 10f,
            baseArmor = 30f, baseMagicResist = 28f,
            baseAttackSpeed = 0.82f, baseMoveSpeed = 182f, baseAttackRange = 140f
        )
    )

    /** Get a champion by ID. */
    fun getById(id: String): RosterChampion? = allChampions.find { it.id == id }

    /** Get champions filtered by role. */
    fun getByRole(role: String): List<RosterChampion> = allChampions.filter { it.role == role }
}
