package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class AdPopcornNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId

    val titleViewId = builder.titleViewId
    val descViewId = builder.descViewId
    val mainImageViewId = builder.mainImageViewId
    val iconImageViewId = builder.iconImageViewId
    val callToActionId = builder.callToActionId
    val privacyIconVisibility = builder.privacyIconVisibility
    val privacyIconWidth = builder.privacyIconWidth
    val privacyIconHeight = builder.privacyIconHeight
    val privacyIconPosition = builder.privacyIconPosition
    val privacyIconLeftRightMargin = builder.privacyIconLeftRightMargin
    val privacyIconTopBottomMargin = builder.privacyIconTopBottomMargin

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var titleViewId: Int? = null
            private set

        internal var descViewId: Int? = null
            private set

        internal var mainImageViewId: Int? = null
            private set

        internal var iconImageViewId: Int? = null
            private set

        internal var callToActionId: Int? = null
            private set

        internal var privacyIconVisibility: Boolean = true
            private set

        internal var privacyIconWidth: Int? = null
            private set

        internal var privacyIconHeight: Int? = null
            private set

        internal var privacyIconPosition: Int? = null
            private set

        internal var privacyIconLeftRightMargin: Int? = null
            private set

        internal var privacyIconTopBottomMargin: Int? = null
            private set

        fun setTitleViewId(viewId: Int) = apply {
            this.titleViewId = viewId
        }

        fun setDescViewId(viewId: Int) = apply {
            this.descViewId = viewId
        }

        fun setMainImageViewId(viewId: Int) = apply {
            this.mainImageViewId = viewId
        }

        fun setIconImageViewId(viewId: Int) = apply {
            this.iconImageViewId = viewId
        }

        fun setCallToActionId(viewId: Int) = apply {
            this.callToActionId = viewId
        }

        fun setPrivacyIconVisibility(visibility: Boolean) = apply {
            this.privacyIconVisibility = visibility
        }

        fun setPrivacyIconWidth(width: Int) = apply {
            this.privacyIconWidth = width
        }

        fun setPrivacyIconHeight(height: Int) = apply {
            this.privacyIconHeight = height
        }

        fun setPrivacyIconPosition(position: Int) = apply {
            this.privacyIconPosition = position
        }

        fun setPrivacyIconLeftRightMargin(margin: Int) = apply {
            this.privacyIconLeftRightMargin = margin
        }

        fun setPrivacyIconTopBottomMargin(margin: Int) = apply {
            this.privacyIconTopBottomMargin = margin
        }

        fun build(): AdPopcornNativeViewBinder {
            return AdPopcornNativeViewBinder(this)
        }
    }
}