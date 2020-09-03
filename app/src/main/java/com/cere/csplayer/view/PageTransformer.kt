package com.cere.csplayer.view

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

/**
 * Created by CheRevir on 2019/10/25
 */
class PageTransformer : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        if (position < -1 || position > 1) {
            page.alpha = 0f
        } else if (position <= 0) {
            page.alpha = 1 + position
            page.translationX = 0f
            page.scaleX = 1f
            page.scaleY = 1f
        } else if (position <= 1) {
            page.alpha = 1 - position
            page.translationX = page.width * -position
            val scaleFactor = 0.6f + (1 - 0.6f) * (1 - abs(position))
            page.scaleX = scaleFactor
            page.scaleY = scaleFactor
        }
    }
}