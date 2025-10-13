package com.avatye.adcash.platform.library.network.capacitor

interface ICapacitorCallback<in T : CapacitorResponseFactory> {
    fun onSuccess(success: T)
    fun onFailure(failure: CapacitorFailure)
}