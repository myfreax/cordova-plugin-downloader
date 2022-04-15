package com.bumoyu.downloader

import com.tonyodev.fetch2.Download

class Task(val id: Int) {
    private var progresses: Array<Int> = arrayOf()
    var download: Download? = null
    private var progressChanged: Boolean = true
    fun addProgress(progress: Int) {
        progresses += progress
    }

    fun setDownload(downloads: Array<Download>) {
        download = downloads.find { id == it.id }
    }

    fun progressISChanged(): Boolean {
        progressChanged = progresses.distinct().size != 1
        return progressChanged
    }

}