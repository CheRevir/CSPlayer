package com.cere.csplayer.activity

import android.os.Bundle
import android.view.KeyEvent
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cere.csplayer.R
import com.cere.csplayer.adapter.MusicAdapter
import com.cere.csplayer.until.PermissionUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_right.*

class MainActivity : BaseActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var playViewModel: PlayViewModel

    private lateinit var adapter: MusicAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionUtils.instance.activity = this
        setContentView(R.layout.activity_main)

        playViewModel = ViewModelProvider(this).get(PlayViewModel::class.java)

        setSupportActionBar(main_toolbar)

        /*Observable.create<List<Music>> {
            Log.e("TAG", "MainActivity -> onCreate: ${Thread.currentThread().name}")
            val list = ArrayList<Music>()
            val project = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.RELATIVE_PATH
            )
            this.contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                project,
                null,
                null,
                MediaStore.Audio.Media.TITLE
            )?.let { cursor ->
                while (cursor.moveToNext()) {
                    val id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID))
                    val title =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                    val artist =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))
                    val album =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                    val duration =
                        cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION))
                    val parent =
                        cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.RELATIVE_PATH))
                    list.add(
                        Music(
                            id,
                            title,
                            artist,
                            album,
                            duration,
                            0,
                            parent.substring(0, parent.length - 1)
                        )
                    )
                }
                cursor.close()
            }
            it.onNext(list)
        }.subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Log.e("TAG", "MainActivity -> onCreate: ${Thread.currentThread().name}")
                Log.e("TAG", "MainActivity -> onCreate: ${it.size}")
                for (m in it) {
                    Log.e(
                        "TAG",
                        "MainActivity -> onCreate: ${
                            Uri.withAppendedPath(
                                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                m.id.toString()
                            )
                        }"
                    )
                }
                val fd = FileUtils.getFileDescriptor(
                    this, Uri.withAppendedPath(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        "3365"
                    )
                )
                Log.e("TAG", "MainActivity -> onCreate: ${fd.fd}")


                it.toObservable().distinct { m ->
                    return@distinct m.parent
                }.subscribe { i ->
                    Log.e("TAG", "MainActivity -> onCreate: $i")
                }

                it.toObservable().groupBy {
                    return@groupBy it.parent
                }.subscribe {
                    Log.e("TAG", "MainActivity -> onCreate: ${it.key}")
                    it.count().subscribe(Consumer {
                        Log.e("TAG", "MainActivity -> onCreate: $it")
                    })
                }
            }*/

        val navController = findNavController(R.id.main_nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow),
            main_drawer_layout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        main_nav_view.setupWithNavController(navController)

        main_right_bottom_appbar.setNavigationOnClickListener {
            main_drawer_layout.closeDrawer(GravityCompat.END)
        }

        adapter = MusicAdapter(this)
        main_right_recyclerview.adapter = adapter
        main_right_recyclerview.layoutManager = LinearLayoutManager(this)
        main_right_recyclerview.setHasFixedSize(true)

        playViewModel.musics.observe(this) {
            adapter.setList(it)
        }
        playViewModel.id.observe(this) {
            adapter.setPosition(adapter.getPosition(it))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.main_nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (main_drawer_layout.isDrawerOpen(GravityCompat.START)) {
                main_drawer_layout.closeDrawer(GravityCompat.START)
            } else if (main_drawer_layout.isDrawerOpen(GravityCompat.END)) {
                main_drawer_layout.closeDrawer(GravityCompat.END)
            } else if (!fragment!!.onBackPressed()) {
                return super.onKeyDown(keyCode, event)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun finish() {
        moveTaskToBack(false)
    }
}
