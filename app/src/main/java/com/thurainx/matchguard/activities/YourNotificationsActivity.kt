package com.thurainx.matchguard.activities

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.thurainx.matchguard.R
import com.thurainx.matchguard.adapters.MatchListAdapter
import com.thurainx.matchguard.adapters.TYPE_SUBSCRIBE
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.delegate.NotiDelegate
import com.thurainx.matchguard.receiver.AlarmReceiver
import com.thurainx.matchguard.utils.AppUtils
import com.thurainx.matchguard.viewmodels.YourNotificationsViewModel
import kotlinx.android.synthetic.main.activity_your_notifications.*

class YourNotificationsActivity : AppCompatActivity(), NotiDelegate {
    companion object{
        fun getIntent(context: Context): Intent {
            return Intent(context, YourNotificationsActivity::class.java)
        }
    }
    lateinit var mMatchListAdapter : MatchListAdapter
    private val yourNotificationsViewModel : YourNotificationsViewModel by viewModels()
    lateinit var alarmManager: AlarmManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_your_notifications)

        setupRecyclerView()
        setupListeners()
        setupLiveData()
    }

    private fun setupLiveData(){
        yourNotificationsViewModel.liveMatchList.observe(this){
            mMatchListAdapter.updateData(it)
        }
    }

    private fun setupRecyclerView(){
        mMatchListAdapter = MatchListAdapter(this, TYPE_SUBSCRIBE)
        rvSubscribedNotifications.adapter = mMatchListAdapter
    }

    private fun setupListeners(){
        ivYourNotificationsBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    override fun onTapNoti(match: MatchVO) {
        val message = AppUtils.constructMessage(
            leftTeam = match.leftTeamName.toString(),
            rightTeam = match.rightTeamName.toString()
        )
        val intent = AlarmReceiver.getIntent(this, matchId = match.id, message = message)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, match.uniqueId, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(this, match.uniqueId, intent, PendingIntent.FLAG_IMMUTABLE)
        }
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        Toast.makeText(this,"${match.leftTeamName} vs ${match.rightTeamName} Notification Canceled.", Toast.LENGTH_SHORT).show()

        yourNotificationsViewModel.deleteMatchById(match.id)
        setResult(Activity.RESULT_OK)
    }
}