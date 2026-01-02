package com.alertemedicaments.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alertemedicaments.ui.theme.Teal600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToAuth: () -> Unit = {}
) {
    val context = LocalContext.current
    var pushNotifications by remember { mutableStateOf(true) }
    var emailAlerts by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFB))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0D9488),
                            Color(0xFF0891B2)
                        )
                    )
                )
                .padding(top = 48.dp, bottom = 32.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        Icons.Filled.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Mon Profil",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Gérez vos préférences",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Section
            SectionTitle("Compte")
            SettingsCard {
                SettingsItemRow(
                    icon = Icons.Outlined.PersonAdd,
                    iconColor = Teal600,
                    title = "Se connecter",
                    subtitle = "Synchronisez vos alertes",
                    onClick = { /* TODO */ },
                    trailing = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                )
            }

            // Notifications Section
            SectionTitle("Notifications")
            SettingsCard {
                Column {
                    SettingsToggleRow(
                        icon = Icons.Outlined.Notifications,
                        iconColor = Color(0xFF8B5CF6),
                        title = "Notifications push",
                        subtitle = "Alertes en temps réel",
                        checked = pushNotifications,
                        onCheckedChange = { pushNotifications = it }
                    )
                    Divider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE5E7EB)
                    )
                    SettingsToggleRow(
                        icon = Icons.Outlined.Email,
                        iconColor = Color(0xFF3B82F6),
                        title = "Alertes par email",
                        subtitle = "Résumé quotidien",
                        checked = emailAlerts,
                        onCheckedChange = { emailAlerts = it }
                    )
                }
            }

            // Family Section
            SectionTitle("Famille & Aidants")
            SettingsCard {
                Column {
                    SettingsItemRow(
                        icon = Icons.Outlined.FamilyRestroom,
                        iconColor = Color(0xFFEC4899),
                        title = "Mode Famille",
                        subtitle = "Gérez plusieurs profils",
                        onClick = { /* TODO */ },
                        trailing = {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Teal600.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    text = "Bientôt",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Teal600,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE5E7EB)
                    )
                    SettingsItemRow(
                        icon = Icons.Outlined.SupervisorAccount,
                        iconColor = Color(0xFFF59E0B),
                        title = "Partager avec un aidant",
                        subtitle = "Donnez accès à vos alertes",
                        onClick = { /* TODO */ },
                        trailing = {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    )
                }
            }

            // Info Section
            SectionTitle("Informations")
            SettingsCard {
                Column {
                    SettingsItemRow(
                        icon = Icons.Outlined.Info,
                        iconColor = Color(0xFF6B7280),
                        title = "À propos",
                        subtitle = "Version 1.0.0",
                        onClick = { /* TODO */ },
                        trailing = {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE5E7EB)
                    )
                    SettingsItemRow(
                        icon = Icons.Outlined.Policy,
                        iconColor = Color(0xFF6B7280),
                        title = "Politique de confidentialité",
                        subtitle = null,
                        onClick = { /* TODO */ },
                        trailing = {
                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }
                    )
                    Divider(
                        modifier = Modifier.padding(start = 56.dp),
                        color = Color(0xFFE5E7EB)
                    )
                    SettingsItemRow(
                        icon = Icons.Outlined.OpenInNew,
                        iconColor = Color(0xFF0D9488),
                        title = "Site de l'ANSM",
                        subtitle = "ansm.sante.fr",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://ansm.sante.fr"))
                            context.startActivity(intent)
                        },
                        trailing = {
                            Icon(
                                Icons.Default.OpenInNew,
                                contentDescription = null,
                                tint = Teal600,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    )
                }
            }

            // Data source
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Teal600.copy(alpha = 0.08f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.Verified,
                        contentDescription = null,
                        tint = Teal600,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "Données officielles",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "Issues de l'ANSM et de la base de données publique des médicaments",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = Color.Gray,
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        content()
    }
}

@Composable
fun SettingsItemRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(8.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        trailing()
    }
}

@Composable
fun SettingsToggleRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String?,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = RoundedCornerShape(10.dp),
            color = iconColor.copy(alpha = 0.1f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.padding(8.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            subtitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Teal600,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE5E7EB)
            )
        )
    }
}
