package com.cere.csplayer.service;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.palette.graphics.Palette;

import com.cere.csplayer.Constants;
import com.cere.csplayer.R;
import com.cere.csplayer.activity.MainActivity;
import com.cere.csplayer.control.PlayControlled;
import com.cere.csplayer.entity.Music;
import com.cere.csplayer.entity.Play;
import com.cere.csplayer.until.BitmapUtils;
import com.cere.csplayer.until.MusicMetadataRetriever;
import com.cere.csplayer.view.CustomViews;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.io.IOException;
import java.util.List;

/**
 * Created by CheRevir on 2019/10/24
 */
public class PlayService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, Palette.PaletteAsyncListener,
        AudioManager.OnAudioFocusChangeListener {
    private MediaPlayer mPlayer;
    private AudioManager mAudioManager;
    private AudioFocusRequest mAudioFocusRequest;
    private PlayControlled mPlayControlled;
    private Handler mHandler;
    private UpdateUI mUpdateUI;
    private UpdateProgress mUpdateProgress;
    private List<Music> mMusics;
    private List<Play> mPlays;
    private int index = 0;

    private Broadcast mBroadcast;
    private NotificationCompat.Builder mNotificationCompatBuilder;
    private UpdateNotification mUpdateNotification;
    private RemoteViews mRemoteViews, mRemoteViewsBig;
    public static final String NOTIFICATION_ACTION = "notification_aciton";
    public static final String NOTIFICATION_ACTION_PLAY = "notification_action_play";
    public static final String NOTIFICATION_ACTION_PRE = "notification_action_pre";
    public static final String NOTIFICATION_ACTION_NEXT = "notification_action_next";
    public static final String NOTIFICATION_ACTION_CLOSE = "notification_action_close";

    private Headset mHeadset;
    private boolean isHeadsetPressState = false;
    private int headsetPressCount = 0;

    private boolean isPlaying = false;
    private boolean isPause = false;
    private boolean isShuffleMode = false;
    private int repeatMode = 0;
    private final int MODE_REPEAT_NONE = 0;
    private final int MODE_REPEAT_SINGLE = 1;
    private final int MODE_REPEAT_ALL = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(this.getMainLooper());
        mUpdateUI = new UpdateUI();
        mUpdateProgress = new UpdateProgress();
        mHeadset = new Headset();
        initMediaPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mPlayControlled = new Controlled();
        return mPlayControlled.getBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initMediaPlayer() {
        mPlayer = new MediaPlayer();
        mPlayer.setVolume(1.0f, 1.0f);
        mPlayer.setOnCompletionListener(this);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build();
        mPlayer.setAudioAttributes(attributes);
        mAudioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mAudioFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attributes)
                    .setOnAudioFocusChangeListener(this)
                    .setAcceptsDelayedFocusGain(true)
                    .build();
        }
        //ComponentName componentName = new ComponentName(this, HeadsetButtonReceiver.class);
        //mAudioManager.registerMediaButtonEventReceiver(componentName);
        initNotification();
    }

    private void initNotification() {
        mBroadcast = new Broadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(NOTIFICATION_ACTION);
        filter.addAction(Constants.ACTION_HEADSET);
        filter.addAction(AudioManager.ACTION_HEADSET_PLUG);
        filter.addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        this.registerReceiver(mBroadcast, filter);
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        mNotificationCompatBuilder = new NotificationCompat.Builder(this, "music");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("music", "CSPlayer", NotificationManager.IMPORTANCE_LOW);
            channel.enableLights(false);
            channel.setDescription("CSPlayer播放通知");
            channel.setShowBadge(true);
            channel.setBypassDnd(true);
            channel.enableVibration(false);
            channel.setVibrationPattern(new long[]{0});
            channel.setSound(null, null);
            channel.setLockscreenVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            managerCompat.createNotificationChannel(channel);
        } else {
            mNotificationCompatBuilder.setVibrate(new long[]{0});
            mNotificationCompatBuilder.setSound(null);
        }
        mNotificationCompatBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mNotificationCompatBuilder.setOngoing(true);
        mNotificationCompatBuilder.setAutoCancel(false);
        mNotificationCompatBuilder.setPriority(NotificationCompat.PRIORITY_MAX);
        mNotificationCompatBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        mNotificationCompatBuilder.setContentIntent(PendingIntent.getActivity(this, 100, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
        mUpdateNotification = new UpdateNotification();
    }

    private void initAtBinded() {
        if (XXPermissions.isHasPermission(this, Manifest.permission.WAKE_LOCK)) {
            mPlayer.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
        } else {
            mPlayControlled.getCallback().sendAction(Constants.PERMISSION_WAKE_LOCK, null);
        }
        initTelephonyManager();
    }

    private void initTelephonyManager() {
        if (XXPermissions.isHasPermission(this, Permission.READ_PHONE_STATE)) {
            TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            telephonyManager.listen(new TelephonyListener(), PhoneStateListener.LISTEN_CALL_STATE);
        } else {
            mPlayControlled.getCallback().sendAction(Constants.PERMISSION_READ_PHONE_STATE, null);
        }
    }

    private class Controlled extends PlayControlled {

        @Override
        public void onPlay() {
            PlayService.this.play();
        }

        @Override
        public void onPause() {
            PlayService.this.pause(true);
        }

        @Override
        public void onPrevious() {
            PlayService.this.previous();
        }

        @Override
        public void onNext() {
            PlayService.this.next();
        }

        @Override
        public void seekTo(long progress) {
            PlayService.this.mPlayer.seekTo((int) progress);
        }

        @Override
        public void setData(String data) {
            int index = getPosition(getPosition(data));
            if (index != -1) {
                PlayService.this.index = index;
                PlayService.this.setData(mPlays.get(index).getPosition());
            }
        }

        @Override
        public void setList(List<Music> list) {
            PlayService.this.mMusics = list;
        }

        @Override
        public void setPlayList(List<Play> list) {
            PlayService.this.mPlays = list;
        }

        @Override
        public void setRepeatMode(int repeatMode) {
            PlayService.this.repeatMode = repeatMode;
        }

        @Override
        public void setShuffleModeEnabled(boolean shuffleMode) {
            PlayService.this.isShuffleMode = shuffleMode;
        }

        @Override
        public void onAction(String action, Bundle bundle) {
            switch (action) {
                case Constants.PLAY_SERVICE_INIT:
                    initAtBinded();
                    break;
                case Constants.PERMISSION_WAKE_LOCK:
                    PlayService.this.mPlayer.setWakeMode(PlayService.this, PowerManager.PARTIAL_WAKE_LOCK);
                    break;
                case Constants.PERMISSION_READ_PHONE_STATE:
                    initTelephonyManager();
                    break;
            }
        }
    }

    private class Broadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constants.ACTION_HEADSET:
                    headsetPressCount++;
                    if (!isHeadsetPressState) {
                        isHeadsetPressState = true;
                        mHandler.postDelayed(mHeadset, 1000);
                    }
                    break;
                case AudioManager.ACTION_AUDIO_BECOMING_NOISY:
                    if (mPlayer.isPlaying()) {
                        pause(true);
                    }
                    break;
                case AudioManager.ACTION_HEADSET_PLUG:
                    if (intent.hasExtra("state") && intent.getExtras().getInt("state") == 1 && isPause) {
                        play();
                    }
                    break;
                case NOTIFICATION_ACTION:
                    switch (intent.getExtras().getString(NOTIFICATION_ACTION)) {
                        case NOTIFICATION_ACTION_PLAY:
                            if (mPlayer.isPlaying()) {
                                pause(true);
                            } else {
                                play();
                            }
                            break;
                        case NOTIFICATION_ACTION_PRE:
                            previous();
                            break;
                        case NOTIFICATION_ACTION_NEXT:
                            next();
                            break;
                        case NOTIFICATION_ACTION_CLOSE:
                            pause(false);
                            PlayService.this.stopForeground(true);
                            break;
                    }
                    break;
            }
        }
    }

    private class Headset implements Runnable {
        @Override
        public void run() {
            switch (headsetPressCount) {
                case 1:
                    if (mPlayer.isPlaying()) {
                        pause(true);
                    } else {
                        play();
                    }
                    break;
                case 2:
                    next();
                    break;
                default:
                    previous();
                    break;
            }
            headsetPressCount = 0;
            isHeadsetPressState = false;
        }
    }

    private void play() {
        if (!mPlayer.isPlaying() && requestAudioFocus()) {
            mPlayer.start();
            isPlaying = true;
            isPause = false;
            mPlayControlled.getCallback().setPlay(mPlayer.isPlaying());
            mHandler.postDelayed(mUpdateProgress, 0);
            mHandler.post(mUpdateNotification);
        }
    }

    private void pause(boolean isNotify) {
        if (mPlayer.isPlaying() || isPlaying) {
            mPlayer.pause();
            isPlaying = false;
            isPause = true;
            mPlayControlled.getCallback().setPlay(mPlayer.isPlaying());
            mHandler.removeCallbacks(mUpdateProgress);
            if (isNotify) {
                mHandler.post(mUpdateNotification);
            }
            abandonAudioFocus();
        }
    }

    private void previous() {
        index--;
        if (index < 0) {
            index = mPlays.size() - 1;
        }
        setData(mPlays.get(index).getPosition());
    }

    private void next() {
        index++;
        if (index > mPlays.size() - 1) {
            index = 0;
        }
        setData(mPlays.get(index).getPosition());
    }

    private void setData(final int position) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mPlayer.reset();
                try {
                    mPlayer.setDataSource(mMusics.get(position).getPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPlayer.prepareAsync();
            }
        });
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        switch (repeatMode) {
            case MODE_REPEAT_NONE:
                index++;
                if (index > mPlays.size() - 1) {
                    pause(true);
                    index = mPlays.size() - 1;
                } else {
                    setData(mPlays.get(index).getPosition());
                }
                break;
            case MODE_REPEAT_SINGLE:
                setData(mPlays.get(index).getPosition());
                break;
            case MODE_REPEAT_ALL:
                next();
                break;
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (isPlaying) {
            play();
        }
        mHandler.post(mUpdateUI);
        mHandler.post(mUpdateNotification);
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    private class UpdateNotification implements Runnable {
        @Override
        public void run() {
            mRemoteViews = new RemoteViews(PlayService.this.getPackageName(), R.layout.notification_play);
            mRemoteViewsBig = new RemoteViews(PlayService.this.getPackageName(), R.layout.notification_play_big);
            Intent intent = new Intent(NOTIFICATION_ACTION);
            intent.setPackage(PlayService.this.getPackageName());

            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_PLAY);
            PendingIntent intent_play = PendingIntent.getBroadcast(PlayService.this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_play, intent_play);
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_play, intent_play);

            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_PRE);
            PendingIntent intent_pre = PendingIntent.getBroadcast(PlayService.this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_prev, intent_pre);
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_prev, intent_pre);

            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_NEXT);
            PendingIntent intent_next = PendingIntent.getBroadcast(PlayService.this, 3, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_next, intent_next);
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_next, intent_next);

            intent.putExtra(NOTIFICATION_ACTION, NOTIFICATION_ACTION_CLOSE);
            PendingIntent intent_close = PendingIntent.getBroadcast(PlayService.this, 4, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteViews.setOnClickPendingIntent(R.id.notification_play_close, intent_close);
            mRemoteViewsBig.setOnClickPendingIntent(R.id.notification_play_big_close, intent_close);

            Music music = mMusics.get(mPlays.get(index).getPosition());
            mRemoteViews.setTextViewText(R.id.notification_play_title, music.getTitle());
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_title, music.getTitle());
            mRemoteViews.setTextViewText(R.id.notification_play_artist, music.getArtist());
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_artist, music.getArtist());
            mRemoteViews.setTextViewText(R.id.notification_play_album, music.getAlbum());
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_album, music.getAlbum());
            mRemoteViewsBig.setTextViewText(R.id.notification_play_big_number, index + 1 + "/" + mPlays.size());
            Bitmap bitmap = BitmapUtils.getScaleBitmap(MusicMetadataRetriever.getInstance().getAlbumArt(music.getPath()), 200, 200);
            mRemoteViews.setImageViewBitmap(R.id.notification_play_album_art, bitmap);
            mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_album_art, bitmap);
            if (bitmap == null) {
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clannad);
            }
            Palette.Builder palette = Palette.from(bitmap);
            palette.generate(PlayService.this);
        }
    }

    @Override
    public void onGenerated(@Nullable Palette palette) {
        int color = palette.getLightMutedColor(Color.WHITE);
        if (mPlayer.isPlaying()) {
            Bitmap play = CustomViews.getPlay(color);
            mRemoteViews.setImageViewBitmap(R.id.notification_play_play, play);
            mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_play, play);
        } else {
            Bitmap pause = CustomViews.getPause(color);
            mRemoteViews.setImageViewBitmap(R.id.notification_play_play, pause);
            mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_play, pause);
        }
        Bitmap prev = CustomViews.getPrev(color);
        mRemoteViews.setImageViewBitmap(R.id.notification_play_prev, prev);
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_prev, prev);
        Bitmap next = CustomViews.getNext(color);
        mRemoteViews.setImageViewBitmap(R.id.notification_play_next, next);
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_next, next);
        Bitmap close = CustomViews.getClose(color);
        mRemoteViews.setImageViewBitmap(R.id.notification_play_close, close);
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_close, close);
        Bitmap bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        bitmap.setPixel(0, 0, palette.getDarkMutedColor(Color.DKGRAY));
        mRemoteViews.setImageViewBitmap(R.id.notification_play_background, bitmap);
        mRemoteViewsBig.setImageViewBitmap(R.id.notification_play_big_background, bitmap);
        mRemoteViews.setTextColor(R.id.notification_play_title, palette.getLightVibrantColor(Color.WHITE));
        mRemoteViewsBig.setTextColor(R.id.notification_play_big_title, palette.getLightVibrantColor(Color.WHITE));
        mRemoteViews.setTextColor(R.id.notification_play_artist, palette.getLightVibrantColor(Color.WHITE));
        mRemoteViewsBig.setTextColor(R.id.notification_play_big_artist, palette.getVibrantColor(Color.WHITE));
        mRemoteViews.setTextColor(R.id.notification_play_album, palette.getLightMutedColor(Color.WHITE));
        mRemoteViewsBig.setTextColor(R.id.notification_play_big_album, palette.getLightMutedColor(Color.WHITE));
        mNotificationCompatBuilder.setCustomContentView(mRemoteViews);
        mNotificationCompatBuilder.setCustomBigContentView(mRemoteViewsBig);
        PlayService.this.startForeground(14, mNotificationCompatBuilder.build());
    }

    private class UpdateUI implements Runnable {
        @Override
        public void run() {
            mPlayControlled.getCallback().setData(mMusics.get(mPlays.get(index).getPosition()).getPath());
            mPlayControlled.getCallback().setDuration(mPlayer.getDuration());
        }
    }

    private class UpdateProgress implements Runnable {
        @Override
        public void run() {
            mPlayControlled.getCallback().setCurrentDuration(mPlayer.getCurrentPosition());
            mHandler.postDelayed(mUpdateProgress, 1000);
        }
    }


    private boolean requestAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return mAudioManager.requestAudioFocus(mAudioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            return mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
    }

    private boolean abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return mAudioManager.abandonAudioFocusRequest(mAudioFocusRequest) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else {
            return mAudioManager.abandonAudioFocus(this) == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                mPlayer.setVolume(1.0f, 1.0f);
                if (!mPlayer.isPlaying()) {
                    play();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mPlayer.isPlaying()) {
                    pause(true);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mPlayer.isPlaying()) {
                    pause(true);
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                mPlayer.setVolume(0.5f, 0.5f);
                break;
        }
    }


    private int getPosition(String data) {
        Music music = new Music();
        music.setPath(data);
        return mMusics.indexOf(music);
    }

    private int getPosition(int position) {
        Play play = new Play();
        play.setPosition(position);
        return mPlays.indexOf(play);
    }

    private class TelephonyListener extends PhoneStateListener {
        private boolean isPhone = false;

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {
            super.onCallStateChanged(state, phoneNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    if (mPlayer.isPlaying()) {
                        pause(true);
                        isPhone = true;
                    }
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    if (isPhone && !mPlayer.isPlaying()) {
                        play();
                        isPhone = false;
                    }
            }
        }
    }
}
