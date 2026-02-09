package com.ad.test.learn

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.ad.test.ui.theme.TestTheme
import java.io.IOException
import kotlin.concurrent.thread


class MediaPlayerAc : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold {
                    Column(Modifier.padding(it)) {
                        Button({
                            thread {
                                val url =
                                    "https://file-examples.com/wp-content/storage/2017/11/file_example_WAV_1MG.wav" // your URL here

                                val mediaPlayer = MediaPlayer().apply {
                                    setAudioAttributes(
                                        AudioAttributes.Builder()
                                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                            .setUsage(AudioAttributes.USAGE_MEDIA)
                                            .build()
                                    )
                                    try {
                                        setDataSource(url)
                                        prepareAsync() // use prepareAsync to avoid blocking the main thread
                                        setOnPreparedListener {
                                            start()
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }) {
                            Text("Play")
                        }
                    }
                }
            }
        }
    }
}