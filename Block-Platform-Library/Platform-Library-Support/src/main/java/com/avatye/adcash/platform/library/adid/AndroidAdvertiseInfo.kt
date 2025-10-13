package com.avatye.adcash.platform.library.adid

data class AndroidAdvertiseInfo(
    val id: String,
    val isLimitAdTrackingEnabled: Boolean
) {
    val isValid: Boolean
        get() {
            return !(id.isEmpty() || id == "0" || id == "00000000-0000-0000-0000-000000000000")
        }
}