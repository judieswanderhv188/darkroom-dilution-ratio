package com.darkroomdilution.ratio.presentation.chemistryshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.data.sample.SampleData
import com.darkroomdilution.ratio.domain.model.ChemistryBatch
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChemistryShelfUiState(
    val batches: List<ChemistryBatch> = emptyList(),
    val now: Long = System.currentTimeMillis(),
    val showEditor: Boolean = false,
    val draftIndex: Int = 0,
    val draftVolume: Int = 1000,
    val draftCapacity: Int = 16,
    val draftShelfLife: Int = 90,
    val names: List<String> = SampleData.developers,
    val pendingDeleteId: String = ""
)

class ChemistryShelfViewModel(private val repository: DarkroomRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChemistryShelfUiState())
    val state: StateFlow<ChemistryShelfUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.batches.collect { batches ->
                _state.update { it.copy(batches = batches, now = System.currentTimeMillis()) }
            }
        }
    }

    fun consume(id: String) {
        repository.consumeBatch(id, 1)
    }

    fun openEditor() {
        _state.update { it.copy(showEditor = true) }
    }

    fun closeEditor() {
        _state.update { it.copy(showEditor = false) }
    }

    fun setName(index: Int) {
        _state.update { it.copy(draftIndex = index) }
    }

    fun stepVolume(delta: Int) {
        _state.update {
            it.copy(
                draftVolume = (it.draftVolume + delta)
                    .coerceIn(GameConfig.MIN_TANK_VOLUME_ML, GameConfig.MAX_TANK_VOLUME_ML)
            )
        }
    }

    fun stepCapacity(delta: Int) {
        _state.update { it.copy(draftCapacity = (it.draftCapacity + delta).coerceIn(1, 60)) }
    }

    fun stepShelfLife(delta: Int) {
        _state.update { it.copy(draftShelfLife = (it.draftShelfLife + delta).coerceIn(7, 365)) }
    }

    fun confirmEditor() {
        val current = _state.value
        val name = current.names.getOrNull(current.draftIndex) ?: return
        repository.addBatch(
            ChemistryBatch(
                id = "batch_" + System.currentTimeMillis(),
                name = name,
                dilution = repository.recipe.value.dilutionInput,
                mixedAt = System.currentTimeMillis(),
                volumeMl = current.draftVolume,
                rollsUsed = 0,
                rollCapacity = current.draftCapacity,
                shelfLifeDays = current.draftShelfLife
            )
        )
        _state.update { it.copy(showEditor = false) }
    }

    fun requestDelete(id: String) {
        _state.update { it.copy(pendingDeleteId = id) }
    }

    fun dismissDelete() {
        _state.update { it.copy(pendingDeleteId = "") }
    }

    fun confirmDelete() {
        val id = _state.value.pendingDeleteId
        if (id.isNotEmpty()) repository.deleteBatch(id)
        _state.update { it.copy(pendingDeleteId = "") }
    }
}
