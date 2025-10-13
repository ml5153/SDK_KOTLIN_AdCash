package com.avatye.adcash.platform.provider.basement.nativeviewbinder

class MediationNativeViewBinder private constructor(private val builder: Builder) {

    val adfitNativeViewBinder = builder.adfitNativeViewBinder
    val admobNativeViewBinder = builder.admobNativeViewBinder
    val adPopcornNativeViewBinder = builder.adPopcornNativeViewBinder
    val appLovinMaxNativeViewBinder = builder.appLovinMaxNativeViewBinder
    val bizBoardNativeViewBinder = builder.bizBoardNativeViewBinder
    val facebookNativeViewBinder = builder.facebookNativeViewBinder
    val gamNativeViewBinder = builder.gamNativeViewBinder
    val mobonNativeViewBinder = builder.mobonNativeViewBinder
    val mobwithNativeViewBinder = builder.mobwithNativeViewBinder
    val namNativeViewBinder = builder.namNativeViewBinder
    val pangleNativeViewBinder = builder.pangleNativeViewBinder

    class Builder() {
        internal var adfitNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdFitNativeViewBinder? = null
            private set

        internal var admobNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdMobNativeViewBinder? = null
            private set

        internal var adPopcornNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdPopcornNativeViewBinder? = null
            private set

        internal var appLovinMaxNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AppLovinMaxNativeViewBinder? = null
            private set

        internal var bizBoardNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.BizBoardNativeViewBinder? = null
            private set

        internal var facebookNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.FacebookNativeViewBinder? = null
            private set

        internal var gamNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.GAMNativeViewBinder? = null
            private set

        internal var mobonNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobonNativeViewBinder? = null
            private set

        internal var mobwithNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobwithNativeViewBinder? = null
            private set


        internal var namNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.NamNativeViewBinder? = null
            private set

        internal var pangleNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.PangleNativeViewBinder? = null
            private set

        internal var admixerNativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdmixerNativeViewBinder? = null
            private set

        fun setAdFitNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdFitNativeViewBinder) = apply {
            this.adfitNativeViewBinder = nativeViewBinder
        }

        fun setBizBoardNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.BizBoardNativeViewBinder) = apply {
            this.bizBoardNativeViewBinder = nativeViewBinder
        }

        fun setAdmobNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdMobNativeViewBinder) = apply {
            this.admobNativeViewBinder = nativeViewBinder
        }

        fun setAdPopcornNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdPopcornNativeViewBinder) = apply {
            this.adPopcornNativeViewBinder = nativeViewBinder
        }

        fun setAppLovinMaxNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AppLovinMaxNativeViewBinder) = apply {
            this.appLovinMaxNativeViewBinder = nativeViewBinder
        }

        fun setFacebookNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.FacebookNativeViewBinder) = apply {
            this.facebookNativeViewBinder = nativeViewBinder
        }

        fun setGAMNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.GAMNativeViewBinder) = apply {
            this.gamNativeViewBinder = nativeViewBinder
        }

        fun setMobonNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobonNativeViewBinder) = apply {
            this.mobonNativeViewBinder = nativeViewBinder
        }

        fun setMobwithNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.MobwithNativeViewBinder) = apply {
            this.mobwithNativeViewBinder = nativeViewBinder
        }

        fun setNamNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.NamNativeViewBinder) = apply {
            this.namNativeViewBinder = nativeViewBinder
        }

        fun setPangleNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.PangleNativeViewBinder) = apply {
            this.pangleNativeViewBinder = nativeViewBinder
        }

        fun setAdmixerNativeViewBinder(nativeViewBinder: com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder.AdmixerNativeViewBinder) = apply {
            this.admixerNativeViewBinder = nativeViewBinder
        }

        fun build(): MediationNativeViewBinder {
            return MediationNativeViewBinder(this)
        }
    }
}