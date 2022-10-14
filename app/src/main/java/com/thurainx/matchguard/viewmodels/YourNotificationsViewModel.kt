package com.thurainx.matchguard.viewmodels

import android.app.PendingIntent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.thurainx.matchguard.data.models.MatchModelImpl
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.receiver.AlarmReceiver

class YourNotificationsViewModel: ViewModel() {
    var liveMatchList = MutableLiveData<List<MatchVO>>()

    init {
        getAllMatchesFromDB()
    }

    private fun getAllMatchesFromDB(){
       liveMatchList.postValue(MatchModelImpl.mMatchDatabase?.matchDao()?.getAllMatches() ?: listOf())
        MatchModelImpl.mMatchDatabase?.matchDao()?.getAllMatches()?.forEach {
            Log.d("db",it.id)
        }
    }

    fun deleteMatchById(id: String){
        MatchModelImpl.mMatchDatabase?.matchDao()?.deleteMatchById(id)
        getAllMatchesFromDB()




    }

}