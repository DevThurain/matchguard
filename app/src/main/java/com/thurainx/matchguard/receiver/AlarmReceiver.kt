package com.thurainx.matchguard.receiver

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.thurainx.matchguard.R
import com.thurainx.matchguard.data.models.MatchModelImpl
import com.thurainx.matchguard.foreground.ForegroundService
import com.thurainx.matchguard.utils.NotiUtils
import com.thurainx.matchguard.utils.OWN_CHANNEL
import com.thurainx.matchguard.utils.getCurrentTimeInMilli


const val EXTRA_MATCH_ID = "EXTRA_MATCH_ID"
const val EXTRA_MESSAGE = "EXTRA_MESSAGE"

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        fun getIntent(context: Context, matchId: String, message: String): Intent {
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra(EXTRA_MATCH_ID, matchId)
            intent.putExtra(EXTRA_MESSAGE, message)

            return intent
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
//            updateCheckStatus(intent?.getStringExtra(EXTRA_MATCH_ID)?: "")
//            buildNotification(context,intent?.getStringExtra(EXTRA_MESSAGE) ?: "-")
            ForegroundService.startService(
                context,
                message = intent?.getStringExtra(EXTRA_MESSAGE) ?: "",
                matchId = intent?.getStringExtra(EXTRA_MATCH_ID) ?: ""
            )
        }
    }

    private fun buildNotification(context: Context, message: String) {
        NotiUtils.buildNotification(context)

        val soundUri =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + "/" + R.raw.marci_whistle);

        val notiBuilder = NotificationCompat.Builder(context, OWN_CHANNEL)
            .setContentTitle("Match Guard")
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_logo)
            .setSound(soundUri)
            .setOnlyAlertOnce(true)

        NotificationManagerCompat.from(context)
            .notify(getCurrentTimeInMilli().toInt(), notiBuilder.build())
    }

    private fun updateCheckStatus(matchId: String) {
        val mMatchModel = MatchModelImpl

        mMatchModel.mMatchDatabase?.matchDao()?.getMatchOrNull(matchId)?.let {
            it.alreadyShow = true
            mMatchModel.mMatchDatabase?.matchDao()?.insertMatch(it)
            Log.d("db_match", "update success")
        }
    }
}