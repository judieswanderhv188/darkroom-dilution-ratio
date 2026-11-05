package com.darkroomdilution.ratio.domain.model

sealed interface Dilution {
    val label: String

    data object Stock : Dilution {
        override val label: String = "STOCK"
    }

    data class Parts(val parts: Double) : Dilution {
        override val label: String = "1+" + trimNumber(parts)
    }

    data class Ratio(val total: Double) : Dilution {
        override val label: String = "1:" + trimNumber(total)
    }
}

private val partsPattern = Regex("""^1\+([0-9]{1,4}(\.[0-9]{1,2})?)$""")
private val ratioPattern = Regex("""^1:([0-9]{1,4}(\.[0-9]{1,2})?)$""")

fun parseDilution(raw: String): Dilution? {
    val normalized = raw.trim().uppercase().replace(" ", "").replace(",", ".")
    if (normalized.isEmpty()) return null
    if (normalized == "STOCK" || normalized == "1+0" || normalized == "1:1") return Dilution.Stock
    partsPattern.find(normalized)?.let { match ->
        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        return if (value <= 0.0) Dilution.Stock else Dilution.Parts(value)
    }
    ratioPattern.find(normalized)?.let { match ->
        val value = match.groupValues[1].toDoubleOrNull() ?: return null
        return if (value <= 1.0) Dilution.Stock else Dilution.Ratio(value)
    }
    return null
}

fun trimNumber(value: Double): String =
    if (value == value.toLong().toDouble()) value.toLong().toString() else String.format("%.1f", value)
