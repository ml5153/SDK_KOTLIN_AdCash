package com.avatye.adcash

internal enum class AdErrorUnit(val code: Int, val message: String) {
    EXCEPTION(
        code = 1000,
        message = "exception"
    ),
    EXCEPTION_SERVER(
        code = 1000,
        message = "exception(server)"
    ),
    EXCEPTION_LOADER_IS_NULL(
        code = 1000,
        message = "exception { adcash loader is null }"
    ),
    NEED_AGE_VERIFICATION(
        code = 1100,
        message = "need age verification"
    ),
    INVALID_APID_KEY(
        code = 1200,
        message = "invalid apid key"
    ),
    INVALID_APID_TYPE(
        code = 1200,
        message = "invalid apid type"
    ),
    NOT_EXISTS_APID_CAMPAIGN(
        code = 1300,
        message = "not exists apid campaign"
    ),
    DISCONNECT_SERVER(
        code = 2000,
        message = "exception(server) network is disconnected"
    ),
    UNKNOWN_SERVER_ERROR(
        code = 2001,
        message = "ad network unknown server error"
    ),
    NOT_LOADED(
        code = 1400,
        message = "advertise is not loaded(loader is not ready)"
    );
}