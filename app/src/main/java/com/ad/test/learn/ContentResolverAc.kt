package com.ad.test.learn

import android.Manifest
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.ad.test.R
import com.ad.test.ui.theme.TestTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.channels.FileChannel

class ContentResolverAc : ComponentActivity() {

    @OptIn(ExperimentalPermissionsApi::class)
    private lateinit var permissionState: PermissionState

    private var selectedImageUri by mutableStateOf<Uri?>(null)

    @OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestTheme {

                val snackBarHostState = remember { SnackbarHostState() }

                Scaffold(
                    snackbarHost = { SnackbarHost(snackBarHostState) }
                ) { innerPadding ->

                    val galleryImages = remember { mutableStateListOf<GalleryImage>() }
                    val scope = rememberCoroutineScope()
                    val launcher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        selectedImageUri = uri
                    }
                    var fileInfo by remember { mutableStateOf<FileInfo?>(null) }
                    val openFileLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.GetContent()
                    ) { uri: Uri? ->
                        uri?.let {
                            val info =
                                FileMetadataHelper.getFileQuickInfo(this@ContentResolverAc, uri)
                            info?.let { info ->
                                Toast.makeText(
                                    this@ContentResolverAc,
                                    when {
                                        info.isPng() ->"PNG File"
                                        info.isPdf() ->"PDF File "
                                        else -> "Other file"
                                    },
                                    Toast.LENGTH_SHORT
                                ).show()
                                fileInfo = info
                            }
                        }
                    }
                    permissionState = rememberPermissionState(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            Manifest.permission.READ_MEDIA_IMAGES
                        } else {
                            Manifest.permission.READ_EXTERNAL_STORAGE
                        }
                    ) { isGranted ->
                        if (isGranted) queryImages {
                            galleryImages.apply { clear() }.addAll(it)
                        } else if (permissionState.status.shouldShowRationale)
                            scope.launch {
                                snackBarHostState.showSnackbar(
                                    message = "rationale",
                                )
                            }
                        else scope.launch {
                            val result = snackBarHostState.showSnackbar(
                                message = "go to setting",
                                actionLabel = "ok",
                                withDismissAction = true
                            )
                            when (result) {
                                SnackbarResult.Dismissed -> {}
                                SnackbarResult.ActionPerformed -> {
                                    startActivity(
                                        Intent(
                                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", packageName, null)
                                        )
                                    )
                                }
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Button({
                                when (permissionState.status) {
                                    is PermissionStatus.Denied -> permissionState.launchPermissionRequest()
                                    PermissionStatus.Granted -> {
                                        queryImages {
                                            galleryImages.apply {
                                                clear()
                                            }.addAll(it)
                                        }
                                    }
                                }
                            }) {
                                Text("Ask Gallery for images")
                            }
                        }
                        item {
                            Button({
                                BitmapFactory.decodeResource(resources, R.drawable.test)?.let {
                                    saveImageToGallery(
                                        bitmap = it,
                                        fileName = "test"
                                    )
                                }
                            }) {
                                Text("Add Image")
                            }
                        }
                        item {
                            Column(
                                Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxSize()
                                    .height(100.dp)
                                    .clickable {
                                        openFileLauncher.launch("*/*")
                                    }
                            ) {
                                fileInfo?.let { fileInfo ->
                                    Text("$fileInfo")
                                }
                            }
                        }
                        item {
                            val context = LocalContext.current
                            AsyncImage(
                                model = "android.resource://${context.packageName}/${R.drawable.outline_save_24}",
                                null
                            )
                        }
                        item {
                            AndroidView(
                                update = {
                                    if (selectedImageUri != null) {
                                        registerForContextMenu(it)
                                    } else unregisterForContextMenu(it)
                                },
                                factory = { context ->
                                    FrameLayout(context).apply {
                                        setOnClickListener {
                                            launcher.launch("image/*")
                                        }
                                        val composeView = ComposeView(context).apply {
                                            setContent {
                                                selectedImageUri?.let {
                                                    AsyncImage(
                                                        model = ImageRequest.Builder(LocalContext.current)
                                                            .data(it)
                                                            .crossfade(true)
                                                            .build(),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .size(200.dp)
                                                    )
                                                } ?: Image(
                                                    painterResource(R.drawable.ic_launcher_foreground),
                                                    contentDescription = null
                                                )
                                            }
                                        }
                                        addView(composeView)
                                    }
                                },
                            )
                        }
                        items(galleryImages) {
                            ImageCard(Modifier.fillMaxWidth(), it)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menu.setHeaderTitle("Select The Action")
        menu.add("Save")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.title) {
            "Save" -> {
                handleSaveImage()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    private fun handleSaveImage() {
        selectedImageUri?.let {
            val savedFile = copyImageToPrivateStorage(
                sourceUri = it,
                fileName = "my_private_image_${System.currentTimeMillis()}.jpg"
            )

            if (savedFile != null && savedFile.exists()) {
                Toast.makeText(
                    this,
                    "Lưu ảnh thành công",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun saveImageToGallery(
        bitmap: Bitmap,
        fileName: String
    ) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            // Từ Android 10 (Q) trở lên, bạn có thể chỉ định thư mục con
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/MyApp")
                put(MediaStore.MediaColumns.IS_PENDING, 1) // Đánh dấu đang xử lý
            }
        }

        val resolver = contentResolver
        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { targetUri ->
            resolver.openOutputStream(targetUri).use { outputStream ->
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }
            }

            // Sau khi ghi xong, bỏ đánh dấu IS_PENDING để ảnh hiện lên trong Gallery
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(targetUri, contentValues, null, null)
            }
        }
    }

    private fun queryImages(onMoveCursor: (List<GalleryImage>) -> Unit) {
        val galleryImages = mutableListOf<GalleryImage>()
        contentResolver
            .query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.WIDTH,
                    MediaStore.Images.ImageColumns.HEIGHT,
                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                ),
                "${MediaStore.Images.ImageColumns.WIDTH} > ? AND ${MediaStore.Images.ImageColumns.HEIGHT} > ?",
                arrayOf("1000", "1000"),
                MediaStore.Images.ImageColumns.WIDTH + " * " +
                        MediaStore.Images.ImageColumns.HEIGHT,
                null,
            )?.use { c ->
                while (c.moveToNext()) {
                    galleryImages.add(
                        GalleryImage(
                            uri = ContentUris.withAppendedId(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                c.getLong(0)
                            ),
                            size = "${c.getInt(1)}x${c.getInt(2)}",
                            displayName = "${c.getString(3)}"
                        )
                    )
                }
            }
        onMoveCursor(galleryImages)
    }

    private fun copyImageToPrivateStorage(sourceUri: Uri, fileName: String): File? {
        val resolver = contentResolver
        // Mở InputStream từ Uri nhận được
        val inputStream: InputStream? = resolver.openInputStream(sourceUri)

        return inputStream?.use { input ->
            // Tạo file đích trong bộ nhớ riêng của app
            val outputFile = File(filesDir, fileName)

            FileOutputStream(outputFile).use { output ->
                val buffer = ByteArray(4 * 1024) // 4KB buffer
                var read: Int
                while (input.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                }
                output.flush()
            }
            outputFile // Trả về file đã lưu thành công
        }
    }
}

data class GalleryImage(
    val uri: Uri,
    val size: String,
    val displayName: String
)

@Composable
fun ImageCard(modifier: Modifier = Modifier, galleryImage: GalleryImage) {
    Card(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = galleryImage.uri,
                contentDescription = galleryImage.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .size(80.dp)
                    .clip(ShapeDefaults.Medium)
            )
            Column {
                Text(galleryImage.displayName, style = MaterialTheme.typography.titleMedium)
                Text(galleryImage.size, style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

object FileMetadataHelper {

    /**
     * Lấy kích thước file và kiểm tra định dạng nhanh (Magic Bytes)
     */
    fun getFileQuickInfo(context: Context, uri: Uri): FileInfo? {
        return try {
            // "r" nghĩa là chỉ đọc
            context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                val fileSize = pfd.statSize

                // Sử dụng FileInputStream và Channel để đọc 4 byte đầu
                val fis = FileInputStream(pfd.fileDescriptor)
                val channel = fis.channel

                // Ánh xạ chỉ 4 byte đầu tiên vào bộ nhớ (Memory Mapping)
                val mappedBuffer =
                    channel.map(FileChannel.MapMode.READ_ONLY, 0, 4L.coerceAtMost(fileSize))

                val magicBytes = StringBuilder()
                while (mappedBuffer.hasRemaining()) {
                    magicBytes.append(String.format("%02X", mappedBuffer.get()))
                }

                FileInfo(
                    size = fileSize,
                    magicBytesHex = magicBytes.toString(),
                    mimeType = context.contentResolver.getType(uri) ?: "unknown"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

data class FileInfo(
    val size: Long,
    val magicBytesHex: String,
    val mimeType: String
) {
    fun isPdf() = magicBytesHex == "25504446" // %PDF
    fun isPng() = magicBytesHex == "89504E47" // .PNG
}