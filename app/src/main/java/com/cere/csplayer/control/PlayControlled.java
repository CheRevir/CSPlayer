package com.cere.csplayer.control;

import android.os.Bundle;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;

import java.util.List;

/**
 * Created by CheRevir on 2019/10/24
 */
public abstract class PlayControlled {
    private Binder mBinder;
    private Callback mCallback;
    private RemoteCallbackList<IPlayCallback> mCallbackList;

    public PlayControlled() {
        mBinder = new Binder();
        mCallback = new Callback();
        mCallbackList = new RemoteCallbackList<>();
    }

    public abstract void onPlay();

    public abstract void onPause();

    public abstract void onPrevious();

    public abstract void onNext() ;

    public abstract void seekTo(long progress);

    public abstract void setData(String data);

    public abstract void setList(List<Music> list);

    public abstract void setPlayList(List<Play> list);

    public abstract void setRepeatMode(int repeatMode);

    public abstract void setShuffleModeEnabled(boolean shuffleMode);

    public abstract void onAction(String action, Bundle bundle);

    private void registerCallback(IPlayCallback callback) {
        mCallbackList.register(callback);
    }

    private void unregisterCallback(IPlayCallback callback) {
        mCallbackList.unregister(callback);
    }

    public Binder getBinder() {
        return mBinder;
    }

    public Callback getCallback() {
        return mCallback;
    }

    public class Callback {
        public void setPlay(boolean isPlay) {
            int num = mCallbackList.beginBroadcast();
            for (int n = 0; n < num; n++) {
                try {
                    mCallbackList.getBroadcastItem(n).setPlay(isPlay);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbackList.finishBroadcast();
        }

        public void setData(String data) {
            int num = mCallbackList.beginBroadcast();
            for (int n = 0; n < num; n++) {
                try {
                    mCallbackList.getBroadcastItem(n).setData(data);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbackList.finishBroadcast();
        }

        public void setDuration(int duration) {
            int num = mCallbackList.beginBroadcast();
            for (int n = 0; n < num; n++) {
                try {
                    mCallbackList.getBroadcastItem(n).setDuration(duration);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbackList.finishBroadcast();
        }

        public void setCurrentDuration(int duration) {
            int num = mCallbackList.beginBroadcast();
            for (int n = 0; n < num; n++) {
                try {
                    mCallbackList.getBroadcastItem(n).setCurrentDuration(duration);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbackList.finishBroadcast();
        }

        public void sendAction(String action, Bundle bundle) {
            int num = mCallbackList.beginBroadcast();
            for (int n = 0; n < num; n++) {
                try {
                    mCallbackList.getBroadcastItem(n).sendAction(action, bundle);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            mCallbackList.finishBroadcast();
        }
    }

    private class Binder extends IPlayControl.Stub {

        @Override
        public void onPlay() throws RemoteException {
            PlayControlled.this.onPlay();
        }

        @Override
        public void onPause() throws RemoteException {
            PlayControlled.this.onPause();
        }

        @Override
        public void onPrevious() throws RemoteException {
            PlayControlled.this.onPrevious();
        }

        @Override
        public void onNext() throws RemoteException {
            PlayControlled.this.onNext();
        }

        @Override
        public void seekTo(long progress) throws RemoteException {
            PlayControlled.this.seekTo(progress);
        }

        @Override
        public void setData(String data) throws RemoteException {
            PlayControlled.this.setData(data);
        }

        @Override
        public void setList(List<Music> list) throws RemoteException {
            PlayControlled.this.setList(list);
        }

        @Override
        public void setPlayList(List<Play> list) throws RemoteException {
            PlayControlled.this.setPlayList(list);
        }

        @Override
        public void setRepeatMode(int repeatMode) throws RemoteException {
            PlayControlled.this.setRepeatMode(repeatMode);
        }

        @Override
        public void setShuffleModeEnabled(boolean shuffleMode) throws RemoteException {
            PlayControlled.this.setShuffleModeEnabled(shuffleMode);
        }

        @Override
        public void sendAction(String action, Bundle bundle) throws RemoteException {
            PlayControlled.this.onAction(action, bundle);
        }

        @Override
        public void registerCallback(IPlayCallback callback) throws RemoteException {
            PlayControlled.this.registerCallback(callback);
        }

        @Override
        public void unregisterCallback(IPlayCallback callback) throws RemoteException {
            PlayControlled.this.unregisterCallback(callback);
        }
    }
}
