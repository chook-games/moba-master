package com.moba.master

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.moba.master.engine.GameEngine
import com.moba.master.ui.navigation.Screen
import com.moba.master.ui.screens.*
import com.moba.master.ui.theme.MobaTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class MainActivity : ComponentActivity() {

    private lateinit var gameEngine: GameEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the game engine
        gameEngine = GameEngine()

        setContent {
            MobaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    // Navigation state
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }
                    var selectedChampionIds by remember { mutableStateOf<List<String>>(emptyList()) }

                    // Game state from engine
                    val gameState by gameEngine.gameState.collectAsState()

                    // Apply system bars padding only on non-game screens
                    // (game screen is truly full-screen)
                    val contentModifier = if (currentScreen is Screen.Game) {
                        Modifier.fillMaxSize()
                    } else {
                        Modifier
                            .fillMaxSize()
                            .systemBarsPadding()
                    }

                    Box(modifier = contentModifier) {
                        when (currentScreen) {
                            is Screen.MainMenu -> {
                                MainMenuScreen(
                                    onPlayClick = {
                                        currentScreen = Screen.GameMode
                                    }
                                )
                            }

                            is Screen.GameMode -> {
                                GameModeScreen(
                                    onSelectARAM = {
                                        currentScreen = Screen.ChampionSelect
                                    },
                                    onBack = {
                                        currentScreen = Screen.MainMenu
                                    }
                                )
                            }

                            is Screen.ChampionSelect -> {
                                ChampionSelectScreen(
                                    onConfirm = { ids ->
                                        selectedChampionIds = ids
                                        // Start the game with selected champions
                                        gameEngine.startNewGame(ids)
                                        gameEngine.startEngine(CoroutineScope(Dispatchers.Default))
                                        currentScreen = Screen.Game
                                    },
                                    onBack = {
                                        currentScreen = Screen.GameMode
                                    }
                                )
                            }

                            is Screen.Game -> {
                                GameScreen(
                                    gameState = gameState,
                                    onSelectChampion = { championId ->
                                        gameEngine.selectChampion(championId)
                                    },
                                    onIssueOrder = { championId, order ->
                                        gameEngine.issueTacticalOrder(championId, order)
                                    },
                                    onClearOrder = { championId ->
                                        gameEngine.clearTacticalOrder(championId)
                                    },
                                    onPurchaseItem = { itemId ->
                                        val selectedId = gameState.selectedChampionId
                                        if (selectedId != null) {
                                            gameEngine.purchaseItem(selectedId, itemId)
                                        }
                                    },
                                    onBackToMenu = {
                                        gameEngine.stopEngine()
                                        currentScreen = Screen.MainMenu
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // ── IMMERSIVE FULL-SCREEN MODE (AFTER setContent, so decorView exists) ──
        enableImmersiveMode()
    }

    /**
     * Enable immersive full-screen mode using the modern WindowInsetsController API
     * (API 30+) with fallback to the legacy systemUiVisibility for older devices.
     */
    private fun enableImmersiveMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // Modern API (Android 11+)
            @Suppress("DEPRECATION")
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            // Legacy API (Android 10 and below)
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            )
        }

        // Transparent bars so there's a small gap at the bottom
        @Suppress("DEPRECATION")
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.TRANSPARENT
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Re-apply immersive mode when window regains focus
            enableImmersiveMode()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::gameEngine.isInitialized) {
            gameEngine.stopEngine()
        }
    }
}
