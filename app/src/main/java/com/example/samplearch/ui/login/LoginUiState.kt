package com.example.samplearch.ui.login

import androidx.lifecycle.MutableLiveData
import com.example.samplearch.data.model.LoggedInUser

sealed class LoginUiState {
    object Loading: LoginUiState()
    data class Success(val loggedInUser: LoggedInUserView): LoginUiState()
    data class Error(val error: Throwable): LoginUiState()
}


fun MutableLiveData<LoginUiState>.toLoading() {
    value = LoginUiState.Loading
}

fun MutableLiveData<LoginUiState>.toError(error: Throwable) {
    value = LoginUiState.Error(error)
}

fun MutableLiveData<LoginUiState>.toSuccess(loggedInUserView: LoggedInUserView) {
    value = LoginUiState.Success(loggedInUserView)
}
