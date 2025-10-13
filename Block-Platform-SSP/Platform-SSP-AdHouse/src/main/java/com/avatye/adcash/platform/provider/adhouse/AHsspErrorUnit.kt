package com.avatye.adcash.platform.provider.adhouse

import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit

enum class AHsspErrorUnit(val code: Int, val message: String) {
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
        message = "server timeout { ${AdsviserProviderUnit.ADMIXER.providerName}: 5000 }"
    ),
    INVALID_PARAMETER(
        code = 9100,
        message = "invalid parameter {${AdsviserProviderUnit.ADMIXER.providerName}: 1000 }"
    ),
    INVALID_ADN_MEDIA_KEY(
        code = 9100,
        message = "invalid advertise network media key { ${AdsviserProviderUnit.ADMIXER.providerName}: 2000 }"
    ),
    INVALID_ADN_SPOT_KEY(
        code = 9100,
        message = "invalid advertise network spot key { ${AdsviserProviderUnit.ADMIXER.providerName}: 2030 }"
    ),
    INVALID_THIRD_PARTY(
        code = 9100,
        message = "invalid third party { ${AdsviserProviderUnit.ADMIXER.providerName}: 2200 }"
    ),
    INVALID_HOUSE_AD(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid house advertise asset"
    ),
    INVALID_NATIVE_INIT(
        code = 9100,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.ADMIXER.providerName}: 3200 }"
    ),
    NOT_EXISTS_AD(
        code = 9200,
        message = "not exists advertises[no-ad] { ${AdsviserProviderUnit.ADPOPCORN.providerName}: 5002 }"
    ),
    NOT_EXISTS_QUEUE(
        code = 9200,
        message = "not exists advertises[no-ad] { queue is empty }"
    ),
    NOT_EXISTS_NATIVE_SPOT(
        code = 9200,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.ADPOPCORN.providerName}: 5001 }"
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
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.ADPOPCORN.providerName}: 9999 }"
    ),
    BLOCKED_SIZE(
        code = 9999,
        message = "can't initialize ad size { 9999 }"
    );

    internal companion object {

        fun of(errorCode: Int, errorMessage: String?): AdsviserError {
            return when (errorCode) {
                BLOCKED.code -> of(errorUnit = BLOCKED)
                INVALID_PARAMETER.code -> of(errorUnit = INVALID_PARAMETER)
                SERVER_TIMEOUT.code -> of(errorUnit = SERVER_TIMEOUT)
                INVALID_ADN_MEDIA_KEY.code -> of(errorUnit = INVALID_ADN_MEDIA_KEY)
                INVALID_ADN_SPOT_KEY.code -> of(errorUnit = INVALID_ADN_SPOT_KEY)
                INVALID_THIRD_PARTY.code -> of(errorUnit = INVALID_THIRD_PARTY)
                INVALID_NATIVE_INIT.code -> of(errorUnit = INVALID_NATIVE_INIT)
                NOT_EXISTS_AD.code -> of(errorUnit = NOT_EXISTS_AD)
                NOT_EXISTS_QUEUE.code -> of(errorUnit = NOT_EXISTS_QUEUE)
                NOT_EXISTS_NATIVE_SPOT.code -> of(errorUnit = NOT_EXISTS_NATIVE_SPOT)
                else -> {
                    AdsviserError(
                        isBlocked = false,
                        code = EXCEPTION.code,
                        message = "${EXCEPTION.message} { ${AdsviserProviderUnit.ADMIXER.providerName}(${errorCode}: ${errorMessage} }",
                        adsviserName = Settings.adsviserName,
                        networkUnitName = AdsviserProviderUnit.ADMIXER.providerName
                    )
                }
            }
        }

        fun of(errorUnit: AHsspErrorUnit): AdsviserError {
            return AdsviserError(
                isBlocked = errorUnit == BLOCKED || errorUnit == BLOCKED_SIZE,
                code = errorUnit.code,
                message = errorUnit.message,
                adsviserName = Settings.adsviserName,
                networkUnitName = AdsviserProviderUnit.ADMIXER.providerName
            )
        }

    }
}