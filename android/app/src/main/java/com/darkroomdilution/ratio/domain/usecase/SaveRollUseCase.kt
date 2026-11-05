package com.darkroomdilution.ratio.domain.usecase

import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository

class SaveRollUseCase(private val repository: DarkroomRepository) {

    operator fun invoke(entry: RollEntry) {
        repository.saveRoll(entry.copy(saved = true))
        repository.consumeDeveloperBatch()
    }
}
