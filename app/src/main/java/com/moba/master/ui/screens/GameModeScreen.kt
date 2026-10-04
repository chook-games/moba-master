package com.moba.master.ui.screens

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Game mode selection screen.
 * Currently only ARAM is available.
 */
@Composable
fun GameModeScreen(
    onSelectARAM: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // ── Header ──
            Text(
                text = "SELECT GAME MODE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F),
                letterSpacing = 4.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Choose your battlefield",
                fontSize = 13.sp,
                color = Color(0xFF90A4AE)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // ── ARAM Card ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1F2E))
                    .border(2.dp, Color(0xFFE65100), RoundedCornerShape(16.dp))
                    .clickable { onSelectARAM() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⚔️ ARAM",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "All Random, All Mid",
                        fontSize = 14.sp,
                        color = Color(0xFFB0B0B0)
                    )
                    Text(
                        text = "5v5 — Single Lane — First to Destroy Nexus Wins",
                        fontSize = 11.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Locked modes ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1F2E).copy(alpha = 0.4f))
                    .border(1.dp, Color(0xFF333333), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🔒 Classic 3-Lane",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF444444)
                    )
                    Text(
                        text = "Coming in a future update",
                        fontSize = 11.sp,
                        color = Color(0xFF333333)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Back Button ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
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

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
