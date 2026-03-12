package com.ad.test.learn

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.widget.Button
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import com.ad.test.R
import com.google.android.material.button.MaterialButton

@Preview
@Composable
fun A() {
    AndroidView(
        { context ->
            val themedContext = androidx.appcompat.view.ContextThemeWrapper(
                context,
                com.google.android.material.R.style.Theme_MaterialComponents_DayNight // Hoặc theme bạn muốn
            )
            MaterialButton(themedContext).apply {
                rippleColor = ColorStateList.valueOf(android.graphics.Color.TRANSPARENT)
                text = context.getString(R.string.choice_a)
                val gradient = GradientDrawable().apply {
                    cornerRadius = resources.getDimension(R.dimen.small)
                    orientation = GradientDrawable.Orientation.LEFT_RIGHT
                    colors = intArrayOf(
                        Color(0xFFEF9A9A).toArgb(),
                        Color(0xFF80CBC4).toArgb()
                    )
                    setStroke(
                        resources.getDimensionPixelSize(R.dimen.border),
                        Color(0xFFE01313).toArgb()
                    )
                }

                background = RippleDrawable(
                    ColorStateList.valueOf(Color(50, 115, 81, 255).toArgb()),
                    gradient, // Content
                    gradient  // Mask
                )

                backgroundTintList = null
//                backgroundTintList = ColorStateList(
//                    arrayOf(
//                        intArrayOf(android.R.attr.state_pressed),
//                        intArrayOf(android.R.attr.state_focused),
//                        intArrayOf(-android.R.attr.state_enabled),
//                        intArrayOf(),
//                    ),
//                    intArrayOf(
//                        Color(0xFF80CBC4).toArgb(),
//                        Color(0xFF6C3ADF).toArgb(),
//                        Color(0xFFAAAAAA).toArgb(),
//                        Color(0xFFEF9A9A).toArgb(),
//                    )
//                )
            }
        }, Modifier.padding(16.dp)
    )
}

@Preview
@Composable
fun A1() {
    AndroidView({ context ->
        Button(context).apply {
            background = StateListDrawable().apply {
                val textfieldSearchPressed = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.outline_arrow_drop_up_24,
                    null
                )
                val textfieldSearchDefault = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.outline_arrow_drop_down_24,
                    null
                )
                addState(
                    intArrayOf(android.R.attr.state_pressed),
                    textfieldSearchPressed
                )
                addState(
                    intArrayOf(),
                    textfieldSearchDefault
                )
            }
        }
    })
}