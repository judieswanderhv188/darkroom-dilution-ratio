package com.darkroomdilution.ratio.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.data.local.FeedbackPlayer
import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.ProcessStage
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import com.darkroomdilution.ratio.domain.repository.SettingsRepository
import com.darkroomdilution.ratio.domain.usecase.BuildProcessUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: DarkroomRepository,
    private val settingsRepository: SettingsRepository,
    private val feedback: FeedbackPlayer
) : ViewModel() {

    private val builder = BuildProcessUseCase()

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val finishedChannel = Channel<String>(Channel.BUFFERED)
    val finishedEvents: Flow<String> = finishedChannel.receiveAsFlow()

    private var ticker: Job? = null
    private var tenths = 0
    private var flashTicks = 0
    private var soundEnabled = true
    private var leadSeconds = 3

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                soundEnabled = settings.agitationSound
                leadSeconds = settings.agitationLeadSeconds
                _state.update {
                    it.copy(
                        celsius = settings.celsius,
                        keepScreenOn = settings.keepScreenOn,
                        haptics = settings.hapticPrompts
                    )
                }
            }
        }
    }

    fun arm() {
        if (_state.value.phase == TimerPhase.RUNNING || _state.value.phase == TimerPhase.PAUSED) return
        val recipe = repository.recipe.value
        val stages = builder(recipe.correctedSeconds, recipe.stages).filter { it.enabled }
        val first = stages.firstOrNull()
        _state.value = GameUiState(
            stages = stages,
            stageIndex = 0,
            stageDuration = first?.durationSeconds ?: 0,
            remainingSeconds = first?.durationSeconds ?: 0,
            elapsedSeconds = 0,
            phase = TimerPhase.IDLE,
            secondsToAgitation = initialAgitation(first),
            tempC = recipe.tempC,
            celsius = _state.value.celsius,
            dilution = recipe.dilutionInput,
            pushPull = recipe.pushPull,
            keepScreenOn = _state.value.keepScreenOn,
            haptics = _state.value.haptics
        )
    }

    fun toggleRun() {
        when (_state.value.phase) {
            TimerPhase.RUNNING -> pause()
            else -> start()
        }
    }

    fun start() {
        val current = _state.value
        if (current.stages.isEmpty() || current.phase == TimerPhase.FINISHED) return
        _state.update { it.copy(phase = TimerPhase.RUNNING) }
        if (ticker?.isActive == true) return
        ticker = viewModelScope.launch {
            while (isActive) {
                delay(GameConfig.TICK_MS)
                onTick()
            }
        }
    }

    fun pause() {
        ticker?.cancel()
        ticker = null
        _state.update {
            if (it.phase == TimerPhase.RUNNING) it.copy(phase = TimerPhase.PAUSED) else it
        }
    }

    fun skipStage() {
        if (_state.value.phase == TimerPhase.FINISHED) return
        advanceStage(_state.value.elapsedSeconds + _state.value.remainingSeconds)
    }

    fun requestAbort() {
        pause()
        _state.update { it.copy(showAbort = true) }
    }

    fun dismissAbort() {
        _state.update { it.copy(showAbort = false) }
    }

    private fun onTick() {
        val current = _state.value
        if (current.phase != TimerPhase.RUNNING) return
        if (flashTicks > 0) {
            flashTicks -= 1
            if (flashTicks == 0) {
                _state.update { it.copy(stageFlash = false) }
            }
        }
        tenths += 1
        if (tenths < 10) return
        tenths = 0

        val remaining = current.remainingSeconds - 1
        val elapsed = current.elapsedSeconds + 1
        if (remaining <= 0) {
            advanceStage(elapsed)
            return
        }
        val stage = current.stages.getOrNull(current.stageIndex)
        val interval = stage?.agitation?.intervalSeconds ?: -1
        val inStage = current.stageDuration - remaining
        var agitating = false
        var countdown = -1
        if (stage != null && stage.agitation == AgitationMode.CONTINUOUS) {
            agitating = true
        } else if (interval > 0) {
            val position = inStage % interval
            agitating = position < GameConfig.AGITATION_WINDOW_SECONDS
            countdown = (interval - position) % interval
            if (position == 0 || countdown == leadSeconds) {
                emitFeedback()
            }
        }
        _state.update {
            it.copy(
                remainingSeconds = remaining,
                elapsedSeconds = elapsed,
                agitating = agitating,
                secondsToAgitation = countdown,
                agitationPulse = if (agitating && !it.agitating) it.agitationPulse + 1 else it.agitationPulse
            )
        }
    }

    private fun advanceStage(elapsed: Int) {
        val current = _state.value
        val nextIndex = current.stageIndex + 1
        emitFeedback()
        if (nextIndex >= current.stages.size) {
            ticker?.cancel()
            ticker = null
            _state.update {
                it.copy(
                    phase = TimerPhase.FINISHED,
                    remainingSeconds = 0,
                    elapsedSeconds = elapsed,
                    agitating = false,
                    stageFlash = false,
                    secondsToAgitation = -1
                )
            }
            val id = repository.createDraftRoll(elapsed)
            finishedChannel.trySend(id)
            return
        }
        val next = current.stages[nextIndex]
        flashTicks = (GameConfig.STAGE_FLASH_MS / GameConfig.TICK_MS).toInt()
        tenths = 0
        _state.update {
            it.copy(
                stageIndex = nextIndex,
                stageDuration = next.durationSeconds,
                remainingSeconds = next.durationSeconds,
                elapsedSeconds = elapsed,
                agitating = false,
                stageFlash = it.phase == TimerPhase.RUNNING,
                secondsToAgitation = initialAgitation(next)
            )
        }
    }

    private fun initialAgitation(stage: ProcessStage?): Int {
        val interval = stage?.agitation?.intervalSeconds ?: -1
        return if (interval > 0) interval else -1
    }

    private fun emitFeedback() {
        if (soundEnabled) feedback.beep(180)
    }

    override fun onCleared() {
        ticker?.cancel()
        ticker = null
        feedback.release()
        super.onCleared()
    }
}
