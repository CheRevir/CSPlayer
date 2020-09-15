// IPlayControl.aidl
package com.cere.csplayer.control;

// Declare any non-default types here with import statements
import android.os.Bundle;
import com.cere.csplayer.data.FileData;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;
import com.cere.csplayer.control.IPlayCallback;

oneway interface IPlayControl {
    void play();
    void pause();
    void previous();
    void next();
    void seekTo(int progress);
    void setData(in FileData data);
    void setMusicList(in List<Music> list);
    void setPlayList(in List<Play> list);
    void setRepeatMode(int repeatMode);
    void setShuffleMode(boolean shuffleMode);
    void sendAction(String action, in Bundle bundle);
    void registerCallback(in IPlayCallback callback);
    void unregisterCallback(in IPlayCallback callback);
}
