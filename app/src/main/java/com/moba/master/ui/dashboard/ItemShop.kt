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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.moba.master.items.Item
import com.moba.master.items.ItemDatabase
import com.moba.master.ui.theme.TeamColors

/**
 * The parody item shop displayed in the dashboard.
 * Shows available items that can be purchased for the selected champion.
 */
@Composable
fun ItemShop(
    selectedChampionGold: Int,
    onPurchaseItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1A1F2E))
            .padding(8.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ITEM SHOP",
                color = TeamColors.Gold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Gold: ${selectedChampionGold}G",
                color = TeamColors.Gold,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Item list (horizontal scroll)
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(ItemDatabase.ALL_ITEMS) { item ->
                ItemCard(
                    item = item,
                    canAfford = selectedChampionGold >= item.cost,
                    onPurchase = { onPurchaseItem(item.id) }
                )
            }
        }
    }
}

@Composable
private fun ItemCard(
    item: Item,
    canAfford: Boolean,
    onPurchase: () -> Unit
) {
    val bgColor = if (canAfford) Color(0xFF252A3A) else Color(0xFF1A1F2E)
    val borderColor = if (canAfford) Color(0xFF4A4F5E) else Color(0xFF2A2F3E)

    Column(
        modifier = Modifier
            .width(100.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(6.dp))
            .clickable(enabled = canAfford) { onPurchase() }
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Item icon placeholder (colored box)
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(getItemColor(item)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = item.name.first().toString(),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Item name
        Text(
            text = item.name,
            color = if (canAfford) Color.White else Color(0xFF666666),
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        // Cost
        Text(
            text = "${item.cost}G",
            color = if (canAfford) TeamColors.Gold else Color(0xFF555555),
            fontSize = 8.sp
        )

        // Stats preview (first line)
        val statsPreview = buildStatPreview(item)
        if (statsPreview.isNotEmpty()) {
            Text(
                text = statsPreview,
                color = if (canAfford) Color(0xFF90A4AE) else Color(0xFF444444),
                fontSize = 7.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/** Get a color for the item based on its primary stat. */
private fun getItemColor(item: Item): Color {
    return when {
        item.bonusAD > 50f -> Color(0xFFE65100) // Orange for AD
        item.bonusAP > 80f -> Color(0xFF7B1FA2) // Purple for AP
        item.bonusMaxHP > 500f -> Color(0xFF2E7D32) // Green for HP
        item.bonusArmor > 50f -> Color(0xFF1565C0) // Blue for Armor
        item.bonusAttackSpeed > 0.2f -> Color(0xFFF9A825) // Yellow for AS
        else -> Color(0xFF546E7A) // Grey for utility
    }
}

/** Build a short stat preview string for the item. */
private fun buildStatPreview(item: Item): String {
    val parts = mutableListOf<String>()
    if (item.bonusAD > 0f) parts.add("+${item.bonusAD.toInt()} AD")
    if (item.bonusAP > 0f) parts.add("+${item.bonusAP.toInt()} AP")
    if (item.bonusMaxHP > 0f) parts.add("+${item.bonusMaxHP.toInt()} HP")
    if (item.bonusArmor > 0f) parts.add("+${item.bonusArmor.toInt()} Armor")
    if (item.bonusMagicResist > 0f) parts.add("+${item.bonusMagicResist.toInt()} MR")
    if (item.bonusAttackSpeed > 0f) parts.add("+${(item.bonusAttackSpeed * 100).toInt()}% AS")
    if (item.bonusLifesteal > 0f) parts.add("+${(item.bonusLifesteal * 100).toInt()}% LS")
    if (item.bonusCritChance > 0f) parts.add("+${(item.bonusCritChance * 100).toInt()}% Crit")
    return parts.take(2).joinToString(" ")
}
