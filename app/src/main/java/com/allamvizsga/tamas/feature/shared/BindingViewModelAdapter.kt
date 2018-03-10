package com.allamvizsga.tamas.feature.shared

import android.content.Context
import android.databinding.DataBindingUtil
import android.databinding.OnRebindCallback
import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allamvizsga.tamas.BR

abstract class BindingViewModelAdapter<VB : ViewDataBinding, VM : Any> : RecyclerView.Adapter<BindingViewModelAdapter.BindingViewHolder<VB, VM>>() {

    protected var recyclerView: RecyclerView? = null
    private var itemClickListener: ((view: View, position: Int) -> Unit)? = null
    private val rebindCallback: OnRebindCallback<VB>

    init {
        rebindCallback = object : OnRebindCallback<VB>() {
            override fun onPreBind(binding: VB): Boolean {
                return recyclerView?.let { recyclerView ->
                    val childAdapterPosition = recyclerView.getChildAdapterPosition(binding.root)
                    (recyclerView.isComputingLayout || childAdapterPosition == RecyclerView.NO_POSITION).also {
                        if (!it) {
                            notifyItemChanged(childAdapterPosition, DB_PAYLOAD)
                        }
                    }
                } ?: true
            }
        }
    }

    @LayoutRes
    protected abstract fun getItemLayoutId(position: Int): Int

    protected abstract fun bindItem(holder: BindingViewHolder<VB, VM>, position: Int, payloads: List<Any>)

    protected abstract fun createViewModel(context: Context, @LayoutRes viewType: Int): VM?

    fun setItemClickListener(itemClickListener: (view: View, position: Int) -> Unit) {
        this.itemClickListener = itemClickListener
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        this.recyclerView = null
    }

    override fun onBindViewHolder(holder: BindingViewHolder<VB, VM>, position: Int, payloads: MutableList<Any>) {
        // when a VH is rebound to the same item, we don't have to call the setters
        if (payloads.isEmpty() || payloads.any { it !== DB_PAYLOAD }) {
            bindItem(holder, position, payloads)
        }
        holder.binding.executePendingBindings()
    }

    final override fun onBindViewHolder(holder: BindingViewHolder<VB, VM>, position: Int) {
        throw IllegalArgumentException("Just overridden to make final.")
    }

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, @LayoutRes viewType: Int): BindingViewHolder<VB, VM> {
        val viewHolder = BindingViewHolder.create<VB, VM>(parent, viewType, createViewModel(parent.context, viewType))
        viewHolder.binding.addOnRebindCallback(rebindCallback)
        viewHolder.setItemClickListener(itemClickListener)
        return viewHolder
    }

    final override fun getItemViewType(position: Int) = getItemLayoutId(position)

    companion object {
        private val DB_PAYLOAD = Any()
    }

    class BindingViewHolder<out VB : ViewDataBinding, out VM : Any>
    private constructor(val binding: VB, private val viewModel: VM?) : RecyclerView.ViewHolder(binding.root) {

        init {
            viewModel?.let {
                binding.setVariable(BR.viewModel, viewModel)
            }
        }

        fun setItemClickListener(itemClickListener: ((view: View, position: Int) -> Unit)?) {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    itemClickListener?.invoke(it, adapterPosition)
                }
            }
        }

        companion object {
            fun <VB : ViewDataBinding, VM : Any> create(parent: ViewGroup, @LayoutRes layoutId: Int, viewModel: VM?): BindingViewHolder<VB, VM> {
                val binding: VB = DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutId, parent, false)
                return BindingViewHolder(binding, viewModel)
            }
        }
    }
}