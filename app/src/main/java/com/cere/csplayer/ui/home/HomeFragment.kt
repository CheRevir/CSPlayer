package com.cere.csplayer.ui.home

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.cere.csplayer.Constants
import com.cere.csplayer.GlideApp
import com.cere.csplayer.R
import com.cere.csplayer.activity.PlayViewModel
import com.cere.csplayer.adapter.AlbumArtAdapter
import com.cere.csplayer.data.AppDatabase
import com.cere.csplayer.data.SharePre
import com.cere.csplayer.glide.BlurTransformation
import com.cere.csplayer.ui.BaseFragment
import com.cere.csplayer.until.FileUtils
import com.cere.csplayer.until.Utils
import com.cere.csplayer.view.PageTransformer
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class HomeFragment : BaseFragment(), View.OnClickListener {
    private lateinit var playViewModel: PlayViewModel
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var adapter: AlbumArtAdapter
    private var smoothScroll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        playViewModel = ViewModelProvider(requireActivity()).get(PlayViewModel::class.java)
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        this.setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        requireActivity().main_appbar.setBackgroundColor(Color.TRANSPARENT)
        requireActivity().main_toolbar.setBackgroundColor(Color.TRANSPARENT)
        requireActivity().main_toolbar.title = ""
        requireActivity().main_toolbar.setNavigationIcon(R.drawable.ic_action_bar_home)
        requireActivity().main_toolbar.overflowIcon =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_action_bar_overflow)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        home_ib_play.setOnClickListener(this)
        home_ib_prev.setOnClickListener(this)
        home_ib_next.setOnClickListener(this)
        home_ib_repeat.setOnClickListener(this)
        home_ib_shuffle.setOnClickListener(this)
        home_sb_progress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    playViewModel.control.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        home_rating_bar.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                GlobalScope.launch {
                    playViewModel.musics.value?.let {
                        val music =
                            AppDatabase.instance.getMusicDao().query(playViewModel.id.value!!)
                        music?.let {
                            it.star = rating
                            AppDatabase.instance.getMusicDao().update(it)
                        }
                    }
                }
            }
        }
        home_viewpager.setPageTransformer(PageTransformer())
        home_viewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                adapter.position = position
                playViewModel.position.value = position
                val music =
                    playViewModel.musics.value!![adapter.getPosition(adapter.list[position].id)]
                val fd = FileUtils.getFileData(requireContext(), music.getData())
                if (fd.exists && playViewModel.control.isConnecting) {
                    playViewModel.control.setData(fd)
                }
                GlideApp.with(requireContext()).asBitmap()
                    .transform(BlurTransformation(requireContext()))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .load(fd.fd).into(home_iv_background)
            }
        })

        homeViewModel.adapter.observe(viewLifecycleOwner) {
            adapter = it
            home_viewpager.adapter = it
        }
        playViewModel.position.observe(viewLifecycleOwner) {
            if (adapter.position != it) {
                adapter.position = it
                home_viewpager.setCurrentItem(adapter.position, smoothScroll)
                if (!smoothScroll) {
                    smoothScroll = true
                }
            }
        }
        playViewModel.musics.observe(viewLifecycleOwner) {
            adapter.musics = it
        }
        playViewModel.plays.observe(viewLifecycleOwner) {
            adapter.setList(it)
        }
        /*playViewModel.id.observe(viewLifecycleOwner) {

        }
*/
        playViewModel.isPlay.observe(viewLifecycleOwner) {
            if (it) home_ib_play.setImageResource(R.drawable.ic_play)
            else home_ib_play.setImageResource(R.drawable.ic_pause)
        }
        playViewModel.title.observe(viewLifecycleOwner) {
            home_tv_title.text = it
        }
        playViewModel.artist.observe(viewLifecycleOwner) {
            home_tv_artist.text = it
        }
        playViewModel.album.observe(viewLifecycleOwner) {
            home_tv_album.text = it
        }
        playViewModel.duration.observe(viewLifecycleOwner) {
            home_sb_progress.max = it
            home_tv_time_max.text = Utils.timeToString(it)
        }
        playViewModel.current.observe(viewLifecycleOwner) {
            home_sb_progress.progress = it
            home_tv_time_now.text = Utils.timeToString(it)
        }
        playViewModel.star.observe(viewLifecycleOwner) {
            home_rating_bar.rating = it
        }
        playViewModel.repeat.observe(viewLifecycleOwner) {
            when (it) {
                0 -> home_ib_repeat.setImageResource(R.drawable.ic_repeat_none)
                1 -> home_ib_repeat.setImageResource(R.drawable.ic_repeat_single)
                2 -> home_ib_repeat.setImageResource(R.drawable.ic_repeat_all)
            }
        }
        playViewModel.shuffle.observe(viewLifecycleOwner) {
            if (it) {
                home_ib_shuffle.setImageResource(R.drawable.ic_shuffle_all)
            } else {
                home_ib_shuffle.setImageResource(R.drawable.ic_shuffle_none)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().main_appbar.setBackgroundColor(requireActivity().getColor(R.color.colorPrimaryDark))
        requireActivity().main_toolbar.setBackgroundColor(requireActivity().getColor(R.color.colorPrimary))
        requireActivity().main_toolbar.overflowIcon =
            AppCompatResources.getDrawable(requireContext(), R.drawable.ic_menu_overflow_material)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.home_ib_play -> {
                playViewModel.isPlay.value?.let {
                    if (it) playViewModel.control.pause()
                    else playViewModel.control.play()
                }
            }
            R.id.home_ib_prev -> {
                if (adapter.position == 0) {
                    smoothScroll = false
                }
                playViewModel.previous()
            }
            R.id.home_ib_next -> {
                if (adapter.position == adapter.list.size - 1) {
                    smoothScroll = false
                }
                playViewModel.next()
            }
            R.id.home_ib_repeat -> {
                var repeat = playViewModel.repeat.value!!
                repeat++
                if (repeat > 2) {
                    repeat = 0
                }
                playViewModel.repeat.value = repeat
                SharePre.putInt(Constants.MODE_REPEAT, repeat)
            }
            R.id.home_ib_shuffle -> {
                var shuffle = playViewModel.shuffle.value!!
                shuffle = !shuffle
                playViewModel.shuffle.value = shuffle
                SharePre.putBoolean(Constants.MODE_SHUFFLE, shuffle)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home_setting -> {
            }
            R.id.menu_home_music_list -> {
                requireActivity().main_drawer_layout.openDrawer(GravityCompat.END)
            }
            R.id.menu_home_scan -> {
                playViewModel.manager.scan()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}