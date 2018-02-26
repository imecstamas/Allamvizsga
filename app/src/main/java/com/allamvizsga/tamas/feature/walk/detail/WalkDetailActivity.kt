package com.allamvizsga.tamas.feature.walk.detail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkDetailActivityBinding
import com.allamvizsga.tamas.feature.shared.BaseActivity
import com.allamvizsga.tamas.util.extension.setUpToolbar
import org.koin.android.architecture.ext.getViewModel

class WalkDetailActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<WalkDetailActivityBinding>(this, R.layout.walk_detail_activity).apply {
            viewModel = getViewModel<WalkDetailViewModel>().apply {
                imageUrl.set(intent.getStringExtra(IMAGE_URL))
                getWalkDetail(intent.getStringExtra(WALK_ID))
            }
            setUpToolbar(toolbar)
        }
    }

    companion object {

        private const val WALK_ID = "walk_id"
        private const val IMAGE_URL = "image_url"

        fun getStartIntent(context: Context, walkId: String, imageUrl: String): Intent =
                Intent(context, WalkDetailActivity::class.java).putExtra(WALK_ID, walkId).putExtra(IMAGE_URL, imageUrl)
    }
}
