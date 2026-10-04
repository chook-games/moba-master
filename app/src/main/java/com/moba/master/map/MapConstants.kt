package com.moba.master.map

import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig

/**
 * Constants defining the ARAM map layout.
 * The map is a single diagonal lane from bottom-left (Blue) to top-right (Red).
 * Walls line both sides of the lane to create boundaries.
 */
object MapConstants {

    // ── Lane Path (center line of the lane) ──
    // Blue spawn is bottom-left, Red spawn is top-right
    val LANE_START = Coordinate(400f, 2400f)  // Blue side lane entrance
    val LANE_END = Coordinate(1600f, 600f)    // Red side lane entrance

    // ── Lane Midpoint ──
    val LANE_MID = Coordinate(
        (LANE_START.x + LANE_END.x) / 2f,
        (LANE_START.y + LANE_END.y) / 2f
    )

    // ── Tower Positions ──
    val BLUE_TOWER = GameConfig.BLUE_TOWER_POS
    val RED_TOWER = GameConfig.RED_TOWER_POS

    // ── Nexus Positions ──
    val BLUE_NEXUS = GameConfig.BLUE_NEXUS_POS
    val RED_NEXUS = GameConfig.RED_NEXUS_POS

    // ── Spawn Positions ──
    val BLUE_SPAWN = GameConfig.BLUE_SPAWN
    val RED_SPAWN = GameConfig.RED_SPAWN

    // ── Minion Spawn Points ──
    val BLUE_MINION_SPAWN = Coordinate(350f, 2500f)
    val RED_MINION_SPAWN = Coordinate(1650f, 500f)

    // ── Wall Definitions (for rendering boundaries) ──
    // Each wall is defined by start/end coordinates and thickness
    data class Wall(val start: Coordinate, val end: Coordinate, val thickness: Float = GameConfig.WALL_THICKNESS)

    /** Walls that line the ARAM lane. */
    val WALLS: List<Wall> = listOf(
        // Top-left wall (above the lane)
        Wall(
            start = Coordinate(200f, 2500f),
            end = Coordinate(1500f, 700f),
            thickness = GameConfig.WALL_THICKNESS
        ),
        // Bottom-right wall (below the lane)
        Wall(
            start = Coordinate(600f, 2300f),
            end = Coordinate(1800f, 500f),
            thickness = GameConfig.WALL_THICKNESS
        ),
        // Base walls
        Wall(
            start = Coordinate(100f, 2900f),
            end = Coordinate(600f, 2900f),
            thickness = GameConfig.WALL_THICKNESS
        ),
        Wall(
            start = Coordinate(1400f, 100f),
            end = Coordinate(1900f, 100f),
            thickness = GameConfig.WALL_THICKNESS
        )
    )

    /** Get a position along the lane at a given progress (0.0 = Blue, 1.0 = Red). */
    fun getLanePosition(progress: Float): Coordinate {
        val clamped = progress.coerceIn(0f, 1f)
        return Coordinate(
            x = LANE_START.x + (LANE_END.x - LANE_START.x) * clamped,
            y = LANE_START.y + (LANE_END.y - LANE_START.y) * clamped
        )
    }

    /** Get the lane progress for a given position (closest point on lane). */
    fun getLaneProgress(position: Coordinate): Float {
        val dx = LANE_END.x - LANE_START.x
        val dy = LANE_END.y - LANE_START.y
        val lengthSq = dx * dx + dy * dy
        if (lengthSq <= 0f) return 0f
        val t = ((position.x - LANE_START.x) * dx + (position.y - LANE_START.y) * dy) / lengthSq
        return t.coerceIn(0f, 1f)
    }
}
