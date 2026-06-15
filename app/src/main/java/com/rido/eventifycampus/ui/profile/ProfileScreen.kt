package com.rido.eventifycampus.ui.profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rido.eventifycampus.data.ProfileRepository
import com.rido.eventifycampus.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onLogoutClick: () -> Unit
) {
    val repository = remember { ProfileRepository() }
    val context = LocalContext.current
    
    var user by remember { mutableStateOf<User?>(null) }
    var isEditing by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Form states
    var editName by remember { mutableStateOf("") }
    var editNim by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isLoading = true
            repository.uploadProfileImage(it) { success, result ->
                isLoading = false
                if (success) {
                    user = user?.copy(profileImageUrl = result)
                    Toast.makeText(context, "Foto profil berhasil diganti!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, result ?: "Gagal mengunggah foto", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        repository.getUserProfile { fetchedUser ->
            user = fetchedUser
            editName = fetchedUser?.name ?: ""
            editNim = fetchedUser?.nim ?: ""
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profil Saya") },
                actions = {
                    if (isEditing) {
                        IconButton(onClick = { isEditing = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Batal")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tampilan Foto Profil
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                ) {
                    if (user?.profileImageUrl != null) {
                        AsyncImage(
                            model = user?.profileImageUrl,
                            contentDescription = "Foto Profil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Ikon Profil Default",
                            modifier = Modifier.size(60.dp).align(Alignment.Center),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                TextButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Ganti Foto Profil")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isEditing) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Nama Lengkap") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editNim,
                        onValueChange = { editNim = it },
                        label = { Text("NIM") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            val updatedUser = user?.copy(name = editName, nim = editNim)
                            if (updatedUser != null) {
                                isLoading = true
                                repository.updateUserProfile(updatedUser) { success, error ->
                                    isLoading = false
                                    if (success) {
                                        user = updatedUser
                                        isEditing = false
                                        Toast.makeText(context, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, error ?: "Gagal memperbarui profil", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Simpan Perubahan")
                    }
                } else {
                    Text(
                        text = user?.name ?: "Nama tidak tersedia",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = user?.email ?: "Email tidak tersedia",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (!user?.nim.isNullOrEmpty()) {
                        Surface(
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = "NIM: ${user?.nim}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    ProfileMenuItem(
                        icon = Icons.Default.Edit,
                        title = "Edit Nama & NIM",
                        onClick = { isEditing = true }
                    )
                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Keluar",
                        color = Color.Red,
                        onClick = onLogoutClick
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {}
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = color)
        }
    }
}
