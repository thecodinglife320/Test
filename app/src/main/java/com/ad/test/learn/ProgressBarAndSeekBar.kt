package com.ad.test.learn

import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.SeekBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ad.test.R

class StyleProvider : PreviewParameterProvider<Int> {
    override val values = sequenceOf(
        android.R.style.Widget_ProgressBar_Horizontal,
        androidx.appcompat.R.style.Widget_AppCompat_ProgressBar_Horizontal,
    )
}

@Composable
fun ProgressBar(style: Int) {
    AndroidView(
        { context ->
            ProgressBar(
                context,
                null,
                0,
                style,
            ).apply {
                secondaryProgress = 50
                progress = 25
                progressDrawable =
                    AppCompatResources.getDrawable(context, R.drawable.our_progress_drawable)
            }
        },
        Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Composable
fun SeekBar() {

    var value by remember { mutableIntStateOf(0) }

    Column {
        AndroidView(
            { context ->
                SeekBar(context).apply {
                    thumb = AppCompatResources.getDrawable(context, R.drawable.bolt_24px)
                    setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                            value = progress
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {
//                            TODO("Not yet implemented")
                        }

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {
//                            TODO("Not yet implemented")
                        }
                    })
                }
            },
            Modifier
                .fillMaxWidth()
        )
        Text(value.toString())
    }
}

@Composable
fun RatingBar() {
    AndroidView(
        { context ->
            RatingBar(context).apply {
                stepSize = 1f
            }
        },
    )
}

@Composable
fun CircularProgress() {
    AndroidView({ context ->
        ProgressBar(context).apply {
            indeterminateDrawable = AppCompatResources.getDrawable(
                context,
                R.drawable.rotate_drawable
            )
        }
    })
}

@Preview
@Composable
fun ProgressBarPreview(
    @PreviewParameter(StyleProvider::class) style: Int
) {
    ProgressBar(style)
}

@Preview
@Composable
fun SeekBarPreview() {
    SeekBar()
}

@Preview
@Composable
fun RatingBarPreview() {
    RatingBar()
}

@Preview
@Composable
fun CircularProgressPreview() {
    CircularProgress()
}