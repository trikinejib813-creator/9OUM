package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.R
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GoumLogo(
  size: Dp = 120.dp,
  assemblyProgress: Float = 1f,
  modifier: Modifier = Modifier
) {
  val cornerRadius = size * 0.22f

  if (size <= 48.dp) {
    // Compact logo for App Bars and headers
    Box(
      modifier = modifier
        .size(size)
        .clip(RoundedCornerShape(cornerRadius))
        .border(
          width = 1.dp,
          color = Color(0x33F5BD38),
          shape = RoundedCornerShape(cornerRadius)
        ),
      contentAlignment = Alignment.Center
    ) {
      Image(
        painter = painterResource(id = R.drawable.ic_goum_app_logo),
        contentDescription = "شعار 9GOUM",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(size)
      )
    }
  } else {
    // Large animated logo for Splash & Login screens
    val infiniteTransition = rememberInfiniteTransition(label = "GoumPulse")
    val pulseScale by infiniteTransition.animateFloat(
      initialValue = 0.97f,
      targetValue = 1.03f,
      animationSpec = infiniteRepeatable(
        animation = tween(2000, easing = FastOutSlowInEasing),
        repeatMode = RepeatMode.Reverse
      ),
      label = "Pulse"
    )

    val rotation by infiniteTransition.animateFloat(
      initialValue = 0f,
      targetValue = 360f,
      animationSpec = infiniteRepeatable(
        animation = tween(14000, easing = LinearEasing),
        repeatMode = RepeatMode.Restart
      ),
      label = "Orbit"
    )

    Box(
      modifier = modifier.size(size),
      contentAlignment = Alignment.Center
    ) {
      // Outer ambient dynamic aura & orbit
      Canvas(modifier = Modifier.size(size)) {
        val center = Offset(this.size.width / 2, this.size.height / 2)
        val radius = (this.size.minDimension / 2) - 6.dp.toPx()

        // Soft golden and emerald halo
        drawCircle(
          brush = Brush.radialGradient(
            colors = listOf(
              Color(0x330A7E6E),
              Color(0x22F5BD38),
              Color.Transparent
            ),
            center = center,
            radius = radius * 1.25f
          ),
          radius = radius * 1.25f,
          center = center
        )

        // Dynamic segmented arcs
        val arcSweep = 70f * assemblyProgress
        drawArc(
          brush = Brush.sweepGradient(
            colors = listOf(Color(0xFFF5BD38), Color(0xFFFBBF24), Color(0xFF0A7E6E))
          ),
          startAngle = rotation,
          sweepAngle = arcSweep,
          useCenter = false,
          style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        drawArc(
          brush = Brush.sweepGradient(
            colors = listOf(Color(0xFF0A7E6E), Color(0xFF10B981), Color(0xFFF5BD38))
          ),
          startAngle = rotation + 180f,
          sweepAngle = arcSweep,
          useCenter = false,
          style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
        )

        // Orbit dot
        val angleRad = Math.toRadians(rotation.toDouble())
        val dot = Offset(
          (center.x + radius * cos(angleRad)).toFloat(),
          (center.y + radius * sin(angleRad)).toFloat()
        )
        drawCircle(Color(0xFFF5BD38), radius = 3.5.dp.toPx() * assemblyProgress, center = dot)
      }

      // Center Official Logo
      val badgeSize = size * 0.76f
      val badgeRadius = badgeSize * 0.22f

      Box(
        modifier = Modifier
          .size(badgeSize)
          .scale(pulseScale)
          .clip(RoundedCornerShape(badgeRadius))
          .border(
            width = 2.dp,
            brush = Brush.linearGradient(
              listOf(Color(0xFFF5BD38), Color(0x660A7E6E))
            ),
            shape = RoundedCornerShape(badgeRadius)
          ),
        contentAlignment = Alignment.Center
      ) {
        Image(
          painter = painterResource(id = R.drawable.ic_goum_app_logo),
          contentDescription = "شعار 9GOUM",
          contentScale = ContentScale.Crop,
          modifier = Modifier.size(badgeSize)
        )
      }
    }
  }
}
