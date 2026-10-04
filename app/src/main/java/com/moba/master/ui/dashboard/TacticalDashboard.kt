package com.moba.master.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moba.master.ai.TacticalOrder
import com.moba.master.engine.GameState
import com.moba.master.entity.ChampionEntity
import com.moba.master.ui.theme.TeamColors

/**
 * The tactical dashboard shown in the bottom 50% of the screen.
 * Displays champion cards, selected champion stats, tactical commands, and item shop.
 */
@Composable
fun TacticalDashboard(
    gameState: GameState,
    onSelectChampion: (String) -> Unit,
    onIssueOrder: (String, TacticalOrder) -> Unit,
    onClearOrder: (String) -> Unit,
    onPurchaseItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedChampion = gameState.selectedChampionId?.let { gameState.findChampionById(it) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(8.dp)
    ) {
        // ── Top Row: Champion Cards ──
        Text(
            text = "SQUAD",
            color = Color(0xFF90A4AE),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(gameState.blueChampions) { champion ->
                ChampionCard(
                    champion = champion,
                    isSelected = champion.id == gameState.selectedChampionId,
                    onSelect = {
                        if (gameState.selectedChampionId == champion.id) {
                            onSelectChampion("") // Deselect
                        } else {
                            onSelectChampion(champion.id)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Selected Champion Details ──
        if (selectedChampion != null) {
            SelectedChampionPanel(
                champion = selectedChampion,
                onIssueOrder = { order -> onIssueOrder(selectedChampion.id, order) },
                onClearOrder = { onClearOrder(selectedChampion.id) }
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Item Shop ──
            ItemShop(
                selectedChampionGold = selectedChampion.gold,
                onPurchaseItem = { itemId -> onPurchaseItem(itemId) }
            )
        } else {
            // No champion selected - show instructions
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tap a champion card to view stats and issue commands",
                    color = Color(0xFF666666),
                    fontSize = 12.sp
                )
            }
        }

        // ── Game Info Bar ──
        Spacer(modifier = Modifier.height(4.dp))
        GameInfoBar(gameState)
    }
}

/**
 * Panel showing the selected champion's detailed stats and tactical commands.
 */
@Composable
private fun SelectedChampionPanel(
    champion: ChampionEntity,
    onIssueOrder: (TacticalOrder) -> Unit,
    onClearOrder: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1F2E))
            .padding(8.dp)
    ) {
        // Champion name and stats header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${champion.displayName} (${champion.role})",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Lv.1",
                color = Color(0xFF90A4AE),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("HP", "${champion.currentHP.toInt()}/${champion.stats.maxHP.toInt()}", TeamColors.HealthGreen)
            StatItem("AD", "${champion.stats.attackDamage.toInt()}", Color(0xFFE65100))
            StatItem("AP", "${champion.stats.abilityPower.toInt()}", Color(0xFF7B1FA2))
            StatItem("Armor", "${champion.stats.armor.toInt()}", Color(0xFF1565C0))
            StatItem("AS", "${"%.2f".format(champion.stats.attackSpeed)}", Color(0xFFF9A825))
            StatItem("LS", "${(champion.stats.lifesteal * 100).toInt()}%", Color(0xFFE53935))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tactical command buttons
        Text(
            text = "TACTICAL ORDERS",
            color = Color(0xFF90A4AE),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            TacticalButton("Farm", TacticalOrder.FOCUS_FARM, Color(0xFF66BB6A), Modifier.weight(1f), onIssueOrder)
            TacticalButton("Push", TacticalOrder.PUSH_TOWER, Color(0xFFFF7043), Modifier.weight(1f), onIssueOrder)
            TacticalButton("Defend", TacticalOrder.DEFEND_BASE, Color(0xFF42A5F5), Modifier.weight(1f), onIssueOrder)
            TacticalButton("Gank", TacticalOrder.HELP_GANK, Color(0xFFAB47BC), Modifier.weight(1f), onIssueOrder)
            TacticalButton("Fight", TacticalOrder.FORCE_TEAMFIGHT, Color(0xFFEF5350), Modifier.weight(1f), onIssueOrder)
            TacticalButton("Retreat", TacticalOrder.RETREAT_HEAL, Color(0xFF78909C), Modifier.weight(1f), onIssueOrder)
        }

        // Clear order button
        if (champion.tacticalOrder != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Active Order: ${champion.tacticalOrder!!.name}  [Tap to clear]",
                color = Color(0xFFFFA726),
                fontSize = 9.sp,
                modifier = Modifier.clickable { onClearOrder() }
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFF90A4AE),
            fontSize = 8.sp
        )
    }
}

@Composable
private fun TacticalButton(
    label: String,
    order: TacticalOrder,
    color: Color,
    modifier: Modifier = Modifier,
    onIssueOrder: (TacticalOrder) -> Unit
) {
    Box(
        modifier = modifier
            .height(28.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color, RoundedCornerShape(4.dp))
            .clickable { onIssueOrder(order) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun GameInfoBar(gameState: GameState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(Color(0xFF1A1F2E))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val minutes = (gameState.gameTime / 60).toInt()
        val seconds = (gameState.gameTime % 60).toInt()
        Text(
            text = "⏱ ${"%02d:%02d".format(minutes, seconds)}",
            color = Color(0xFFB0B0B0),
            fontSize = 10.sp
        )
        Text(
            text = "Wave ${gameState.waveNumber} | Next: ${gameState.timeUntilNextWave.toInt()}s",
            color = Color(0xFFB0B0B0),
            fontSize = 10.sp
        )
        Text(
            text = "💰 ${gameState.totalGold}G",
            color = TeamColors.Gold,
            fontSize = 10.sp
        )
        if (gameState.isGameOver) {
            Text(
                text = "🏆 ${gameState.winningTeam} WINS!",
                color = TeamColors.Gold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
