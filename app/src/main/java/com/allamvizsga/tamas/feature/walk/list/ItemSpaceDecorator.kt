package com.allamvizsga.tamas.feature.walk.list

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.annotation.Px
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by tamas on 1/22/18.
 */
class ItemSpaceDecorator(context: Context, @DimenRes offsetResId: Int) : RecyclerView.ItemDecoration() {

    @Px
    private val offset = context.resources.getDimensionPixelSize(offsetResId)
    @Px
    private val halfOffset = offset / 2

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        outRect.set(offset, if (position == 0) offset else halfOffset, offset, if (position == parent.childCount) offset else halfOffset)
    }
}