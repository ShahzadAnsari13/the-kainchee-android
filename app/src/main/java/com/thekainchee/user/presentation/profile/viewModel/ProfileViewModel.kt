package com.thekainchee.user.presentation.profile.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thekainchee.user.domain.repository.ProfileRepository
import com.thekainchee.user.presentation.profile.state.EditProfileEvent
import com.thekainchee.user.presentation.profile.state.ProfileState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class ProfileViewModel @Inject constructor(private val repository: ProfileRepository) : ViewModel() {
    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState : StateFlow<ProfileState> = _profileState
    private val _event = MutableSharedFlow<EditProfileEvent>()
    val event = _event.asSharedFlow()
    private var profileJob: Job? = null
    fun getProfile(){
        _profileState.value = ProfileState.Loading
        profileJob?.cancel()
        profileJob = viewModelScope.launch {
            val result = repository.getProfile()
            result.onSuccess { data ->
                _profileState.value = ProfileState.Success(data)
            }.onFailure { error ->
                if (error is CancellationException) return@onFailure

                _profileState.value =
                    ProfileState.Error(error.message ?: "Failed to load profile")
            }
        }

    }
    fun updateProfile(name : String){
        viewModelScope.launch {
            val result = repository.updateProfile(name)
            result.onSuccess { message ->
                _event.emit(EditProfileEvent.Success(name))
            }.onFailure { error ->
                _event.emit(EditProfileEvent.Error(error.message ?: "Failed to update profile"))
            }
        }
    }
    fun updateFcmToken(token: String) {
        viewModelScope.launch {
            repository.updateFcmToken(token)
                .onFailure {
                    Log.e("FCM", "Failed to update FCM token", it)
                }
        }
    }
}