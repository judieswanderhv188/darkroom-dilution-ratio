package com.darkroomdilution.ratio.domain.repository

import com.darkroomdilution.ratio.domain.model.ChemistryBatch
import com.darkroomdilution.ratio.domain.model.NotebookEntry
import com.darkroomdilution.ratio.domain.model.Recipe
import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.model.TankPreset
import kotlinx.coroutines.flow.StateFlow

interface DarkroomRepository {
    val rolls: StateFlow<List<RollEntry>>
    val presets: StateFlow<List<TankPreset>>
    val notebook: StateFlow<List<NotebookEntry>>
    val batches: StateFlow<List<ChemistryBatch>>
    val recipe: StateFlow<Recipe>

    fun updateRecipe(transform: (Recipe) -> Recipe)

    fun addPreset(preset: TankPreset)
    fun deletePreset(id: String)
    fun selectPreset(id: String)

    fun addNotebookEntry(entry: NotebookEntry)
    fun deleteNotebookEntry(id: String)
    fun selectNotebookEntry(id: String)

    fun addBatch(batch: ChemistryBatch)
    fun deleteBatch(id: String)
    fun consumeBatch(id: String, rolls: Int)
    fun consumeDeveloperBatch()

    fun createDraftRoll(elapsedSeconds: Int): String
    fun roll(id: String): RollEntry?
    fun saveRoll(entry: RollEntry)
    fun resetAll()
}
