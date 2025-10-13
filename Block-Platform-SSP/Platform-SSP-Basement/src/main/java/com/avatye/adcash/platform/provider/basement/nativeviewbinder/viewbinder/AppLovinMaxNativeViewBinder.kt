package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class AppLovinMaxNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId
    val titleViewId = builder.titleViewId
    val bodyViewId = builder.bodyViewId
    val advertiserViewId = builder.advertiserViewId
    val iconViewId = builder.iconViewId
    val mediaViewId = builder.mediaViewId
    val optionViewId = builder.optionViewId
    val ctaViewId = builder.ctaViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var titleViewId: Int? = null
            private set

        internal var bodyViewId: Int? = null
            private set

        internal var advertiserViewId: Int? = null
            private set

        internal var iconViewId: Int? = null
            private set

        internal var mediaViewId: Int? = null
            private set

        internal var optionViewId: Int? = null
            private set

        internal var ctaViewId: Int? = null
            private set

        fun setTitleViewId(viewId: Int) = apply {
            this.titleViewId = viewId
        }

        fun setBodyViewId(viewId: Int) = apply {
            this.bodyViewId = viewId
        }

        fun setAdvertiserViewId(viewId: Int) = apply {
            this.advertiserViewId = viewId
        }

        fun setIconViewId(viewId: Int) = apply {
            this.iconViewId = viewId
        }

        fun setMediaViewId(viewId: Int) = apply {
            this.mediaViewId = viewId
        }

        fun setOptionViewId(viewId: Int) = apply {
            this.optionViewId = viewId
        }

        fun setCtaViewId(viewId: Int) = apply {
            this.ctaViewId = viewId
        }

        fun build(): AppLovinMaxNativeViewBinder {
            return AppLovinMaxNativeViewBinder(builder = this)
        }
    }
}