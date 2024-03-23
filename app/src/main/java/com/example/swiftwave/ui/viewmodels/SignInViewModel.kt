package com.example.swiftwave.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swiftwave.auth.SignInResult
import com.example.swiftwave.auth.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel: ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        viewModelScope.launch (Dispatchers.IO){
            _state.update { it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            ) }
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}