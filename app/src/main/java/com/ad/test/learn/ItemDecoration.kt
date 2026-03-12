package com.ad.test.learn

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ad.test.R

@Preview
@Composable
fun ItemDecoration() {
    AndroidView(
        { context ->
            RecyclerView(context).apply {
                background
                layoutManager = LinearLayoutManager(context)
                adapter = SimpleAdapter().apply {
                    submitList(List(200) {
                        "Apple $it"
                    })
                }
                addItemDecoration(
                    IconItemDecoration(
                        resources.getDrawable(R.drawable.rotate_drawable),
                        resources.getDimensionPixelSize(R.dimen.icon_size),
                        resources.getDimensionPixelSize(R.dimen.zero)
                    )
                )
                addItemDecoration(
                    FrameItemDecoration(
                        resources.getDimensionPixelSize(R.dimen.frame_width)
                    )
                )
                addItemDecoration(
                    OffsetItemDecoration(resources.getDimensionPixelSize(R.dimen.offsets))
                )
            }
        },
        Modifier
            .fillMaxWidth()
    )
}

class SimpleAdapter : ListAdapter<String, SimpleAdapter.SimpleViewHolder>(
    object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ) = oldItem === newItem

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ) = oldItem == newItem
    }
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SimpleViewHolder {
        val context = parent.context
        val layout = LinearLayout(context).apply {
            background = Color(243, 229, 245, 255).toArgb().toDrawable()
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(resources.getDimensionPixelSize(R.dimen.medium))
        }

        val textView = TextView(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            textSize = 18f
//            gravity = Gravity.CENTER
        }

        layout.addView(textView)

        return SimpleViewHolder(layout, textView)
    }

    override fun onBindViewHolder(
        holder: SimpleViewHolder,
        position: Int
    ) {
        holder.textView.text = currentList[position]
    }

    class SimpleViewHolder(
        rootView: LinearLayout,
        val textView: TextView
    ) : RecyclerView.ViewHolder(rootView)
}

class OffsetItemDecoration(private val spacingInPx: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spacingInPx
        outRect.right = spacingInPx
        outRect.bottom = spacingInPx

        if (parent.getChildViewHolder(view).layoutPosition == 0)
            outRect.top = spacingInPx
    }
}

class IconItemDecoration(
    private val iconDrawable: Drawable,
    private val iconSizeInPx: Int,
    private val padding: Int
) : RecyclerView.ItemDecoration() {

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        // parent.childCount returns the number of items displayed on the screen
        for (i in 0 until parent.childCount) {

            val view = parent.getChildAt(i)

            val left = view.left + padding
            val top = view.top + padding
            val right = left + iconSizeInPx
            val bottom = top + iconSizeInPx

            iconDrawable.setBounds(left, top, right, bottom)
            iconDrawable.draw(c)
        }
    }
}

class FrameItemDecoration(private val frameWidthInPx: Int) : RecyclerView.ItemDecoration() {

    private val framePaint = Paint().apply {
        this.isAntiAlias = true
        this.style = Paint.Style.STROKE
        this.strokeWidth = frameWidthInPx.toFloat()
        this.color = "#CCE5FF".toColorInt()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

        for (i in 0 until parent.childCount) {
            val view = parent.getChildAt(i)

            /* rect top,left,right,bottom coordinates are in the middle
            of the frame border, so it should start to draw from
            view.left + half of the desired width */

            val left = (view.left - frameWidthInPx / 2).toFloat()
            val top = (view.top - frameWidthInPx / 2).toFloat()
            val bottom = (view.bottom + frameWidthInPx / 2).toFloat()
            val right = (view.right + frameWidthInPx / 2).toFloat()

            // the fifth (rx) parameter is the outer radius of the corner
            // the sixth (ry) parameter is the inner radius of the corner
            c.drawRoundRect(left, top, right, bottom, 32f, 32f, framePaint)
        }
    }
}