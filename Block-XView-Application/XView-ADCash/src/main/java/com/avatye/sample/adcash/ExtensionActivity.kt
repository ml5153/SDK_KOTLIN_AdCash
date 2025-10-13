package com.avatye.sample.adcash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

fun Activity.launch(intent: Intent, transition: Pair<Int, Int>? = null, close: Boolean = false, options: Bundle? = null) {
    startActivity(intent, options)
    transition?.let {
        overridePendingTransition(it.first, it.second)
    } ?: overridePendingTransition(0, 0)
    if (close) {
        finish()
    }
}

fun <T : Parcelable> Activity.extraParcel(key: String): T? {
    return intent?.extras?.getParcelable(key)
}