package com.allamvizsga.tamas.feature.walklist

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkListActivityBinding
import com.allamvizsga.tamas.util.extension.observe
import com.allamvizsga.tamas.util.extension.setUpToolbar
import org.koin.android.architecture.ext.getViewModel

class WalkListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val adapter = WalkListAdapter()
        DataBindingUtil.setContentView<WalkListActivityBinding>(this, R.layout.walk_list_activity).apply {
            setUpToolbar(toolbar)
            recyclerView.addItemDecoration(ItemSpaceDecorator(this@WalkListActivity, R.dimen.base_line))
            recyclerView.adapter = adapter
        }
        adapter.setItemClickListener { position ->
            Toast.makeText(this, adapter.walks[position].title, Toast.LENGTH_SHORT).show()
        }
        getViewModel<WalkListViewModel>().walks.observe(this) { walks ->
            walks?.let {
                adapter.walks = it
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        supportFinishAfterTransition()
        return super.onSupportNavigateUp()
    }
}
