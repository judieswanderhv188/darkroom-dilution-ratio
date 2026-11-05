package com.darkroomdilution.ratio.presentation.pushpull

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.usecase.ApplyPushPullUseCase
import com.darkroomdilution.ratio.domain.usecase.CompensateTemperatureUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PushPullViewModel(
    private val repository: DarkroomRepository,
    private val compensate: CompensateTemperatureUseCase,
    private val pushPull: ApplyPushPullUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PushPullUiState())
    val state: StateFlow<PushPullUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.recipe.collect { recipe ->
                val notebook = repository.notebook.value
                    .firstOrNull { it.id == recipe.selectedNotebookId }
                val iso = notebook?.iso ?: 400
                val corrected = compensate(recipe.baseSeconds, recipe.tempC).correctedSeconds
                _state.update { current ->
                    val stops = if (current.stops == 0) recipe.pushPull else current.stops
                    current.copy(
                        stops = stops,
                        iso = iso,
                        tempCorrectedSeconds = corrected,
                        resultSeconds = pushPull(corrected, stops),
                        effectiveIso = pushPull.effectiveIso(iso, stops)
                    )
                }
            }
        }
    }

    fun select(stops: Int) {
        _state.update {
            it.copy(
                stops = stops,
                resultSeconds = pushPull(it.tempCorrectedSeconds, stops),
                effectiveIso = pushPull.effectiveIso(it.iso, stops)
            )
        }
    }

    fun apply() {
        val current = _state.value
        repository.updateRecipe {
            it.copy(pushPull = current.stops, correctedSeconds = current.resultSeconds)
        }
    }
}
