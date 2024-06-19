package com.noatnoat.todoapp.ad

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.noatnoat.todoapp.R

class NativeAdManager(private val activity: Activity) {
    var adLoader: AdLoader? = null
    var nativeAd: NativeAd? = null
    var adListener: AdListener? = null

    fun loadAd(adContainer: ViewGroup) {
        adListener = object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Handle the failure by logging, altering the UI, and so on.
            }
        }

        adLoader = AdLoader.Builder(activity, AdConfig.NATIVE_AD_ID)
            .forNativeAd { ad: NativeAd ->
                // On native ad loaded
                nativeAd = ad
                showAd(adContainer)
            }
            .withAdListener(adListener as AdListener)
            .withNativeAdOptions(
                NativeAdOptions.Builder()
                    .build())
            .build()

        adLoader?.loadAd(AdRequest.Builder().build())
    }

    private fun showAd(adContainer: ViewGroup) {
        // Ensure ad is loaded
        if (nativeAd != null) {
            // Inflate the Native Express ad view.
            val inflater = LayoutInflater.from(activity)
            val adView = inflater.inflate(R.layout.native_ad_item_layout, null)

            // Populate the native ad view with the native ad data.
            // Note: be sure to read the native ad policies and guidelines
            // to understand what each field should contain.
            val headlineView = adView.findViewById<TextView>(R.id.ad_headline)
            headlineView.text = nativeAd?.headline

            val bodyView = adView.findViewById<TextView>(R.id.ad_body)
            bodyView.text = nativeAd?.body

            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            adContainer.removeAllViews()
            adContainer.addView(adView)
        } else {
            Log.d("NativeAdManager", "Ad was not ready to be shown.")
        }
    }
}