package com.moba.master.engine

/**
 * Central configuration constants for the MOBA simulation.
 * All timings, ranges, and balance values are defined here.
 */
object GameConfig {
    // ── Engine Timing ──
    const val TICK_RATE_HZ = 60
    const val TICK_INTERVAL_MS = 1000L / TICK_RATE_HZ
    const val TICK_INTERVAL_SEC = 1.0 / TICK_RATE_HZ

    // ── Map Dimensions (World Coordinates) ──
    const val MAP_WIDTH = 2000f
    const val MAP_HEIGHT = 3000f

    // ── ARAM Lane ──
    const val LANE_WIDTH = 300f
    const val WALL_THICKNESS = 40f

    // ── Spawn Points ──
    // Blue team spawns bottom-left
    val BLUE_SPAWN = Coordinate(300f, 2600f)
    val BLUE_NEXUS_POS = Coordinate(300f, 2700f)
    val BLUE_TOWER_POS = Coordinate(500f, 2200f)

    // Red team spawns top-right
    val RED_SPAWN = Coordinate(1700f, 400f)
    val RED_NEXUS_POS = Coordinate(1700f, 300f)
    val RED_TOWER_POS = Coordinate(1500f, 800f)

    // ── Entity Sizes (radii for circle collision) ──
    const val CHAMPION_RADIUS = 20f
    const val MINION_RADIUS = 12f
    const val TOWER_RADIUS = 40f
    const val NEXUS_RADIUS = 55f

    // ── Champion Base Stats ──
    const val BASE_HP = 1500f
    const val BASE_AD = 55f
    const val BASE_AP = 0f
    const val BASE_ARMOR = 30f
    const val BASE_MAGIC_RESIST = 30f
    const val BASE_ATTACK_SPEED = 0.75f // attacks per second
    const val BASE_MOVE_SPEED = 180f    // world units per second
    const val BASE_ATTACK_RANGE = 150f
    const val BASE_LIFESTEAL = 0.0f
    const val BASE_CRIT_CHANCE = 0.0f
    const val BASE_CRIT_DAMAGE = 1.75f

    // ── Minion Stats ──
    const val MELEE_MINION_HP = 450f
    const val MELEE_MINION_AD = 12f
    const val MELEE_MINION_GOLD = 20
    const val CASTER_MINION_HP = 280f
    const val CASTER_MINION_AD = 22f
    const val CASTER_MINION_GOLD = 17
    const val SIEGE_MINION_HP = 1500f
    const val SIEGE_MINION_AD = 40f
    const val SIEGE_MINION_GOLD = 45
    const val MINION_MOVE_SPEED = 120f
    const val MINION_ATTACK_RANGE = 100f

    // ── Tower Stats ──
    const val TOWER_HP = 3500f
    const val TOWER_AD = 180f
    const val TOWER_ATTACK_RANGE = 350f
    const val TOWER_ATTACK_SPEED = 0.83f

    // ── Nexus Stats ──
    const val NEXUS_HP = 5500f

    // ── Minion Wave Spawning ──
    const val WAVE_INTERVAL_SEC = 30L
    const val MELEE_PER_WAVE = 3
    const val CASTER_PER_WAVE = 3
    const val SIEGE_EVERY_NTH_WAVE = 3

    // ── Gold & Experience ──
    const val GOLD_PER_MINION_MELEE = 20
    const val GOLD_PER_MINION_CASTER = 17
    const val GOLD_PER_MINION_SIEGE = 45
    const val GOLD_PER_CHAMPION_KILL = 300
    const val GOLD_PER_ASSIST = 150
    const val GOLD_PASSIVE_PER_SEC = 1.0 // 1 gold/sec passive income

    // ── Combat Formulas ──
    const val ARMOR_DENOMINATOR = 100.0  // DR = armor / (armor + 100)
    const val MAGIC_RESIST_DENOMINATOR = 100.0
}
