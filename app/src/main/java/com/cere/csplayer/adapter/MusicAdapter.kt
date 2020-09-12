package com.cere.csplayer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.cere.csplayer.R
import com.cere.csplayer.entity.Music
import com.cere.csplayer.until.Utils

/**
 * Created by CheRevir on 2020/9/11
 */
class MusicAdapter(private val context: Context) :
    BaseAdapter<Music, MusicAdapter.ViewHolder>(context) {

    fun getPosition(id: Int): Int {
        return list.indexOf(Music(id))
    }

    override fun getCallback(): DiffUtil.ItemCallback<Music> {
        return object : DiffUtil.ItemCallback<Music>() {
            override fun areItemsTheSame(oldItem: Music, newItem: Music): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Music, newItem: Music): Boolean {
                return true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.adapter_music, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.title.text = list[position].title
        holder.artist.text = list[position].artist
        holder.duration.text = Utils.timeToString(list[position].duration)
        holder.number.text = "${position + 1}/$itemCount"
    }

    class ViewHolder(itemView: View) : BaseAdapter.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.adapter_music_tv_title)
        val artist: TextView = itemView.findViewById(R.id.adapter_music_tv_artist)
        val duration: TextView = itemView.findViewById(R.id.adapter_music_tv_duration)
        val number: TextView = itemView.findViewById(R.id.adapter_music_tv_number)

        override fun getTip(): View? {
            return itemView.findViewById(R.id.adapter_music_tip)
        }
    }
}