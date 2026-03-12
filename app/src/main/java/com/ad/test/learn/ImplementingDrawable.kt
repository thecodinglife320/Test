package com.ad.test.learn

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.support.annotation.Px
import android.text.TextPaint
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.withTranslation
import com.ad.test.ui.theme.AppTheme
import kotlin.math.roundToInt

class GradientTextDrawable : Drawable() {

    private val paint = TextPaint(Paint.ANTI_ALIAS_FLAG)

    private val metrics = Paint.FontMetrics()

    var size: Float
        @Px get() = paint.textSize
        set(@Px size) {
            if (paint.textSize != size) {
                paint.textSize = size
                paint.shader = null
                invalidateSelf()
            }
        }

    var text: String = ""
        set(text) {
            if (field != text) {
                field = text
                paint.shader = null
                invalidateSelf()
            }
        }

    override fun draw(canvas: Canvas) {
        if (paint.textSize == 0f || text.isBlank()) return
        paint.getFontMetrics(metrics)
        val x = bounds.left.toFloat()
//        val y = bounds.top - metrics.ascent // metrics are all Float, Int - Float = Float
        if (paint.shader == null)
            paint.shader = LinearGradient(
                x, 0f, x + paint.measureText(text), 0f,
                intArrayOf(
                    0xFF8c5aff.toInt(),
                    0xFF_408bcf.toInt(),
                    0xFF_18b2b7.toInt(),
                    0xFF_21d789.toInt(),
                ),
                floatArrayOf(0f, .36f, .724f, 1f),
                Shader.TileMode.CLAMP
            )
        canvas.withTranslation(bounds.left.toFloat(), bounds.top.toFloat()) {
            canvas.drawText(text, 0f, -metrics.ascent, paint)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun getAlpha(): Int = paint.alpha

    override fun setAlpha(alpha: Int) {
        if (paint.alpha != alpha) {
            paint.alpha = alpha
            invalidateSelf()
        }
    }

    override fun getColorFilter(): ColorFilter? = paint.colorFilter

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
        invalidateSelf()
    }

    override fun getIntrinsicWidth(): Int =
        paint.measureText(text).roundToInt()

    override fun getIntrinsicHeight(): Int =
        metrics.run {
            paint.getFontMetrics(this)
            descent - ascent
        }.roundToInt()
}

@Preview
@Composable
fun GradientText() {
    AppTheme {

        var str by remember { mutableStateOf("") }
        var textSize by remember { mutableFloatStateOf(0f) }

        Column(Modifier.padding(8.dp)) {
            AndroidView(
                { context ->
                    View(context).apply {
                        background = GradientTextDrawable()
                    }
                    AlertDialog.Builder(context).setCancelable()
                },
                Modifier
                    .height(200.dp)
                    .fillMaxWidth(),
                {
                    (it.background as GradientTextDrawable).apply {
                        size = textSize * TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,
                            textSize,
                            it.resources.displayMetrics
                        )
                        text = str
                    }
                }
            )
            TextField(str, { str = it }, label = { Text("Name") })
            Slider(
                textSize,
                { textSize = it },
                valueRange = 0f..50f,
            )
        }
    }
}
