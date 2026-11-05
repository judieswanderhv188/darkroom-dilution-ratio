package com.darkroomdilution.ratio.presentation.processbuilder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.ProcessStage
import com.darkroomdilution.ratio.domain.model.StageKind
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.usecase.BuildProcessUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProcessBuilderViewModel(
    private val repository: DarkroomRepository,
    private val buildProcess: BuildProcessUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProcessBuilderUiState())
    val state: StateFlow<ProcessBuilderUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.recipe.collect { recipe ->
                _state.update { current ->
                    when {
                        current.stages.isEmpty() -> current.copy(
                            stages = buildProcess(recipe.correctedSeconds, recipe.stages),
                            correctedSeconds = recipe.correctedSeconds
                        )
                        current.correctedSeconds != recipe.correctedSeconds -> current.copy(
                            stages = buildProcess(recipe.correctedSeconds, current.stages),
                            correctedSeconds = recipe.correctedSeconds
                        )
                        else -> current
                    }
                }
            }
        }
    }

    fun stepDuration(index: Int, delta: Int) {
        mutate { stages ->
            stages.mapIndexed { position, stage ->
                if (position == index) {
                    stage.copy(
                        durationSeconds = (stage.durationSeconds + delta)
                            .coerceIn(GameConfig.STAGE_STEP_SECONDS, GameConfig.MAX_CORRECTED_SECONDS)
                    )
                } else {
                    stage
                }
            }
        }
    }

    fun setAgitation(index: Int, mode: AgitationMode) {
        mutate { stages ->
            stages.mapIndexed { position, stage ->
                if (position == index) stage.copy(agitation = mode) else stage
            }
        }
    }

    fun toggle(index: Int, enabled: Boolean) {
        mutate { stages ->
            stages.mapIndexed { position, stage ->
                if (position == index) stage.copy(enabled = enabled) else stage
            }
        }
    }

    fun move(index: Int, delta: Int) {
        val target = index + delta
        val stages = _state.value.stages
        if (index !in stages.indices || target !in stages.indices) return
        mutate { current ->
            val mutable = current.toMutableList()
            val moved = mutable.removeAt(index)
            mutable.add(target, moved)
            mutable.toList()
        }
    }

    fun addPresoak() {
        if (_state.value.stages.any { it.kind == StageKind.PRESOAK }) return
        mutate { stages ->
            listOf(ProcessStage(StageKind.PRESOAK, 60, AgitationMode.CONTINUOUS, true)) + stages
        }
    }

    fun apply() {
        repository.updateRecipe { it.copy(stages = _state.value.stages) }
    }

    private fun mutate(transform: (List<ProcessStage>) -> List<ProcessStage>) {
        _state.update { it.copy(stages = transform(it.stages)) }
        repository.updateRecipe { it.copy(stages = _state.value.stages) }
    }
}
