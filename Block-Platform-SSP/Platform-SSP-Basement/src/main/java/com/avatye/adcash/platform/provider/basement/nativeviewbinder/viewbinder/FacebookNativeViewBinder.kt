package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class FacebookNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId

    val titleId = builder.titleId
    val bodyId = builder.bodyId
    val mediaViewId = builder.mediaViewId
    val adIconViewId = builder.adIconViewId
    val callToActionId = builder.callToActionId
    val adChoiceViewId = builder.adChoiceViewId
    val sponsoredViewId = builder.sponsoredViewId
    val socialContextViewId = builder.socialContextViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var titleId: Int? = null
            private set

        internal var bodyId: Int? = null
            private set

        internal var mediaViewId: Int? = null
            private set

        internal var adIconViewId: Int? = null
            private set

        internal var callToActionId: Int? = null
            private set

        internal var adChoiceViewId: Int? = null
            private set

        internal var sponsoredViewId: Int? = null
            private set

        internal var socialContextViewId: Int? = null
            private set

        fun setTitleId(viewId: Int) = apply {
            this.titleId = viewId
        }

        fun setBodyId(viewId: Int) = apply {
            this.bodyId = viewId
        }

        fun setMediaViewId(viewId: Int) = apply {
            this.mediaViewId = viewId
        }

        fun setAdIconViewId(viewId: Int) = apply {
            this.adIconViewId = viewId
        }

        fun setCallToActionId(viewId: Int) = apply {
            this.callToActionId = viewId
        }

        fun setAdChoiceViewId(viewId: Int) = apply {
            this.adChoiceViewId = viewId
        }

        fun setSponsoredViewId(viewId: Int) = apply {
            this.sponsoredViewId = viewId
        }

        fun setSocialContextViewId(viewId: Int) = apply {
            this.socialContextViewId = viewId
        }

        fun build(): FacebookNativeViewBinder {
            return FacebookNativeViewBinder(this)
        }
    }
}