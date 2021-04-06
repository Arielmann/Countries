package com.example.matrixassignment.crossapplication.utils

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


/**
 * Observing a [LiveData] object but ignoring the first invoked callback.
 * This is useful when we want to prevent the Livedata object from invoking
 * the observer upon a new observation request.
 *
 * CAUTION: A Livedata object containing a null value will NOT invoke
 * the observer upon a new observation request. Therefore under this condition
 * should not rely on this method.
 */
fun <T> LiveData<T?>.observeAndIgnoreInitialNotification(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {

    var isFirstNotification = true

    observe(lifecycleOwner, object : Observer<T?> {
        override fun onChanged(t: T?) {
            if (isFirstNotification) {
                isFirstNotification = false
                return
            }
            observer.onChanged(t)
        }
    })
}



