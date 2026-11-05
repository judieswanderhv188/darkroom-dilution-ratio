package com.darkroomdilution.ratio.presentation.safetytips

import androidx.lifecycle.ViewModel
import com.darkroomdilution.ratio.domain.model.SafetyTopic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SafetyTipsUiState(
    val topics: List<SafetyTopic> = SafetyTopic.entries.toList()
)

class SafetyTipsViewModel : ViewModel() {

    private val _state = MutableStateFlow(SafetyTipsUiState())
    val state: StateFlow<SafetyTipsUiState> = _state.asStateFlow()
}
