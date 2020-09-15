package com.cere.csplayer.activity

import androidx.appcompat.app.AppCompatActivity
import com.cere.csplayer.ui.BaseFragment

/**
 * Created by CheRevir on 2020/9/12
 */
abstract class BaseActivity : AppCompatActivity(), BaseFragment.OnFragmentChangeListener {
    protected var fragment: BaseFragment? = null

    override fun onFragmentChange(fragment: BaseFragment) {
        this.fragment = fragment
    }
}