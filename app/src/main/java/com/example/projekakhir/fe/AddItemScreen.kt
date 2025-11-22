package com.example.projekakhir.fe

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.projekakhir.be.data.viewmodel.AddItemViewModel
import com.example.projekakhir.be.data.viewmodel.UploadState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    viewModel: AddItemViewModel = viewModel(),
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val context = LocalContext.current

    var selectedJenis by remember { mutableStateOf<String?>(null) }
    var nama by remember { mutableStateOf("") }
    var harga by remember { mutableStateOf("") }
    var kategori by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var ukuran by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var lokasi by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var latitude by remember { mutableStateOf<Double?>(null) }
    var longitude by remember { mutableStateOf<Double?>(null) }

    val uploadState by viewModel.uploadState.collectAsState()

    // Image picker
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Camera launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (!success) imageUri = null
    }

    // Location permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getLocation(context) { lat, lon ->
                latitude = lat
                longitude = lon
                lokasi = "Lat: $lat, Lon: $lon"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tambah Item") },
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
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // PILIH JENIS: RESELL, DONATE, RECYCLE
            Text("Pilih Aksi:", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedJenis == "resell",
                    onClick = { selectedJenis = "resell" },
                    label = { Text("Resell") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedJenis == "donate",
                    onClick = { selectedJenis = "donate" },
                    label = { Text("Donate") },
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedJenis == "recycle",
                    onClick = { selectedJenis = "recycle" },
                    label = { Text("Recycle") },
                    modifier = Modifier.weight(1f)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            // UPLOAD GAMBAR
            Text("Foto Pakaian:", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Pilih dari Galeri")
                }
                Button(
                    onClick = {
                        val uri = createImageUri(context)
                        imageUri = uri
                        takePictureLauncher.launch(uri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ambil Foto")
                }
            }

            imageUri?.let { uri ->
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            // FORM UNTUK RESELL
            if (selectedJenis == "resell") {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Pakaian") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kondisi,
                    onValueChange = { kondisi = it },
                    label = { Text("Kondisi (Baru/Bekas/Layak)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ukuran,
                    onValueChange = { ukuran = it },
                    label = { Text("Ukuran (S/M/L/XL)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = harga,
                    onValueChange = { harga = it },
                    label = { Text("Harga (Rp)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kategori,
                    onValueChange = { kategori = it },
                    label = { Text("Kategori (Kemeja/Celana/Jaket)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            // FORM UNTUK DONATE & RECYCLE
            if (selectedJenis == "donate" || selectedJenis == "recycle") {
                OutlinedTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text("Nama Pakaian (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = kategori,
                    onValueChange = { kategori = it },
                    label = { Text("Kategori (opsional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text("Lokasi Penjemputan:", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            getLocation(context) { lat, lon ->
                                latitude = lat
                                longitude = lon
                                lokasi = "Lokasi GPS: Lat $lat, Lon $lon"
                            }
                        } else {
                            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ambil Lokasi GPS")
                }

                if (lokasi.isNotEmpty()) {
                    Text(lokasi, style = MaterialTheme.typography.bodySmall)
                }

                OutlinedTextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text("Catatan tambahan (opsional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }

            Spacer(Modifier.height(16.dp))

            // TOMBOL SUBMIT
            Button(
                onClick = {
                    val currentJenis = selectedJenis
                    val currentImageUri = imageUri

                    if (currentJenis != null && currentImageUri != null) {
                        viewModel.uploadItem(
                            nama = nama.ifEmpty { "Pakaian Bekas" },
                            harga = if (currentJenis == "resell") harga.toDoubleOrNull() ?: 0.0 else 0.0,
                            kategori = kategori.ifEmpty { "Umum" },
                            deskripsi = deskripsi,
                            lokasi = lokasi,
                            jenis = currentJenis,
                            kondisi = kondisi,
                            ukuran = ukuran,
                            lat = latitude,
                            lon = longitude,
                            imageUri = currentImageUri
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedJenis != null && imageUri != null
            ) {
                Text(
                    when (selectedJenis) {
                        "resell" -> "Upload ke Marketplace"
                        "donate" -> "Request Pickup untuk Donasi"
                        "recycle" -> "Request Pickup untuk Daur Ulang"
                        else -> "Pilih Jenis Terlebih Dahulu"
                    }
                )
            }

            // STATUS UPLOAD
            when (uploadState) {
                is UploadState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UploadState.Success -> {
                    Text(
                        "Berhasil! ${
                            when (selectedJenis) {
                                "resell" -> "Item tersedia di Marketplace"
                                "donate" -> "Pickup akan dijadwalkan"
                                "recycle" -> "Pickup akan dijadwalkan"
                                else -> ""
                            }
                        }",
                        color = MaterialTheme.colorScheme.primary
                    )
                    LaunchedEffect(Unit) {
                        kotlinx.coroutines.delay(1500)
                        onSuccess()
                    }
                }
                is UploadState.Error -> {
                    Text(
                        (uploadState as UploadState.Error).msg,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {}
            }
        }
    }
}

// Helper: Create URI for camera
fun createImageUri(context: Context): Uri {
    val contentResolver = context.contentResolver
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "restyle_${System.currentTimeMillis()}.jpg")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
    }
    return contentResolver.insert(
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ) ?: Uri.EMPTY
}

// Helper: Get GPS Location
fun getLocation(context: Context, onResult: (Double, Double) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    try {
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    onResult(location.latitude, location.longitude)
                }
            }
    } catch (e: SecurityException) {
        // Handle exception
    }
}