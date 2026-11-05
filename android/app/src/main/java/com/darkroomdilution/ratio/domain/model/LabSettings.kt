package com.darkroomdilution.ratio.domain.model

data class LabSettings(
    val celsius: Boolean = true,
    val redLightMode: Boolean = true,
    val planningLightMode: Boolean = false,
    val agitationSound: Boolean = true,
    val hapticPrompts: Boolean = true,
    val keepScreenOn: Boolean = true,
    val agitationLeadSeconds: Int = 3
)
