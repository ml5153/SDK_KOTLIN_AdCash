package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class AdMobNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId
    val mediaViewId = builder.mediaViewId
    val headlineViewId = builder.headlineViewId
    val bodyViewId = builder.bodyViewId
    val callToActionId = builder.callToActionId
    val iconViewId = builder.iconViewId
    val priceViewId = builder.priceViewId
    val starRatingViewId = builder.starRatingViewId
    val storeViewId = builder.storeViewId
    val advertiserViewId = builder.advertiserViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {

        internal var mediaViewId: Int? = null
            private set

        internal var headlineViewId: Int? = null
            private set

        internal var bodyViewId: Int? = null
            private set

        internal var callToActionId: Int? = null
            private set

        internal var iconViewId: Int? = null
            private set

        internal var priceViewId: Int? = null
            private set

        internal var starRatingViewId: Int? = null
            private set

        internal var storeViewId: Int? = null
            private set

        internal var advertiserViewId: Int? = null
            private set

        fun setMediaViewId(viewId: Int) = apply {
            this.mediaViewId = viewId
        }

        fun setHeadlineViewId(viewId: Int) = apply {
            this.headlineViewId = viewId
        }

        fun setBodyViewId(viewId: Int) = apply {
            this.bodyViewId = viewId
        }

        fun setCallToActionId(viewId: Int) = apply {
            this.callToActionId = viewId
        }

        fun setIconViewId(viewId: Int) = apply {
            this.iconViewId = viewId
        }

        fun setPriceViewId(viewId: Int) = apply {
            this.priceViewId = viewId
        }

        fun setStarRatingViewId(viewId: Int) = apply {
            this.starRatingViewId = viewId
        }

        fun setStoreViewId(viewId: Int) = apply {
            this.storeViewId = viewId
        }

        fun setAdvertiserViewId(viewId: Int) = apply {
            this.advertiserViewId = viewId
        }

        fun build(): AdMobNativeViewBinder {
            return AdMobNativeViewBinder(this)
        }
    }
}