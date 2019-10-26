package com.cere.csplayer.control;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;

import java.util.List;

/**
 * Created by CheRevir on 2019/10/24
 */
public class PlayControl implements ServiceConnection {
    private IPlayControl mIPlayControl;
    private boolean isConnected = false;
    private OnConnectedListener mOnConnectedListener;
    private Context mContext;
    private Class<?> mClass;

    public PlayControl(Context context, Class<?> mediaService) {
        mContext = context;
        mClass = mediaService;
    }

    public void connect(OnConnectedListener onConnectedListener) {
        Intent intent = new Intent(mContext, mClass);
        mContext.bindService(intent, this, Service.BIND_AUTO_CREATE);
        this.mOnConnectedListener = onConnectedListener;
    }

    public void disconnect() {
        if (isConnected) {
            mContext.unbindService(this);
            isConnected = false;
            if (mOnConnectedListener != null) {
                mOnConnectedListener.onConnected(isConnected);
            }
        }
    }

    public void play() {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.onPlay();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void pause() {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.onPause();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void previous() {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.onPrevious();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void next() {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.onNext();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void seekTo(long progress) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.seekTo(progress);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(String data) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.setData(data);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setList(List<Music> list) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.setList(list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setPlayList(List<Play> list) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.setPlayList(list);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setRepeatMode(int repeatMode) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.setRepeatMode(repeatMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setShuffleModeEnabled(boolean shuffleMode) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.setShuffleModeEnabled(shuffleMode);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendAction(String action, Bundle bundle) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.sendAction(action, bundle);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerCallback(IPlayCallback callback) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.registerCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void unregisterCallback(IPlayCallback callback) {
        if (mIPlayControl != null) {
            try {
                mIPlayControl.unregisterCallback(callback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isConnected() {
        return this.isConnected;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        mIPlayControl = IPlayControl.Stub.asInterface(service);
        isConnected = true;
        if (mOnConnectedListener != null) {
            mOnConnectedListener.onConnected(isConnected);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        isConnected = false;
        if (mOnConnectedListener != null) {
            mOnConnectedListener.onConnected(isConnected);
        }
    }

    public interface OnConnectedListener {
        void onConnected(boolean isConnected);
    }
}
