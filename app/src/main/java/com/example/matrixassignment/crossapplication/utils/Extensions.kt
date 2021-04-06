package com.example.matrixassignment.crossapplication.utils

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


private const val TAG: String = "MatrixAExtensions"

fun <T> LiveData<T?>.observeAndIgnoreInitialNotification(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {

    var isFirstNotification = true

    observe(lifecycleOwner, object : Observer<T?> {
        override fun onChanged(t: T?) {
            Log.d(TAG, "isFirstNotification: $isFirstNotification")
            if (isFirstNotification) {
                isFirstNotification = false
                return
            }
            observer.onChanged(t)
        }
    })
}



