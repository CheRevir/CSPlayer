package com.cere.csplayer.data

import android.content.Context
import com.litesuits.orm.LiteOrm
import com.litesuits.orm.db.DataBaseConfig

/**
 * Created by CheRevir on 2019/10/21
 */
object SQLite {
    @JvmStatic
    fun getLiteOrm(context: Context): LiteOrm {
        val config = DataBaseConfig(context.applicationContext)
        config.dbName = "Music.db"
        config.dbVersion = 1
        config.debugged = false
        config.onUpdateListener = null
        return LiteOrm.newSingleInstance(config)
    }
}