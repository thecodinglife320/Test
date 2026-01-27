package com.ad.test

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.content.FileProvider
import com.ad.test.ui.theme.TestTheme
import java.io.File

class MainActivity : ComponentActivity() {

    private var photoFile: File? = null
    private var onPhotoTaken: ((ImageBitmap) -> Unit)? = null

    private val takePhotoLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            photoFile?.let { file ->
                val imageBitmap = BitmapFactory.decodeFile(file.path).asImageBitmap()
                onPhotoTaken?.invoke(imageBitmap)
            }
        }
        photoFile?.delete()
        photoFile = null
        onPhotoTaken = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {
                Scaffold { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
                        Button({
                            takePhoto {
                                imageBitmap = it
                            }
                        }) {
                            Text("Chup")
                        }
                        imageBitmap?.let {
                            Image(
                                it, null,
                            )
                        }
                    }
                }
            }
        }
    }


    private fun takePhoto(onSuccess: (ImageBitmap) -> Unit) {
        onPhotoTaken = onSuccess
        // Set everything up to make a photo.
        val photosDir = File(cacheDir, "photos")
        photosDir.mkdirs()
        photoFile = File.createTempFile("photo", ".jpg", photosDir)

        val fileUri = photoFile?.let {
            FileProvider.getUriForFile(this, "$packageName.fileprovider", it)
        }

        // Start camera!
        fileUri?.let {
            takePhotoLauncher.launch(it)
        }
    }
}
