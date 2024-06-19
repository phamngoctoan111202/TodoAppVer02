package com.noatnoat.todoapp.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.noatnoat.todoapp.ad.RewardAdListener

class RewardedAdManager(private val context: Context, private val listener: RewardAdListener) {
    private var mRewardedAd: RewardedAd? = null
    var adViewed = false

    fun loadRewardedAd(adId: String) {
        val adRequest = AdRequest.Builder().build()

        RewardedAd.load(context, adId, adRequest, object : RewardedAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d("RewardedAdManager", "Failed to load rewarded ad: ${adError.message}")
                mRewardedAd = null
            }

            override fun onAdLoaded(rewardedAd: RewardedAd) {
                Log.d("RewardedAdManager", "Rewarded ad was loaded.")
                mRewardedAd = rewardedAd
                adViewed = false
            }
        })
    }

    fun showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd?.fullScreenContentCallback = object: FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("RewardedAdManager", "Ad was dismissed.")
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.d("RewardedAdManager", "Ad failed to show with error: ${adError.message}")
                    listener.onAdFailedToShow()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("RewardedAdManager", "Ad was shown successfully.")
                }
            }

            mRewardedAd?.show(context as Activity, OnUserEarnedRewardListener { rewardItem: RewardItem ->
                Log.d("RewardedAdManager", "User earned reward: ${rewardItem.amount}")
                listener.onAdRewarded()
                adViewed = true
            })
        } else {
            Log.d("RewardedAdManager", "The rewarded ad wasn't ready yet.")
        }
    }
}