package com.rido.eventifycampus.ui.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rido.eventifycampus.model.Event
import com.rido.eventifycampus.util.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    event: Event,
    isFinished: Boolean = false,
    onBackClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showTicketDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Konfirmasi Selesai") },
            text = { Text("Apakah kamu sudah mengikuti acara ini? Event akan dipindahkan ke daftar Selesai.") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDialog = false
                    onCompleteClick()
                }) {
                    Text("Ya, Selesaikan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    if (showTicketDialog) {
        TicketDialog(
            event = event,
            onDismiss = { showTicketDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Event") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (!isFinished) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (event.isRegistered) {
                            OutlinedButton(
                                onClick = { showTicketDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Lihat Tiket")
                            }
                        }

                        Button(
                            onClick = {
                                if (event.isRegistered) {
                                    showConfirmDialog = true
                                } else {
                                    onRegisterClick()
                                }
                            },
                            modifier = Modifier
                                .weight(if (event.isRegistered) 1f else 2f)
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = if (event.isRegistered) 
                                ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                                else ButtonDefaults.buttonColors()
                        ) {
                            Text(if (event.isRegistered) "Selesaikan Event" else "Daftar Sekarang")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = event.imageRes),
                contentDescription = event.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                    Text(
                        text = event.category,
                        color = MaterialTheme.colorScheme.onTertiary,
                        modifier = Modifier.padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                InfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Waktu",
                    value = "${event.date} | ${event.time}"
                )

                InfoRow(
                    icon = Icons.Default.LocationOn,
                    label = "Lokasi",
                    value = event.location
                )

                InfoRow(
                    icon = Icons.Default.Person,
                    label = "Penyelenggara",
                    value = event.organizer
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Deskripsi Acara",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (event.isRegistered) {
                    Spacer(modifier = Modifier.height(32.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Kamu Sudah Terdaftar",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Tunjukkan QR Code saat datang ke lokasi acara.",
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            val qrBitmap = remember(event.id) {
                                QRCodeGenerator.generateQRCode("TICKET-${event.id}-${event.title}")
                            }
                            
                            qrBitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "QR Code Tiket",
                                    modifier = Modifier.size(150.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun TicketDialog(
    event: Event,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tiket Digital",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                val qrBitmap = remember(event.id) {
                    QRCodeGenerator.generateQRCode("TICKET-${event.id}-${event.title}")
                }
                
                qrBitmap?.let {
                    Surface(
                        modifier = Modifier.padding(8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "QR Code Tiket",
                            modifier = Modifier.size(200.dp).padding(12.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Text(
                    text = event.date,
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Tutup")
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
