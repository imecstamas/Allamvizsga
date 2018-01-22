package com.allamvizsga.tamas.feature.walklist

import android.content.Context
import com.allamvizsga.tamas.R
import com.allamvizsga.tamas.WalkItemBinding
import com.allamvizsga.tamas.feature.shared.BindingViewModelAdapter
import com.allamvizsga.tamas.model.Walk

/**
 * Created by tamas on 1/22/18.
 */
class WalkListAdapter : BindingViewModelAdapter<WalkItemBinding, WalkItemViewModel>() {

    private val items = mutableListOf<Walk>()

    override fun getItemLayoutId(position: Int) = R.layout.walk_item

    override fun bindItem(holder: BindingViewHolder<WalkItemBinding, WalkItemViewModel>, position: Int, payloads: List<Any>) {
        holder.binding.viewModel?.setWalk(items[position])
    }

    override fun createViewModel(context: Context, viewType: Int) = WalkItemViewModel()

    override fun getItemCount() = items.size

    fun refresh(walks: List<Walk>) {
        items.clear()
        items.addAll(walks)
        notifyDataSetChanged()
    }
}