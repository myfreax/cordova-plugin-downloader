package com.bumoyu.downloader

import android.content.Intent
import android.util.Log
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Error
import com.tonyodev.fetch2.FetchListener
import com.tonyodev.fetch2core.DownloadBlock

class DownloaderListener(private val context: android.content.Context) : FetchListener {
    private fun sendProgress(id: Int, progress: Int, downloadedBytesPerSecond: Long) {
        try {
            val intent = Intent()
            intent.action = PROGRESS
            intent.`package` = context.packageName
            intent.putExtra("id", id)
            intent.putExtra("progress", progress)
            intent.putExtra("downloadedBytesPerSecond",downloadedBytesPerSecond)
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object{
        const val PROGRESS = "downloader.listener.progress"
    }
    override fun onAdded(download: Download) {
        Log.d("fetchListener", download.progress.toString())
    }

    override fun onCancelled(download: Download) {
        Log.d("fetchListener", "onCancelled")
    }

    override fun onCompleted(download: Download) {
        Log.d("fetchListener", "onCompleted")
    }

    override fun onDeleted(download: Download) {
        Log.d("fetchListener", "onDeleted")
    }

    override fun onDownloadBlockUpdated(
        download: Download,
        downloadBlock: DownloadBlock,
        totalBlocks: Int
    ) {
        Log.d("fetchListener", "onDownloadBlockUpdated")
    }

    override fun onError(download: Download, error: Error, throwable: Throwable?) {
        Log.d("fetchListener", error.throwable?.message!!)
    }

    override fun onPaused(download: Download) {
        Log.d("fetchListener", "onPaused")
    }

    override fun onProgress(
        download: Download,
        etaInMilliSeconds: Long,
        downloadedBytesPerSecond: Long
    ) {
        sendProgress(download.id,download.progress,downloadedBytesPerSecond)
        Log.d("onProgress", "onProgress")
    }

    override fun onQueued(download: Download, waitingOnNetwork: Boolean) {
        Log.d("fetchListener", "onQueued")
    }

    override fun onRemoved(download: Download) {
        Log.d("fetchListener", "onRemoved")
    }

    override fun onResumed(download: Download) {
        Log.d("fetchListener", "onResumed")
    }

    override fun onStarted(
        download: Download,
        downloadBlocks: List<DownloadBlock>,
        totalBlocks: Int
    ) {
        Log.d("fetchListener", "onStarted")
    }

    override fun onWaitingNetwork(download: Download) {
        Log.d("fetchListener", "onWaitingNetwork")
    }
}