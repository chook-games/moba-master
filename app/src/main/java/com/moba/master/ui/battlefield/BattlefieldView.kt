package com.moba.master.ui.battlefield

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.moba.master.engine.Coordinate
import com.moba.master.engine.GameConfig
import com.moba.master.engine.GameState
import com.moba.master.entity.*
import com.moba.master.map.MapConstants
import com.moba.master.ui.theme.TeamColors

/**
 * The top 50% of the screen - renders the 2D battlefield using Android Canvas.
 * All entities are drawn as simple shapes (circles, rectangles, diamonds).
 */
@Composable
fun BattlefieldView(
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val scaleX = size.width / GameConfig.MAP_WIDTH
        val scaleY = size.height / GameConfig.MAP_HEIGHT
        val scale = minOf(scaleX, scaleY)

        // Center the map
        val offsetX = (size.width - GameConfig.MAP_WIDTH * scale) / 2f
        val offsetY = (size.height - GameConfig.MAP_HEIGHT * scale) / 2f

        // ── 1. Draw Background ──
        drawRect(
            color = Color(0xFF1B2A3A),
            topLeft = Offset(offsetX, offsetY),
            size = Size(GameConfig.MAP_WIDTH * scale, GameConfig.MAP_HEIGHT * scale)
        )

        // ── 2. Draw Lane ──
        drawLane(scale, offsetX, offsetY)

        // ── 3. Draw Walls ──
        drawWalls(scale, offsetX, offsetY)

        // ── 4. Draw Nexuses ──
        gameState.blueNexus?.let { drawNexus(it, scale, offsetX, offsetY) }
        gameState.redNexus?.let { drawNexus(it, scale, offsetX, offsetY) }

        // ── 5. Draw Towers ──
        gameState.blueTowers.forEach { drawTower(it, scale, offsetX, offsetY) }
        gameState.redTowers.forEach { drawTower(it, scale, offsetX, offsetY) }

        // ── 6. Draw Minions ──
        gameState.blueMinions.forEach { drawMinion(it, scale, offsetX, offsetY) }
        gameState.redMinions.forEach { drawMinion(it, scale, offsetX, offsetY) }

        // ── 7. Draw Champions ──
        gameState.blueChampions.forEach { drawChampion(it, scale, offsetX, offsetY, gameState.selectedChampionId) }
        gameState.redChampions.forEach { drawChampion(it, scale, offsetX, offsetY, gameState.selectedChampionId) }

        // ── 8. Draw Spawn Bases ──
        drawSpawnBase(scale, offsetX, offsetY)
    }
}

/** Draw the diagonal ARAM lane. */
private fun DrawScope.drawLane(scale: Float, offsetX: Float, offsetY: Float) {
    val start = worldToScreen(MapConstants.LANE_START, scale, offsetX, offsetY)
    val end = worldToScreen(MapConstants.LANE_END, scale, offsetX, offsetY)
    val laneWidth = GameConfig.LANE_WIDTH * scale

    // Draw lane as a thick line
    val path = Path().apply {
        moveTo(start.x, start.y)
        lineTo(end.x, end.y)
    }

    drawPath(
        path = path,
        color = Color(0xFF2D4A3E),
        style = Stroke(width = laneWidth)
    )

    // Lane center line (dashed)
    drawPath(
        path = path,
        color = Color(0xFF4A6B5A),
        style = Stroke(width = 2f * scale)
    )
}

/** Draw the walls bordering the lane. */
private fun DrawScope.drawWalls(scale: Float, offsetX: Float, offsetY: Float) {
    for (wall in MapConstants.WALLS) {
        val start = worldToScreen(wall.start, scale, offsetX, offsetY)
        val end = worldToScreen(wall.end, scale, offsetX, offsetY)
        val wallWidth = wall.thickness * scale

        drawLine(
            color = Color(0xFF3E2723),
            start = start,
            end = end,
            strokeWidth = wallWidth
        )
    }
}

/** Draw a nexus as a large diamond shape. */
private fun DrawScope.drawNexus(
    nexus: NexusEntity,
    scale: Float,
    offsetX: Float,
    offsetY: Float
) {
    val pos = worldToScreen(nexus.position, scale, offsetX, offsetY)
    val radius = nexus.radius * scale
    val color = if (nexus.team == Team.BLUE) TeamColors.Blue else TeamColors.Red

    // Diamond shape
    val path = Path().apply {
        moveTo(pos.x, pos.y - radius)
        lineTo(pos.x + radius, pos.y)
        lineTo(pos.x, pos.y + radius)
        lineTo(pos.x - radius, pos.y)
        close()
    }

    drawPath(path, color.copy(alpha = 0.8f))
    drawPath(path, color, style = Stroke(width = 3f * scale))

    // HP bar
    val hpPercent = nexus.currentHP / nexus.maxHP
    drawRect(
        color = TeamColors.HealthGreen,
        topLeft = Offset(pos.x - radius, pos.y - radius - 8f * scale),
        size = Size(radius * 2f * hpPercent, 4f * scale)
    )
    drawRect(
        color = TeamColors.HealthRed,
        topLeft = Offset(pos.x - radius + radius * 2f * hpPercent, pos.y - radius - 8f * scale),
        size = Size(radius * 2f * (1f - hpPercent), 4f * scale)
    )
}

/** Draw a tower as a square. */
private fun DrawScope.drawTower(
    tower: TowerEntity,
    scale: Float,
    offsetX: Float,
    offsetY: Float
) {
    val pos = worldToScreen(tower.position, scale, offsetX, offsetY)
    val size = tower.radius * scale * 2f
    val color = if (tower.team == Team.BLUE) TeamColors.BlueLight else TeamColors.RedLight

    // Square tower
    drawRect(
        color = color.copy(alpha = 0.7f),
        topLeft = Offset(pos.x - size / 2f, pos.y - size / 2f),
        size = Size(size, size)
    )
    drawRect(
        color = color,
        topLeft = Offset(pos.x - size / 2f, pos.y - size / 2f),
        size = Size(size, size),
        style = Stroke(width = 2f * scale)
    )

    // HP bar
    val hpPercent = tower.currentHP / tower.maxHP
    drawRect(
        color = TeamColors.HealthGreen,
        topLeft = Offset(pos.x - size / 2f, pos.y - size / 2f - 6f * scale),
        size = Size(size * hpPercent, 3f * scale)
    )
    drawRect(
        color = TeamColors.HealthRed,
        topLeft = Offset(pos.x - size / 2f + size * hpPercent, pos.y - size / 2f - 6f * scale),
        size = Size(size * (1f - hpPercent), 3f * scale)
    )
}

/** Draw a minion as a small circle. */
private fun DrawScope.drawMinion(
    minion: MinionEntity,
    scale: Float,
    offsetX: Float,
    offsetY: Float
) {
    if (!minion.isAlive) return
    val pos = worldToScreen(minion.position, scale, offsetX, offsetY)
    val radius = minion.radius * scale
    val color = if (minion.team == Team.BLUE) TeamColors.Blue else TeamColors.Red

    // Circle
    drawCircle(color = color.copy(alpha = 0.6f), radius = radius, center = pos)
    drawCircle(color = color, radius = radius, center = pos, style = Stroke(width = 1.5f * scale))

    // Type indicator
    when (minion.minionType) {
        MinionType.MELEE -> { /* plain circle */ }
        MinionType.CASTER -> {
            // Small dot in center
            drawCircle(color = Color.White, radius = radius * 0.3f, center = pos)
        }
        MinionType.SIEGE -> {
            // Larger, with a square outline
            drawRect(
                color = Color(0xFFFFD700),
                topLeft = Offset(pos.x - radius * 0.5f, pos.y - radius * 0.5f),
                size = Size(radius, radius),
                style = Stroke(width = 2f * scale)
            )
        }
    }
}

/** Draw a champion as a larger circle with role indicator. */
private fun DrawScope.drawChampion(
    champion: ChampionEntity,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    selectedChampionId: String?
) {
    if (!champion.isAlive) return
    val pos = worldToScreen(champion.position, scale, offsetX, offsetY)
    val radius = champion.radius * scale
    val color = if (champion.team == Team.BLUE) TeamColors.Blue else TeamColors.Red

    // Body circle
    drawCircle(color = color.copy(alpha = 0.8f), radius = radius, center = pos)
    drawCircle(color = color, radius = radius, center = pos, style = Stroke(width = 2.5f * scale))

    // Role indicator (small shape inside)
    val innerRadius = radius * 0.4f
    when (champion.role) {
        "Marksman" -> drawCircle(color = Color(0xFFFFD700), radius = innerRadius, center = pos)
        "Mage" -> drawCircle(color = Color(0xFF9C27B0), radius = innerRadius, center = pos)
        "Tank" -> drawRect(
            color = Color(0xFF4CAF50),
            topLeft = Offset(pos.x - innerRadius, pos.y - innerRadius),
            size = Size(innerRadius * 2f, innerRadius * 2f)
        )
        "Assassin" -> {
            // Diamond
            val path = Path().apply {
                moveTo(pos.x, pos.y - innerRadius)
                lineTo(pos.x + innerRadius, pos.y)
                lineTo(pos.x, pos.y + innerRadius)
                lineTo(pos.x - innerRadius, pos.y)
                close()
            }
            drawPath(path, Color(0xFFF44336))
        }
        "Support" -> drawCircle(color = Color(0xFF66BB6A), radius = innerRadius, center = pos)
        else -> drawCircle(color = Color.White, radius = innerRadius, center = pos)
    }

    // Selection highlight
    if (champion.id == selectedChampionId) {
        drawCircle(
            color = TeamColors.NeonHighlight,
            radius = radius + 6f * scale,
            center = pos,
            style = Stroke(width = 3f * scale)
        )
    }

    // HP bar
    val hpPercent = champion.currentHP / champion.stats.maxHP
    val barWidth = radius * 2.5f
    val barHeight = 3f * scale
    drawRect(
        color = TeamColors.HealthGreen,
        topLeft = Offset(pos.x - barWidth / 2f, pos.y - radius - 8f * scale),
        size = Size(barWidth * hpPercent, barHeight)
    )
    drawRect(
        color = TeamColors.HealthRed,
        topLeft = Offset(pos.x - barWidth / 2f + barWidth * hpPercent, pos.y - radius - 8f * scale),
        size = Size(barWidth * (1f - hpPercent), barHeight)
    )

    // Name label - draw a small indicator dot (text drawing requires drawIntoCanvas)
    drawCircle(color = Color.White, radius = 2f * scale, center = Offset(pos.x, pos.y + radius + 12f * scale))
}

/** Draw spawn base indicators. */
private fun DrawScope.drawSpawnBase(scale: Float, offsetX: Float, offsetY: Float) {
    // Blue spawn
    val blueSpawn = worldToScreen(GameConfig.BLUE_SPAWN, scale, offsetX, offsetY)
    drawCircle(
        color = TeamColors.Blue.copy(alpha = 0.2f),
        radius = 60f * scale,
        center = blueSpawn
    )

    // Red spawn
    val redSpawn = worldToScreen(GameConfig.RED_SPAWN, scale, offsetX, offsetY)
    drawCircle(
        color = TeamColors.Red.copy(alpha = 0.2f),
        radius = 60f * scale,
        center = redSpawn
    )
}

/** Convert world coordinates to screen coordinates. */
private fun worldToScreen(world: Coordinate, scale: Float, offsetX: Float, offsetY: Float): Offset {
    return Offset(
        x = world.x * scale + offsetX,
        y = world.y * scale + offsetY
    )
}
