package com.example.projekakhir.be.data.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projekakhir.be.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object Success : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    fun handleGoogleLogin(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.loginWithGoogle(idToken)
            _authState.value = if (result.isSuccess) AuthState.Success
            else AuthState.Error("Login Gagal")
        }
    }

    fun checkLoginStatus(): Boolean = repository.isUserLoggedIn()
}
