package com.moba.master.engine

import com.moba.master.entity.*

/**
 * Immutable snapshot of the entire game state at a given tick.
 * This is the single source of truth for the UI layer.
 * The engine produces a new GameState every tick and emits it via StateFlow.
 */
data class GameState(
    // ── Game Status ──
    val gameTime: Float,
    val isGameOver: Boolean,
    val winningTeam: Team?,

    // ── Champions ──
    val blueChampions: List<ChampionEntity>,
    val redChampions: List<ChampionEntity>,

    // ── Minions ──
    val blueMinions: List<MinionEntity>,
    val redMinions: List<MinionEntity>,

    // ── Structures ──
    val blueTowers: List<TowerEntity>,
    val redTowers: List<TowerEntity>,
    val blueNexus: NexusEntity?,
    val redNexus: NexusEntity?,

    // ── UI State ──
    val selectedChampionId: String?,
    val totalGold: Int,

    // ── Wave Info ──
    val waveNumber: Int,
    val timeUntilNextWave: Float
) {
    companion object {
        /** Create an initial empty game state. */
        fun initial(): GameState = GameState(
            gameTime = 0f,
            isGameOver = false,
            winningTeam = null,
            blueChampions = emptyList(),
            redChampions = emptyList(),
            blueMinions = emptyList(),
            redMinions = emptyList(),
            blueTowers = emptyList(),
            redTowers = emptyList(),
            blueNexus = null,
            redNexus = null,
            selectedChampionId = null,
            totalGold = 0,
            waveNumber = 0,
            timeUntilNextWave = 5f
        )
    }

    /** Find a champion by ID across both teams. */
    fun findChampionById(id: String): ChampionEntity? {
        return blueChampions.find { it.id == id } ?: redChampions.find { it.id == id }
    }

    /** Get all alive enemy champions relative to a team. */
    fun getEnemyChampions(team: Team): List<ChampionEntity> {
        return if (team == Team.BLUE) redChampions.filter { it.isAlive }
        else blueChampions.filter { it.isAlive }
    }

    /** Get all alive allied champions for a team. */
    fun getAlliedChampions(team: Team): List<ChampionEntity> {
        return if (team == Team.BLUE) blueChampions.filter { it.isAlive }
        else redChampions.filter { it.isAlive }
    }

    /** Get all alive enemy minions relative to a team. */
    fun getEnemyMinions(team: Team): List<MinionEntity> {
        return if (team == Team.BLUE) redMinions.filter { it.isAlive }
        else blueMinions.filter { it.isAlive }
    }

    /** Get all alive allied minions for a team. */
    fun getAlliedMinions(team: Team): List<MinionEntity> {
        return if (team == Team.BLUE) blueMinions.filter { it.isAlive }
        else redMinions.filter { it.isAlive }
    }

    /** Get all alive enemy towers relative to a team. */
    fun getEnemyTowers(team: Team): List<TowerEntity> {
        return if (team == Team.BLUE) redTowers.filter { it.isAlive }
        else blueTowers.filter { it.isAlive }
    }

    /** Get all alive allied towers for a team. */
    fun getAlliedTowers(team: Team): List<TowerEntity> {
        return if (team == Team.BLUE) blueTowers.filter { it.isAlive }
        else redTowers.filter { it.isAlive }
    }

    /** Get the enemy nexus for a team. */
    fun getEnemyNexus(team: Team): NexusEntity? {
        return if (team == Team.BLUE) redNexus
        else blueNexus
    }
}
