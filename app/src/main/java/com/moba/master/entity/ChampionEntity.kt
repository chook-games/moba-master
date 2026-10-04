package com.moba.master.entity

import com.moba.master.ai.AIState
import com.moba.master.ai.TacticalOrder
import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig
import com.moba.master.items.Item

/**
 * A champion entity controlled by the AI FSM with tactical overrides from the player.
 * Champions have stats, items, and an AI state machine.
 */
class ChampionEntity(
    id: String,
    position: Coordinate,
    team: Team,
    /** Display name for the champion (e.g., "Aetherblade", "Voidcaller"). */
    val displayName: String,
    /** The champion's role/class for UI display. */
    val role: String = "Fighter"
) : GameEntity(
    id = id,
    position = position,
    team = team,
    currentHP = GameConfig.BASE_HP,
    maxHP = GameConfig.BASE_HP,
    radius = GameConfig.CHAMPION_RADIUS
) {
    /** Current computed stats (base + items). */
    var stats: ChampionStats = ChampionStats.base()
        private set

    /** Items currently equipped by this champion. */
    val items: MutableList<Item> = mutableListOf()

    /** Current AI state. */
    var aiState: AIState = AIState.STATE_MOVING_TO_LANE

    /** Player's tactical override order. Null means no override (AI acts autonomously). */
    var tacticalOrder: TacticalOrder? = null

    /** Gold accumulated by this champion. */
    var gold: Int = 0

    /** Kill/death/assist tracking. */
    var kills: Int = 0
    var deaths: Int = 0
    var assists: Int = 0

    /** Attack cooldown timer (seconds until next auto-attack). */
    var attackCooldownRemaining: Float = 0f

    /** Respawn timer. */
    var respawnTimer: Float = 0f

    /** Whether the champion is currently in stasis (invulnerable, can't act). */
    var isInStasis: Boolean = false

    /** Whether the revive passive has been used. */
    var reviveUsed: Boolean = false

    /** Target entity ID that this champion is currently attacking/following. */
    var targetEntityId: String? = null

    /** Recalculate stats from base + all equipped items. */
    fun recalcStats() {
        stats = ChampionStats.fromBaseAndItems(items)
    }

    /**
     * Initialize stats from a roster template (used when starting a new game).
     * This bypasses the normal item-based calculation.
     */
    fun initStatsFromRoster(
        maxHP: Float,
        attackDamage: Float,
        abilityPower: Float,
        armor: Float,
        magicResist: Float,
        attackSpeed: Float,
        moveSpeed: Float,
        attackRange: Float
    ) {
        stats = ChampionStats(
            maxHP = maxHP,
            attackDamage = attackDamage,
            abilityPower = abilityPower,
            armor = armor,
            magicResist = magicResist,
            attackSpeed = attackSpeed,
            moveSpeed = moveSpeed,
            attackRange = attackRange,
            lifesteal = GameConfig.BASE_LIFESTEAL,
            critChance = GameConfig.BASE_CRIT_CHANCE,
            critDamage = GameConfig.BASE_CRIT_DAMAGE,
            hpRegen = 0f
        )
    }

    /** Maximum number of items a champion can equip (standard MOBA limit). */
    companion object {
        const val MAX_ITEMS = 6
    }

    /** Purchase an item if the champion has enough gold and has an open slot. */
    fun purchaseItem(item: Item): Boolean {
        if (gold < item.cost) return false
        if (items.size >= MAX_ITEMS) return false
        gold -= item.cost
        items.add(item)
        recalcStats()
        return true
    }

    /** Sell the last purchased item (refund 50% gold). */
    fun sellLastItem(): Boolean {
        if (items.isEmpty()) return false
        val item = items.removeLast()
        gold += (item.cost * 0.5).toInt()
        recalcStats()
        return true
    }

    /** Get the effective max HP from stats. */
    fun effectiveMaxHP(): Float = stats.maxHP

    /** Override takeDamage to use stats for maxHP clamping. */
    override fun takeDamage(rawDamage: Float): Float {
        if (!isAlive || isInStasis) return 0f
        currentHP = (currentHP - rawDamage).coerceAtLeast(0f)
        if (currentHP <= 0f) {
            // Check for revive
            if (stats.hasRevive && !reviveUsed) {
                reviveUsed = true
                currentHP = stats.maxHP * 0.5f
                return rawDamage * 0.5f
            }
            die()
        }
        return rawDamage
    }

    override fun die() {
        super.die()
        deaths++
        respawnTimer = 10f // 10 second respawn
    }

    /** Respawn the champion at the given position. */
    fun respawn(spawnPosition: Coordinate) {
        isAlive = true
        position = spawnPosition
        currentHP = stats.maxHP
        attackCooldownRemaining = 0f
        targetEntityId = null
        aiState = AIState.STATE_MOVING_TO_LANE
    }

    /** Update attack cooldown. Returns true if ready to attack. */
    fun tickAttackCooldown(deltaSeconds: Float): Boolean {
        if (attackCooldownRemaining > 0f) {
            attackCooldownRemaining -= deltaSeconds
        }
        return attackCooldownRemaining <= 0f
    }

    /** Reset attack cooldown after attacking. */
    fun resetAttackCooldown() {
        attackCooldownRemaining = 1.0f / stats.attackSpeed.coerceAtLeast(0.1f)
    }
}
