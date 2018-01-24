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

    private val _walks = mutableListOf<Walk>()
    var walks: List<Walk>
        get() = _walks
        set(value) {
            _walks.clear()
            _walks.addAll(value)
            notifyDataSetChanged()
        }

    override fun getItemLayoutId(position: Int) = R.layout.walk_item

    override fun bindItem(holder: BindingViewHolder<WalkItemBinding, WalkItemViewModel>, position: Int, payloads: List<Any>) {
        holder.binding.viewModel?.setWalk(_walks[position])
    }

    override fun createViewModel(context: Context, viewType: Int) = WalkItemViewModel()

    override fun getItemCount() = _walks.size
}