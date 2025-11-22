package com.example.projekakhir.fe

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.projekakhir.be.data.model.ItemModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: ItemModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Item") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Image
            if (item.imageUrl.isNotEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(item.imageUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // Details
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    item.namaBarang,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                if (item.jenis == "resell") {
                    Text(
                        "Rp ${item.harga.toLong()}",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Divider()

                DetailRow("Jenis", item.jenis.uppercase())
                DetailRow("Kategori", item.kategori)

                if (item.jenis == "resell") {
                    DetailRow("Kondisi", item.kondisi)
                    DetailRow("Ukuran", item.ukuran)
                }

                if (item.jenis != "resell") {
                    DetailRow("Status", item.status)
                    DetailRow("Lokasi Pickup", item.lokasiPickup)
                }

                if (item.deskripsi.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Text("Deskripsi", fontWeight = FontWeight.Bold)
                    Text(item.deskripsi)
                }

                Spacer(Modifier.height(16.dp))

                if (item.jenis == "resell") {
                    Button(
                        onClick = { /* TODO: Implement buy */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Beli Sekarang")
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.outline)
        Text(value, fontWeight = FontWeight.Medium)
    }
}