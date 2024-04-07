package com.example.got2go.map

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.got2go.data.Coordinates
import com.example.got2go.data.FirebaseRestroomsRepository
import com.example.got2go.data.Restroom
import com.example.got2go.data.RestroomsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

sealed interface UiState {
    data object Loading : UiState
    data class Success(
        val restrooms: List<Restroom>
    ) : UiState

    data class Error(
        val throwable: Throwable?
    ) : UiState
}


private const val RETRY_TIME_IN_MILLIS = 15_000L
private const val RETRY_ATTEMPT_COUNT = 3

sealed interface Result<out T> {
    data class Success<T>(val data: T) : Result<T>
    data class Error(val exception: Throwable? = null) : Result<Nothing>
    data object Loading : Result<Nothing>
}

fun <T> Flow<T>.asResult(): Flow<Result<T>> {
    return this
        .map<T, Result<T>> {
            Result.Success(it)
        }
        .onStart { emit(Result.Loading) }
        .retryWhen { cause, attempt ->
            if (cause is IOException && attempt < RETRY_ATTEMPT_COUNT) {
                delay(RETRY_TIME_IN_MILLIS)
                true
            } else {
                false
            }
        }
        .catch { emit(Result.Error(it)) }
}

/**
 * ViewModel for the map screen.
 */
class MapViewModel(
    private val restroomsRepository: RestroomsRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _restroomFlow = MutableStateFlow<UiState>(UiState.Loading)
    val restroomFlow: StateFlow<UiState> = _restroomFlow.asStateFlow()

    fun getNearbyRestrooms(coordinates: Coordinates, radius: Double, limit: Int) {
        viewModelScope.launch {
            restroomsRepository
                .getNearbyRestrooms(coordinates, radius, limit).asResult()
                .collect { result ->
                    _restroomFlow.update {
                        when (result) {
                            is Result.Loading -> UiState.Loading
                            is Result.Success -> UiState.Success(result.data)
                            is Result.Error -> UiState.Error(result.exception)
                        }
                    }
                }
        }
    }

    companion object {
        private const val ECS_LATITUDE = 33.6405
        private const val ECS_LONGITUDE = -117.8443
        private const val DEFAULT_RADIUS = 0.01
        private const val DEFAULT_LIMIT = 10
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val savedStateHandle = createSavedStateHandle()
                val restroomsRepository = FirebaseRestroomsRepository()
                MapViewModel(restroomsRepository, savedStateHandle)
            }
        }
    }
}
