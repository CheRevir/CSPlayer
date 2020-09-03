package com.cere.csplayer.activity

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.request.RequestOptions
import com.cere.csplayer.BuildConfig
import com.cere.csplayer.Constants
import com.cere.csplayer.GlideApp
import com.cere.csplayer.OnPermissionCallback
import com.cere.csplayer.R
import com.cere.csplayer.adapter.AlbumArtAdapter
import com.cere.csplayer.adapter.MusicAdapter
import com.cere.csplayer.content.MusicManager
import com.cere.csplayer.control.PlayCallback
import com.cere.csplayer.control.PlayControl
import com.cere.csplayer.data.SQLite
import com.cere.csplayer.data.SharedPre
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import com.cere.csplayer.glide.BlurTransformation
import com.cere.csplayer.service.PlayService
import com.cere.csplayer.util.BitmapUtils
import com.cere.csplayer.util.DialogUtils
import com.cere.csplayer.util.Utils
import com.cere.csplayer.view.MarqueeTextView
import com.google.android.material.bottomappbar.BottomAppBar
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView

class MainActivity : AppCompatActivity(), View.OnClickListener, MusicManager.OnLoadDoneListener, PlayControl.OnConnectedListener, AlbumArtAdapter.OnPageChangeListener {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var mPlayControl: PlayControl
    private lateinit var mMusicManager: MusicManager
    private lateinit var mAlbumArtAdapter: AlbumArtAdapter
    private lateinit var mMusicAdapter: MusicAdapter
    private lateinit var mHandler: Handler
    private lateinit var mUpdateUI: UpdateUI
    private lateinit var mDrawerLayout: DrawerLayout
    private lateinit var mRatingBar: RatingBar
    private lateinit var ivBackground: ImageView
    private lateinit var ibPlay: ImageButton
    private lateinit var ibRepeat: ImageButton
    private lateinit var ibShuffle: ImageButton
    private lateinit var mSeekBar: SeekBar
    private lateinit var tvTimeNow: TextView
    private lateinit var tvTimeMax: TextView
    private lateinit var tvArtist: TextView
    private lateinit var tvAlbum: TextView
    private lateinit var tvNumber: TextView
    private lateinit var tvTitle: MarqueeTextView
    private var isPlaying = false
    private var isFistRun = true
    private var isAlbumArtSmoothScroll = false
    var data: String? = ""
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        mHandler = Handler()
        mMusicManager = MusicManager(this)
        initView()
        mUpdateUI = UpdateUI()
        initService()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mPlayControl.isConnected) {
            mPlayControl.unregisterCallback(mPlayCallback.callback)
            mPlayControl.disconnect()
        }
    }

    private fun initService() {
        mPlayControl = PlayControl(this, PlayService::class.java)
        mPlayControl.connect(this)
    }

    override fun onConnected(isConnected: Boolean) {
        if (isConnected) {
            mPlayControl.registerCallback(mPlayCallback.callback)
            mPlayControl.sendAction(Constants.PLAY_SERVICE_INIT, null)
            mMusicManager.load()
            initData()
        }
    }

    override fun onMusicLoadDone(list: ArrayList<Music>) {
        if (list.size > 0) {
            mMusicAdapter.setList(list)
            if (mPlayControl.isConnected) {
                mPlayControl.setList(list)
            }
        } else {
            mPlayControl.disconnect()
        }
    }

    override fun onPlayLoadDone(list: ArrayList<Play>) {
        if (list.size > 0) {
            mAlbumArtAdapter.setList(list)
            if (mPlayControl.isConnected) {
                mPlayControl.setPlayList(list)
                mPlayControl.setData(mMusicManager.musics!![mMusicManager.plays!![SharedPre.getInt(this, Constants.PLAY_INDEX, 0)].position].path!!)
            }
        } else {
            mPlayControl.disconnect()
        }
    }

    private fun initData() {
        repeat(false)
        shuffle(false)
    }

    private fun initView() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.title = ""
        toolbar.setNavigationIcon(R.drawable.ic_action_bar_home)
        setSupportActionBar(toolbar)
        mDrawerLayout = findViewById(R.id.main_drawerlayout)
        val toggle = ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        mDrawerLayout.addDrawerListener(toggle)
        val bottomAppBar = findViewById<BottomAppBar>(R.id.main_right_bottom_appbar)
        bottomAppBar.inflateMenu(R.menu.activity_main_menu_bottom_bar)
        bottomAppBar.setOnMenuItemClickListener { item ->
            onOptionsItemSelected(item)
            true
        }
        bottomAppBar.setNavigationOnClickListener {
            if (mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.closeDrawer(GravityCompat.END)
            }
        }
        ivBackground = findViewById(R.id.main_background)
        ibPlay = findViewById(R.id.main_ib_play)
        ibPlay.setOnClickListener(this)
        ibRepeat = findViewById(R.id.main_ib_repeat)
        ibRepeat.setOnClickListener(this)
        ibShuffle = findViewById(R.id.main_ib_shuffle)
        ibShuffle.setOnClickListener(this)
        ibShuffle.setOnLongClickListener {
            if (SharedPre.getBoolean(this@MainActivity, Constants.MODE_SHUFFLE, false)) {
                mMusicManager.reBuildPlays()
            }
            true
        }
        findViewById<View>(R.id.main_ib_prev).setOnClickListener(this)
        findViewById<View>(R.id.main_ib_next).setOnClickListener(this)
        tvTimeNow = findViewById(R.id.main_tv_time_now)
        tvTimeMax = findViewById(R.id.main_tv_time_max)
        tvTitle = findViewById(R.id.main_tv_title)
        tvArtist = findViewById(R.id.main_tv_artist)
        tvAlbum = findViewById(R.id.main_tv_album)
        tvNumber = findViewById(R.id.main_tv_number)
        findViewById<View>(R.id.main_right_bottom_fab).setOnClickListener(this)
        mSeekBar = findViewById(R.id.main_progress)
        mSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser && mPlayControl.isConnected) {
                    mPlayControl.seekTo(progress.toLong())
                    tvTimeNow.text = Utils.timeToString(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        mRatingBar = findViewById(R.id.main_ratingbar)
        mRatingBar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                val music = mMusicManager.getMusic(data!!)
                music.star = rating.toInt()
                SQLite.getLiteOrm(this@MainActivity).update(music)
            }
        }
        mAlbumArtAdapter = AlbumArtAdapter(this, (findViewById<View>(R.id.main_viewpager) as ViewPager2), mMusicManager)
        mAlbumArtAdapter.setOnPageChangeListener(this)
        mMusicAdapter = MusicAdapter(this, (findViewById<View>(R.id.main_right_recyclerview) as FastScrollRecyclerView))
        mMusicAdapter.setOnItemClickListener(object : MusicAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                if (mPlayControl.isConnected) {
                    if (SharedPre.getBoolean(this@MainActivity, Constants.MODE_SHUFFLE, false)) {
                        data = mMusicManager.musics!![position].path
                        mMusicManager.reBuildPlays()
                    } else {
                        mPlayControl.setData(mMusicManager.musics!![mMusicManager.plays!![position].position].path!!)
                    }
                }
            }
        })
    }

    override fun onPageSelected(position: Int) {
        if (mPlayControl.isConnected) {
            mPlayControl.setData(mMusicManager.musics!![mMusicManager.plays!![position].position].path!!)
        }
    }

    private val mPlayCallback: PlayCallback = object : PlayCallback() {
        override fun onPlay(isPlay: Boolean) {
            isPlaying = isPlay
            if (isPlay) {
                ibPlay.setImageResource(R.drawable.ic_play)
            } else {
                ibPlay.setImageResource(R.drawable.ic_pause)
            }
        }

        @SuppressLint("SetTextI18n")
        override fun onData(data: String) {
            this@MainActivity.data = data
            val music = mMusicManager.getMusic(data)
            tvTitle.text = music.title
            tvArtist.text = music.artist
            tvAlbum.text = music.album
            mRatingBar.rating = music.star.toFloat()
            val positionMusics = mMusicManager.getPosition(data)
            mMusicAdapter.setTip(positionMusics)
            if (isFistRun) {
                mMusicAdapter.scrollToTip()
                isFistRun = false
            }
            val positionPlays = mMusicManager.getPosition(positionMusics)
            tvNumber.text = "${positionPlays + 1}/${mMusicManager.musics!!.size}"
            if (isAlbumArtSmoothScroll) {
                mAlbumArtAdapter.setCurrentItem(positionPlays, true)
                isAlbumArtSmoothScroll = false
            } else {
                mAlbumArtAdapter.setCurrentItem(positionPlays, false)
            }
            mUpdateUI.setData(data)
            mHandler.post(mUpdateUI)
            SharedPre.putInt(this@MainActivity, Constants.PLAY_INDEX, positionPlays)
        }

        override fun onDuration(duration: Int) {
            mSeekBar.max = duration
            tvTimeMax.text = Utils.timeToString(duration.toLong())
        }

        override fun onCurrentDuration(duration: Int) {
            mSeekBar.progress = duration
            tvTimeNow.text = Utils.timeToString(duration.toLong())
        }

        override fun onAction(action: String, bundle: Bundle?) {
            when (action) {
                Constants.PERMISSION_WAKE_LOCK -> XXPermissions.with(this@MainActivity).constantRequest().permission(Manifest.permission.WAKE_LOCK).request(object : OnPermissionCallback() {
                    override fun hasPermission(isAll: Boolean) {
                        if (mPlayControl.isConnected) {
                            mPlayControl.sendAction(Constants.PERMISSION_WAKE_LOCK, null)
                        }
                    }
                })
                Constants.PERMISSION_READ_PHONE_STATE -> XXPermissions.with(this@MainActivity).constantRequest().permission(Permission.READ_PHONE_STATE).request(object : OnPermissionCallback() {
                    override fun hasPermission(isAll: Boolean) {
                        if (mPlayControl.isConnected) {
                            mPlayControl.sendAction(Constants.PERMISSION_READ_PHONE_STATE, null)
                        }
                    }
                })
            }
        }
    }

    @SuppressLint("CheckResult")
    private inner class UpdateUI : Runnable {
        private lateinit var data: String
        private val mRequestOptions = RequestOptions()

        init {
            val bitmap = BitmapUtils.getBitmapBlur(this@MainActivity, BitmapUtils.getScaleBitmap(BitmapFactory.decodeResource(this@MainActivity.resources, R.drawable.clannad), 10, 10), 1.0f)
            mRequestOptions.error(BitmapDrawable(this@MainActivity.resources, bitmap))
        }

        fun setData(data: String) {
            this.data = data
        }

        override fun run() {
            GlideApp.with(this@MainActivity).asBitmap().apply(mRequestOptions).transform(BlurTransformation(this@MainActivity)).load(data).into(ivBackground)
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.main_ib_play -> if (mPlayControl.isConnected) {
                if (isPlaying) {
                    mPlayControl.pause()
                } else {
                    mPlayControl.play()
                }
            }
            R.id.main_ib_prev -> if (mPlayControl.isConnected) {
                mPlayControl.previous()
                isAlbumArtSmoothScroll = true
            }
            R.id.main_ib_next -> if (mPlayControl.isConnected) {
                mPlayControl.next()
                isAlbumArtSmoothScroll = true
            }
            R.id.main_ib_repeat -> if (mPlayControl.isConnected) {
                repeat(true)
            }
            R.id.main_ib_shuffle -> if (mPlayControl.isConnected) {
                shuffle(true)
            }
            R.id.main_right_bottom_fab -> mMusicAdapter.smoothScrollToTip()
        }
    }

    private fun repeat(isClick: Boolean) {
        var mode = SharedPre.getInt(this, Constants.MODE_REPEAT, 0)
        if (isClick) {
            mode++
            if (mode > 2) {
                mode = 0
            }
            SharedPre.putInt(this, Constants.MODE_REPEAT, mode)
        }
        when (mode) {
            0 -> ibRepeat.setImageResource(R.drawable.ic_repeat_none)
            1 -> ibRepeat.setImageResource(R.drawable.ic_repeat_single)
            2 -> ibRepeat.setImageResource(R.drawable.ic_repeat_all)
        }
        if (mPlayControl.isConnected) {
            mPlayControl.setRepeatMode(mode)
        }
    }

    private fun shuffle(isClick: Boolean) {
        var mode = SharedPre.getBoolean(this, Constants.MODE_SHUFFLE, false)
        if (isClick) {
            mode = !mode
            SharedPre.putBoolean(this, Constants.MODE_SHUFFLE, mode)
            mMusicManager.reBuildPlays()
        }
        if (mode) {
            ibShuffle.setImageResource(R.drawable.ic_shuffle_all)
        } else {
            ibShuffle.setImageResource(R.drawable.ic_shuffle_none)
        }
        if (mPlayControl.isConnected) {
            mPlayControl.setShuffleModeEnabled(mode)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_setting -> {
            }
            R.id.menu_main_list -> if (!mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                mDrawerLayout.openDrawer(GravityCompat.END)
            }
            R.id.menu_main_scan -> mMusicManager.reScanMusics()
            R.id.menu_main_about -> about()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            when {
                mDrawerLayout.isDrawerOpen(GravityCompat.START) -> {
                    mDrawerLayout.closeDrawer(GravityCompat.START)
                }
                mDrawerLayout.isDrawerOpen(GravityCompat.END) -> {
                    mDrawerLayout.closeDrawer(GravityCompat.END)
                }
                else -> {
                    moveTaskToBack(false)
                }
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    private fun about() {
        DialogUtils.getAlertDialog(this, "关于", """
     版本号：${BuildConfig.VERSION_CODE}

     版本名：${BuildConfig.VERSION_NAME}

     开发者：${BuildConfig.DEVELOPER}
     """.trimIndent(), R.mipmap.ic_launcher)
                .setPositiveButton("确定", null)
                .show()
    }
}