package com.example.ui.window

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.HorizontalRule
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun WindowFrame(
  title: String,
  icon: ImageVector,
  isMaximized: Boolean,
  onMinimize: () -> Unit,
  onToggleMaximize: () -> Unit,
  onClose: () -> Unit,
  content: @Composable () -> Unit
) {
  // Modal window layer overlaying the background smoothly
  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(Color.Black.copy(alpha = 0.55f))
      .clickable(enabled = false) {}
      .statusBarsPadding()
      .navigationBarsPadding()
      .imePadding(),
    contentAlignment = Alignment.Center
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(if (isMaximized) 1f else 0.94f)
        .fillMaxHeight(if (isMaximized) 1f else 0.88f)
        .padding(if (isMaximized) 0.dp else 8.dp)
        .clip(RoundedCornerShape(if (isMaximized) 0.dp else 24.dp)),
      shape = RoundedCornerShape(if (isMaximized) 0.dp else 24.dp),
      colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.background
      ),
      elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
      Column(modifier = Modifier.fillMaxSize()) {
        // Window Title Bar
        Surface(
          color = MaterialTheme.colorScheme.surface,
          modifier = Modifier.fillMaxWidth(),
          shadowElevation = 2.dp
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            // Window Icon & Title
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(32.dp)
                  .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = icon,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
              Spacer(modifier = Modifier.width(10.dp))
              Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
            }

            // Window Control Buttons (Minimize, Maximize, Close)
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
              // Minimize button
              IconButton(
                onClick = onMinimize,
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Remove,
                  contentDescription = "Minimize",
                  modifier = Modifier.size(18.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Maximize / Restore button
              IconButton(
                onClick = onToggleMaximize,
                modifier = Modifier.size(36.dp)
              ) {
                Icon(
                  imageVector = if (isMaximized) Icons.Default.FullscreenExit else Icons.Default.Fullscreen,
                  contentDescription = if (isMaximized) "Restore" else "Maximize",
                  modifier = Modifier.size(18.dp),
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }

              // Close button
              IconButton(
                onClick = onClose,
                modifier = Modifier
                  .size(36.dp)
                  .background(MaterialTheme.colorScheme.error.copy(alpha = 0.12f), CircleShape)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Close Window",
                  tint = MaterialTheme.colorScheme.error,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }

        // Window Content Body
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {
          content()
        }
      }
    }
  }
}
