package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class AdmixerNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId

    val iconImageId = builder.iconImageId
    val ctaId = builder.ctaId
    val titleId = builder.titleId
    val descriptionId = builder.descriptionId
    val advertiserId = builder.advertiserId
    val mainViewId = builder.mainViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var iconImageId: Int? = null
            private set
        internal var ctaId: Int? = null
            private set
        internal var titleId: Int? = null
            private set
        internal var descriptionId: Int? = null
            private set
        internal var advertiserId: Int? = null
            private set
        internal var mainViewId: Int? = null
            private set

        fun setIconImageId(viewId: Int) = apply {
            this.iconImageId = viewId
        }

        fun setCtaId(viewId: Int) = apply {
            this.ctaId = viewId
        }

        fun setTitleId(viewId: Int) = apply {
            this.titleId = viewId
        }

        fun setDescriptionId(viewId: Int) = apply {
            this.descriptionId = viewId
        }

        fun setAdvertiserId(viewId: Int) = apply {
            this.advertiserId = viewId
        }

        fun setMainViewId(viewId: Int) = apply {
            this.mainViewId = viewId
        }

        fun build(): AdmixerNativeViewBinder {
            return AdmixerNativeViewBinder(builder = this)
        }
    }

}