package com.moba.master.entity

import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig

/**
 * The Nexus - the core structure of each team's base.
 * Destroying the enemy Nexus wins the game.
 */
class NexusEntity(
    id: String,
    position: Coordinate,
    team: Team
) : GameEntity(
    id = id,
    position = position,
    team = team,
    currentHP = GameConfig.NEXUS_HP,
    maxHP = GameConfig.NEXUS_HP,
    radius = GameConfig.NEXUS_RADIUS
)
