package com.cere.csplayer.control;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;

import androidx.annotation.NonNull;

/**
 * Created by CheRevir on 2019/10/24
 */
public abstract class PlayCallback {
    private Callback mCallback;
    private MainHelder mMainHelder;
    public static final int ON_PLAY = 0x1230;
    public static final int ON_DATA = 0x1231;
    public static final int ON_DURATION = 0x1232;
    public static final int ON_CURRENT_DURATON = 0x1233;
    public static final int ON_ACTION = 0x1234;

    public PlayCallback() {
        mCallback = new Callback();
        mMainHelder = new MainHelder(Looper.getMainLooper());
    }

    public abstract void onPlay(boolean isPlay);

    public abstract void onData(String data);

    public abstract void onDuration(int duration);

    public abstract void onCurrentDuration(int duration);

    public abstract void onAction(String action, Bundle bundle);

    public Callback getCallback() {
        return mCallback;
    }

    private class MainHelder extends Handler {
        public MainHelder(@NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ON_PLAY:
                    onPlay((boolean) msg.obj);
                    break;
                case ON_DATA:
                    onData(String.valueOf(msg.obj));
                    break;
                case ON_DURATION:
                    onDuration(msg.arg1);
                    break;
                case ON_CURRENT_DURATON:
                    onCurrentDuration(msg.arg1);
                    break;
                case ON_ACTION:
                    onAction(String.valueOf(msg.obj), msg.getData());
                    break;
            }
        }
    }

    private class Callback extends IPlayCallback.Stub {
        @Override
        public void setPlay(boolean isPlay) throws RemoteException {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_PLAY, isPlay));
        }

        @Override
        public void setData(String data) throws RemoteException {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_DATA, data));
        }

        @Override
        public void setDuration(int duration) throws RemoteException {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_DURATION, duration, 0));
        }

        @Override
        public void setCurrentDuration(int duration) throws RemoteException {
            mMainHelder.sendMessage(mMainHelder.obtainMessage(ON_CURRENT_DURATON, duration, 0));
        }

        @Override
        public void sendAction(String action, Bundle bundle) throws RemoteException {
            Message message = Message.obtain();
            message.setData(bundle);
            message.obj = action;
            message.what = ON_ACTION;
            mMainHelder.sendMessage(message);
        }
    }
}
