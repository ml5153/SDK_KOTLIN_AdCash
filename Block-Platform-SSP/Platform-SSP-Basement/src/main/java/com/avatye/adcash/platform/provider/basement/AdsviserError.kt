package com.avatye.adcash.platform.provider.basement

data class AdsviserError(
    val code: Int,
    val message: String,
    val isBlocked: Boolean,
    val adsviserName: String,
    val networkUnitName: String
)