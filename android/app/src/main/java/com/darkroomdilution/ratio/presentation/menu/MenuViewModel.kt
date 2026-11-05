package com.darkroomdilution.ratio.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MenuViewModel(repository: DarkroomRepository) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.rolls,
                repository.batches,
                repository.recipe
            ) { rolls, batches, recipe ->
                Triple(
                    rolls.count { it.saved },
                    batches.count { it.rollsUsed < it.rollCapacity },
                    recipe.correctedSeconds
                )
            }.collect { triple ->
                _state.update {
                    it.copy(
                        rollCount = triple.first,
                        openBatches = triple.second,
                        lastDevSeconds = triple.third
                    )
                }
            }
        }
    }
}
