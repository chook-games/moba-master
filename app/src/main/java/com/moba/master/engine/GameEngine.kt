package com.moba.master.engine

import com.moba.master.ai.FSMSystem
import com.moba.master.combat.DamageCalculator
import com.moba.master.entity.*
import com.moba.master.map.MapConstants
import com.moba.master.map.MinionSpawner
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The core simulation engine.
 * Runs a fixed-timestep game loop on a background coroutine.
 * Produces immutable GameState snapshots that are emitted via StateFlow.
 *
 * This engine is COMPLETELY DECOUPLED from UI/Rendering.
 * It can be swapped to run on a server for multiplayer with zero changes.
 */
class GameEngine {

    // ── Mutable Game State (only accessed from the simulation thread) ──
    private var gameTime: Float = 0f
    private var isGameOver: Boolean = false
    private var winningTeam: Team? = null

    // Blue Team
    private val blueChampions = mutableListOf<ChampionEntity>()
    private val blueMinions = mutableListOf<MinionEntity>()
    private val blueTowers = mutableListOf<TowerEntity>()
    private var blueNexus: NexusEntity? = null

    // Red Team
    private val redChampions = mutableListOf<ChampionEntity>()
    private val redMinions = mutableListOf<MinionEntity>()
    private val redTowers = mutableListOf<TowerEntity>()
    private var redNexus: NexusEntity? = null

    // UI State
    private var selectedChampionId: String? = null
    private var totalGold: Int = 0

    // ── Public StateFlow for UI ──
    private val _gameState = MutableStateFlow(GameState.initial())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    // ── Engine Control ──
    private var engineJob: Job? = null
    private var isRunning: Boolean = false

    // ── Player is always Blue team ──
    val playerTeam: Team = Team.BLUE

    /**
     * Initialize a new ARAM game with player-selected champions.
     * @param playerChampionIds List of 5 champion IDs from the roster for the Blue team.
     *                         Red team gets random champions from the remaining roster.
     */
    fun startNewGame(playerChampionIds: List<String>) {
        resetState()

        val allRoster = com.moba.master.data.ChampionRoster.allChampions

        // Create Blue team champions from player selection
        for ((i, champId) in playerChampionIds.withIndex()) {
            val rosterChamp = allRoster.find { it.id == champId }
            val displayName = rosterChamp?.displayName ?: "Champion $i"
            val role = rosterChamp?.role ?: "Fighter"
            val champion = ChampionEntity(
                id = "blue_champ_$i",
                position = MapConstants.BLUE_SPAWN + Coordinate(i * 40f - 80f, 0f),
                team = Team.BLUE,
                displayName = displayName,
                role = role
            )
            // Apply unique base stats from roster
            if (rosterChamp != null) {
                champion.initStatsFromRoster(
                    maxHP = rosterChamp.baseHP,
                    attackDamage = rosterChamp.baseAD,
                    abilityPower = rosterChamp.baseAP,
                    armor = rosterChamp.baseArmor,
                    magicResist = rosterChamp.baseMagicResist,
                    attackSpeed = rosterChamp.baseAttackSpeed,
                    moveSpeed = rosterChamp.baseMoveSpeed,
                    attackRange = rosterChamp.baseAttackRange
                )
                champion.currentHP = rosterChamp.baseHP
            }
            champion.gold = 500 // Starting gold
            blueChampions.add(champion)
        }

        // Create Red team champions (random from remaining roster)
        val usedIds = playerChampionIds.toSet()
        val availableForRed = allRoster.filter { it.id !in usedIds }.shuffled()
        val redRoles = listOf("Marksman", "Mage", "Tank", "Assassin", "Support")
        for (i in 0 until 5) {
            val rosterChamp = availableForRed.getOrNull(i) ?: allRoster[i % allRoster.size]
            val champion = ChampionEntity(
                id = "red_champ_$i",
                position = MapConstants.RED_SPAWN + Coordinate(i * 40f - 80f, 0f),
                team = Team.RED,
                displayName = rosterChamp.displayName,
                role = rosterChamp.role
            )
            champion.initStatsFromRoster(
                maxHP = rosterChamp.baseHP,
                attackDamage = rosterChamp.baseAD,
                abilityPower = rosterChamp.baseAP,
                armor = rosterChamp.baseArmor,
                magicResist = rosterChamp.baseMagicResist,
                attackSpeed = rosterChamp.baseAttackSpeed,
                moveSpeed = rosterChamp.baseMoveSpeed,
                attackRange = rosterChamp.baseAttackRange
            )
            champion.currentHP = rosterChamp.baseHP
            champion.gold = 500
            redChampions.add(champion)
        }

        // Create Towers
        blueTowers.add(TowerEntity("blue_tower_0", MapConstants.BLUE_TOWER, Team.BLUE))
        redTowers.add(TowerEntity("red_tower_0", MapConstants.RED_TOWER, Team.RED))

        // Create Nexuses
        blueNexus = NexusEntity("blue_nexus", MapConstants.BLUE_NEXUS, Team.BLUE)
        redNexus = NexusEntity("red_nexus", MapConstants.RED_NEXUS, Team.RED)

        // Reset spawner
        MinionSpawner.reset()

        // Emit initial state
        emitState()
    }

    /**
     * Start the game loop on a background coroutine.
     */
    fun startEngine(scope: CoroutineScope) {
        if (isRunning) return
        isRunning = true

        engineJob = scope.launch(Dispatchers.Default) {
            var lastTickTime = System.nanoTime()

            while (isRunning && !isGameOver) {
                val now = System.nanoTime()
                val deltaNanos = (now - lastTickTime).toFloat()
                lastTickTime = now

                // Fixed timestep: cap delta to prevent spiral of death
                val deltaSeconds = (deltaNanos / 1_000_000_000f).coerceAtMost(0.05f)

                // Run simulation tick
                tick(deltaSeconds)

                // Emit state to UI
                emitState()

                // Sleep to maintain ~60 TPS
                val tickDuration = (System.nanoTime() - now) / 1_000_000f
                val sleepTime = (GameConfig.TICK_INTERVAL_MS - tickDuration.toLong()).coerceAtLeast(1L)
                delay(sleepTime)
            }
        }
    }

    /**
     * Stop the game engine.
     */
    fun stopEngine() {
        isRunning = false
        engineJob?.cancel()
        engineJob = null
    }

    /**
     * Select a champion to view stats and issue commands.
     */
    fun selectChampion(championId: String?) {
        selectedChampionId = championId
    }

    /**
     * Issue a tactical order to a specific champion.
     */
    fun issueTacticalOrder(championId: String, order: com.moba.master.ai.TacticalOrder) {
        val champion = blueChampions.find { it.id == championId }
        champion?.tacticalOrder = order
    }

    /**
     * Clear a champion's tactical order (return to autonomous AI).
     */
    fun clearTacticalOrder(championId: String) {
        val champion = blueChampions.find { it.id == championId }
        champion?.tacticalOrder = null
    }

    /**
     * Purchase an item for a champion.
     */
    fun purchaseItem(championId: String, itemId: String): Boolean {
        val champion = blueChampions.find { it.id == championId } ?: return false
        val item = com.moba.master.items.ItemDatabase.getItemById(itemId) ?: return false
        val success = champion.purchaseItem(item)
        if (success) {
            // Update total gold
            totalGold = blueChampions.sumOf { it.gold }
        }
        return success
    }

    // ── Private Simulation Methods ──

    private fun tick(deltaSeconds: Float) {
        gameTime += deltaSeconds

        // 1. Passive gold income
        totalGold += (GameConfig.GOLD_PASSIVE_PER_SEC * deltaSeconds).toInt()

        // 2. Spawn minions
        MinionSpawner.update(deltaSeconds, blueMinions, redMinions)

        // 3. Update minion AI (march down the lane)
        updateMinions(deltaSeconds)

        // 4. Update tower AI
        updateTowers(deltaSeconds)

        // 5. Update champion AI
        updateChampions(deltaSeconds)

        // 6. Process combat
        processCombat(deltaSeconds)

        // 7. Clean up dead entities
        cleanupDeadEntities()

        // 8. Check win condition
        checkWinCondition()
    }

    private fun updateMinions(deltaSeconds: Float) {
        // Blue minions march towards Red base
        for (minion in blueMinions.filter { it.isAlive }) {
            updateMinionMovement(minion, MapConstants.LANE_END, deltaSeconds)
        }
        // Red minions march towards Blue base
        for (minion in redMinions.filter { it.isAlive }) {
            updateMinionMovement(minion, MapConstants.LANE_START, deltaSeconds)
        }
    }

    private fun updateMinionMovement(minion: MinionEntity, target: Coordinate, deltaSeconds: Float) {
        // Check for enemies in range
        val enemies = if (minion.team == Team.BLUE) {
            redMinions.filter { it.isAlive } + redChampions.filter { it.isAlive } + listOfNotNull(redNexus).filter { it.isAlive }
        } else {
            blueMinions.filter { it.isAlive } + blueChampions.filter { it.isAlive } + listOfNotNull(blueNexus).filter { it.isAlive }
        }

        // Find nearest enemy in attack range
        val nearestEnemy = enemies
            .filter { minion.position.distanceTo(it.position) <= minion.attackRange }
            .minByOrNull { minion.position.distanceTo(it.position) }

        if (nearestEnemy != null) {
            minion.isAttacking = true
            minion.targetEntityId = nearestEnemy.id
        } else {
            minion.isAttacking = false
            minion.targetEntityId = null
            // March forward
            val dist = minion.position.distanceTo(target)
            if (dist > 5f) {
                val moveAmount = minion.moveSpeed * deltaSeconds
                minion.position = minion.position.moveTowards(target, moveAmount)
            }
        }
    }

    private fun updateTowers(deltaSeconds: Float) {
        for (tower in (blueTowers + redTowers).filter { it.isAlive }) {
            // Find nearest enemy in range
            val enemies = if (tower.team == Team.BLUE) {
                redMinions.filter { it.isAlive } + redChampions.filter { it.isAlive }
            } else {
                blueMinions.filter { it.isAlive } + blueChampions.filter { it.isAlive }
            }

            val target = enemies
                .filter { tower.position.distanceTo(it.position) <= tower.attackRange }
                .minByOrNull { tower.position.distanceTo(it.position) }

            if (target != null) {
                tower.targetEntityId = target.id
            } else {
                tower.targetEntityId = null
            }
        }
    }

    private fun updateChampions(deltaSeconds: Float) {
        // Update Blue champions
        for (champion in blueChampions) {
            FSMSystem.updateChampion(
                champion = champion,
                allies = blueChampions,
                enemies = redChampions,
                alliedMinions = blueMinions,
                enemyMinions = redMinions,
                alliedTowers = blueTowers,
                enemyTowers = redTowers,
                enemyNexus = redNexus,
                laneTarget = MapConstants.LANE_END,
                deltaSeconds = deltaSeconds
            )
        }

        // Update Red champions
        for (champion in redChampions) {
            FSMSystem.updateChampion(
                champion = champion,
                allies = redChampions,
                enemies = blueChampions,
                alliedMinions = redMinions,
                enemyMinions = blueMinions,
                alliedTowers = redTowers,
                enemyTowers = blueTowers,
                enemyNexus = blueNexus,
                laneTarget = MapConstants.LANE_START,
                deltaSeconds = deltaSeconds
            )
        }
    }

    private fun processCombat(deltaSeconds: Float) {
        // ── Champion Attacks ──
        for (champion in (blueChampions + redChampions).filter { it.isAlive }) {
            val targetId = champion.targetEntityId ?: continue
            val target = findEntityById(targetId) ?: continue
            if (!target.isAlive) {
                champion.targetEntityId = null
                continue
            }

            // Check if target is in attack range
            val dist = champion.position.distanceTo(target.position)
            val attackRange = champion.stats.attackRange + champion.radius + target.radius

            if (dist <= attackRange) {
                if (champion.tickAttackCooldown(deltaSeconds)) {
                    DamageCalculator.processChampionAttack(champion, target)

                    // Gold reward for killing minions
                    if (!target.isAlive && target is MinionEntity) {
                        champion.gold += target.goldReward
                    }
                    // Gold reward for champion kills
                    if (!target.isAlive && target is ChampionEntity) {
                        champion.gold += GameConfig.GOLD_PER_CHAMPION_KILL
                        champion.kills++
                    }
                }
            } else {
                champion.targetEntityId = null
            }

            // Process special effects
            val allEnemies = if (champion.team == Team.BLUE) {
                (redChampions + redMinions + listOfNotNull(redNexus)).filter { it.isAlive }
            } else {
                (blueChampions + blueMinions + listOfNotNull(blueNexus)).filter { it.isAlive }
            }

            DamageCalculator.processAoeBurn(champion, allEnemies, deltaSeconds)

            if (champion.targetEntityId != null) {
                val burnTarget = findEntityById(champion.targetEntityId!!)
                if (burnTarget != null) {
                    DamageCalculator.processPercentMaxHpBurn(champion, burnTarget, deltaSeconds)
                }
            }
        }

        // ── Minion Attacks ──
        for (minion in (blueMinions + redMinions).filter { it.isAlive }) {
            if (!minion.isAttacking) continue
            val targetId = minion.targetEntityId ?: continue
            val target = findEntityById(targetId) ?: continue
            if (!target.isAlive) {
                minion.targetEntityId = null
                minion.isAttacking = false
                continue
            }

            val dist = minion.position.distanceTo(target.position)
            if (dist <= minion.attackRange + minion.radius + target.radius) {
                if (minion.tickAttackCooldown(deltaSeconds)) {
                    DamageCalculator.processMinionAttack(minion, target)
                }
            } else {
                minion.targetEntityId = null
                minion.isAttacking = false
            }
        }

        // ── Tower Attacks ──
        for (tower in (blueTowers + redTowers).filter { it.isAlive }) {
            val targetId = tower.targetEntityId ?: continue
            val target = findEntityById(targetId) ?: continue
            if (!target.isAlive) {
                tower.targetEntityId = null
                continue
            }

            val dist = tower.position.distanceTo(target.position)
            if (dist <= tower.attackRange + tower.radius + target.radius) {
                if (tower.tickAttackCooldown(deltaSeconds)) {
                    DamageCalculator.processTowerAttack(tower, target)
                }
            } else {
                tower.targetEntityId = null
            }
        }
    }

    private fun cleanupDeadEntities() {
        // Remove dead minions after a brief delay (they linger for 1 second for visual)
        blueMinions.removeAll { !it.isAlive }
        redMinions.removeAll { !it.isAlive }
    }

    private fun checkWinCondition() {
        // Check if either nexus is destroyed
        if (blueNexus != null && !blueNexus!!.isAlive) {
            isGameOver = true
            winningTeam = Team.RED
        }
        if (redNexus != null && !redNexus!!.isAlive) {
            isGameOver = true
            winningTeam = Team.BLUE
        }
    }

    private fun findEntityById(id: String): GameEntity? {
        // Check champions
        (blueChampions + redChampions).forEach { if (it.id == id) return it }
        // Check minions
        (blueMinions + redMinions).forEach { if (it.id == id) return it }
        // Check towers
        (blueTowers + redTowers).forEach { if (it.id == id) return it }
        // Check nexuses
        if (blueNexus?.id == id) return blueNexus
        if (redNexus?.id == id) return redNexus
        return null
    }

    private fun emitState() {
        _gameState.value = GameState(
            gameTime = gameTime,
            isGameOver = isGameOver,
            winningTeam = winningTeam,
            blueChampions = blueChampions.toList(),
            redChampions = redChampions.toList(),
            blueMinions = blueMinions.toList(),
            redMinions = redMinions.toList(),
            blueTowers = blueTowers.toList(),
            redTowers = redTowers.toList(),
            blueNexus = blueNexus,
            redNexus = redNexus,
            selectedChampionId = selectedChampionId,
            totalGold = totalGold,
            waveNumber = MinionSpawner.waveNumber,
            timeUntilNextWave = MinionSpawner.timeUntilNextWave
        )
    }

    private fun resetState() {
        gameTime = 0f
        isGameOver = false
        winningTeam = null
        blueChampions.clear()
        blueMinions.clear()
        blueTowers.clear()
        blueNexus = null
        redChampions.clear()
        redMinions.clear()
        redTowers.clear()
        redNexus = null
        selectedChampionId = null
        totalGold = 0
    }
}
