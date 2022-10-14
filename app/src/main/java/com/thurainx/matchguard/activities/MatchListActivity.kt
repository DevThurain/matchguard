package com.thurainx.matchguard.activities

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.thurainx.matchguard.R
import com.thurainx.matchguard.adapters.DateListAdapter
import com.thurainx.matchguard.adapters.MatchListAdapter
import com.thurainx.matchguard.adapters.TYPE_NORMAL
import com.thurainx.matchguard.data.vos.DateVO
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.delegate.DateDelegate
import com.thurainx.matchguard.delegate.NotiDelegate
import com.thurainx.matchguard.receiver.AlarmReceiver
import com.thurainx.matchguard.utils.AppUtils
import com.thurainx.matchguard.utils.DateUtils
import com.thurainx.matchguard.utils.getCurrentTimeInMilli
import com.thurainx.matchguard.viewmodels.MatchListViewModel
import kotlinx.android.synthetic.main.activity_match_list.*


class MatchListActivity : AppCompatActivity(), DateDelegate, NotiDelegate {

    lateinit var mDateListAdapter: DateListAdapter
    lateinit var mMatchListAdapter: MatchListAdapter

    private val matchListViewModel: MatchListViewModel by viewModels()
    lateinit var alarmManager: AlarmManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_list)

        checkPermissions()
        setupDateRecyclerView()
        setupMatchRecyclerView()
        setupLiveData()
        setupListeners()
        setupSwipeLayout()
    }


    private fun setupLiveData() {
        matchListViewModel.liveMatchList.observe(this) { newMatchList ->
            swipeLayout.isRefreshing = false
            mMatchListAdapter.updateData(newMatchList)
            if(newMatchList.isEmpty()){
                tvListStatus.visibility = View.VISIBLE
                tvListStatus.text = getString(R.string.lbl_empty_match)
            }else{
                tvListStatus.visibility = View.GONE
                tvListStatus.text = ""
            }
        }

        matchListViewModel.liveDateList.observe(this) { newDateList ->
            mDateListAdapter.updateData(newDateList)
        }

        matchListViewModel.errorLiveData.observe(this){error ->
            swipeLayout.isRefreshing = false
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwipeLayout(){
        swipeLayout.isRefreshing = true
        swipeLayout.setOnRefreshListener {
            matchListViewModel.initializeMatchList()
        }
    }

    private fun setupMatchRecyclerView() {
        mMatchListAdapter = MatchListAdapter(this, TYPE_NORMAL)
        rvMatchList.adapter = mMatchListAdapter
    }

    private fun setupDateRecyclerView() {
        mDateListAdapter = DateListAdapter(this)
        rvDate.adapter = mDateListAdapter

    }

    private fun setupListeners() {
        ivYourNotifications.setOnClickListener {
            val intent = YourNotificationsActivity.getIntent(this)
            intentLauncher.launch(intent)
        }
    }

    override fun onTapDate(date: DateVO) {
        var newDayList = DateUtils().getNextWeekDates()

        newDayList.forEachIndexed { index, dayVO ->
            if (dayVO.formatDate == date.formatDate) {
                dayVO.isSelected = true
                matchListViewModel.selectedDate = date.standardMatchDate.toString()
            }
        }
        matchListViewModel.liveDateList.postValue(newDayList)
        matchListViewModel.getMatchListBySelectedDate()
    }


    override fun onTapNoti(match: MatchVO) {
        matchListViewModel.liveMatchList.value?.forEach {
            if (it.id == match.id && !it.subscribeNoti) {
                it.subscribeNoti = true
                match.uniqueId = getCurrentTimeInMilli().toInt()
                matchListViewModel.insertMatchToDB(match)
                addNotificationV2(matchVO = it)
            }
        }

//        matchListViewModel.addNotification(this,match)

        matchListViewModel.liveMatchList.postValue(matchListViewModel.liveMatchList.value)

    }

    private fun addNotificationV2(matchVO: MatchVO) {
        if(checkIfAlreadyHavePermission()){
            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val message = AppUtils.constructMessage(
                leftTeam = matchVO.leftTeamName.toString(),
                rightTeam = matchVO.rightTeamName.toString()
            )
            val intent = AlarmReceiver.getIntent(this, matchId = matchVO.id, message = message)
            var pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(this, matchVO.uniqueId, intent, PendingIntent.FLAG_MUTABLE)
            } else {
                PendingIntent.getBroadcast(this, matchVO.uniqueId, intent, PendingIntent.FLAG_IMMUTABLE)
            }

            var info = AlarmManager.AlarmClockInfo(
                DateUtils().unixTimeToMillis(matchVO.unixTime.toString()),
                pendingIntent
            )
            alarmManager.setAlarmClock(info, pendingIntent)

//            pendingIntent = PendingIntent.getBroadcast(this, getCurrentTimeInMilli().toInt() + 1, intent, 0)
//            info = AlarmManager.AlarmClockInfo(
//                DateUtils().unixTimeToMillis(1665066420),
////                DateUtils().unixTimeToMillis(matchVO.unixTime.toString()),
//                pendingIntent
//            )
//            alarmManager.setAlarmClock(info, pendingIntent)
            Toast.makeText(this,"${matchVO.leftTeamName} vs ${matchVO.rightTeamName} match will notify in time.", Toast.LENGTH_SHORT).show()

        }else{
            Snackbar.make(window.decorView,"Please Grant Schedule Alarm Permission.", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.SCHEDULE_EXACT_ALARM),
                100
            )
        }
    }


    // handlers
    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("result", "ok")
                matchListViewModel.reInitializeMatchList()
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            100 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    Snackbar.make(window.decorView, "Permission Granted", Snackbar.LENGTH_SHORT)
                        .show()

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Snackbar.make(
                        window.decorView,
                        "Unable to Subscribe Notifications Without Permission",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun checkIfAlreadyHavePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val result =
                ContextCompat.checkSelfPermission(this, Manifest.permission.SCHEDULE_EXACT_ALARM)
            return result == PackageManager.PERMISSION_GRANTED

        } else {
            return true
        }

    }


}