package com.avatye.adcash.platform.provider.mezzo

import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit

enum class MsspErrorUnit(val code: Int, val message: String) {
    EXCEPTION(
        code = 9000,
        message = "exception"
    ),
    EXCEPTION_LOADER_IS_NULL(
        code = 9000,
        message = "exception { loader is null }"
    ),
    EXCEPTION_CONTEXT_IS_NULL(
        code = 9000,
        message = "exception { context(activity) is null }"
    ),
    SERVER_TIMEOUT(
        code = 9002,
        message = "server timeout { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 400 }"
    ),
    INVALID_PARAMETER(
        code = 9100,
        message = "invalid parameter {${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 400 }"
    ),
    INVALID_ADN_MEDIA_KEY(
        code = 9100,
        message = "invalid advertise network media key { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 2000 }"
    ),
    INVALID_ADN_SPOT_KEY(
        code = 9100,
        message = "invalid advertise network spot key { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 2030 }"
    ),
    INVALID_THIRD_PARTY(
        code = 9100,
        message = "invalid third party { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 2200 }"
    ),
    INVALID_HOUSE_AD(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid house advertise asset"
    ),
    INVALID_NATIVE_INIT(
        code = 9100,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 3200 }"
    ),
    NOT_EXISTS_AD(
        code = 9200,
        message = "not exists advertises[no-ad] { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 404 }"
    ),
    NOT_EXISTS_QUEUE(
        code = 9200,
        message = "not exists advertises[no-ad] { queue is empty }"
    ),
    NOT_EXISTS_NATIVE_SPOT(
        code = 9200,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 5001 }"
    ),
    FAIL_OPEN(
        code = 9300,
        message = "advertise open failed"
    ),
    LOAD_TIMEOUT(
        code = 9400,
        message = "advertise load time out(video)"
    ),
    BLOCKED(
        code = 9999,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}: 9999 }"
    ),
    BLOCKED_SIZE(
        code = 9999,
        message = "can't initialize ad size { 9999 }"
    );

    internal companion object {
        fun of(status: String, errorMessage: String? = null): AdsviserError {
            return when (status) {
                "404" -> of(errorUnit = NOT_EXISTS_AD)             // AdResponseCode.Status.NOAD
                "405" -> of(errorUnit = INVALID_PARAMETER)        // AdResponseCode.Status.NEEDSYNC
                "408" -> of(errorUnit = SERVER_TIMEOUT)           // AdResponseCode.Status.TIMEOUT
                "415" -> of(errorUnit = EXCEPTION)                // AdResponseCode.Status.ERROR_PARSING
                "498" -> of(errorUnit = EXCEPTION)                // AdResponseCode.Status.DUPLICATIONCALL
                "499" -> of(errorUnit = EXCEPTION)                // AdResponseCode.Status.ERROR
                "1000" -> of(errorUnit = INVALID_PARAMETER)       // AdResponseCode.Status.DEVICE_NETWORK_ERROR
                "2000" -> of(errorUnit = INVALID_PARAMETER)       // AdResponseCode.Status.DEVICE_SETTING_ERROR
                "3000" -> of(errorUnit = INVALID_PARAMETER)       // AdResponseCode.Status.DEVICE_AD_INTERVAL
                "5000" -> of(errorUnit = BLOCKED)                 // AdResponseCode.Status.APP_LIFECYCLE_BACK

                else -> AdsviserError(
                    isBlocked = false,
                    code = EXCEPTION.code,
                    message = "${EXCEPTION.message} { ${AdsviserProviderUnit.MEZZOMEDIA.providerName}($status: $errorMessage) }",
                    adsviserName = Settings.adsviserName,
                    networkUnitName = AdsviserProviderUnit.MEZZOMEDIA.providerName
                )
            }
        }

        fun of(errorUnit: MsspErrorUnit): AdsviserError {
            return AdsviserError(
                isBlocked = errorUnit == BLOCKED || errorUnit == BLOCKED_SIZE,
                code = errorUnit.code,
                message = errorUnit.message,
                adsviserName = Settings.adsviserName,
                networkUnitName = AdsviserProviderUnit.MEZZOMEDIA.providerName
            )
        }
    }
}