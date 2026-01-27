package com.ad.test

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.ad.test.ui.theme.TestTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class)
class ContentResolver : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {

                val snackBarHostState = remember { SnackbarHostState() }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(rememberScrollState())
                    ) {
                        var image by remember { mutableStateOf("") }
                        val scope = rememberCoroutineScope()
                        var numberOfDeny by remember { mutableIntStateOf(0) }
                        val permissionState = rememberPermissionState(
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                Manifest.permission.READ_MEDIA_IMAGES
                            } else {
                                Manifest.permission.READ_EXTERNAL_STORAGE
                            }
                        ) {
                            if (!it) {
                                numberOfDeny++
                            }
                        }

                        LaunchedEffect(permissionState.status) {
                            if (permissionState.status.isGranted) {
                                queryImages { image = it }
                            }
                        }

                        LaunchedEffect(numberOfDeny) {
                            when {
                                numberOfDeny <= 2 -> Log.d(
                                    ContentResolver::class.simpleName,
                                    "rationale"
                                )

                                else -> Log.d(ContentResolver::class.simpleName, "setting")
                            }
                        }

                        Button({
                            if (permissionState.status.isGranted) {
                                queryImages { image = it }
                            } else permissionState.launchPermissionRequest()
                        }) {
                            Text("Ask Gallery for images")
                        }
                        Text(image.ifEmpty { "No images" })
                    }
                }
            }
        }
    }

    private fun queryImages(onMoveCursor: (String) -> Unit) {
        val stringBuilder = StringBuilder()
        contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.WIDTH,
                    MediaStore.Images.ImageColumns.HEIGHT,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                ),
                null, null, null,
            )?.use { c ->
                while (c.moveToNext()) {
                    stringBuilder.appendLine(
                        "#${c.getInt(0)}|${c.getInt(1)}|${c.getInt(2)}|${c.getString(3)}"
                    )
                }
            }
        onMoveCursor(stringBuilder.toString())
    }
}
