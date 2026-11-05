package com.darkroomdilution.ratio.domain.model

data class NotebookEntry(
    val id: String,
    val film: String,
    val developer: String,
    val dilution: String,
    val iso: Int,
    val baseSeconds: Int
)
