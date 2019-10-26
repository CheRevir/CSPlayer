package com.cere.csplayer.data;

import android.content.Context;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;

/**
 * Created by CheRevir on 2019/10/21
 */
public class SQLite {
    private static LiteOrm sLiteOrm = null;

    public static LiteOrm getLiteOrm(Context context) {
        if (sLiteOrm == null) {
            DataBaseConfig config = new DataBaseConfig(context.getApplicationContext());
            config.dbName = "Music.db";
            config.dbVersion = 1;
            config.debugged = false;
            config.onUpdateListener = null;
            sLiteOrm = LiteOrm.newSingleInstance(config);
        }
        return sLiteOrm;
    }
}
