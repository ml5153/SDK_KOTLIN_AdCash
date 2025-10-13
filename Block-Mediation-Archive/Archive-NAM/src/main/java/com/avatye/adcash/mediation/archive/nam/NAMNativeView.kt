package com.avatye.adcash.mediation.archive.nam

import android.content.Context

/**
 * reflection 객체
 */
object NAMNativeView {
    @JvmStatic
    fun getLayoutId(context: Context): Int =
        R.layout.acb_adcash_archive_layout_native_nam_dynamic

    @JvmStatic
    fun getViewId(context: Context): Int =
        R.id.adcash_archive_native_view_nam_container
}