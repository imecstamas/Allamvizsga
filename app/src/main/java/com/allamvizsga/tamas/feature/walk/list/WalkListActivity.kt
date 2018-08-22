package com.allamvizsga.tamas.feature.walk.list

import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.ImageView
import com.allamvizsga.tamas.MainActivity
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkListActivityBinding
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.feature.walk.detail.WalkDetailActivity
import com.allamvizsga.tamas.util.extension.observe
import com.allamvizsga.tamas.util.extension.setUpToolbar
import com.allamvizsga.tamas.util.extension.startActivityWithTransition
import org.koin.android.viewmodel.ext.android.getViewModel

class WalkListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = WalkListAdapter()
        DataBindingUtil.setContentView<WalkListActivityBinding>(this, R.layout.walk_list_activity).apply {
            setUpToolbar(toolbar, false)
            viewModel = getViewModel<WalkListViewModel>().apply {
                walks.observe(this@WalkListActivity) { walks ->
                    walks?.let {
                        adapter.walks = it
                    }
                }
            }
            recyclerView.addItemDecoration(ItemSpaceDecorator(this@WalkListActivity, R.dimen.base_line))
            recyclerView.adapter = adapter

            // setup long click for opening the upload screen
            toolbar.setOnLongClickListener {
                startActivity(Intent(this@WalkListActivity, MainActivity::class.java))
                true
            }
        }
        adapter.setItemClickListener { view, position ->
            startActivityWithTransition(
                    WalkDetailActivity.getStartIntent(this, adapter.walks[position]),
                    view.findViewById<ImageView>(R.id.image_view)
            )
        }
    }
}
