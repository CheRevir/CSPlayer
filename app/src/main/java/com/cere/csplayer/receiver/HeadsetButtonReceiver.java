package com.cere.csplayer.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cere.csplayer.Constants;

/**
 * Created by CheRevir on 2019/10/26
 */
public class HeadsetButtonReceiver extends BroadcastReceiver {
    private static int count = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        count++;
        if (count == 2) {
            count = 0;
            if (intent.hasExtra(Intent.EXTRA_KEY_EVENT)) {
                context.sendBroadcast(new Intent(Constants.ACTION_HEADSET));
            }
        }
    }
}
