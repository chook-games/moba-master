package com.moba.master.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.moba.master.entity.ChampionEntity
import com.moba.master.ui.theme.TeamColors

/**
 * A card displaying a champion's basic info in the dashboard.
 * Tapping selects/deselects the champion.
 */
@Composable
fun ChampionCard(
    champion: ChampionEntity,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) Color(0xFF2A3F5F) else Color(0xFF1A1F2E)
    val borderColor = if (isSelected) TeamColors.NeonHighlight else Color(0xFF3A3F4E)

    Column(
        modifier = modifier
            .width(90.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable { onSelect() }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Champion name
        Text(
            text = champion.displayName,
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Role
        Text(
            text = champion.role,
            color = Color(0xFF90A4AE),
            fontSize = 8.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        // HP bar
        val hpPercent = champion.currentHP / champion.stats.maxHP
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(Color(0xFF333333))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(hpPercent.coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(
                        if (hpPercent > 0.5f) TeamColors.HealthGreen
                        else if (hpPercent > 0.2f) Color(0xFFFFA726)
                        else TeamColors.HealthRed
                    )
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Gold
        Text(
            text = "${champion.gold}G",
            color = TeamColors.Gold,
            fontSize = 9.sp
        )

        // KDA
        Text(
            text = "${champion.kills}/${champion.deaths}/${champion.assists}",
            color = Color(0xFFB0B0B0),
            fontSize = 8.sp
        )

        // AI State indicator
        Text(
            text = champion.aiState.name.replace("STATE_", ""),
            color = Color(0xFF66BB6A),
            fontSize = 7.sp,
            maxLines = 1
        )
    }
}
