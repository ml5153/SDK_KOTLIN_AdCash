package com.avatye.adcash.platform.provider.adpopcorn

import androidx.annotation.Keep

enum class APsspNetworkUnit(val value: Int) {
    UNKNOWN(-1),
    IGAW(0),
    ADMOB(1),
    FAN(2),
    MOPUB(3),
    CAULY(4),
    MEZZOMEDIA(5),
    MOBON(6),
    UNITY_ADS(7),
    MINTEGRAL(8),
    FAN_NATIVE_BANNER(9),
    ADFIT(10),
    APPNEXT(11),
    CRITEO(12),
    ADCOLONY(13),
    VUNGLE(14),
    APPLOVIN(15),
    FYBER(16),
    TAPJOY(17),
    PANGLE(18),
    GAM(19),
    COUPANG(20),
    NAM(22),
    APPLOVIN_MAX_DYNAMIC_BID(23),
    ADOP(24),
    MobWith(25),
    HOUSE(1000);

    @Keep
    companion object {
        fun fromValue(value: Int): APsspNetworkUnit {
            return when (value) {
                0 -> IGAW
                1 -> ADMOB
                2 -> FAN
                3 -> MOPUB
                4 -> CAULY
                5 -> MEZZOMEDIA
                6 -> MOBON
                7 -> UNITY_ADS
                8 -> MINTEGRAL
                9 -> FAN_NATIVE_BANNER
                10 -> ADFIT
                11 -> APPNEXT
                12 -> CRITEO
                13 -> ADCOLONY
                14 -> VUNGLE
                15 -> APPLOVIN
                16 -> FYBER
                17 -> TAPJOY
                18 -> PANGLE
                19 -> GAM
                20 -> COUPANG
                22 -> NAM
                23 -> APPLOVIN_MAX_DYNAMIC_BID
                24 -> ADOP
                25 -> MobWith
                1000 -> HOUSE
                else -> UNKNOWN
            }
        }

        fun fromName(name: String): APsspNetworkUnit {
            return when (name) {
                IGAW.name -> IGAW
                ADMOB.name -> ADMOB
                FAN.name -> FAN
                MOPUB.name -> MOPUB
                CAULY.name -> CAULY
                MEZZOMEDIA.name -> MEZZOMEDIA
                MOBON.name -> MOBON
                UNITY_ADS.name -> UNITY_ADS
                MINTEGRAL.name -> MINTEGRAL
                FAN_NATIVE_BANNER.name -> FAN_NATIVE_BANNER
                ADFIT.name -> ADFIT
                APPNEXT.name -> APPNEXT
                CRITEO.name -> CRITEO
                ADCOLONY.name -> ADCOLONY
                VUNGLE.name -> VUNGLE
                APPLOVIN.name -> APPLOVIN
                FYBER.name -> FYBER
                TAPJOY.name -> TAPJOY
                PANGLE.name -> PANGLE
                GAM.name -> GAM
                COUPANG.name -> COUPANG
                NAM.name -> NAM
                APPLOVIN_MAX_DYNAMIC_BID.name -> APPLOVIN_MAX_DYNAMIC_BID
                ADOP.name -> ADOP
                MobWith.name -> MobWith
                HOUSE.name -> HOUSE
                else -> UNKNOWN
            }
        }
    }
}