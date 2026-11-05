package com.darkroomdilution.ratio.data.repository

import com.darkroomdilution.ratio.core.config.GameConfig
import com.darkroomdilution.ratio.data.local.DarkroomPreferences
import com.darkroomdilution.ratio.data.sample.SampleData
import com.darkroomdilution.ratio.domain.model.ChemistryBatch
import com.darkroomdilution.ratio.domain.model.Contrast
import com.darkroomdilution.ratio.domain.model.Density
import com.darkroomdilution.ratio.domain.model.NotebookEntry
import com.darkroomdilution.ratio.domain.model.ProcessStage
import com.darkroomdilution.ratio.domain.model.Recipe
import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.model.TankPreset
import com.darkroomdilution.ratio.domain.repository.DarkroomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DarkroomRepositoryImpl(private val preferences: DarkroomPreferences) : DarkroomRepository {

    private val rollState = MutableStateFlow(loadRolls())
    private val presetState = MutableStateFlow(loadPresets())
    private val notebookState = MutableStateFlow(loadNotebook())
    private val batchState = MutableStateFlow(loadBatches())
    private val recipeState = MutableStateFlow(loadRecipe())

    override val rolls: StateFlow<List<RollEntry>> = rollState.asStateFlow()
    override val presets: StateFlow<List<TankPreset>> = presetState.asStateFlow()
    override val notebook: StateFlow<List<NotebookEntry>> = notebookState.asStateFlow()
    override val batches: StateFlow<List<ChemistryBatch>> = batchState.asStateFlow()
    override val recipe: StateFlow<Recipe> = recipeState.asStateFlow()

    override fun updateRecipe(transform: (Recipe) -> Recipe) {
        val next = transform(recipeState.value)
        recipeState.value = next
        persistRecipe(next)
    }

    override fun addPreset(preset: TankPreset) {
        presetState.value = presetState.value + preset
        persistPresets()
    }

    override fun deletePreset(id: String) {
        presetState.value = presetState.value.filterNot { it.id == id }
        persistPresets()
    }

    override fun selectPreset(id: String) {
        val preset = presetState.value.firstOrNull { it.id == id } ?: return
        updateRecipe { it.copy(selectedPresetId = id, tankVolumeMl = preset.volumeMl) }
    }

    override fun addNotebookEntry(entry: NotebookEntry) {
        notebookState.value = notebookState.value + entry
        persistNotebook()
    }

    override fun deleteNotebookEntry(id: String) {
        notebookState.value = notebookState.value.filterNot { it.id == id }
        persistNotebook()
    }

    override fun selectNotebookEntry(id: String) {
        val entry = notebookState.value.firstOrNull { it.id == id } ?: return
        updateRecipe {
            it.copy(
                selectedNotebookId = id,
                film = entry.film,
                developer = entry.developer,
                dilutionInput = entry.dilution,
                baseSeconds = entry.baseSeconds
            )
        }
    }

    override fun addBatch(batch: ChemistryBatch) {
        batchState.value = batchState.value + batch
        persistBatches()
    }

    override fun deleteBatch(id: String) {
        batchState.value = batchState.value.filterNot { it.id == id }
        persistBatches()
    }

    override fun consumeBatch(id: String, rolls: Int) {
        batchState.value = batchState.value.map { batch ->
            if (batch.id == id) {
                batch.copy(rollsUsed = (batch.rollsUsed + rolls).coerceIn(0, batch.rollCapacity))
            } else {
                batch
            }
        }
        persistBatches()
    }

    override fun consumeDeveloperBatch() {
        val target = batchState.value.firstOrNull { it.rollsUsed < it.rollCapacity } ?: return
        consumeBatch(target.id, 1)
    }

    override fun createDraftRoll(elapsedSeconds: Int): String {
        val current = recipeState.value
        val id = "roll_" + System.currentTimeMillis()
        val entry = RollEntry(
            id = id,
            createdAt = System.currentTimeMillis(),
            film = current.film,
            developer = current.developer,
            dilution = current.dilutionInput,
            tempC = current.tempC,
            baseSeconds = current.baseSeconds,
            correctedSeconds = current.correctedSeconds,
            pushPull = current.pushPull,
            elapsedSeconds = elapsedSeconds,
            density = Density.NORMAL,
            contrast = Contrast.NORMAL,
            notes = "",
            saved = false
        )
        rollState.value = listOf(entry) + rollState.value
        persistRolls()
        return id
    }

    override fun roll(id: String): RollEntry? = rollState.value.firstOrNull { it.id == id }

    override fun saveRoll(entry: RollEntry) {
        val known = rollState.value.any { it.id == entry.id }
        rollState.value = if (known) {
            rollState.value.map { if (it.id == entry.id) entry else it }
        } else {
            listOf(entry) + rollState.value
        }
        persistRolls()
    }

    override fun resetAll() {
        preferences.clear()
        rollState.value = emptyList()
        presetState.value = SampleData.tankPresets
        notebookState.value = SampleData.notebookEntries
        batchState.value = SampleData.chemistryBatches(System.currentTimeMillis())
        recipeState.value = defaultRecipe()
        persistPresets()
        persistNotebook()
        persistBatches()
        persistRolls()
        persistRecipe(recipeState.value)
    }

    private fun defaultRecipe(): Recipe = Recipe(
        selectedPresetId = SampleData.tankPresets[1].id,
        selectedNotebookId = SampleData.notebookEntries[0].id,
        film = SampleData.notebookEntries[0].film,
        developer = SampleData.notebookEntries[0].developer,
        baseSeconds = SampleData.notebookEntries[0].baseSeconds,
        correctedSeconds = SampleData.notebookEntries[0].baseSeconds
    )

    private fun loadRolls(): List<RollEntry> =
        decodeAll(preferences.readString(KEY_ROLLS, ""), ::decodeRoll)

    private fun loadPresets(): List<TankPreset> {
        val decoded = decodeAll(preferences.readString(KEY_PRESETS, ""), ::decodePreset)
        return decoded.ifEmpty { SampleData.tankPresets }
    }

    private fun loadNotebook(): List<NotebookEntry> {
        val decoded = decodeAll(preferences.readString(KEY_NOTEBOOK, ""), ::decodeNotebook)
        return decoded.ifEmpty { SampleData.notebookEntries }
    }

    private fun loadBatches(): List<ChemistryBatch> {
        val decoded = decodeAll(preferences.readString(KEY_BATCHES, ""), ::decodeBatch)
        return decoded.ifEmpty { SampleData.chemistryBatches(System.currentTimeMillis()) }
    }

    private fun loadRecipe(): Recipe {
        val stages: List<ProcessStage> = decodeAll(preferences.readString(KEY_STAGES, ""), ::decodeStage)
        val fallback = defaultRecipe()
        return Recipe(
            tankVolumeMl = preferences.readInt(KEY_VOLUME, fallback.tankVolumeMl),
            dilutionInput = preferences.readString(KEY_DILUTION, fallback.dilutionInput),
            concentrateMl = preferences.readFloat(KEY_CONCENTRATE, fallback.concentrateMl.toFloat()).toDouble(),
            waterMl = preferences.readFloat(KEY_WATER, fallback.waterMl.toFloat()).toDouble(),
            film = preferences.readString(KEY_FILM, fallback.film),
            developer = preferences.readString(KEY_DEVELOPER, fallback.developer),
            baseSeconds = preferences.readInt(KEY_BASE, fallback.baseSeconds),
            tempC = preferences.readFloat(KEY_TEMP, GameConfig.REFERENCE_TEMP_C.toFloat()).toDouble(),
            pushPull = preferences.readInt(KEY_PUSH, 0),
            correctedSeconds = preferences.readInt(KEY_CORRECTED, fallback.correctedSeconds),
            selectedPresetId = preferences.readString(KEY_PRESET_ID, fallback.selectedPresetId),
            selectedNotebookId = preferences.readString(KEY_NOTEBOOK_ID, fallback.selectedNotebookId),
            stages = stages
        )
    }

    private fun persistRolls() {
        preferences.writeString(KEY_ROLLS, encodeAll(rollState.value, ::encodeRoll))
    }

    private fun persistPresets() {
        preferences.writeString(KEY_PRESETS, encodeAll(presetState.value, ::encodePreset))
    }

    private fun persistNotebook() {
        preferences.writeString(KEY_NOTEBOOK, encodeAll(notebookState.value, ::encodeNotebook))
    }

    private fun persistBatches() {
        preferences.writeString(KEY_BATCHES, encodeAll(batchState.value, ::encodeBatch))
    }

    private fun persistRecipe(value: Recipe) {
        preferences.writeInt(KEY_VOLUME, value.tankVolumeMl)
        preferences.writeString(KEY_DILUTION, value.dilutionInput)
        preferences.writeFloat(KEY_CONCENTRATE, value.concentrateMl.toFloat())
        preferences.writeFloat(KEY_WATER, value.waterMl.toFloat())
        preferences.writeString(KEY_FILM, value.film)
        preferences.writeString(KEY_DEVELOPER, value.developer)
        preferences.writeInt(KEY_BASE, value.baseSeconds)
        preferences.writeFloat(KEY_TEMP, value.tempC.toFloat())
        preferences.writeInt(KEY_PUSH, value.pushPull)
        preferences.writeInt(KEY_CORRECTED, value.correctedSeconds)
        preferences.writeString(KEY_PRESET_ID, value.selectedPresetId)
        preferences.writeString(KEY_NOTEBOOK_ID, value.selectedNotebookId)
        preferences.writeString(KEY_STAGES, encodeAll(value.stages, ::encodeStage))
    }

    private companion object {
        const val KEY_ROLLS = "rolls"
        const val KEY_PRESETS = "presets"
        const val KEY_NOTEBOOK = "notebook"
        const val KEY_BATCHES = "batches"
        const val KEY_STAGES = "stages"
        const val KEY_VOLUME = "recipe_volume"
        const val KEY_DILUTION = "recipe_dilution"
        const val KEY_CONCENTRATE = "recipe_concentrate"
        const val KEY_WATER = "recipe_water"
        const val KEY_FILM = "recipe_film"
        const val KEY_DEVELOPER = "recipe_developer"
        const val KEY_BASE = "recipe_base"
        const val KEY_TEMP = "recipe_temp"
        const val KEY_PUSH = "recipe_push"
        const val KEY_CORRECTED = "recipe_corrected"
        const val KEY_PRESET_ID = "recipe_preset_id"
        const val KEY_NOTEBOOK_ID = "recipe_notebook_id"
    }
}
