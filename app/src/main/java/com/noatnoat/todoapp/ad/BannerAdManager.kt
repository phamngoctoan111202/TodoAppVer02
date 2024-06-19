package com.noatnoat.todoapp.ad

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

class BannerAdManager(private val context: Context) {

    fun loadAd(adView: AdView) {

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
    fun showAd() {
        // Not implemented
    }

}