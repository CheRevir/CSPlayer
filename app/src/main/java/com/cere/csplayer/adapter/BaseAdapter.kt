package com.cere.csplayer.adapter

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.cere.csplayer.R

/**
 * Created by CheRevir on 2020/9/10
 */
@Suppress("LeakingThis")
abstract class BaseAdapter<T, V : BaseAdapter.ViewHolder>(private val context: Context) :
    RecyclerView.Adapter<V>() {
    private val differ = AsyncListDiffer(this, getCallback())
    val list: List<T>
        get() = differ.currentList
    var position = -1
        private set
    private var before = 0

    fun setList(list: List<T>) {
        differ.submitList(list)
    }

    fun setPosition(position: Int) {
        this.position = position
        this.notifyItemChanged(before, TIP)
        this.notifyItemChanged(position, TIP)
        this.before = position
    }

    override fun onBindViewHolder(holder: V, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else if (payloads[0] as Int == TIP) {
            holder.getTip()?.let {
                if (this@BaseAdapter.position == position) {
                    it.setBackgroundColor(context.getColor(R.color.colorAccent))
                } else {
                    it.setBackgroundColor(context.getColor(R.color.transparent))
                }
            }
        }
    }

    override fun onBindViewHolder(holder: V, position: Int) {
        holder.getTip()?.let {
            if (this@BaseAdapter.position == position) {
                it.setBackgroundColor(context.getColor(R.color.colorAccent))
            } else {
                it.setBackgroundColor(context.getColor(R.color.transparent))
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    abstract fun getCallback(): DiffUtil.ItemCallback<T>

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun getTip(): View?
    }

    companion object {
        private const val TIP = -1
    }
}