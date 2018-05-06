package com.hideactive.util

import android.content.Context
import android.graphics.Rect
import android.support.annotation.DimenRes
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 网格模式分割
 *
 * @author zhouchunjie
 * @date 2018/5/6
 */
class GridItemDecoration(
        context: Context,
        @DimenRes dividerSize: Int,
        includeEdge: Boolean = false
) : RecyclerView.ItemDecoration() {

    private var spanCount = -1
    private var spacing: Int = 0
    private var includeEdge: Boolean = false

    init {
        spacing = context.resources.getDimensionPixelSize(dividerSize)
        this.includeEdge = includeEdge
    }

    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        if (outRect == null || parent == null) return
        if (spanCount < 0) {
            val layoutManager = parent.layoutManager
            if (layoutManager is GridLayoutManager) {
                spanCount = layoutManager.spanCount
            } else {
                throw IllegalArgumentException("LayoutManager must be GridLayoutManager!")
            }
        }

        val position = parent.getChildAdapterPosition(view)
        val column = position % spanCount

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount
            outRect.right = (column + 1) * spacing / spanCount

            if (position < spanCount) {
                outRect.top = spacing
            }
            outRect.bottom = spacing
        } else {
            outRect.left = column * spacing / spanCount
            outRect.right = spacing - (column + 1) * spacing / spanCount
            if (position >= spanCount) {
                outRect.top = spacing
            }
        }
    }
}