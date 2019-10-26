// IPlayControl.aidl
package com.cere.csplayer.control;

// Declare any non-default types here with import statements
import android.os.Bundle;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;
import com.cere.csplayer.control.IPlayCallback;

oneway interface IPlayControl {
    void onPlay();
    void onPause();
    void onPrevious();
    void onNext();
    void seekTo(long progress);
    void setData(in String data);
    void setList(in List<Music> list);
    void setPlayList(in List<Play> list);
    void setRepeatMode(int repeatMode);
    void setShuffleModeEnabled(boolean shuffleMode);
    void sendAction(in String action, in Bundle bundle);
    void registerCallback(in IPlayCallback callback);
    void unregisterCallback(in IPlayCallback callback);
}
