package com.example.projekakhir.be.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekakhir.be.data.model.ItemModel
import com.example.projekakhir.be.data.repository.MarketPlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MarketplaceUiState {
    object Loading : MarketplaceUiState()
    data class Success(val data: List<ItemModel>) : MarketplaceUiState()
    data class Error(val msg: String) : MarketplaceUiState()
}

class MarketPlaceViewModel(
    private val repository: MarketPlaceRepository = MarketPlaceRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<MarketplaceUiState>(MarketplaceUiState.Loading)
    val uiState: StateFlow<MarketplaceUiState> = _uiState

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _uiState.value = MarketplaceUiState.Loading
            val result = repository.getAllItems()
            if (result.isSuccess) {
                _uiState.value = MarketplaceUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = MarketplaceUiState.Error("Gagal memuat data")
            }
        }
    }
}