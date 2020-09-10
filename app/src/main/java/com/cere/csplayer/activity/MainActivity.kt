package com.cere.csplayer.activity

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.cere.csplayer.R
import com.cere.csplayer.until.PermissionUtils
import com.google.android.material.snackbar.Snackbar
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableOnSubscribe
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS

@JvmOverloads
fun View.setOnShakeProofClickListener(
    windowDuration: Long = 1000,
    unit: TimeUnit = MILLISECONDS,
    listener: (view: View) -> Unit
): Disposable {
    return Observable.create(ObservableOnSubscribe<View> { emitter ->
        setOnClickListener {
            if (!emitter.isDisposed) {
                emitter.onNext(it)
            }
        }
    }).throttleFirst(windowDuration, unit)
        .subscribe { listener(it) }
}

class MainActivity : AppCompatActivity() {
    private lateinit var playViewModel: PlayViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        PermissionUtils.instance.activity = this
        setContentView(R.layout.activity_main)

        playViewModel = ViewModelProvider(this).get(PlayViewModel::class.java)

        setSupportActionBar(toolbar)

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

        /*Observable.just("a", "b").filter {
            it == "a"
        }.subscribe {
            Log.e("TAG", "MainActivity -> onCreate: $it")
        }*/

        /* GlobalScope.launch {
             delay(3000)
             Snackbar.make(fab, "Replace with your own action", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show()
         }*/


        fab.setOnShakeProofClickListener(3000, MILLISECONDS) {
            Snackbar.make(it, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action") {
                    /* runBlocking {
                         val n = AppDatabase.instance.getMusicDao()
                             .insert(Music(i++, "1", "1", "1", 1, 0, Uri.parse("scard/it.txt")))
                         Log.e("TAG", "MainActivity -> onCreatesdgasgasdga: $n")
                     }*/
                }.show()
        }

        playViewModel.isPlay.observe(this) {
            Log.e("TAG", "MainActivity -> onCreate: $it")
        }

        playViewModel.musics.observe(this) { list ->
            Log.e("TAG", "MainActivity -> onCreate: ${list.size}")
            /*list.toObservable()
                //.subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe {
                    Log.e("TAG", "MainActivity -> onCreate: ${Thread.currentThread().name}")
                    Log.e("TAG", "MainActivity -> onCreate: ${it.id}")
                }*/
        }

        //val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        //val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow),
            drawer_layout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        nav_view.setupWithNavController(navController)
    }

    /*override fun onStart() {
        super.onStart()
        if (XXPermissions.hasPermission(this, Permission.Group.STORAGE)) {
            Toast.makeText(this, "有权限", Toast.LENGTH_LONG).show()
        } else {
            XXPermissions.with(this).permission(Permission.Group.STORAGE)
                .request(object : OnPermissionCallback(this) {
                    override fun hasPermission(granted: MutableList<String>, all: Boolean) {
                        Toast.makeText(this@MainActivity, "权限已允许", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }*/

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}