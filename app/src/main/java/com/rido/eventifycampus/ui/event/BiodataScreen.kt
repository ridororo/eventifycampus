package com.rido.eventifycampus.ui.event

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BiodataScreen(
    eventTitle: String,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit
) {
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var noHp by remember { mutableStateOf("") }

    val isFormValid = nama.isNotBlank() &&
            nim.isNotBlank() &&
            prodi.isNotBlank() &&
            noHp.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lengkapi Biodata") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Pendaftaran Acara: $eventTitle",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text("Nama Lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = nim,
                onValueChange = { nim = it },
                label = { Text("NIM") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = prodi,
                onValueChange = { prodi = it },
                label = { Text("Program Studi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = noHp,
                onValueChange = { noHp = it },
                label = { Text("Nomor HP") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSubmitClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = isFormValid
            ) {
                Text("Kirim Pendaftaran")
            }
        }
    }
}