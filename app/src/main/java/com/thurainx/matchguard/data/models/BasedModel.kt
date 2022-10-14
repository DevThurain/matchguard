package com.thurainx.matchguard.data.models

import android.content.Context
import com.thurainx.matchguard.persistence.MatchDatabase

abstract class BasedModel {
    var mMatchDatabase: MatchDatabase? = null

    fun initDatabase(context: Context){
        mMatchDatabase = MatchDatabase.getDBInstant(context)
    }
}