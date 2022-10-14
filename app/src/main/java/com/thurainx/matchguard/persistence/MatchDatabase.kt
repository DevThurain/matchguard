package com.thurainx.matchguard.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thurainx.matchguard.data.vos.MatchVO
import com.thurainx.matchguard.persistence.daos.MatchDao

@Database(entities = [MatchVO::class], version = 1, exportSchema = false)
abstract class MatchDatabase : RoomDatabase() {
    companion object{
        private const val DB_NAME = "THE_MATCH_DATABASE"
        var dbInstant : MatchDatabase? = null

        fun getDBInstant(context: Context) : MatchDatabase?{
            when(dbInstant){
                null -> {
                    dbInstant = Room.databaseBuilder(context, MatchDatabase::class.java, DB_NAME)
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return dbInstant
        }
    }

    abstract fun matchDao() : MatchDao

}