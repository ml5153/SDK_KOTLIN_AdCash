package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class AdFitNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId
    val titleViewId = builder.titleViewId
    val bodyViewId = builder.bodyViewId
    val callToActionButtonId = builder.callToActionButtonId
    val profileNameViewId = builder.profileNameViewId
    val profileIconViewId = builder.profileIconViewId
    val mediaViewId = builder.mediaViewId
    val useTestMode = builder.useTestMode

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var titleViewId: Int? = null
            private set

        internal var bodyViewId: Int? = null
            private set

        internal var callToActionButtonId: Int? = null
            private set

        internal var profileNameViewId: Int? = null
            private set

        internal var profileIconViewId: Int? = null
            private set

        internal var mediaViewId: Int? = null
            private set

        internal var useTestMode = false
            private set

        fun setTitleViewId(viewId: Int) = apply {
            this.titleViewId = viewId
        }

        fun setBodyViewId(viewId: Int) = apply {
            this.bodyViewId = viewId
        }

        fun setCallToActionButtonId(viewId: Int) = apply {
            this.callToActionButtonId = viewId
        }

        fun setProfileNameViewId(viewId: Int) = apply {
            this.profileNameViewId = viewId
        }

        fun setProfileIconViewId(viewId: Int) = apply {
            this.profileIconViewId = viewId
        }

        fun setMediaViewId(viewId: Int) = apply {
            this.mediaViewId = viewId
        }

        fun setUseTestMode(use: Boolean) = apply {
            this.useTestMode = use
        }

        fun build(): AdFitNativeViewBinder {
            return AdFitNativeViewBinder(this)
        }
    }
}