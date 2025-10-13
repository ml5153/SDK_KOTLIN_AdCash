package com.avatye.adcash

import com.avatye.adcash.platform.provider.basement.AdsviserError

data class AdError(
    val errorCode: Int,
    val errorMessage: String
) {
    internal companion object {
        fun of(adErrorUnit: AdErrorUnit, appendix: String = ""): AdError {
            return AdError(
                errorCode = adErrorUnit.code,
                errorMessage = adErrorUnit.message + appendix
            )
        }

        fun of(adsviserError: AdsviserError): AdError {
            return if (adsviserError.isBlocked) {
                of(AdErrorUnit.NOT_LOADED)
            } else {
                AdError(
                    errorCode = adsviserError.code,
                    errorMessage = adsviserError.message
                )
            }
        }

        fun of(status: Int, serverError: String, serverMessage: String): AdError {
            return if (serverError.equals(other = "err_cannot_find_mediation", ignoreCase = true)) {
                of(AdErrorUnit.UNKNOWN_SERVER_ERROR)
            } else if (status == 2000) {
                of(AdErrorUnit.DISCONNECT_SERVER)
            } else {
                of(adErrorUnit = AdErrorUnit.EXCEPTION_SERVER, appendix = "{ $serverMessage }")
            }
        }
    }

    override fun toString(): String {
        return "AdError { errorCode: $errorCode, errorMessage: $errorMessage }"
    }
}