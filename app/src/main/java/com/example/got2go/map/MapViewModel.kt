package com.example.got2go.map

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.got2go.restroom.Coordinates
import com.example.got2go.restroom.Restroom
import com.example.got2go.restroom.RestroomsRepository
import com.example.got2go.restroom.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * UI state for the map screen.
 */
data class MapUIState(
    val userLocation: Coordinates?,
    val restrooms: Map<String, Restroom>,
    val isLoading: Boolean = false,
    val selectedRestroom: Restroom?,
    val limit: Double,
    val radius: Double,
)

/**
 * ViewModel for the map screen.
 */
class MapViewModel(
    private val restroomsRepository: RestroomsRepository,
    private val userRepository: UserRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _userLocation = MutableStateFlow<Coordinates?>(null)
    private val _restrooms = MutableStateFlow<Map<String, Restroom>>(emptyMap())
    fun fetchRestrooms() {
        viewModelScope.launch {
            _isLoading.value = true
            val userLocation = userRepository.getLocation()
            _userLocation.value = userLocation
            restroomsRepository.getNearbyRestrooms(userLocation, 0.0, 10).collect {
                _restrooms.value = it.associateBy { it.id }
                _isLoading.value = false
            }
        }
    }

}
