package com.avatye.adcash.platform.provider.basement.nativeviewbinder

interface IMediationConnect {

    enum class Size { W320XH50, W320XH100, W300XH250, W320XH480, DYNAMIC }
    enum class Mediation(private val mediationName: String) {
        ADFIT("adfit"),
        ADMOB("admob"),
        ADPOPCORN("adpopcorn"),
        APPLOVIN("applovin"),
        CAULY("cauly"),
        FAN("fan"),
        FYBER("fyber"),
        MOBON("mobon"),
        MOBWITH("mobwith"),
        NAM("nam"),
        PANGLE("pangle"),
        UNITY("unity"),
        VUNGLE("vungle");

        companion object {
            val Mediation.connectorName: String
                get() {
                    return "com.avatye.adcash.mediation.archive.%s.Connector".format(this.mediationName)
                }
        }
    }

    val mediation: Mediation

    // Native Banner
    fun requestNativeViewBinder(size: Size): MediationNativeViewBinder?

}