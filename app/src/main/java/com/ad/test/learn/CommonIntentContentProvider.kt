package com.ad.test.learn

import android.Manifest
import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.IntentCompat
import androidx.core.net.toUri
import com.ad.test.R
import com.ad.test.ui.theme.AppTheme
import kotlinx.coroutines.launch
import java.io.File

class Main1 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        CallButton()
                        A()
                        B()
                        C()
                        D()
                        E()
                        F()
                        G()
                        H()
                    }
                }
            }
        }
    }

    @Composable
    private fun A() {
        Button({
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setData("https://www.example.com/".toUri())
                    .addCategory(Intent.CATEGORY_BROWSABLE)

            )
        }) {
            Text("A")
        }
    }

    @Composable
    private fun B() {
        Button({
            startActivity(
                Intent(Intent.ACTION_VIEW)
                    .setDataAndType(
                        "https://www.moj.gov.vn/dtvb/dtvbp/Lists/DuThaoVBQPPL/Attachments/287/D%E1%BB%B1%20th%E1%BA%A3o%20Th%C3%B4ng%20t%C6%B0.pdf".toUri(),
                        "application/pdf"
                    )
                    .addCategory(Intent.CATEGORY_BROWSABLE)
            )
        }) {
            Text("B")
        }
    }

    @Composable
    private fun C() {
        val context = LocalContext.current
        Button({
            try {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    // Schema mailto: đảm bảo chỉ có các app email mới nhận được
                    data = "mailto:support@example.com".toUri()

                    putExtra(Intent.EXTRA_SUBJECT, "Tiêu đề hỗ trợ")
                    putExtra(Intent.EXTRA_TEXT, "Chào bạn, mình cần giúp đỡ về...")
                }
                startActivity(intent)
            } catch (_: ActivityNotFoundException) {
                Toast.makeText(context, "No email app found!", Toast.LENGTH_SHORT).show()
            }
        }) {
            Text("C")
        }
    }

    @Composable
    private fun D() {
        val context = LocalContext.current
        val photoPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) {
            Log.d(this::class.simpleName, "$it")
            Toast.makeText(context, "$it", Toast.LENGTH_SHORT).show()
        }
        Button({
            photoPicker.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageAndVideo
                )
            )
        }) {
            Text("photo picker")
        }
    }

    @Composable
    private fun E() {
        Column {

            var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
            var photoFile by remember { mutableStateOf<File?>(null) }
            val takePhotoLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.TakePicture()
            ) { success ->
                if (success) {
                    photoFile?.let { photoFile ->
                        imageBitmap =
                            BitmapFactory.decodeFile(photoFile.path).asImageBitmap()
                    }
                }
                photoFile?.delete()
                photoFile = null
            }
            val context = LocalContext.current

            Button({
                val photosDir = File(cacheDir, "photos")

                photosDir.mkdirs()
                photoFile = File.createTempFile("photo", ".jpg", photosDir)

                val fileUri = photoFile?.let { file ->
                    FileProvider.getUriForFile(
                        context,
                        "$packageName.fileprovider",
                        file
                    )
                }

                //Start camera!
                fileUri?.let {
                    takePhotoLauncher.launch(it)
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

    @Preview
    @Composable
    fun F() {
        val context = LocalContext.current
        LaunchedEffect(Unit) {
            createDocsDirectory(context)
        }
        var text by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        TextField(
            modifier = Modifier.width(300.dp),
            onValueChange = { text = it },
            value = text,
            singleLine = false,
            trailingIcon = {
                IconButton({
                    val builder = AlertDialog.Builder(context)
                    val inputField = EditText(context).apply {
                        hint = "Example: note_ubuntu"
                    }
                    builder
                        .setTitle("Create new file")
                        .setMessage("Enter file name:")
                        .setView(inputField)
                        .setPositiveButton("Save") { _, _ ->
                            val fileName = inputField.text.toString()
                            if (fileName.isNotBlank()) {
                                scope.launch {

                                    val txtFile = File(
                                        File(context.filesDir, "docs"),
                                        "$fileName.txt"
                                    )

                                    try {
                                        // 4. Ghi nội dung vào file
                                        // writeText sẽ tự động tạo file mới hoặc ghi đè nếu file đã tồn tại
                                        txtFile.writeText(text)
                                        Toast.makeText(
                                            context,
                                            "Save successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        Toast.makeText(context, "${e.message}", Toast.LENGTH_SHORT)
                                            .show()
                                    }

                                }
                            } else
                                Toast.makeText(context, "File name empty", Toast.LENGTH_SHORT)
                                    .show()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()

                }) {
                    Icon(painterResource(R.drawable.outline_save_24), null)
                }
            }
        )
        text
    }

    @Composable
    fun G() {
        val context = LocalContext.current
        Button({
            val docsDir = File(context.filesDir, "docs")

            if (docsDir.exists() && docsDir.isDirectory) {
                val files = docsDir
                    .listFiles()
                    ?.map { it.name }
                    ?: emptyList()
                files.toTypedArray().let {
                    AlertDialog.Builder(this)
                        .setTitle("Alert Dialog with a List!")
                        .setItems(it) { _, i ->
                            Toast.makeText(this, it[i] + " is selected", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                }
            } else Toast.makeText(context, "No folder found", Toast.LENGTH_SHORT).show()
        }) {
            Text("Get file")
        }
    }

    @Composable
    fun H() {
        val intent = Intent(Intent.ACTION_DIAL).setData("tel:123456789".toUri())
        intent.putExtra(Intent.EXTRA_TEXT, "Hello")

        val title = "Share this image with"
        val chooser = Intent.createChooser(intent, title)
        Button({ startActivity(chooser) }) {
            Text("Implicit intent with chooser")
        }
    }

    @Composable
    private fun CallButton() {
        val context = LocalContext.current

        // Launcher để xử lý yêu cầu cấp quyền
        val permissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                // Nếu được cấp quyền, tiến hành gọi
                makePhoneCall(context)
            } else {
                // Xử lý khi bị từ chối quyền
                Toast.makeText(
                    context,
                    "Bạn cần cấp quyền gọi điện để tiếp tục",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        Button(onClick = {
            // Kiểm tra quyền trước khi gọi
            val permissionCheck =
                ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(context)
            } else {
                permissionLauncher.launch(Manifest.permission.CALL_PHONE)
            }
        }) {
            Text("Gọi")
        }
    }

    // Hàm helper để thực hiện Intent CALL
    private fun makePhoneCall(context: Context) {
        val intent = Intent(
            Intent.ACTION_CALL
//            Intent.ACTION_DIAL
        ).apply {
            data = "tel:$0362581355".toUri()
        }
        context.startActivity(intent)
    }

    fun createDocsDirectory(context: Context): File? {
        val docsDir = File(context.filesDir, "docs")

        return if (!docsDir.exists()) {
            if (docsDir.mkdirs()) docsDir else null
        } else docsDir
    }
}

class Main2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        val action = intent?.action
                        val data = intent?.data
                        Text("$data")
                        Text("$action")
                    }
                }
            }
        }
    }
}

class Main3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)
        } else if (intent?.type?.startsWith("image/") == true) {
            IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
        }
        enableEdgeToEdge()
        setContent {
            AppTheme {
                Scaffold {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(it)
                    ) {
                        val action = intent?.action
                        val data = intent?.data
                        Text("$data")
                        Text("$action")
                    }
                }
            }
        }
    }
}

class AppFileProvider : FileProvider(R.xml.provider_paths)