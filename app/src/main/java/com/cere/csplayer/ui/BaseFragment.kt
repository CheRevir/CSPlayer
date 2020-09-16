package com.cere.csplayer.ui

import android.content.Context
import androidx.fragment.app.Fragment

/**
 * Created by CheRevir on 2020/9/12
 */
abstract class BaseFragment : Fragment() {


    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as OnFragmentChangeListener).onFragmentChange(this)
    }

    open fun onBackPressed(): Boolean {
        return false
    }

    open fun onAction(string: String) {

    }

    interface OnFragmentChangeListener {
        fun onFragmentChange(fragment: BaseFragment)
    }
}