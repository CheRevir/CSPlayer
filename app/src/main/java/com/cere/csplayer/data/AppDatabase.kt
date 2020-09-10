package com.cere.csplayer.data

import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.cere.csplayer.App
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play

/**
 * Created by CheRevir on 2020/9/5
 */
@Database(entities = [Music::class, Play::class], version = 1)
//@TypeConverters(UriConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getMusicDao(): MusicDao
    abstract fun getPlayDao(): PlayDao

    companion object {
        val instance: AppDatabase by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(App.context, AppDatabase::class.java, "Music.db")
                .allowMainThreadQueries()
                //.addCallback(AppDatabaseCallback())
                .build()
        }
    }

    private class AppDatabaseCallback : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            Log.e("TAG", "AppDatabaseCallback -> onCreate: ${db.path}");
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            Log.e("TAG", "AppDatabaseCallback -> onOpen: ${db.version}");
        }
    }
}