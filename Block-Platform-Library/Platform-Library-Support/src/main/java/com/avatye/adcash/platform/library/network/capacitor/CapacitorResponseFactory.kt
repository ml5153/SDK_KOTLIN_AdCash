package com.avatye.adcash.platform.library.network.capacitor

abstract class CapacitorResponseFactory {
    fun of(responseValue: String) = mapper(responseValue)
    protected abstract fun mapper(responseValue: String)
}