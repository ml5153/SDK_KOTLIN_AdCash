package com.avatye.adcash.platform.provider.adpopcorn

import com.avatye.adcash.platform.provider.basement.AdsviserError
import com.avatye.adcash.platform.provider.basement.AdsviserProviderUnit
import com.igaworks.ssp.SSPErrorCode

enum class APsspErrorUnit(val code: Int, val message: String) {
    EXCEPTION(
        code = 9000,
        message = "[${Settings.adsviserName}] exception"
    ),
    EXCEPTION_LOADER_IS_NULL(
        code = 9000,
        message = "[${Settings.adsviserName}] exception 'loader is null'"
    ),
    EXCEPTION_CONTEXT_IS_NULL(
        code = 9000,
        message = "[${Settings.adsviserName}] exception 'context(activity) is null'"
    ),
    SERVER_TIMEOUT(
        code = 9002,
        message = "[${Settings.adsviserName}] server timeout 'time over 5000'"
    ),
    INVALID_PARAMETER(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid parameter: 1000 }"
    ),
    INVALID_ADN_MEDIA_KEY(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid advertise network media key: 2000 }"
    ),
    INVALID_ADN_SPOT_KEY(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid advertise network spot key: 2030 }"
    ),
    INVALID_THIRD_PARTY(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid third party: 2200 }"
    ),
    INVALID_NATIVE_INIT(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid native spot dose not initialize: 3200 }"
    ),
    INVALID_HOUSE_AD(
        code = 9100,
        message = "[${Settings.adsviserName}] invalid house advertise asset"
    ),
    NOT_EXISTS_CAMPAIGN(
        code = 9200,
        message = "[${Settings.adsviserName}] not exists campaign: 2100 }"
    ),
    NOT_EXISTS_QUEUE(
        code = 9200,
        message = "[${Settings.adsviserName}] not exists advertises 'queue is empty'"
    ),
    NOT_EXISTS_AD(
        code = 9200,
        message = "[${Settings.adsviserName}] not exists advertises[no-ad]: 5002"
    ),
    NOT_EXISTS_NATIVE_SPOT(
        code = 9200,
        message = "[${Settings.adsviserName}] invalid native spot dose not initialize: 5001"
    ),
    FAIL_OPEN(
        code = 9300,
        message = "[${Settings.adsviserName}] advertise open failed"
    ),
    LOAD_TIMEOUT(
        code = 9400,
        message = "[${Settings.adsviserName}] advertise load time out(video)"
    ),
    BLOCKED(
        code = 9999,
        message = "[${Settings.adsviserName}] invalid native spot dose not initialize: 9999"
    ),
    BLOCKED_SIZE(
        code = 9999,
        message = "can't initialize ad size { 9999 }"
    );

    internal companion object {
        fun of(sspErrorUnit: SSPErrorCode?, networkUnit: APsspNetworkUnit): AdsviserError {
            return if (sspErrorUnit == null) {
                of(errorUnit = EXCEPTION, networkUnit = networkUnit)
            } else {
                when (sspErrorUnit.errorCode) {
                    BLOCKED.code -> of(errorUnit = BLOCKED, networkUnit = networkUnit)
                    INVALID_PARAMETER.code -> of(errorUnit = INVALID_PARAMETER, networkUnit = networkUnit)
                    SERVER_TIMEOUT.code -> of(errorUnit = SERVER_TIMEOUT, networkUnit = networkUnit)
                    INVALID_ADN_MEDIA_KEY.code -> of(errorUnit = INVALID_ADN_MEDIA_KEY, networkUnit = networkUnit)
                    INVALID_ADN_SPOT_KEY.code -> of(errorUnit = INVALID_ADN_SPOT_KEY, networkUnit = networkUnit)
                    INVALID_THIRD_PARTY.code -> of(errorUnit = INVALID_THIRD_PARTY, networkUnit = networkUnit)
                    INVALID_NATIVE_INIT.code -> of(errorUnit = INVALID_NATIVE_INIT, networkUnit = networkUnit)
                    NOT_EXISTS_CAMPAIGN.code -> of(errorUnit = NOT_EXISTS_CAMPAIGN, networkUnit = networkUnit)
                    NOT_EXISTS_AD.code -> of(errorUnit = NOT_EXISTS_AD, networkUnit = networkUnit)
                    NOT_EXISTS_NATIVE_SPOT.code -> of(errorUnit = NOT_EXISTS_NATIVE_SPOT, networkUnit = networkUnit)
                    else -> {
                        AdsviserError(
                            isBlocked = false,
                            code = EXCEPTION.code,
                            message = "${EXCEPTION.message} { ${AdsviserProviderUnit.ADPOPCORN.providerName}(${sspErrorUnit.errorCode}: ${sspErrorUnit.errorMessage} }",
                            adsviserName = Settings.adsviserName,
                            networkUnitName = networkUnit.name
                        )
                    }
                }
            }
        }

        fun of(errorUnit: APsspErrorUnit, networkUnit: APsspNetworkUnit): AdsviserError {
            return AdsviserError(
                isBlocked = errorUnit == BLOCKED,
                code = errorUnit.code,
                message = errorUnit.message,
                adsviserName = Settings.adsviserName,
                networkUnitName = networkUnit.name
            )
        }

        fun of(errorUnit: APsspErrorUnit, networkUnitName: String): AdsviserError {
            return AdsviserError(
                isBlocked = errorUnit == BLOCKED,
                code = errorUnit.code,
                message = errorUnit.message,
                adsviserName = Settings.adsviserName,
                networkUnitName = networkUnitName
            )
        }
    }
}