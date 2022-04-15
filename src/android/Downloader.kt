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
import com.tonyodev.fetch2.Status

class Downloader : CordovaPlugin() {
    private val TAG = "Downloader"
    private lateinit var context: Context
    private var callbackContext: CallbackContext? = null
    private var listenCallbackContext: CallbackContext? = null
    private var serviceIsStarted: Boolean = false
    private val fetch by lazy {
        Fetch.Impl.getInstance(
            FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(1)
                .build()
        )
    }

    private val gsonDisableHtmlEscaping by lazy {
        GsonBuilder().disableHtmlEscaping().create()
    }

    private val gsonBuilder by lazy {
        GsonBuilder().create()
    }

    private val receiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                val event = intent.getStringExtra("event")
                val jsonObject: JSONObject
                if (event == "onTimeoutTasks") {
                    jsonObject = JSONObject("{}")
                    val tasks = intent.getStringExtra("tasks")
                    jsonObject.put("tasks", tasks)
                    ///jsonObject.put("timestap",)
                } else {
                    val download = intent.getParcelableExtra<Download>("Download")
                    val downloadJson = gsonDisableHtmlEscaping.toJson(download)
                    jsonObject = JSONObject(downloadJson)
                }
                jsonObject.put("event", event)
                when (event) {
                    "onDownloadBlockUpdated" -> {
                        val totalBlocks = intent.getIntExtra("totalBlocks", 0)
                        jsonObject.put("totalBlocks", totalBlocks)
                    }
                    "onError" -> {
                        val message = intent.getStringExtra("message")
                        jsonObject.put("message", message)
                    }
                    "onProgress" -> {
                        val progress = intent.getIntExtra("progress", 0)
                        val downloadedBytesPerSecond =
                            intent.getLongExtra("downloadedBytesPerSecond", 0)
                        jsonObject.put("progress", progress)
                        jsonObject.put("downloadedBytesPerSecond", downloadedBytesPerSecond)
                    }
                    "onQueued" -> {
                        val waitingOnNetwork = intent.getBooleanExtra("waitingOnNetwork", false)
                        jsonObject.put("waitingOnNetwork", waitingOnNetwork)
                    }
                    "onStarted" -> {
                        val totalBlocks = intent.getIntExtra("totalBlocks", 0)
                        jsonObject.put("totalBlocks", totalBlocks)
                    }
                }
                sendToJavascript(jsonObject)
            }
        }
    }

    override fun initialize(cordova: CordovaInterface?, webView: CordovaWebView?) {
        super.initialize(cordova, webView)
        context = cordova?.activity?.applicationContext!!
        fetch.addListener(DownloaderListener(context))
        context.registerReceiver(receiver, IntentFilter(DownloaderListener.INTENTACTION))
    }

    private fun unregisterReceiver() {
        context.unregisterReceiver(receiver)
    }

    private fun registerReceiver() {
        context.registerReceiver(receiver, IntentFilter(DownloaderListener.INTENTACTION))
    }

    override fun onDestroy() {
        this.unregisterReceiver()
    }

    override fun onPause(multitasking: Boolean) {
        this.unregisterReceiver()
    }

    override fun onResume(multitasking: Boolean) {
        super.onResume(multitasking)
        ///this.unregisterReceiver()
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
            "stopTimeoutCheck" -> {
                stopTimeoutCheck()
                return true
            }
            "startTimeoutCheck" -> {
                startTimeoutCheck(args, callbackContext)
                return true
            }
            "getTimeoutTasks" -> {
                getTimeoutTasks(callbackContext)
                return true
            }
            "download" -> {
                val url = args?.getString(0).toString().trimIndent()
                val file = args?.getString(1).toString().trimIndent()
                download(url, file, callbackContext)
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
            "listen" -> {
                listenCallbackContext = callbackContext
                val pluginResult = PluginResult(PluginResult.Status.NO_RESULT)
                pluginResult.keepCallback = true
                listenCallbackContext?.sendPluginResult(pluginResult)
                return true
            }
            "delete" -> {
                delete(args, callbackContext)
                return true
            }
            "remove" -> {
                remove(args, callbackContext)
                return true
            }
            "getDownloads" -> {
                getDownloads(callbackContext)
                return true
            }
            "getDownloadsWithStatus" -> {
                val status = args?.get(0).toString()
                getDownloadsWithStatus(Status.valueOf(status), callbackContext)
                return true
            }
        }
        return false
    }

    private fun startTimeoutCheck(args: JSONArray?, callbackContext: CallbackContext?) {
        if (serviceIsStarted) {
            callbackContext?.success(1)
            return
        }

        val interval = args?.getLong(0)!!
        val time = args.getInt(1)
        if (interval.toInt() == 0) {
            callbackContext?.error("interval can not eq 0")
        }
        ProgressMonitorService.start(context, fetch, interval, time)
        serviceIsStarted = true
        callbackContext?.success(1)
    }

    private fun stopTimeoutCheck() {
        ProgressMonitorService.stop(context)
        serviceIsStarted = false
        callbackContext?.success(1)
    }

    private fun getTimeoutTasks(callbackContext: CallbackContext?) {
        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(results: List<Download>) {
                val tasks = ProgressMonitorService.getTimeoutTasks()
                results.map {
                    val download = it
                    tasks?.downloads?.find { download.id == it.id }?.setLatestDownload(download)
                    //tasks?.find { download.id == it.id }?.setLatestDownload(download)
                }
                val res = tasks?.downloads?.filter { !it.progressISChanged() }
                val json = gsonDisableHtmlEscaping.toJson(res)
                ///val jsonArray = JSONArray(json)
                callbackContext?.success(json)
            }
        })
    }

    private fun getDownloadsWithStatus(status: Status, callbackContext: CallbackContext?) {
        fetch.getDownloadsWithStatus(status, object : Func<List<Download>> {
            override fun call(result: List<Download>) {
                val json = gsonDisableHtmlEscaping.toJson(result)
                val jsonArray = JSONArray(json)
                callbackContext?.success(jsonArray)
            }
        })
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
            callbackContext?.success(id)
        } catch (e: FetchException) {
            callbackContext?.error(e.message)
        }
        return id
    }

    private fun remove(args: JSONArray?, callbackContext: CallbackContext?): JSONArray? {
        try {
            val ids =
                gsonBuilder.fromJson(args.toString(), Array<Int>::class.java).toList()
            fetch.remove(ids)
            callbackContext?.success(args)
        } catch (e: FetchException) {
            callbackContext?.error(e.message)
        }
        return args
    }

    private fun delete(args: JSONArray?, callbackContext: CallbackContext?): JSONArray? {
        try {
            val ids =
                gsonBuilder.fromJson(args.toString(), Array<Int>::class.java).toList()
            fetch.delete(ids)
            callbackContext?.success(args)
        } catch (e: FetchException) {
            callbackContext?.error(e.message)
        }
        return args
    }

    private fun resume(id: Int, callbackContext: CallbackContext?): Int {
        try {
            fetch.resume(id)
            callbackContext?.success(id)
        } catch (e: FetchException) {
            callbackContext?.error(e.message)
        }
        return id
    }

    private fun getDownloads(callbackContext: CallbackContext?) {
        fetch.getDownloads(object : Func<List<Download>> {
            override fun call(result: List<Download>) {
                val json = gsonDisableHtmlEscaping.toJson(result)
                val jsonArray = JSONArray(json)
                callbackContext?.success(jsonArray)
            }
        })
    }

    private fun sendToJavascript(jsonObject: JSONObject) {
        val result = PluginResult(PluginResult.Status.OK, jsonObject)
        result.keepCallback = true
        listenCallbackContext?.sendPluginResult(result)
    }
}