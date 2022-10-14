package com.thurainx.matchguard.workers

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.thurainx.matchguard.R
import com.thurainx.matchguard.utils.NotiUtils
import com.thurainx.matchguard.utils.OWN_CHANNEL

const val KEY_NOTI_MESSAGE = "KEY_NOTI_MESSAGE"
class NotifyWorker(private val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        try {

//           TaskModelImpl.getTaskById(id.toString())?.let {
//               buildNotification(it.message)
//               it.status = STATUS_COMPLETE
//               TaskModelImpl.insertTask(it)
//           }

            val message = inputData.getString(KEY_NOTI_MESSAGE).toString()
            buildNotification(message)

            return Result.success()
        }catch (e: Exception){
            return Result.failure()
        }
    }

    private fun buildNotification(message: String){
        NotiUtils.buildNotification(context)

        val notiBuilder = NotificationCompat.Builder(context, OWN_CHANNEL)
            .setContentTitle("Match Guard")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_logo)


        NotificationManagerCompat.from(context).notify(100, notiBuilder.build())
    }
}