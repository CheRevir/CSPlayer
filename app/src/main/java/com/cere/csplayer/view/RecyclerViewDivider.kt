package com.cere.csplayer.view

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

/**
 * Created by CheRevir on 2019/10/24
 */
class RecyclerViewDivider constructor(@ColorInt dividerColor: Int, dividerWeight: Int = 4) : ItemDecoration() {
    private val mDivider: Drawable
    private val mDividerWeight: Int

    init {
        mDivider = ColorDrawable(dividerColor)
        mDividerWeight = dividerWeight
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight + mDividerWeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect[0, 0, 0] = mDivider.intrinsicHeight + mDividerWeight
    }
}