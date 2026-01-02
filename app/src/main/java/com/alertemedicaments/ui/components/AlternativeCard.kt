package com.alertemedicaments.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alertemedicaments.data.models.Alternative
import com.alertemedicaments.ui.theme.*

@Composable
fun AlternativeCard(
    alternative: Alternative,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAvailable = alternative.status == "AVAILABLE"
    val borderColor = if (isAvailable) StatusGreen.copy(alpha = 0.3f) else MaterialTheme.colorScheme.outline

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = alternative.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 2
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Match type badge
                    val matchTypeLabel = when (alternative.matchType) {
                        "generic" -> "Générique"
                        "sameIngredient" -> "Même principe actif"
                        "similar" -> "Similaire"
                        else -> alternative.matchType
                    }
                    val matchTypeColor = when (alternative.matchType) {
                        "generic" -> Color(0xFF3B82F6)
                        "sameIngredient" -> Color(0xFF8B5CF6)
                        else -> Color.Gray
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = matchTypeColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = matchTypeLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = matchTypeColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                alternative.laboratory?.let {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Default.Business,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status badge
                val statusLabel = when (alternative.status) {
                    "AVAILABLE" -> "Disponible"
                    "TENSION" -> "Tension"
                    "RUPTURE" -> "Rupture"
                    else -> "Inconnu"
                }
                val statusColor = when (alternative.status) {
                    "AVAILABLE" -> StatusGreen
                    "TENSION" -> StatusOrange
                    "RUPTURE" -> StatusRed
                    else -> StatusGray
                }
                val statusIcon = when (alternative.status) {
                    "AVAILABLE" -> Icons.Default.CheckCircle
                    "TENSION" -> Icons.Default.Schedule
                    "RUPTURE" -> Icons.Default.Warning
                    else -> Icons.Default.Help
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = statusLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
