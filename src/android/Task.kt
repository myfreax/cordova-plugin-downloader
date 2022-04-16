package com.bumoyu.downloader

import com.tonyodev.fetch2.Download

class Task(val id: Int) {
    private var downloaded: Array<Long> = arrayOf()
    private var latestDownload: Download? = null
    private var progressChanged: Boolean = true

    fun addProgress(progress: Long) {
        downloaded += progress
    }

    fun setLatestDownload(download: Download) {
        addProgress(download.downloaded)
        latestDownload = download
    }

    fun setLatestDownload(downloads: Array<Download>) {
        val latestDownloads = downloads.filter {
            it.id == id
        }
        latestDownload = latestDownloads.last()
    }

    fun progressISChanged(): Boolean {
        progressChanged = downloaded.distinct().size != 1
        return progressChanged
    }
}