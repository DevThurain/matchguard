package com.thurainx.matchguard

import android.app.Application
import com.thurainx.matchguard.data.models.MatchModelImpl

class MatchGuardApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        MatchModelImpl.initDatabase(applicationContext)
    }

}