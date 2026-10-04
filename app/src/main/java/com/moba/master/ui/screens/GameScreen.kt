package com.moba.master.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.moba.master.ai.TacticalOrder
import com.moba.master.engine.GameState
import com.moba.master.ui.battlefield.BattlefieldView
import com.moba.master.ui.dashboard.TacticalDashboard

/**
 * The main 50/50 split screen composable.
 * Top 50%: Real-time 2D Battlefield (Canvas)
 * Bottom 50%: Tactical Dashboard + Item Shop
 *
 * All user interactions are exposed as callback lambdas,
 * keeping this composable fully decoupled from the engine.
 */
@Composable
fun GameScreen(
    gameState: GameState,
    onSelectChampion: (String) -> Unit,
    onIssueOrder: (String, TacticalOrder) -> Unit,
    onClearOrder: (String) -> Unit,
    onPurchaseItem: (String) -> Unit,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        // ── Top 50%: Battlefield ──
        BattlefieldView(
            gameState = gameState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )

        // ── Bottom 50%: Dashboard ──
        TacticalDashboard(
            gameState = gameState,
            onSelectChampion = onSelectChampion,
            onIssueOrder = onIssueOrder,
            onClearOrder = onClearOrder,
            onPurchaseItem = onPurchaseItem,
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
        )
    }
}
