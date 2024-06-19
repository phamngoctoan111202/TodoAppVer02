package com.noatnoat.todoapp.ad

import android.app.Activity

class AdManager(private val activity: Activity, private val listener: RewardAdListener) {
    private val rewardedAdManager = RewardedAdManager(activity, listener)

    fun loadRewardedAd() {
        rewardedAdManager.loadRewardedAd(AdConfig.REWARDED_AD_ID)
    }

    fun showRewardedAd() {
        rewardedAdManager.showRewardedAd()
    }

    fun isAdViewed(): Boolean {
        return rewardedAdManager.adViewed
    }
}