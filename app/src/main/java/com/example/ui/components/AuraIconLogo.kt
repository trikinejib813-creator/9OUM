package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animated multi-part vector logo that can be assembled piece-by-piece
 * based on the [assemblyProgress] (0f to 1f).
 *
 * Sequence of visual assembly:
 * - 0.00 -> 0.25: Stage 1 - Ambient background aura & gradient ring
 * - 0.25 -> 0.50: Stage 2 - Orbit arcs with rotating dashed nodes
 * - 0.50 -> 0.75: Stage 3 - Hexagonal crystal shield and facets
 * - 0.75 -> 1.00: Stage 4 - Central glowing nexus diamond and radiant particle sparkles
 */
@Composable
fun AuraIconLogo(
  modifier: Modifier = Modifier,
  size: Dp = 100.dp,
  assemblyProgress: Float = 1f,
  rotationAngle: Float = 0f
) {
  Canvas(modifier = modifier.size(size)) {
    val w = this.size.width
    val h = this.size.height
    val center = Offset(w / 2f, h / 2f)
    val maxRadius = w.coerceAtMost(h) / 2f

    // 1. Stage 1: Ambient background aura (0.0 .. 0.25)
    if (assemblyProgress > 0.05f) {
      val auraAlpha = (assemblyProgress.coerceAtMost(0.25f) / 0.25f) * 0.4f
      drawCircle(
        brush = Brush.radialGradient(
          colors = listOf(
            Color(0xFF38BDF8).copy(alpha = auraAlpha),
            Color(0xFF6366F1).copy(alpha = auraAlpha * 0.5f),
            Color.Transparent
          ),
          center = center,
          radius = maxRadius * 0.95f
        ),
        radius = maxRadius * 0.95f,
        center = center
      )
    }

    // 2. Stage 2: Outer orbital rotating rings (0.2 .. 0.55)
    if (assemblyProgress > 0.20f) {
      val ringProgress = ((assemblyProgress - 0.20f) / 0.35f).coerceIn(0f, 1f)
      val sweep = 360f * ringProgress

      rotate(rotationAngle, center) {
        // Outer glow track
        drawArc(
          brush = Brush.sweepGradient(
            colors = listOf(
              Color(0xFF06B6D4),
              Color(0xFF818CF8),
              Color(0xFFA855F7),
              Color(0xFF06B6D4)
            )
          ),
          startAngle = -90f,
          sweepAngle = sweep,
          useCenter = false,
          topLeft = Offset(w * 0.08f, h * 0.08f),
          size = Size(w * 0.84f, h * 0.84f),
          style = Stroke(width = w * 0.035f, cap = StrokeCap.Round)
        )

        // Orbital nodes
        for (i in 0 until 4) {
          val nodeAngle = Math.toRadians((i * 90.0 + rotationAngle).toDouble())
          val nodeDist = maxRadius * 0.84f
          val nx = center.x + (nodeDist * cos(nodeAngle)).toFloat()
          val ny = center.y + (nodeDist * sin(nodeAngle)).toFloat()
          drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = ringProgress * 0.9f),
            radius = w * 0.022f,
            center = Offset(nx, ny)
          )
        }
      }
    }

    // 3. Stage 3: Hexagonal crystal facets (0.45 .. 0.8)
    if (assemblyProgress > 0.45f) {
      val hexProgress = ((assemblyProgress - 0.45f) / 0.35f).coerceIn(0f, 1f)
      val hexRadius = maxRadius * 0.56f * hexProgress

      val hexPath = Path()
      for (i in 0 until 6) {
        val angle = Math.toRadians((60.0 * i - 30.0).toDouble())
        val px = center.x + (hexRadius * cos(angle)).toFloat()
        val py = center.y + (hexRadius * sin(angle)).toFloat()
        if (i == 0) hexPath.moveTo(px, py) else hexPath.lineTo(px, py)
      }
      hexPath.close()

      // Fill
      drawPath(
        path = hexPath,
        brush = Brush.linearGradient(
          colors = listOf(
            Color(0xFF1E1B4B).copy(alpha = 0.85f * hexProgress),
            Color(0xFF0C4A6E).copy(alpha = 0.85f * hexProgress)
          ),
          start = Offset(0f, 0f),
          end = Offset(w, h)
        )
      )

      // Hexagon Outline
      drawPath(
        path = hexPath,
        brush = Brush.linearGradient(
          colors = listOf(Color(0xFF38BDF8), Color(0xFFA855F7))
        ),
        style = Stroke(width = w * 0.03f, cap = StrokeCap.Round)
      )
    }

    // 4. Stage 4: Central Nexus diamond and radiant star (0.7 .. 1.0)
    if (assemblyProgress > 0.70f) {
      val coreProgress = ((assemblyProgress - 0.70f) / 0.30f).coerceIn(0f, 1f)
      val diamondSize = maxRadius * 0.28f * coreProgress

      val diamond = Path().apply {
        moveTo(center.x, center.y - diamondSize)
        lineTo(center.x + diamondSize, center.y)
        lineTo(center.x, center.y + diamondSize)
        lineTo(center.x - diamondSize, center.y)
        close()
      }

      drawPath(
        path = diamond,
        brush = Brush.radialGradient(
          colors = listOf(Color.White, Color(0xFF38BDF8), Color(0xFF4F46E5)),
          center = center,
          radius = diamondSize * 1.2f
        )
      )

      // Center sparkle cross
      val crossLen = diamondSize * 1.5f * coreProgress
      drawLine(
        color = Color.White.copy(alpha = coreProgress),
        start = Offset(center.x - crossLen, center.y),
        end = Offset(center.x + crossLen, center.y),
        strokeWidth = w * 0.02f,
        cap = StrokeCap.Round
      )
      drawLine(
        color = Color.White.copy(alpha = coreProgress),
        start = Offset(center.x, center.y - crossLen),
        end = Offset(center.x, center.y + crossLen),
        strokeWidth = w * 0.02f,
        cap = StrokeCap.Round
      )
    }
  }
}
