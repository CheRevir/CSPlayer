// IPlayCallback.aidl
package com.cere.csplayer.control;

// Declare any non-default types here with import statements
import android.os.Bundle;

oneway interface IPlayCallback {
     void setPlay(boolean isPlay);
     void setData(int id);
     void setDuration(int duration);
     void setCurrentDuration(int duration);
     void sendAction(String action, in Bundle bundle);
}
