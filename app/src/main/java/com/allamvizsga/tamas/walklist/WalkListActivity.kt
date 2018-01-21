package com.allamvizsga.tamas.walklist

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkListActivityBinding
import com.allamvizsga.tamas.util.extension.setUpToolbar
import org.koin.android.architecture.ext.getViewModel

class WalkListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<WalkListActivityBinding>(this, R.layout.walk_list_activity).apply {
            setUpToolbar(toolbar)
        }
        val viewModel = getViewModel<WalkListViewModel>()
    }
}
