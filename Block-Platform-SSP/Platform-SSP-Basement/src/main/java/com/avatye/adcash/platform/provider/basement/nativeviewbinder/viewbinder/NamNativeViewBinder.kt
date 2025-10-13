package com.avatye.adcash.platform.provider.basement.nativeviewbinder.viewbinder

class NamNativeViewBinder private constructor(private val builder: Builder) {

    val nativeAdLayoutId: Int = builder.nativeAdLayoutId
    val nativeAdViewId: Int = builder.nativeAdViewId

    class Builder(val nativeAdLayoutId: Int, val nativeAdViewId: Int) {
        fun build(): NamNativeViewBinder {
            return NamNativeViewBinder(this)
        }
    }
}