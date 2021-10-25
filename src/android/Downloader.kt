package com.bumoyu.downloader

import android.util.Log
import com.tonyodev.fetch2.*
import com.tonyodev.fetch2core.Func
import org.apache.cordova.CallbackContext
import org.apache.cordova.CordovaInterface
import org.apache.cordova.CordovaPlugin
import org.apache.cordova.CordovaWebView
import org.json.JSONArray
import org.json.JSONException
import com.google.gson.GsonBuilder
import com.tonyodev.fetch2.exception.FetchException
import org.apache.cordova.PluginResult
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import org.json.JSONObject

class Downloader : CordovaPlugin() {
    private val TAG = "Downloader"
    private lateinit var context: Context
    private var callbackContext: CallbackContext? = null
    private val fetch by lazy {
        Fetch.Impl.getInstance(
            FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(3)
                .build()
        );
    }

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                updateProgress(
                    intent.getIntExtra("progress", -1),
                    intent.getIntExtra("id", -1),
                    intent.getIntExtra("downloadedBytesPerSecond",0)
                )
            }
        }
    }

    override fun initialize(cordova: CordovaInterface?, webView: CordovaWebView?) {
        super.initialize(cordova, webView)
        context = cordova?.activity?.applicationContext!!
        context.registerReceiver(receiver, IntentFilter(DownloaderListener.PROGRESS));
    }

    private fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    private fun registerReceiver() {
        context.registerReceiver(receiver, IntentFilter(DownloaderListener.PROGRESS));
    }

    override fun onDestroy() {
        this.unregisterReceiver()
    }

    override fun onPause(multitasking: Boolean) {
        this.unregisterReceiver()
    }

    override fun onResume(multitasking: Boolean) {
        super.onResume(multitasking)
        this.unregisterReceiver()
        this.registerReceiver()
    }

    @Throws(JSONException::class)
    override fun execute(
        action: String?,
        args: JSONArray?,
        callbackContext: CallbackContext?
    ): Boolean {
        Log.v(TAG, "Executing action: $action")
        this.callbackContext = callbackContext
        ///super.execute(action, args, callbackContext)
        when (action) {
            "download" -> {
                val url = args?.getString(0).toString().trimIndent()
                val file = args?.getString(1).toString().trimIndent()
                download(url, file, callbackContext)
                val pluginResult = PluginResult(PluginResult.Status.OK, -1)
                pluginResult.keepCallback = true
                callbackContext?.sendPluginResult(pluginResult)
                return true
            }
            "pause" -> {
                val id = args?.getInt(0)!!
                pause(id, callbackContext)
                return true
            }
            "resume" -> {
                val id = args?.getInt(0)!!
                resume(id, callbackContext)
                return true
            }
            "getDownloads" -> {
                getDownloads(callbackContext)
                return true
            }
            "getDownloadsWithStatus" -> {
                ///getDownloadsWithStatus(callbackContext)
            }
        }
        return false
    }

    private fun download(url: String, file: String, callbackContext: CallbackContext?): Int {
        val request = Request(url, file)
        request.priority = Priority.HIGH
        request.networkType = NetworkType.ALL

        fetch.enqueue(request, object : Func<Request> {
            override fun call(result: Request) {
                callbackContext?.success(result.id)
            }
        }, object : Func<Error> {
            override fun call(result: Error) {
                callbackContext?.error(result.throwable?.message)
            }
        })
        return request.id
    }

    private fun pause(id: Int, callbackContext: CallbackContext?): Int {
        try {
            fetch.pause(id)
        } catch (e: FetchException) {
            callbackContext?.error(e.message)
        }
        return id
    }

    private fun resume(id: Int, callbackContext: CallbackContext?): Int {
        try {
            fetch.resume(id)
        } catch (e: FetchException) {
            callbackContext?.error(e.message)
        }
        return id
    }

    private fun getDownloads(callbackContext: CallbackContext?) {
        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(result: List<Download>) {
                val json = GsonBuilder().disableHtmlEscaping().create().toJson(result)
                val jsonArray = JSONArray(json)
                callbackContext?.success(jsonArray)
            }
        })
    }

    //intent.putExtra("downloadedBytesPerSecond",downloadedBytesPerSecond)
    private fun updateProgress(id: Int, progress: Int, downloadedBytesPerSecond:Int) {
        if (callbackContext != null) {
            val jsonObject = JSONObject()
            jsonObject.put("id", id)
            jsonObject.put("progress", progress)
            jsonObject.put("downloadedBytesPerSecond",downloadedBytesPerSecond)
            val result = PluginResult(PluginResult.Status.OK, jsonObject)
            result.keepCallback = true
            callbackContext?.sendPluginResult(result)
        }
        //webView.postMessage("networkconnection", type)
    }
}