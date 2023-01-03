package com.thurainx.matchguard.foreground

import android.app.PendingIntent
import android.app.Service
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.thurainx.matchguard.R
import com.thurainx.matchguard.data.models.MatchModelImpl
import com.thurainx.matchguard.receiver.EXTRA_MATCH_ID
import com.thurainx.matchguard.receiver.EXTRA_MESSAGE
import com.thurainx.matchguard.utils.NotiUtils
import com.thurainx.matchguard.utils.OWN_CHANNEL

const val ACTION_START = "ACTION_START"
const val ACTION_STOP = "ACTION_STOP"
class ForegroundService: Service() {
    companion object{
        fun startService(context: Context, message: String, matchId: String) {
            val intent = Intent(context,ForegroundService::class.java)
            intent.putExtra(EXTRA_MESSAGE,message)
            intent.putExtra(EXTRA_MATCH_ID,matchId)
            intent.action = ACTION_START
            ContextCompat.startForegroundService(context,intent)
        }

        fun stopService(context: Context){
            val intent = Intent(context,ForegroundService::class.java)
            context.stopService(intent)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent?.action == ACTION_START){
            updateCheckStatus(intent.getStringExtra(EXTRA_MATCH_ID) ?: "")
            NotiUtils.buildNotification(this)

            val stopIntent = Intent(this,ForegroundService::class.java)
            stopIntent.action = ACTION_STOP

            val stopPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getService(this,0,stopIntent,PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getService(this,0,stopIntent,PendingIntent.FLAG_IMMUTABLE)
            }

            val soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + this.packageName + "/" + R.raw.marci_whistle);

            val notiBuilder = NotificationCompat.Builder(this, OWN_CHANNEL)
                .setContentTitle("Match Guard")
                .setContentText(intent.getStringExtra(EXTRA_MESSAGE))
                .setSmallIcon(R.drawable.ic_logo)
                .setSound(soundUri)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_close,"Close",stopPendingIntent)
                .build()

            startForeground(111,notiBuilder)
        }else if(intent?.action == ACTION_STOP){
            Companion.stopService(this)
        }


        return START_NOT_STICKY
    }
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun updateCheckStatus(matchId: String){
        val mMatchModel = MatchModelImpl

        if(matchId.isNotEmpty()){
            mMatchModel.mMatchDatabase?.matchDao()?.getMatchOrNull(matchId)?.let {
                it.alreadyShow = true
                mMatchModel.mMatchDatabase?.matchDao()?.insertMatch(it)
                Log.d("db_match","update success")
            }
        }

    }

}