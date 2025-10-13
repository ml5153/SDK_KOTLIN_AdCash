package com.avatye.adcash.platform.library.network.capacitor

internal enum class CapacitorHostType(val value: String) {
    DEV("dev"),
    QA("qa"),
    STAGE("stage"),
    LIVE("live");

    companion object {
        fun from(value: String): CapacitorHostType {
            return when (value.lowercase()) {
                "dev" -> DEV
                "qa" -> QA
                "stage" -> STAGE
                "live" -> LIVE
                else -> LIVE
            }
        }
    }
}