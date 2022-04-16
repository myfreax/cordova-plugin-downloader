package com.bumoyu.downloader

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.tonyodev.fetch2.Download
import com.tonyodev.fetch2.Fetch
import com.tonyodev.fetch2.Status
import com.tonyodev.fetch2core.Func
import kotlin.properties.Delegates

@Suppress("MoveLambdaOutsideParentheses")
class ProgressMonitorService : Service() {

    companion object {
        private var tasks: Tasks? = null
        const val TAG = "ProgressMonitorService"
        private lateinit var fetch: Fetch
        private var interval by Delegates.notNull<Long>()
        private var time: Int by Delegates.notNull<Int>()
        private var serviceIsDestroy: Boolean = false
        fun start(context: Context, fetch: Fetch, interval: Long, time: Int) {
            this.time = time
            this.interval = interval
            this.fetch = fetch
            val intent = Intent(context, ProgressMonitorService::class.java)
            context.startService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, ProgressMonitorService::class.java)
            context.stopService(intent)
        }

        fun getTimeoutTasks():Tasks? {
            return tasks
        }
    }

    private lateinit var setInterval: SetInterval
    //private lateinit var itl: SetInterval


    private val gson by lazy {
        Gson()
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        setInterval = task(interval, time)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun sendIntent(intent: Intent) {
        try {
            this.sendBroadcast(intent)
            //context.sendBroadcast(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun send(tasks: String) {
        val intent = Intent().apply {
            this.putExtra("event", "onTimeoutTasks")
            this.putExtra("Download", "{}")
            this.putExtra("tasks", tasks)
            this.action = DownloaderListener.INTENTACTION
            this.`package` = this@ProgressMonitorService.packageName
        }
        sendIntent(intent)
    }

    private fun task(interval: Long, time: Int): SetInterval {
        ///val ms = minuts*60000
        run(interval, time)
        val setInterval = SetInterval(interval)
        setInterval.execute {
            run(interval, time)
        }
        return setInterval
    }

    private fun run(interval: Long, time: Int) {
        val tasks = Tasks(System.currentTimeMillis())
        var downloads = arrayOf<Download>()
        var counter = 0
        val itl = SetInterval((interval / time))
        itl.execute {
            if (serviceIsDestroy) {
                itl.cancel()
            }
            if (counter == time) {
                val results = downloads.map {
                    it.id
                }.distinct().map {
                    val task = Task(it)
                    task.setLatestDownload(downloads)
                    downloads.filter { it.id == task.id }.forEach {
                        task.addProgress(it.downloaded)
                    }
                    task
                }.filter { !it.progressISChanged() }
                tasks.downloads  = results
                ProgressMonitorService.tasks = tasks
                val tasksString = gson.toJson(tasks)
                if (!tasksString.isNullOrEmpty()) {
                    send(tasksString)
                }
                itl.cancel()
            }
            counter += 1
            Log.d(TAG, "collecting $counter")
            fetch.getDownloads(object : Func<List<Download>> {
                override fun call(result: List<Download>) {
                    result
                        .filter { it.total != -1L }
                        .filter { it.downloaded != it.total }
                        .filter { it.status != Status.PAUSED }
                        .forEach {
                            downloads += it
                        }
                }
            })
        }

    }

    override fun onDestroy() {
        serviceIsDestroy = true
        //itl.cancel()
        setInterval.cancel()
        super.onDestroy()
    }
}