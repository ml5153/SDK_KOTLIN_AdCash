package com.avatye.adcash.platform.provider.doyouad

import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit

enum class DoYouAdErrorUnit(val code: Int, val message: String) {
    ERROR(
        code = 9000,
        message = "AD load error!"
    ),
    RELOAD_ERROR(
        code = 9000,
        message = "AD reload error!"
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
        message = "server timeout { ${AdsviserProviderUnit.DOYOUAD.providerName}: 400 }"
    ),
    INVALID_PARAMETER(
        code = 9100,
        message = "invalid parameter {${AdsviserProviderUnit.DOYOUAD.providerName}: 400 }"
    ),
    INVALID_ADN_MEDIA_KEY(
        code = 9100,
        message = "invalid advertise network media key { ${AdsviserProviderUnit.DOYOUAD.providerName}: 2000 }"
    ),
    INVALID_ADN_SPOT_KEY(
        code = 9100,
        message = "invalid advertise network spot key { ${AdsviserProviderUnit.DOYOUAD.providerName}: 2030 }"
    ),
    INVALID_THIRD_PARTY(
        code = 9100,
        message = "invalid third party { ${AdsviserProviderUnit.DOYOUAD.providerName}: 2200 }"
    ),
    INVALID_HOUSE_AD(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid house advertise asset"
    ),
    INVALID_NATIVE_INIT(
        code = 9100,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.DOYOUAD.providerName}: 3200 }"
    ),
    NOT_EXISTS_AD(
        code = 9200,
        message = "not exists advertises[no-ad] { ${AdsviserProviderUnit.DOYOUAD.providerName}: 404 }"
    ),
    NOT_EXISTS_QUEUE(
        code = 9200,
        message = "not exists advertises[no-ad] { queue is empty }"
    ),
    NOT_EXISTS_NATIVE_SPOT(
        code = 9200,
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.DOYOUAD.providerName}: 5001 }"
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
        message = "invalid native spot dose not initialize { ${AdsviserProviderUnit.DOYOUAD.providerName}: 9999 }"
    ),
    BLOCKED_SIZE(
        code = 9999,
        message = "can't initialize ad size { 9999 }"
    );


    internal companion object {
        fun of(errorMessage: String?, status: String? = ""): AdsviserError {
            return AdsviserError(
                isBlocked = false,
                code = ERROR.code,
                message = "${ERROR.message} { ${AdsviserProviderUnit.DOYOUAD.providerName}($status: $errorMessage) }",
                adsviserName = Settings.adsviserName,
                networkUnitName = AdsviserProviderUnit.DOYOUAD.providerName
            )

        }

        fun of(errorUnit: DoYouAdErrorUnit): AdsviserError {
            return AdsviserError(
                isBlocked = errorUnit == ERROR,
                code = errorUnit.code,
                message = errorUnit.message,
                adsviserName = Settings.adsviserName,
                networkUnitName = AdsviserProviderUnit.DOYOUAD.providerName
            )
        }
    }
}