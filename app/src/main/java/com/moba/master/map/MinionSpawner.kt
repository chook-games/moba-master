package com.moba.master.map

import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig
import com.moba.master.entity.MinionEntity
import com.moba.master.entity.MinionType
import com.moba.master.entity.Team

/**
 * Handles the spawning of minion waves for both teams.
 * Waves spawn every 30 seconds. Every 3rd wave includes a siege minion.
 */
object MinionSpawner {

    /** Timer tracking time until next wave spawn. */
    var timeUntilNextWave: Float = 5f // First wave spawns after 5 seconds
    var waveNumber: Int = 0

    /**
     * Update the spawner. Should be called every tick.
     *
     * @param deltaSeconds Time elapsed since last tick.
     * @param blueMinions Mutable list to add new Blue minions to.
     * @param redMinions Mutable list to add new Red minions to.
     * @return True if a wave was spawned this tick.
     */
    fun update(
        deltaSeconds: Float,
        blueMinions: MutableList<MinionEntity>,
        redMinions: MutableList<MinionEntity>
    ): Boolean {
        timeUntilNextWave -= deltaSeconds
        if (timeUntilNextWave <= 0f) {
            timeUntilNextWave = GameConfig.WAVE_INTERVAL_SEC.toFloat()
            waveNumber++
            spawnWave(Team.BLUE, blueMinions)
            spawnWave(Team.RED, redMinions)
            return true
        }
        return false
    }

    /**
     * Spawn a single wave for the given team.
     */
    private fun spawnWave(team: Team, minions: MutableList<MinionEntity>) {
        val spawnPos = if (team == Team.BLUE) MapConstants.BLUE_MINION_SPAWN else MapConstants.RED_MINION_SPAWN
        val teamPrefix = if (team == Team.BLUE) "blue" else "red"
        val waveId = "${teamPrefix}_wave_${waveNumber}"

        // Spawn melee minions
        for (i in 0 until GameConfig.MELEE_PER_WAVE) {
            val offset = Coordinate((i - 1) * 25f, 0f)
            val minion = MinionEntity(
                id = "${waveId}_melee_$i",
                position = spawnPos + offset,
                team = team,
                minionType = MinionType.MELEE
            )
            minions.add(minion)
        }

        // Spawn caster minions
        for (i in 0 until GameConfig.CASTER_PER_WAVE) {
            val offset = Coordinate((i - 1) * 25f, -30f)
            val minion = MinionEntity(
                id = "${waveId}_caster_$i",
                position = spawnPos + offset,
                team = team,
                minionType = MinionType.CASTER
            )
            minions.add(minion)
        }

        // Spawn siege minion every Nth wave
        if (waveNumber % GameConfig.SIEGE_EVERY_NTH_WAVE == 0) {
            val siege = MinionEntity(
                id = "${waveId}_siege",
                position = spawnPos + Coordinate(0f, -50f),
                team = team,
                minionType = MinionType.SIEGE
            )
            minions.add(siege)
        }
    }

    /** Reset the spawner for a new game. */
    fun reset() {
        timeUntilNextWave = 5f
        waveNumber = 0
    }
}
