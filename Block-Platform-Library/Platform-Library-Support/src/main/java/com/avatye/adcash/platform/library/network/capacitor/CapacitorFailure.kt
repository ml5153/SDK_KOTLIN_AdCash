package com.avatye.adcash.platform.library.network.capacitor

data class CapacitorFailure(
    val status: Int,
    val code: String,
    val message: String = ""
)