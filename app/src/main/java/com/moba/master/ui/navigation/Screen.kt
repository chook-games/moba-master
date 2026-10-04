package com.moba.master.ui.navigation

/**
 * Navigation state for the game's screen flow.
 * Simple sealed class — no external navigation library needed.
 */
sealed class Screen {
    /** Main menu with title and PLAY button. */
    data object MainMenu : Screen()

    /** Game mode selection (ARAM only for now). */
    data object GameMode : Screen()

    /** Champion selection — pick 5 champions for your team. */
    data object ChampionSelect : Screen()

    /** The actual game (battlefield + dashboard). */
    data object Game : Screen()
}
