package com.noatnoat.todoapp

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.noatnoat.todoapp.ad.OpenAdManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TodoApplication : Application(), Application.ActivityLifecycleCallbacks, LifecycleObserver {
    private lateinit var appOpenAdManager: OpenAdManager
    private var currentActivity: Activity? = null

    override fun onCreate() {
        super.onCreate()
        appOpenAdManager = OpenAdManager()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        currentActivity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {
        currentActivity?.let {
            appOpenAdManager.showAdIfAvailable(it)
        }
    }

    override fun onActivityStopped(activity: Activity) {}

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        currentActivity?.let {
            appOpenAdManager.showAdIfAvailable(it)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (currentActivity == activity) {
            currentActivity = null
        }
    }
}