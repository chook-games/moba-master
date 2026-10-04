package com.moba.master.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Main menu screen — the first thing the player sees.
 * Shows the game title, a PLAY button, and a "SOON™" teaser.
 */
@Composable
fun MainMenuScreen(
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D1117),
                        Color(0xFF1A1F2E),
                        Color(0xFF0D1117)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // ── Title ──
            Text(
                text = "MOBA",
                fontSize = 64.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFFE65100),
                letterSpacing = 8.sp
            )
            Text(
                text = "MASTER",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F),
                letterSpacing = 12.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Subtitle ──
            Text(
                text = "IDLE ARAM BATTLE",
                fontSize = 14.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF90A4AE),
                letterSpacing = 6.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── PLAY Button ──
            Button(
                onClick = onPlayClick,
                modifier = Modifier
                    .width(220.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE65100),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "▶  PLAY",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── SOON™ Teaser ──
            Text(
                text = "SOON™",
                fontSize = 11.sp,
                fontWeight = FontWeight.Light,
                color = Color(0xFF666666),
                letterSpacing = 4.sp
            )
            Text(
                text = "Ranked • Leaderboards • Skins",
                fontSize = 10.sp,
                color = Color(0xFF444444),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── Version ──
            Text(
                text = "v1.0.0 — ARAM Pre-Alpha",
                fontSize = 9.sp,
                color = Color(0xFF333333),
                letterSpacing = 1.sp
            )
        }
    }
}
