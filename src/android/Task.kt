package com.bumoyu.downloader

import com.tonyodev.fetch2.Download

class Task(val id: Int) {
    private var progresses: Array<Int> = arrayOf()
    private var latestDownload: Download? = null
    private var progressChanged: Boolean = true
    fun addProgress(progress: Int) {
        progresses += progress
    }

    fun setLatestDownload(download: Download) {
        addProgress(download.progress)
        latestDownload = download
    }

    fun setLatestDownload(downloads: Array<Download>) {
        val latestDownloads = downloads.filter {
            it.id == id
        }
        latestDownload = latestDownloads.last()
    }

    fun progressISChanged(): Boolean {
        progressChanged = progresses.distinct().size != 1
        return progressChanged
    }
}