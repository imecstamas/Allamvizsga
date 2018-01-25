package com.allamvizsga.tamas.feature.walk.detail

import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.databinding.WalkDetailActivityBinding
import com.allamvizsga.tamas.util.extension.setUpToolbar
import org.koin.android.architecture.ext.getViewModel

class WalkDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<WalkDetailActivityBinding>(this, R.layout.walk_detail_activity).apply {
            viewModel = getViewModel<WalkDetailViewModel>().apply {
                imageUrl.set(intent.getStringExtra(IMAGE_URL))
            }
            setUpToolbar(toolbar)
        }
    }

    companion object {

        private const val IMAGE_URL = "image_url"

        fun getStartIntent(context: Context, imageUrl: String): Intent =
                Intent(context, WalkDetailActivity::class.java).putExtra(IMAGE_URL, imageUrl)
    }
}
