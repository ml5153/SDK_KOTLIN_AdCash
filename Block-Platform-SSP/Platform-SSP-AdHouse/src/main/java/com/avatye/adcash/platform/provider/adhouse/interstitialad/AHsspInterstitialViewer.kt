package com.avatye.adcash.platform.provider.adhouse.interstitialad

import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import com.avatye.adcash.platform.provider.adhouse.R
import com.avatye.adcash.platform.provider.adhouse.Settings.printout
import com.avatye.adcash.platform.provider.adhouse.databinding.AcbAdcashSspHouseViewerInterstitialBinding

internal class AHsspInterstitialViewer private constructor(
    private val activity: Activity,
    adView: View,
    private val actionDismiss: DismissActionCallback
) {

    interface DismissActionCallback {
        fun onDismiss()
    }

    internal companion object {
        fun create(
            activity: Activity,
            adView: View,
            actionDismiss: DismissActionCallback
        ) = AHsspInterstitialViewer(activity, adView, actionDismiss)
    }

    private val sourceName = "AhsspInterstitialViewer"
    private val vb by lazy {
        AcbAdcashSspHouseViewerInterstitialBinding.inflate(LayoutInflater.from(activity))
    }
    private val builder: AlertDialog by lazy {
        AlertDialog.Builder(activity, R.style.ADCashSSPHouseContainer).setView(vb.root).create()
    }

    init {
        vb.adsviseContainer.removeAllViews()
        vb.adsviseContainer.addView(adView)
        vb.adsviseContainerClose.setOnClickListener {
            this.dismiss()
        }
    }

    fun show(cancelable: Boolean, blockCallback: (completed: Boolean) -> Unit) {
        try {
            builder.setCancelable(cancelable)
            builder.show()
            blockCallback(true)
        } catch (e: Exception) {
            actionDismiss.onDismiss()
            printout.error(sourceName = sourceName, throwable = e) {
                "show { cancelable: $cancelable }"
            }
            blockCallback(false)
        }
    }

    fun dismiss() {
        actionDismiss.onDismiss()
        builder.dismiss()
    }

    fun isAppeared(): Boolean = builder.isShowing

}