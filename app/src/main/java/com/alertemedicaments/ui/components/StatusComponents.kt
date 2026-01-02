package com.alertemedicaments.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alertemedicaments.data.models.MedicationStatus
import com.alertemedicaments.ui.theme.*

@Composable
fun StatusIcon(
    status: MedicationStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        MedicationStatus.AVAILABLE -> StatusGreen
        MedicationStatus.TENSION -> StatusOrange
        MedicationStatus.RUPTURE -> StatusRed
        MedicationStatus.UNKNOWN -> StatusGray
    }

    val icon = when (status) {
        MedicationStatus.AVAILABLE -> Icons.Default.CheckCircle
        MedicationStatus.TENSION -> Icons.Default.Schedule
        MedicationStatus.RUPTURE -> Icons.Default.Warning
        MedicationStatus.UNKNOWN -> Icons.Default.Help
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = status.displayName,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun StatusBadge(
    status: MedicationStatus,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (status) {
        MedicationStatus.AVAILABLE -> StatusGreen.copy(alpha = 0.15f)
        MedicationStatus.TENSION -> StatusOrange.copy(alpha = 0.15f)
        MedicationStatus.RUPTURE -> StatusRed.copy(alpha = 0.15f)
        MedicationStatus.UNKNOWN -> StatusGray.copy(alpha = 0.15f)
    }

    val textColor = when (status) {
        MedicationStatus.AVAILABLE -> StatusGreen
        MedicationStatus.TENSION -> StatusOrange
        MedicationStatus.RUPTURE -> StatusRed
        MedicationStatus.UNKNOWN -> StatusGray
    }

    val icon = when (status) {
        MedicationStatus.AVAILABLE -> Icons.Default.CheckCircle
        MedicationStatus.TENSION -> Icons.Default.Schedule
        MedicationStatus.RUPTURE -> Icons.Default.Warning
        MedicationStatus.UNKNOWN -> Icons.Default.Help
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }
}
