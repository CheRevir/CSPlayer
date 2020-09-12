package com.cere.csplayer.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import com.cere.csplayer.GlideApp
import com.cere.csplayer.R
import com.cere.csplayer.entity.Music
import com.cere.csplayer.entity.Play
import com.cere.csplayer.until.FileUtils
import java.util.*

/**
 * Created by CheRevir on 2020/9/12
 */
class AlbumArtAdapter(private val context: Context) :
    BaseAdapter<Play, AlbumArtAdapter.ViewHolder>(context) {
    var musics: List<Music> = ArrayList<Music>()

    fun getPosition(id: Int): Int {
        return list.indexOf(Play(id))
    }

    override fun getCallback(): DiffUtil.ItemCallback<Play> {
        return object : DiffUtil.ItemCallback<Play>() {
            override fun areItemsTheSame(oldItem: Play, newItem: Play): Boolean {
                return oldItem.position == newItem.position
            }

            override fun areContentsTheSame(oldItem: Play, newItem: Play): Boolean {
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_viewpager, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val fd =
            FileUtils.getFileData(context, musics[getPosition(list[position].position)].getData())
        if (fd.exists) {
            GlideApp.with(context).asBitmap().load(fd.fd).into(holder.iv)
        }
    }

    class ViewHolder(itemView: View) : BaseAdapter.ViewHolder(itemView) {
        val iv: ImageView = itemView.findViewById(R.id.adapter_viewpager_album_art)

        override fun getTip(): View? {
            return null
        }
    }
}