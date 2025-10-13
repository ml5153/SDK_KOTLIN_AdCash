package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class PangleNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId

    val titleViewId = builder.titleViewId
    val descriptionViewId = builder.descriptionViewId
    val iconViewId = builder.iconViewId
    val dislikeViewId = builder.dislikeViewId
    val creativeButtonViewId = builder.creativeButtonViewId
    val logoViewId = builder.logoViewId
    val mediaViewId = builder.mediaViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var titleViewId: Int? = null
            private set

        internal var descriptionViewId: Int? = null
            private set

        internal var iconViewId: Int? = null
            private set

        internal var dislikeViewId: Int? = null
            private set

        internal var creativeButtonViewId: Int? = null
            private set

        internal var logoViewId: Int? = null
            private set

        internal var mediaViewId: Int? = null
            private set

        fun setTitleViewId(viewId: Int) = apply {
            this.titleViewId = viewId
        }

        fun setDescriptionViewId(viewId: Int) = apply {
            this.descriptionViewId = viewId
        }

        fun setIconViewId(viewId: Int) = apply {
            this.iconViewId = viewId
        }

        fun setDislikeViewId(viewId: Int) = apply {
            this.dislikeViewId = viewId
        }

        fun setCreativeButtonViewId(viewId: Int) = apply {
            this.creativeButtonViewId = viewId
        }

        fun setLogoViewId(viewId: Int) = apply {
            this.logoViewId = viewId
        }

        fun setMediaViewId(viewId: Int) = apply {
            this.mediaViewId = viewId
        }

        fun build(): PangleNativeViewBinder {
            return PangleNativeViewBinder(builder = this)
        }
    }

}