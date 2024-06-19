package com.noatnoat.todoapp.ad

import android.app.Activity
import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.noatnoat.todoapp.ad.AdConfig
import java.util.Date

class OpenAdManager {
    private var appOpenAd: AppOpenAd? = null
    private var isLoadingAd = false
    var isShowingAd = false
    private var loadTime: Long = 0

    fun loadAd(context: Context) {
        if (isLoadingAd || isAdAvailable()) {
            return
        }

        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context, AdConfig.OPEN_AD_ID, request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            object : AppOpenAd.AppOpenAdLoadCallback() {

                @OptIn(UnstableApi::class)
                override fun onAdLoaded(ad: AppOpenAd) {
                    androidx.media3.common.util.Log.d("MusicApplication", "Ad loaded successfully")
                    appOpenAd = ad
                    isLoadingAd = false
                    loadTime = Date().time

                    if (!isAdAvailable()) {
                        androidx.media3.common.util.Log.d("MusicApplication", "Ad expired after loading")
                    }
                }

                @OptIn(UnstableApi::class)
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    androidx.media3.common.util.Log.d("MusicApplication", "Ad failed to load: ${loadAdError.message}")
                    isLoadingAd = false
                }
            })
    }

    @OptIn(UnstableApi::class)
    fun showAdIfAvailable(activity: Activity) {
        loadAd(activity)
        androidx.media3.common.util.Log.d("MusicApplication", "Trying to show ad")
        if (isLoadingAd) {
            androidx.media3.common.util.Log.d("MusicApplication", "Ad is still loading")
            return
        }

        appOpenAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() {
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
            }

            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                appOpenAd = null
                isShowingAd = false
                loadAd(activity)
            }

            override fun onAdShowedFullScreenContent() {
                isShowingAd = true
            }
        }

        if (appOpenAd != null) {
            appOpenAd?.show(activity)
            androidx.media3.common.util.Log.d("MusicApplication", "Ad shown successfully")
        } else {
            androidx.media3.common.util.Log.d("MusicApplication", "Ad is null, cannot show ad")

        }
    }

    private fun isAdAvailable(): Boolean {
        return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)
    }

    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference: Long = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }
}