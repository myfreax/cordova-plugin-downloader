package com.bumoyu.downloader

import android.content.Intent
import android.os.Parcelable
import android.util.Log
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

class DownloaderListener(private val context: android.content.Context) : FetchListener {

    private fun sendIntent(intent: Intent) {
        try {
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun send(event: String, download: Download) {
        val intent = Intent().apply {
            this.putExtra("event", event)
            this.putExtra("Download", download as Parcelable)
            this.action = INTENTACTION
            this.`package` = context.packageName
        }
        sendIntent(intent)

    }

    companion object {
        const val INTENTACTION = "downloader.listener.action"
    }

    override fun onAdded(download: Download) {
        Log.d("fetchListener", download.progress.toString())
        send("onAdded", download)
    }

    override fun onCancelled(download: Download) {
        Log.d("fetchListener", "onCancelled")
        send("event", download)
    }

    override fun onCompleted(download: Download) {
        Log.d("fetchListener", "onCompleted")
        send("onCompleted", download)
    }

    override fun onDeleted(download: Download) {
        Log.d("fetchListener", "onDeleted")
        send("onDeleted", download)
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
        Log.d("fetchListener", "onDownloadBlockUpdated")
        val intent = Intent().apply {
            this.action = INTENTACTION
            this.`package` = context.packageName
            this.putExtra("event", "onDownloadBlockUpdated")
            this.putExtra("Download", download as Parcelable)
            this.putExtra("totalBlocks", totalBlocks)
        }
        sendIntent(intent)
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Log.d("fetchListener", error.throwable?.message!!)
        val intent = Intent().apply {
            this.action = INTENTACTION
            this.`package` = context.packageName
            this.putExtra("event", "onError")
            this.putExtra("Download", download as Parcelable)
            this.putExtra("message", error.throwable?.message!!)
        }
        sendIntent(intent)
    }

    override fun onPaused(download: Download) {
        Log.d("fetchListener", "onPaused")
        send("onPaused", download)
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        Log.d("onProgress", "onProgress")
        val intent = Intent().apply {
            this.action = INTENTACTION
            this.`package` = context.packageName
            this.putExtra("event", "onProgress")
            this.putExtra("Download", download as Parcelable)
            this.putExtra("progress",download.progress)
            this.putExtra("downloadedBytesPerSecond", downloadedBytesPerSecond)
        }
        sendIntent(intent)
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Log.d("fetchListener", "onQueued")
        val intent = Intent().apply {
            this.action = INTENTACTION
            this.`package` = context.packageName
            this.putExtra("event", "onQueued")
            this.putExtra("Download", download as Parcelable)
            this.putExtra("waitingOnNetwork", waitingOnNetwork)
        }
        sendIntent(intent)
    }

    override fun onRemoved(download: Download) {
        Log.d("fetchListener", "onRemoved")
        send("onRemoved", download)
    }

    override fun onResumed(download: Download) {
        Log.d("fetchListener", "onResumed")
        send("onResumed", download)
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Log.d("fetchListener", "onStarted")
        val intent = Intent().apply {
            this.action = INTENTACTION
            this.`package` = context.packageName
            this.putExtra("event", "onStarted")
            this.putExtra("Download", download as Parcelable)
            this.putExtra("totalBlocks", totalBlocks)
        }
        sendIntent(intent)
    }

    override fun onWaitingNetwork(download: Download) {
        Log.d("fetchListener", "onWaitingNetwork")
        send("onWaitingNetwork", download)
    }
}