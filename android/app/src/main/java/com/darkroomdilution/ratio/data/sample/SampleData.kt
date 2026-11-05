package com.darkroomdilution.ratio.data.sample

import com.darkroomdilution.ratio.domain.model.ChemistryBatch
import com.darkroomdilution.ratio.domain.model.NotebookEntry
import com.darkroomdilution.ratio.domain.model.TankPreset

object SampleData {

    private const val DAY_MS = 86_400_000L

    val tankPresets: List<TankPreset> = listOf(
        TankPreset("tank_pat1", "Paterson Super System 4", 1, 290),
        TankPreset("tank_pat2", "Paterson Super System 4", 2, 500),
        TankPreset("tank_jobo1", "Jobo 1520", 1, 240),
        TankPreset("tank_jobo2", "Jobo 1520", 2, 485),
        TankPreset("tank_ap", "AP Compact", 1, 300),
        TankPreset("tank_steel35", "Stainless 35 mm", 1, 250),
        TankPreset("tank_steel120", "Stainless 120", 1, 475)
    )

    val notebookEntries: List<NotebookEntry> = listOf(
        NotebookEntry("nb_hp5", "HP5 PLUS 400", "ID-11", "1+1", 400, 780),
        NotebookEntry("nb_trix", "TRI-X 400", "D-76", "1+1", 400, 585),
        NotebookEntry("nb_fp4", "FP4 PLUS 125", "ID-11", "STOCK", 125, 390),
        NotebookEntry("nb_kentmere", "KENTMERE 400", "RODINAL", "1+50", 400, 1080),
        NotebookEntry("nb_delta", "DELTA 100", "DD-X", "1+4", 100, 540),
        NotebookEntry("nb_ultrafine", "ULTRAFINE 100", "HC-110 B", "1+31", 100, 300)
    )

    fun chemistryBatches(now: Long): List<ChemistryBatch> = listOf(
        ChemistryBatch("batch_id11", "ID-11 STOCK", "STOCK", now - 12 * DAY_MS, 1000, 9, 16, 180),
        ChemistryBatch("batch_stop", "ILFOSTOP", "1+19", now - 4 * DAY_MS, 500, 14, 30, 90),
        ChemistryBatch("batch_fixer", "RAPID FIXER", "1+4", now - 21 * DAY_MS, 1000, 19, 24, 60)
    )

    val films: List<String> = listOf(
        "HP5 PLUS 400",
        "TRI-X 400",
        "FP4 PLUS 125",
        "KENTMERE 400",
        "DELTA 100",
        "ULTRAFINE 100"
    )

    val developers: List<String> = listOf("ID-11", "D-76", "RODINAL", "DD-X", "HC-110 B", "XTOL")

    val dilutionPresets: List<String> = listOf("1+31", "1+49", "1:50", "1+9", "1+14")

    val tankVolumePresets: List<Int> = listOf(290, 500, 1000)
}
