package com.example.projekakhir.fe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projekakhir.be.data.model.ItemModel
import com.example.projekakhir.be.data.viewmodel.MarketPlaceViewModel
import com.example.projekakhir.be.data.viewmodel.MarketplaceUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarketplaceScreen(
    viewModel: MarketPlaceViewModel,
    onAddClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("ReStyle Market") }) },
        floatingActionButton = { FloatingActionButton(onClick = onAddClick) { Text("+") } }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            when (val state = uiState) {
                is MarketplaceUiState.Loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                is MarketplaceUiState.Error -> Text(state.msg, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                is MarketplaceUiState.Success -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.data) { item ->
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(item.namaBarang, style = MaterialTheme.typography.titleMedium)
                                Text("Rp ${item.harga}", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}
