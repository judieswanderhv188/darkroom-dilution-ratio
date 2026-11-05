package com.darkroomdilution.ratio.data.repository

import com.darkroomdilution.ratio.domain.model.AgitationMode
import com.darkroomdilution.ratio.domain.model.ChemistryBatch
import com.darkroomdilution.ratio.domain.model.Contrast
import com.darkroomdilution.ratio.domain.model.Density
import com.darkroomdilution.ratio.domain.model.NotebookEntry
import com.darkroomdilution.ratio.domain.model.ProcessStage
import com.darkroomdilution.ratio.domain.model.RollEntry
import com.darkroomdilution.ratio.domain.model.StageKind
import com.darkroomdilution.ratio.domain.model.TankPreset

internal const val RECORD_SEPARATOR = "\u0001"
internal const val FIELD_SEPARATOR = "\u0002"

internal fun sanitize(value: String): String =
    value.replace(RECORD_SEPARATOR, " ").replace(FIELD_SEPARATOR, " ").trim()

internal fun <T> encodeAll(items: List<T>, encode: (T) -> String): String =
    items.joinToString(RECORD_SEPARATOR) { encode(it) }

internal fun <T> decodeAll(raw: String, decode: (List<String>) -> T?): List<T> {
    if (raw.isBlank()) return emptyList()
    return raw.split(RECORD_SEPARATOR)
        .filter { it.isNotBlank() }
        .mapNotNull { record -> runCatching { decode(record.split(FIELD_SEPARATOR)) }.getOrNull() }
}

internal fun encodePreset(value: TankPreset): String = listOf(
    value.id, sanitize(value.name), value.reels.toString(), value.volumeMl.toString()
).joinToString(FIELD_SEPARATOR)

internal fun decodePreset(parts: List<String>): TankPreset? {
    if (parts.size < 4) return null
    return TankPreset(parts[0], parts[1], parts[2].toInt(), parts[3].toInt())
}

internal fun encodeNotebook(value: NotebookEntry): String = listOf(
    value.id,
    sanitize(value.film),
    sanitize(value.developer),
    sanitize(value.dilution),
    value.iso.toString(),
    value.baseSeconds.toString()
).joinToString(FIELD_SEPARATOR)

internal fun decodeNotebook(parts: List<String>): NotebookEntry? {
    if (parts.size < 6) return null
    return NotebookEntry(parts[0], parts[1], parts[2], parts[3], parts[4].toInt(), parts[5].toInt())
}

internal fun encodeBatch(value: ChemistryBatch): String = listOf(
    value.id,
    sanitize(value.name),
    sanitize(value.dilution),
    value.mixedAt.toString(),
    value.volumeMl.toString(),
    value.rollsUsed.toString(),
    value.rollCapacity.toString(),
    value.shelfLifeDays.toString()
).joinToString(FIELD_SEPARATOR)

internal fun decodeBatch(parts: List<String>): ChemistryBatch? {
    if (parts.size < 8) return null
    return ChemistryBatch(
        id = parts[0],
        name = parts[1],
        dilution = parts[2],
        mixedAt = parts[3].toLong(),
        volumeMl = parts[4].toInt(),
        rollsUsed = parts[5].toInt(),
        rollCapacity = parts[6].toInt(),
        shelfLifeDays = parts[7].toInt()
    )
}

internal fun encodeRoll(value: RollEntry): String = listOf(
    value.id,
    value.createdAt.toString(),
    sanitize(value.film),
    sanitize(value.developer),
    sanitize(value.dilution),
    value.tempC.toString(),
    value.baseSeconds.toString(),
    value.correctedSeconds.toString(),
    value.pushPull.toString(),
    value.elapsedSeconds.toString(),
    value.density.name,
    value.contrast.name,
    sanitize(value.notes),
    if (value.saved) "1" else "0"
).joinToString(FIELD_SEPARATOR)

internal fun decodeRoll(parts: List<String>): RollEntry? {
    if (parts.size < 14) return null
    return RollEntry(
        id = parts[0],
        createdAt = parts[1].toLong(),
        film = parts[2],
        developer = parts[3],
        dilution = parts[4],
        tempC = parts[5].toDouble(),
        baseSeconds = parts[6].toInt(),
        correctedSeconds = parts[7].toInt(),
        pushPull = parts[8].toInt(),
        elapsedSeconds = parts[9].toInt(),
        density = Density.valueOf(parts[10]),
        contrast = Contrast.valueOf(parts[11]),
        notes = parts[12],
        saved = parts[13] == "1"
    )
}

internal fun encodeStage(value: ProcessStage): String = listOf(
    value.kind.name,
    value.durationSeconds.toString(),
    value.agitation.name,
    if (value.enabled) "1" else "0"
).joinToString(FIELD_SEPARATOR)

internal fun decodeStage(parts: List<String>): ProcessStage? {
    if (parts.size < 4) return null
    return ProcessStage(
        kind = StageKind.valueOf(parts[0]),
        durationSeconds = parts[1].toInt(),
        agitation = AgitationMode.valueOf(parts[2]),
        enabled = parts[3] == "1"
    )
}
