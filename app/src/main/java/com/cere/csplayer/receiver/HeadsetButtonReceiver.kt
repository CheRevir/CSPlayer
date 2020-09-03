package com.cere.csplayer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.cere.csplayer.Constants

/**
 * Created by CheRevir on 2019/10/26
 */
class HeadsetButtonReceiver : BroadcastReceiver() {
    companion object {
        private var count = 0
    }

    override fun onReceive(context: Context, intent: Intent) {
        count++
        if (count >= 2) {
            count = 0
            if (intent.hasExtra(Intent.EXTRA_KEY_EVENT)) {
                context.sendBroadcast(Intent(Constants.ACTION_HEADSET))
            }
        }
    }
}