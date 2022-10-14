package com.thurainx.matchguard.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.thurainx.matchguard.data.models.MatchModelImpl
import com.thurainx.matchguard.data.vos.DateVO
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.network.JsoupCrawler
import com.thurainx.matchguard.receiver.AlarmReceiver
import com.thurainx.matchguard.utils.DateUtils
import com.thurainx.matchguard.utils.getCurrentTimeInMilli
import com.thurainx.matchguard.utils.unixToMilli
import com.thurainx.matchguard.workers.KEY_NOTI_MESSAGE
import com.thurainx.matchguard.workers.NotifyWorker
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MatchListViewModel : ViewModel() {

    // liveData
    var liveDateList = MutableLiveData<List<DateVO>>()
    var liveMatchList = MutableLiveData<List<MatchVO>>()
    var errorLiveData = MutableLiveData<String>()

    // state
    var allMatchList: List<MatchVO> = listOf()
    var selectedDate: String = ""





    init {
        initializeDateList()
        initializeMatchList()

    }

    private fun initializeDateList() {
        var dateList: List<DateVO> = listOf()
        dateList = DateUtils().getNextWeekDates()
        if (dateList.isNotEmpty()) {
            dateList.first().isSelected = true
            selectedDate = dateList.first().standardMatchDate.toString()
        }

        liveDateList.postValue(dateList)
    }

    fun initializeMatchList(){
        viewModelScope.launch {
            val idsFromDB = MatchModelImpl.mMatchDatabase?.matchDao()?.getMatchIdList() ?: listOf()
            JsoupCrawler.getMatchListV2(onSuccess = { matchList ->
                matchList.forEach {
                    if(idsFromDB.contains(it.id)){
                        it.subscribeNoti = true
                    }
                }
                    allMatchList = matchList.toList()
                    getMatchListBySelectedDate()
            },
            onError = {
                errorLiveData.postValue(it)
            })
        }
    }

    fun reInitializeMatchList(){
        val idsFromDB = MatchModelImpl.mMatchDatabase?.matchDao()?.getMatchIdList() ?: listOf()
        val matchList = liveMatchList.value
        matchList?.forEach {
            it.subscribeNoti = idsFromDB.contains(it.id)
        }
        liveMatchList.postValue(matchList)
    }

    fun getMatchListBySelectedDate() {
        val filterList: ArrayList<MatchVO> = arrayListOf()
        allMatchList.forEach {
            if (it.standardMatchDate.toString() == selectedDate) {
                filterList.add(it)
            }
        }
        liveMatchList.postValue(listOf())
        liveMatchList.postValue(filterList)

    }

    fun insertMatchToDB(matchVO: MatchVO){
        MatchModelImpl.mMatchDatabase?.matchDao()?.insertMatch(matchVO)
    }

    fun addNotification(context: Context, matchVO: MatchVO) {
        val currentDate2 = Calendar.getInstance()

        matchVO.unixTime?.let { unixTime ->

            val timeDiff = unixToMilli(unixTime) - currentDate2.timeInMillis


            val inputData = Data.Builder()
                .putString(
                    KEY_NOTI_MESSAGE,
                    "${matchVO.leftTeamName} vs ${matchVO.rightTeamName} is coming at ${
                        DateUtils().convertToReadableTime(unixTime)
                    }"
                )
                .build()

            val dailyWorkRequest = OneTimeWorkRequestBuilder<NotifyWorker>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            WorkManager.getInstance(context).enqueue(
                dailyWorkRequest
            )
        }


    }




}