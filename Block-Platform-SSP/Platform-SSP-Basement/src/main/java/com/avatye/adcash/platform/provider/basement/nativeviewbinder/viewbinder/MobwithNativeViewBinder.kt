package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class MobwithNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId

    val mediaContainerViewId = builder.mediaContainerViewId
    val imageViewADId = builder.imageViewADId
    val imageViewLogoId = builder.imageViewLogoId
    val textViewTitleId = builder.textViewTitleId
    val textViewDescId = builder.textViewDescId
    val buttonGoId = builder.buttonGoId
    val layoutInfoViewId = builder.layoutInfoViewId
    val imageViewInfoId = builder.imageViewInfoId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        internal var mediaContainerViewId: Int? = null
            private set

        internal var imageViewADId: Int? = null
            private set

        internal var imageViewLogoId: Int? = null
            private set

        internal var textViewTitleId: Int? = null
            private set

        internal var textViewDescId: Int? = null
            private set

        internal var buttonGoId: Int? = null
            private set

        internal var layoutInfoViewId: Int? = null
            private set

        internal var imageViewInfoId: Int? = null
            private set

        fun setMediaContainerViewId(viewId: Int) = apply {
            this.mediaContainerViewId = viewId
        }

        fun setImageViewId(viewId: Int) = apply {
            this.imageViewADId = viewId
        }

        fun setImageViewLogoId(viewId: Int) = apply {
            this.imageViewLogoId = viewId
        }

        fun setTitleViewId(viewId: Int) = apply {
            this.textViewTitleId = viewId
        }

        fun setDescriptionViewId(viewId: Int) = apply {
            this.textViewDescId = viewId
        }

        fun setButtonGoViewId(viewId: Int) = apply {
            this.buttonGoId = viewId
        }

        fun setLayoutInfoViewId(viewId: Int) = apply {
            this.layoutInfoViewId = viewId
        }

        fun setImageViewInfoId(viewId: Int) = apply {
            this.imageViewInfoId = viewId
        }

        fun build(): MobwithNativeViewBinder {
            return MobwithNativeViewBinder(builder = this)
        }
    }

}