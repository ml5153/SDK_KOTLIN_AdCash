package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class MobonNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId
    val mainImageViewId = builder.mainImageViewId
    val logoImageViewId = builder.logoImageViewId
    val titleViewId = builder.titleViewId
    val descViewId = builder.descViewId
    val priceViewId = builder.priceViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var mainImageViewId: Int? = null
            private set

        internal var logoImageViewId: Int? = null
            private set

        internal var titleViewId: Int? = null
            private set

        internal var descViewId: Int? = null
            private set

        internal var priceViewId: Int? = null
            private set

        fun setMainImageViewId(viewId: Int) = apply {
            this.mainImageViewId = viewId
        }

        fun setLogoImageViewId(viewId: Int) = apply {
            this.logoImageViewId = viewId
        }

        fun setTitleViewId(viewId: Int) = apply {
            this.titleViewId = viewId
        }

        fun setDescViewId(viewId: Int) = apply {
            this.descViewId = viewId
        }

        fun setPriceViewId(viewId: Int) = apply {
            this.priceViewId = viewId
        }

        fun build(): MobonNativeViewBinder {
            return MobonNativeViewBinder(builder = this)
        }
    }
}