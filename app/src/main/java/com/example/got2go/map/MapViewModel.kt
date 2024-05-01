package com.example.got2go.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.got2go.data.Address
import com.example.got2go.data.Coordinates
import com.example.got2go.data.Restroom
import com.example.got2go.data.RestroomData
import com.example.got2go.data.RestroomsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState (
    val restrooms: Map<String, Restroom>
)

/* ViewModel for the map screen. */
class MapViewModel(
    private val restroomsRepository: RestroomsRepository,
    private val state: SavedStateHandle
) : ViewModel() {
    private val _restroomFlow = MutableStateFlow(UiState(emptyMap()))
    val restroomFlow: StateFlow<UiState> = _restroomFlow
    private val userCoordinates = MutableLiveData(Coordinates(0.0, 0.0))
    val coordinates: LiveData<Coordinates> get() = userCoordinates

    fun setCoordinates(latitude: Double, longitude: Double) {
        userCoordinates.value = Coordinates(latitude, longitude)
    }

    fun getNearbyRestrooms(radius: Double? = DEFAULT_RADIUS, limit: Int? = DEFAULT_LIMIT) {
        viewModelScope.launch {
            val restrooms = restroomsRepository
                .getNearbyRestrooms(userCoordinates.value!!, radius!!, limit!!)
            _restroomFlow.update { UiState(restrooms.associateBy { it.id!! }) }
        }
    }

    fun addRestroom(name: String, address: Address, coordinatesParam: Coordinates, status: String) {
//        viewModelScope.launch {
//            restroomsRepository.addRestroom(name, address, coordinatesParam, status)
//        }
        RestroomData.add(Restroom("9", name, address, coordinatesParam, status))
    }

    companion object {
        private const val DEFAULT_RADIUS = 1.0
        private const val DEFAULT_LIMIT = 10
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val restroomsRepository = RestroomsRepository()
                MapViewModel(restroomsRepository, savedStateHandle)
            }
        }
    }
}
