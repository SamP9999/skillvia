package com.skillvia.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

object SkillviaCardDefaults {
    val Shape = RoundedCornerShape(16.dp)

    @Composable
    fun elevation() = CardDefaults.cardElevation(
        defaultElevation = 2.dp,
        pressedElevation = 4.dp,
        focusedElevation = 4.dp,
        hoveredElevation = 3.dp,
        draggedElevation = 4.dp,
        disabledElevation = 0.dp
    )

    @Composable
    fun colors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
}

