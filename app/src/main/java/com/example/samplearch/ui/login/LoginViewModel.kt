package com.example.samplearch.ui.login

import android.util.Patterns
import androidx.lifecycle.*
import com.example.samplearch.data.LoginRepository
import com.example.samplearch.data.Result

import com.example.samplearch.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _uiState = MutableLiveData<LoginUiState>()
    val uiState: LiveData<LoginUiState> = _uiState

    fun login(username: String, password: String) {
        _uiState.toLoading()

        viewModelScope.launch {
            when (val result = loginRepository.login(username, password)) {
                is Result.Success -> _uiState.toSuccess(LoggedInUserView(displayName = result.data.displayName))
                is Result.Error -> _uiState.toError(result.exception)
            }
        }
    }

    // A placeholder username validation check
    fun isUserNameValid(username: String): Boolean {
        return if (username.contains('@')) {
            Patterns.EMAIL_ADDRESS.matcher(username).matches()
        } else {
            username.isNotBlank()
        }
    }

    // A placeholder password validation check
    fun isPasswordValid(password: String): Boolean {
        return password.length > 5
    }
}