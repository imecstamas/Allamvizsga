package com.allamvizsga.tamas.feature.walk.list

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.widget.ImageView
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkListActivityBinding
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.feature.walk.detail.WalkDetailActivity
import com.allamvizsga.tamas.util.extension.observe
import com.allamvizsga.tamas.util.extension.setUpToolbar
import com.allamvizsga.tamas.util.extension.startActivityWithTransition
import org.koin.android.architecture.ext.getViewModel

class WalkListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = WalkListAdapter()
        DataBindingUtil.setContentView<WalkListActivityBinding>(this, R.layout.walk_list_activity).apply {
            setUpToolbar(toolbar)
            recyclerView.addItemDecoration(ItemSpaceDecorator(this@WalkListActivity, R.dimen.base_line))
            recyclerView.adapter = adapter
        }
        adapter.setItemClickListener { view, position ->
            startActivityWithTransition(WalkDetailActivity.getStartIntent(this, adapter.walks[position]), view.findViewById<ImageView>(R.id.image_view))
        }
        getViewModel<WalkListViewModel>().walks.observe(this) { walks ->
            walks?.let {
                adapter.walks = it
            }
        }
    }
}
