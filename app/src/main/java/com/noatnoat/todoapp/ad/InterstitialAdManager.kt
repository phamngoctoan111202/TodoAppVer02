package com.noatnoat.todoapp.ad

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val activity: Activity) {
    private var lastAdShowTime: Long = 0
    private var mInterstitialAd: InterstitialAd? = null
    var isAdLoaded = false // New flag to check if ad is loaded

    fun loadAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            activity,
            AdConfig.INTERSTITIAL_AD_ID,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                    Log.d("InterstitialAdManager", "Interstitial ad loaded successfully.")
                    isAdLoaded = true
                    mInterstitialAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            lastAdShowTime = System.currentTimeMillis()
                        }
                    }

                    mInterstitialAd?.let {
                        it.show(activity)
                        Log.d("InterstitialAdManager", "Interstitial ad shown successfully.")
                    }
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                    Log.d("InterstitialAdManager", "Failed to load interstitial ad: ${adError.message}")
                    isAdLoaded = false // Set flag to false when ad fails to load
                }
            }
        )
    }

    fun showAd() {
        if (isAdLoaded && mInterstitialAd != null) { // Check if ad is loaded before showing
            mInterstitialAd?.let {
                it.show(activity)
                Log.d("InterstitialAdManager", "Interstitial ad shown successfully.")
            }
        } else {
            Log.d("InterstitialAdManager", "Interstitial ad was not ready to be shown.")
        }
    }
}