package com.bumoyu.downloader

import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SetInterval(private val timeMillis: Long) : CoroutineScope {
    companion object {
        const val TAG = "SetInterval"
    }

    private var job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    fun cancel() {
        Log.d(TAG, "cancel")
        job.cancel()
    }

    fun execute(handler: () -> Unit) = launch {
        while (true) {
            delay(timeMillis)
            handler()
        }
    }

}