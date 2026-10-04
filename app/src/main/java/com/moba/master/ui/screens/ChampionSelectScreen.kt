package com.moba.master.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moba.master.data.ChampionRoster
import com.moba.master.data.RosterChampion

/**
 * Champion selection screen.
 * Player picks 5 champions for their team (blind pick style).
 * Shows champion name, role, and a preview of their stats.
 */
@Composable
fun ChampionSelectScreen(
    onConfirm: (List<String>) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Selected champion IDs (max 5)
    var selectedIds by remember { mutableStateOf(setOf<String>()) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ── Header ──
            Text(
                text = "SELECT YOUR SQUAD",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F),
                letterSpacing = 3.sp
            )
            Text(
                text = "Pick ${5 - selectedIds.size} champions (tap to select/deselect)",
                fontSize = 12.sp,
                color = Color(0xFF90A4AE)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Selected Champions Bar ──
            SelectedChampionsBar(
                selectedIds = selectedIds,
                onRemove = { id -> selectedIds = selectedIds - id }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Champion Grid ──
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(ChampionRoster.allChampions) { champion ->
                    ChampionGridItem(
                        champion = champion,
                        isSelected = champion.id in selectedIds,
                        isPicked = selectedIds.size >= 5 && champion.id !in selectedIds,
                        onClick = {
                            if (champion.id in selectedIds) {
                                selectedIds = selectedIds - champion.id
                            } else if (selectedIds.size < 5) {
                                selectedIds = selectedIds + champion.id
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Bottom Buttons ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Back
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A1F2E))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "← BACK",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF90A4AE)
                    )
                }

                // Confirm
                val canConfirm = selectedIds.size == 5
                Box(
                    modifier = Modifier
                        .weight(2f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (canConfirm) Color(0xFFE65100)
                            else Color(0xFF333333)
                        )
                        .clickable(enabled = canConfirm) {
                            onConfirm(selectedIds.toList())
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (canConfirm) "▶ START GAME"
                        else "SELECT ${5 - selectedIds.size} MORE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (canConfirm) Color.White else Color(0xFF666666),
                        letterSpacing = 1.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * Horizontal bar showing the currently selected champions.
 */
@Composable
private fun SelectedChampionsBar(
    selectedIds: Set<String>,
    onRemove: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1F2E))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selectedIds.isEmpty()) {
            Text(
                text = "Tap champions below to build your team...",
                fontSize = 11.sp,
                color = Color(0xFF555555),
                modifier = Modifier.padding(4.dp)
            )
        } else {
            selectedIds.forEach { id ->
                val champ = ChampionRoster.getById(id)
                if (champ != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(roleColor(champ.role).copy(alpha = 0.3f))
                            .clickable { onRemove(id) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = champ.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = roleColor(champ.role)
                        )
                    }
                }
            }
        }
    }
}

/**
 * A single champion card in the selection grid.
 */
@Composable
private fun ChampionGridItem(
    champion: RosterChampion,
    isSelected: Boolean,
    isPicked: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isSelected) roleColor(champion.role).copy(alpha = 0.25f)
    else Color(0xFF1A1F2E)

    val borderColor = if (isSelected) roleColor(champion.role)
    else Color(0xFF2A2A3E)

    Box(
        modifier = Modifier
            .aspectRatio(0.85f)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !isPicked) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(6.dp)
        ) {
            // Role icon placeholder (circle with role initial)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(roleColor(champion.role).copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = champion.role.take(1),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = roleColor(champion.role)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Name
            Text(
                text = champion.displayName,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color(0xFFB0B0B0),
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            // Role
            Text(
                text = champion.role,
                fontSize = 8.sp,
                color = roleColor(champion.role),
                textAlign = TextAlign.Center
            )

            // Stats preview
            Text(
                text = "HP ${champion.baseHP.toInt()} | AD ${champion.baseAD.toInt()}",
                fontSize = 7.sp,
                color = Color(0xFF666666),
                textAlign = TextAlign.Center
            )

            // Picked overlay
            if (isPicked) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "FULL",
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF444444)
                )
            }
        }
    }
}

/** Get a color associated with each role. */
private fun roleColor(role: String): Color = when (role) {
    "Marksman" -> Color(0xFFFFD700)
    "Mage" -> Color(0xFF9C27B0)
    "Tank" -> Color(0xFF4CAF50)
    "Assassin" -> Color(0xFFF44336)
    "Support" -> Color(0xFF66BB6A)
    "Fighter" -> Color(0xFFFF9800)
    else -> Color(0xFF90A4AE)
}
